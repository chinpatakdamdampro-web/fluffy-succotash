package com.igrium.replayfps.game.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.game.event.ClientJoinedWorldEvent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DownloadingTerrainScreen;
import net.minecraft.client.world.ClientWorld;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "joinWorld", at = @At("TAIL"))
    void replayfps$onJoinWorld(ClientWorld world, DownloadingTerrainScreen.WorldEntryReason reason, CallbackInfo ci) {
        ClientJoinedWorldEvent.EVENT.invoker().onJoinedWorld((MinecraftClient) (Object) this, world);
    }
}
