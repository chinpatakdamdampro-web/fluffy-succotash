package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.core.networking.event.CustomPacketReceivedEvent;
import com.igrium.replayfps.core.networking.event.PacketReceivedEvent;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.common.CustomPayloadS2CPacket;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void replayfps$onHandlePacket(
            Packet<T> packet, PacketListener listener, CallbackInfo ci) {

        if (packet instanceof CustomPayloadS2CPacket customPayload) {
            if (CustomPacketReceivedEvent.EVENT.invoker().onPacketReceived(customPayload.payload())) {
                ci.cancel();
                return;
            }
        }

        if (PacketReceivedEvent.EVENT.invoker().onPacketReceived(packet, listener)) {
            ci.cancel();
        }
    }
}
