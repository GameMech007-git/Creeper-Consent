package com.anantaya.creeperconsent;

import com.anantaya.creeperconsent.network.OpenConsentScreenPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;

public class SendConsentGui {

    public static void sendCreeperConsentGui(ServerPlayer player, Creeper creeper) {
        // Pause explosion while waiting for answer
        creeper.setSwellDir(-1);

        ServerPlayNetworking.send(
                player,
                new OpenConsentScreenPayload(creeper.getId())
        );

        CreeperConsent.LOGGER.info(
                "Sent consent GUI packet to player {} for creeper {} at {}",
                player.getName().getString(),
                creeper.getId(),
                creeper.blockPosition()
        );
    }
}