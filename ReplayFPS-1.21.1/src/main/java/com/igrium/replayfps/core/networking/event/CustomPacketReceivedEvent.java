package com.igrium.replayfps.core.networking.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.network.packet.CustomPayload;

/**
 * Called when a custom payload packet of any kind is received on the client.
 */
public interface CustomPacketReceivedEvent {

    public static final Event<CustomPacketReceivedEvent> EVENT = EventFactory.createArrayBacked(
            CustomPacketReceivedEvent.class,
            listeners -> payload -> {
                for (CustomPacketReceivedEvent listener : listeners) {
                    if (listener.onPacketReceived(payload))
                        return true;
                }
                return false;
            });

    /**
     * Called whenever a custom payload packet is received on the client.
     *
     * @param payload The packet's payload.
     * @return If this packet should be "consumed". If {@code true}, no other
     *         receivers will process the packet.
     */
    public boolean onPacketReceived(CustomPayload payload);
}
