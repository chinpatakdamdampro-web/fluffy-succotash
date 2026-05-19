package com.igrium.replayfps.core.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.igrium.replayfps.ReplayFPS;
import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.igrium.replayfps.core.util.PlaybackUtils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.GameMode;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    private GameMode replayfps$prevGamemode = null;

    @Inject(method = "render", at = @At("HEAD"))
    void replayfps$onStartRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        replayfps$prevGamemode = null;

        if (!PlaybackUtils.isPlayingReplay()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        ClientCapPlayer playback = ClientPlaybackModule.getInstance().getCurrentPlayer();

        if (playback == null
                || client.world == null
                || client.player == null
                || client.interactionManager == null
                || client.cameraEntity == null
                || !ReplayFPS.getInstance().config().shouldDrawHud()) {
            return;
        }

        int localPlayerID = playback.getReader().getHeader().getLocalPlayerID();
        Entity localPlayer = client.world.getEntityById(localPlayerID);
        if (localPlayer == null) return;

        if (localPlayer.equals(client.getCameraEntity()) && localPlayer instanceof PlayerEntity) {
            replayfps$prevGamemode = client.interactionManager.getCurrentGameMode();
            client.interactionManager.setGameMode(ClientPlaybackModule.getInstance().getHudGamemode());
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    void replayfps$onEndRender(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        if (replayfps$prevGamemode != null) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.interactionManager != null && client.player != null) {
                client.interactionManager.setGameMode(replayfps$prevGamemode);
            }
            replayfps$prevGamemode = null;
        }
    }
}
