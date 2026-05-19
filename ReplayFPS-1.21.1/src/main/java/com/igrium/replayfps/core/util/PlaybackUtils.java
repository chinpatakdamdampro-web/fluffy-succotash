package com.igrium.replayfps.core.util;

import com.igrium.replayfps.core.playback.ClientCapPlayer;
import com.igrium.replayfps.core.playback.ClientPlaybackContext;
import com.igrium.replayfps.core.playback.ClientPlaybackModule;
import com.replaymod.simplepathing.ReplayModSimplePathing;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public final class PlaybackUtils {
    private PlaybackUtils() {};

    public static Integer getCurrentPlaybackPlayerID() {
        ClientCapPlayer player = ClientPlaybackModule.getInstance().getCurrentPlayer();
        if (player == null) {
            net.minecraft.client.MinecraftClient.getInstance();
            com.mojang.logging.LogUtils.getLogger().warn("[ReplayFPS] getCurrentPlaybackPlayerID: currentPlayer is NULL - ccap not loaded!");
            return null;
        }
        if (player.getReader().getHeader() == null) {
            com.mojang.logging.LogUtils.getLogger().warn("[ReplayFPS] getCurrentPlaybackPlayerID: header is NULL!");
            return null;
        }
        int id = player.getReader().getHeader().getLocalPlayerID();
        com.mojang.logging.LogUtils.getLogger().info("[ReplayFPS] getCurrentPlaybackPlayerID: {}", id);
        return id;
    }

    public static PlayerEntity getCurrentPlaybackPlayer() {
        World world = MinecraftClient.getInstance().world;
        if (world == null) return null;

        Integer id = getCurrentPlaybackPlayerID();
        if (id == null) return null;
        
        if (world.getEntityById(id) instanceof PlayerEntity player) {
            return player;
        }
        return null;
    }

    public static boolean isViewingPlaybackPlayer() {
        Entity camera = MinecraftClient.getInstance().cameraEntity;
        if (camera == null) return false;
        Integer playbackId = getCurrentPlaybackPlayerID();
        boolean result = Integer.valueOf(camera.getId()).equals(playbackId);
        com.mojang.logging.LogUtils.getLogger().info("[ReplayFPS] isViewingPlaybackPlayer: cameraId={} playbackId={} result={}", camera.getId(), playbackId, result);
        return result;
    }
    
    public static boolean isPlayingReplay() {
        return ReplayModSimplePathing.instance.getGuiPathing() != null;
    }
}
