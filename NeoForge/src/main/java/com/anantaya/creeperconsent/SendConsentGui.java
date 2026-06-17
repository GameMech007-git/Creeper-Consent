package com.anantaya.creeperconsent;

import com.anantaya.creeperconsent.network.OpenConsentScreenPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.neoforged.neoforge.network.PacketDistributor;

public class SendConsentGui {

    public static void sendCreeperConsentGui(ServerPlayer player, Creeper creeper) {
        // Pause explosion while waiting for answer
        creeper.setSwellDir(-1);

        PacketDistributor.sendToPlayer(
                player,
                new OpenConsentScreenPayload(creeper.getId())
        );

        CreeperConsentMod.LOGGER.info(
                "Sent consent GUI packet to player {} for creeper {} at {}",
                player.getName().getString(),
                creeper.getId(),
                creeper.blockPosition()
        );
    }
}