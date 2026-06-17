package com.anantaya.creeperconsent;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;

import java.util.List;

public class CreeperEventHandler {

    private static final float EXPLOSION_RADIUS = 8.0f;

    public static void checkAndTriggerConsent(Creeper creeper) {
        if (!(creeper.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        // Prevent sending the GUI again every tick / every cancelled explosion.
        if (!CreeperConsentState.markPending(creeper.getUUID())) {
            return;
        }

        AABB explosionBox = new AABB(creeper.blockPosition()).inflate(EXPLOSION_RADIUS);

        List<ServerPlayer> nearbyPlayers = serverLevel.getPlayers(
                player -> player.getBoundingBox().intersects(explosionBox)
        );

        if (nearbyPlayers.isEmpty()) {
            CreeperConsentState.clear(creeper.getUUID());
            return;
        }

        // Stop fuse countdown while waiting for consent.
        creeper.setSwellDir(-1);

        for (ServerPlayer player : nearbyPlayers) {
            CreeperConsentMod.LOGGER.info(
                    "SERVER sending consent screen packet to {} for creeper {}",
                    player.getName().getString(),
                    creeper.getId()
            );

            SendConsentGui.sendCreeperConsentGui(player, creeper);
        }
    }
}