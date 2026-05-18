package com.igrium.replayfps.game.networking;

import com.igrium.replayfps.core.networking.event.FakePacketRegistrationCallback;
import com.igrium.replayfps.game.networking.fake_packet.SetGamemodeFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.UpdateHotbarFakePacket;
import com.igrium.replayfps.game.networking.fake_packet.UpdateSelectedSlotFakePacket;

public class DefaultFakePackets {
    public static void registerDefaults() {
        FakePacketRegistrationCallback.EVENT.register(manager -> {
            manager.registerReceiver(UpdateHotbarFakePacket.ID, UpdateHotbarFakePacket.class, UpdateHotbarFakePacket::apply);
            manager.registerReceiver(UpdateSelectedSlotFakePacket.ID, UpdateSelectedSlotFakePacket.class, UpdateSelectedSlotFakePacket::apply);
            manager.registerReceiver(SetGamemodeFakePacket.ID, SetGamemodeFakePacket.class, SetGamemodeFakePacket::apply);
        });

        UpdateHotbarFakePacket.registerListener();
        UpdateSelectedSlotFakePacket.registerListener();
        SetGamemodeFakePacket.registerListener();
    }
}
