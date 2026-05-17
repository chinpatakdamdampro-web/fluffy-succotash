package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.igrium.replayfps.core.networking.event.CustomPacketReceivedEvent;

import net.fabricmc.fabric.impl.networking.AbstractChanneledNetworkAddon;

/**
 * Intercepts custom payload handling so fake packets can be consumed before
 * reaching standard Fabric packet handlers.
 *
 * In 1.21.1 the internal Fabric API still has AbstractChanneledNetworkAddon,
 * but the payload type is now net.minecraft.network.packet.CustomPayload.
 */
@Mixin(value = AbstractChanneledNetworkAddon.class, remap = false)
public class AbstractChanneledNetworkAddonMixin<H> {

    @Inject(method = "handle",
            at = @At(value = "INVOKE",
                    target = "Lnet/fabricmc/fabric/impl/networking/AbstractChanneledNetworkAddon;getHandler(Lnet/minecraft/util/Identifier;)Ljava/lang/Object;",
                    remap = false),
            remap = false,
            cancellable = true)
    public void replayfps$handle(net.minecraft.network.packet.CustomPayload payload,
            CallbackInfoReturnable<Boolean> ci) {
        if (CustomPacketReceivedEvent.EVENT.invoker().onPacketReceived(payload)) {
            ci.setReturnValue(true);
        }
    }
}
