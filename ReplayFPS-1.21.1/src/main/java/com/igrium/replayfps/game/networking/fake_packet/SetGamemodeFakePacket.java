package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;
import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;

public record SetGamemodeFakePacket(GameMode gamemode) implements CustomPayload {

    public static final CustomPayload.Id<SetGamemodeFakePacket> ID =
            new CustomPayload.Id<>(Identifier.of("replayfps", "set_gamemode"));

    public static final PacketCodec<PacketByteBuf, SetGamemodeFakePacket> CODEC =
            PacketCodec.of(SetGamemodeFakePacket::write, SetGamemodeFakePacket::read);

    public static SetGamemodeFakePacket read(PacketByteBuf buf) {
        return new SetGamemodeFakePacket(buf.readEnumConstant(GameMode.class));
    }

    public void write(PacketByteBuf buf) {
        buf.writeEnumConstant(gamemode);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void apply(SetGamemodeFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        module.setHudGamemode(packet.gamemode());
    }

    public static void registerListener() {
        ClientPlayerEvents.SET_GAMEMODE.register((player, oldGamemode, newGamemode) -> {
            FakePacketManager.injectFakePacket(new SetGamemodeFakePacket(newGamemode));
        });

        ClientJoinedWorldEvent.EVENT.register((client, world) -> {
            FakePacketManager.injectFakePacket(new SetGamemodeFakePacket(
                    client.interactionManager.getGameMode()));
        });
    }
}
