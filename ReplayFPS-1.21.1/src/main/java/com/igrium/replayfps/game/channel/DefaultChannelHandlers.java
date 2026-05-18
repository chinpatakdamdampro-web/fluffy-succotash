package com.igrium.replayfps.game.channel;

import static com.igrium.replayfps.core.channel.ChannelHandlers.register;

import com.igrium.replayfps.core.events.ChannelRegistrationCallback;
import com.igrium.replayfps.game.channel.handler.HorizontalSpeedHandler;
import com.igrium.replayfps.game.channel.handler.PlayerPosChannelHandler;
import com.igrium.replayfps.game.channel.handler.PlayerRotChannelHandler;
import com.igrium.replayfps.game.channel.handler.PlayerStrideChannelHandler;
import com.igrium.replayfps.game.channel.handler.PlayerVelocityChannelHandler;

import net.minecraft.util.Identifier;

public class DefaultChannelHandlers {
    public static final PlayerPosChannelHandler PLAYER_POS = register(new PlayerPosChannelHandler(), Identifier.of("replayfps", "player_pos"));
    public static final PlayerRotChannelHandler PLAYER_ROT = register(new PlayerRotChannelHandler(), Identifier.of("replayfps", "player_rot"));
    public static final PlayerVelocityChannelHandler PLAYER_VELOCITY = register(new PlayerVelocityChannelHandler(), Identifier.of("replayfps", "player_velocity"));
    public static final PlayerStrideChannelHandler PLAYER_STRIDE = register(new PlayerStrideChannelHandler(), Identifier.of("replayfps", "player_stride"));
    public static final HorizontalSpeedHandler HORIZONTAL_SPEED = register(new HorizontalSpeedHandler(), Identifier.of("replayfps", "horizontal_speed"));

    public static void registerDefaults() {
        ChannelRegistrationCallback.EVENT.register(consumer -> {
            consumer.accept(PLAYER_POS);
            consumer.accept(PLAYER_ROT);
            consumer.accept(PLAYER_VELOCITY);
            consumer.accept(PLAYER_STRIDE);
            consumer.accept(HORIZONTAL_SPEED);
        });
    }
}
