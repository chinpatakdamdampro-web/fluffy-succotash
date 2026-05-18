package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldLoadingState;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "joinWorld", at = @At("TAIL"))
    void replayfps$onJoinWorld(ClientWorld world, WorldLoadingState worldLoadingState, CallbackInfo ci) {
        ClientJoinedWorldEvent.EVENT.invoker().onJoinedWorld((MinecraftClient) (Object) this, world);
    }
}
