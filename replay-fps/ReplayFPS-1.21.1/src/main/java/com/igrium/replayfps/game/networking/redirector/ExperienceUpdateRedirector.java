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
            // Yarn 1.21.1 mappings:
            // getBarProgress() -> barProgress field (unchanged)
            // getExperienceLevel() -> experienceLevel (the xp bar fill 0.0-1.0) - SAME
            // getExperience() -> experience (total XP) - but Yarn names changed:
            //   Old: getBarProgress()=bar fill, getExperienceLevel()=level, getExperience()=total
            //   New (1.21.1 yarn): getBarProgress()=bar fill, getLevel()=level, getExperience()=total
            localPlayer.experienceProgress = packet.getBarProgress();
            localPlayer.experienceLevel = packet.getLevel();
            localPlayer.totalExperience = packet.getExperience();

            client.player.setExperience(packet.getBarProgress(), packet.getLevel(), packet.getExperience());
        });
    }
}
