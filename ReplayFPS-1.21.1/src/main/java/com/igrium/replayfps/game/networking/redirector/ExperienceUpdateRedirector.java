package com.igrium.replayfps.game.networking.redirector;

import com.igrium.replayfps.core.networking.PacketRedirector;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ExperienceBarUpdateS2CPacket;

public class ExperienceUpdateRedirector implements PacketRedirector<ExperienceBarUpdateS2CPacket> {

    @Override
    public Class<ExperienceBarUpdateS2CPacket> getPacketClass() {
        return ExperienceBarUpdateS2CPacket.class;
    }

    @Override
    public boolean shouldRedirect(ExperienceBarUpdateS2CPacket packet, PlayerEntity localPlayer,
            MinecraftClient client) {
        return true;
    }

    @Override
    public void redirect(ExperienceBarUpdateS2CPacket packet, PlayerEntity localPlayer, MinecraftClient client) {
        client.execute(() -> {
            // In 1.21.1 yarn: getBarProgress(), getExperienceLevel() (=level int), getExperience() (=total)
            localPlayer.experienceProgress = packet.getBarProgress();
            localPlayer.experienceLevel = packet.getExperienceLevel();
            localPlayer.totalExperience = packet.getExperience();

            client.player.setExperience(packet.getBarProgress(), packet.getExperienceLevel(), packet.getExperience());
        });
    }
}
