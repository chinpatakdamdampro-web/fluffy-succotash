package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.igrium.replayfps.core.networking.event.CustomPacketReceivedEvent;

import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;
import net.minecraft.network.packet.CustomPayload;

@Mixin(value = AbstractChanneledNetworkAddon.class, remap = false)
public abstract class AbstractChanneledNetworkAddonMixin<H> {

    @Inject(
        method = "receive(Lnet/minecraft/network/packet/CustomPayload;)Z",
        at = @At("HEAD"),
        remap = false,
        cancellable = true
    )
    public void replayfps$receive(CustomPayload payload, CallbackInfoReturnable<Boolean> ci) {
        if (CustomPacketReceivedEvent.EVENT.invoker().onPacketReceived(payload)) {
            ci.setReturnValue(true);
        }
    }
}
