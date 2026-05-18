package com.igrium.replayfps.core.networking;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.igrium.replayfps.core.mixin.ClientConnectionAccessor;
import com.igrium.replayfps.core.networking.event.FakePacketRegistrationCallback;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.util.PlaybackUtils;
import com.mojang.logging.LogUtils;

import io.netty.channel.Channel;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;
import net.minecraft.util.Identifier;

/**
 * Manages "fake packets" - custom payloads that are injected into the replay
 * stream during recording and applied during playback.
 */
public class FakePacketManager {

    /** Namespace prefix used to mark payloads as fake/replay packets. */
    public static final String PREFIX = "rp_";

    public static enum SpectatorRule { APPLY, SKIP }

    private final MinecraftClient client;
    private final ClientPlaybackModule module;
    private final ClientCapPlayer clientCap;

    private final Map<Identifier, FakePacketHandlerEntry<?>> handlers =
            Collections.synchronizedMap(new HashMap<>());
    private final Map<Identifier, SpectatorRule> spectatorRules =
            Collections.synchronizedMap(new HashMap<>());

    public FakePacketManager(MinecraftClient client, ClientPlaybackModule module, ClientCapPlayer clientCap) {
        this.client = client;
        this.module = module;
        this.clientCap = clientCap;
    }

    public void initReceivers() {
        FakePacketRegistrationCallback.EVENT.invoker().register(this);
    }

    /** Returns true if the given ID has the fake-packet prefix. */
    public static boolean isFakePacket(Identifier rawId) {
        return rawId.getNamespace().startsWith(PREFIX);
    }

    /**
     * Process an incoming fake-packet payload (called from the mixin).
     * @return true if the payload was consumed.
     */
    public boolean processPacket(CustomPayload payload) {
        Identifier rawId = payload.getId().id();
        if (!rawId.getNamespace().startsWith(PREFIX)) return false;

        // Strip the prefix to get the original id
        String namespace = rawId.getNamespace().substring(PREFIX.length());
        Identifier id = Identifier.of(namespace, rawId.getPath());

        handlePayload(id, payload);
        return true;
    }

    @SuppressWarnings("unchecked")
    private <T extends CustomPayload> void handlePayload(Identifier id, CustomPayload payload) {
        FakePacketHandlerEntry<T> entry = (FakePacketHandlerEntry<T>) handlers.get(id);
        if (entry == null) return;

        client.execute(() -> {
            Optional<PlayerEntity> playerOpt = module.getLocalPlayer();
            if (playerOpt.isEmpty()) return;
            PlayerEntity player = playerOpt.get();

            SpectatorRule rule = spectatorRules.getOrDefault(id, SpectatorRule.APPLY);
            if (client.getCameraEntity() != player && rule != SpectatorRule.APPLY) return;

            try {
                // The payload stored inside PrefixedPayload is the real typed payload
                if (payload instanceof PrefixedPayload prefixed && entry.type.isInstance(prefixed.inner())) {
                    entry.handler.handle(entry.type.cast(prefixed.inner()), module, clientCap, player);
                }
            } catch (Throwable ex) {
                LogUtils.getLogger().error("Error handling fake packet: " + id, ex);
            }
        });
    }

    public <T extends CustomPayload> void registerReceiver(CustomPayload.Id<T> id, Class<T> type, FakePacketHandler<T> handler) {
        handlers.put(id.id(), new FakePacketHandlerEntry<>(type, handler));
    }

    public void addSpectatorRule(CustomPayload.Id<?> id, SpectatorRule spectatorRule) {
        spectatorRules.put(id.id(), Objects.requireNonNull(spectatorRule));
    }

    @FunctionalInterface
    public interface FakePacketHandler<T extends CustomPayload> {
        void handle(T packet, ClientPlaybackModule module, ClientCapPlayer clientCap, PlayerEntity localPlayer);
    }

    private record FakePacketHandlerEntry<T extends CustomPayload>(Class<T> type, FakePacketHandler<T> handler) {}

    /**
     * Inject a fake payload into the replay packet stream during recording.
     */
    public static <T extends CustomPayload> void injectFakePacket(T payload) {
        injectPacket(new CustomPayloadS2CPacket(new PrefixedPayload(payload)));
    }

    public static void injectPacket(Packet<?> packet) {
        MinecraftClient client = MinecraftClient.getInstance();
        ClientPlayNetworkHandler netHandler = client.getNetworkHandler();
        if (netHandler == null) {
            throw new IllegalStateException("Packets can only be injected while a game is active.");
        }
        if (PlaybackUtils.isPlayingReplay()) return;

        Channel channel = ((ClientConnectionAccessor) netHandler.getConnection()).replayfps$getChannel();
        if (channel.eventLoop().inEventLoop()) {
            channel.pipeline().fireChannelRead(packet);
        } else {
            channel.eventLoop().execute(() -> channel.pipeline().fireChannelRead(packet));
        }
    }

    /**
     * A CustomPayload wrapper that prepends the rp_ prefix to the namespace so our
     * mixin can intercept it before standard packet handlers process it.
     */
    public static final class PrefixedPayload implements CustomPayload {

        private final CustomPayload inner;
        private final Id<PrefixedPayload> prefixedId;

        public PrefixedPayload(CustomPayload inner) {
            this.inner = inner;
            Identifier orig = inner.getId().id();
            this.prefixedId = new Id<>(Identifier.of(PREFIX + orig.getNamespace(), orig.getPath()));
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return prefixedId;
        }

        /** Returns the original (un-prefixed) inner payload. */
        public CustomPayload inner() {
            return inner;
        }

        /** Returns the original (un-prefixed) identifier. */
        public Identifier innerId() {
            return inner.getId().id();
        }
    }
}
