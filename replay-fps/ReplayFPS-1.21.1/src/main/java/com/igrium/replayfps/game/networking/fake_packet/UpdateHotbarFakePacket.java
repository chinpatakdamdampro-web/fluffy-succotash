package com.igrium.replayfps.game.networking.fake_packet;

import com.igrium.replayfps.core.networking.FakePacketManager;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.game.event.InventoryModifiedEvent;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class UpdateHotbarFakePacket implements CustomPayload {

    public static final CustomPayload.Id<UpdateHotbarFakePacket> ID =
            new CustomPayload.Id<>(Identifier.of("replayfps", "update_hotbar"));

    public static final PacketCodec<PacketByteBuf, UpdateHotbarFakePacket> CODEC =
            PacketCodec.of(UpdateHotbarFakePacket::write, UpdateHotbarFakePacket::new);

    public final Int2ObjectMap<ItemStack> map;

    public UpdateHotbarFakePacket(Int2ObjectMap<ItemStack> map) {
        this.map = map;
    }

    public UpdateHotbarFakePacket(PacketByteBuf buf) {
        int size = buf.readInt();
        map = new Int2ObjectArrayMap<>(size);
        for (int i = 0; i < size; i++) {
            int slot = buf.readInt();
            ItemStack stack = buf.readItemStack();
            map.put(slot, stack);
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(map.size());
        map.forEach((slot, stack) -> {
            buf.writeInt(slot);
            buf.writeItemStack(stack);
        });
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

    public static void apply(UpdateHotbarFakePacket packet, ClientPlaybackModule module,
            ClientCapPlayer clientCap, PlayerEntity localPlayer) {
        packet.map.forEach((slot, stack) -> {
            localPlayer.getInventory().setStack(slot, stack);
        });
    }

    @SuppressWarnings("resource")
    public static void registerListener() {
        InventoryModifiedEvent.EVENT.register((inv, map) -> {
            if (!inv.player.getWorld().isClient) return;

            Int2ObjectMap<ItemStack> changed = new Int2ObjectArrayMap<>(inv.main.size());
            int i = 0;
            for (ItemStack stack : inv.main) {
                changed.put(i, stack);
                i++;
            }
            FakePacketManager.injectFakePacket(new UpdateHotbarFakePacket(map));
        });
    }
}
