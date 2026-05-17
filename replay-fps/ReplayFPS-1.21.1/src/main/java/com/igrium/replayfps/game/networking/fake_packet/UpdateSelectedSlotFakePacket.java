package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.ClientPlayerEvents;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateSelectedSlotFakePacket(int slot) implements CustomPayload {

    public static final CustomPayload.Id<UpdateSelectedSlotFakePacket> ID =
            new CustomPayload.Id<>(Identifier.of("replayfps", "update_slot"));

    public static final PacketCodec<PacketByteBuf, UpdateSelectedSlotFakePacket> CODEC =
            PacketCodec.of(UpdateSelectedSlotFakePacket::write, UpdateSelectedSlotFakePacket::read);

    public static UpdateSelectedSlotFakePacket read(PacketByteBuf buf) {
        return new UpdateSelectedSlotFakePacket(buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(slot);
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void apply(UpdateSelectedSlotFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        localPlayer.getInventory().selectedSlot = packet.slot();
    }

    @SuppressWarnings("resource")
    public static void registerListener() {
        ClientPlayerEvents.SELECT_SLOT.register((inv, slot) -> {
            if (!inv.player.getWorld().isClient) return;
            FakePacketManager.injectFakePacket(new UpdateSelectedSlotFakePacket(slot));
        });
    }
}
