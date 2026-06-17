package com.anantaya.creeperconsent.network;

import com.anantaya.creeperconsent.CreeperConsent;
import com.anantaya.creeperconsent.CreeperConsentState;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;

public final class CreeperConsentNetworking {

    public static final float EXPLOSION_RADIUS = 8.0f;

    private CreeperConsentNetworking() {}

    public static void register() {
        registerPayloadTypes();
        registerServerReceiver();
        CreeperConsent.LOGGER.info("Creeper Consent networking registered.");
    }

    private static void registerPayloadTypes() {

        PayloadTypeRegistry.clientboundPlay().register(
                OpenConsentScreenPayload.TYPE,
                OpenConsentScreenPayload.CODEC
        );

        PayloadTypeRegistry.serverboundPlay().register(
                CreeperConsentResponsePayload.TYPE,
                CreeperConsentResponsePayload.CODEC
        );
    }

    private static void registerServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(
                CreeperConsentResponsePayload.TYPE,
                (payload, context) -> {
                    ServerPlayer player = context.player();

                    context.server().execute(() ->
                            handleConsentResponse(
                                    player,
                                    payload.creeperEntityId(),
                                    payload.allow()
                            )
                    );
                }
        );
    }

    private static void handleConsentResponse(ServerPlayer player,
                                              int creeperEntityId,
                                              boolean allow) {
        ServerLevel world = player.level();

        Entity entity = world.getEntity(creeperEntityId);

        if (!(entity instanceof Creeper creeper)) {
            CreeperConsent.LOGGER.warn(
                    "Player {} sent consent response for unknown/non-creeper entity {}",
                    player.getName().getString(),
                    creeperEntityId
            );
            return;
        }

        double distanceSq = player.distanceToSqr(creeper);

        if (distanceSq > (EXPLOSION_RADIUS * EXPLOSION_RADIUS)) {
            CreeperConsent.LOGGER.warn(
                    "Player {} responded but is now out of range of creeper {}",
                    player.getName().getString(),
                    creeperEntityId
            );

            CreeperConsentState.clear(creeper.getUUID());
            return;
        }

        if (allow) {
            CreeperConsent.LOGGER.info(
                    "Player {} ALLOWED creeper {} to explode.",
                    player.getName().getString(),
                    creeperEntityId
            );

            CreeperConsentState.approveOnce(creeper.getUUID());

            creeper.setSwellDir(1);
            creeper.ignite();

            player.sendSystemMessage(
                    Component.translatable("creeper-consent-mod.message.explosion_allowed")
            );
        } else {
            CreeperConsent.LOGGER.info(
                    "Player {} DENIED creeper {} — removing entity.",
                    player.getName().getString(),
                    creeperEntityId
            );

            CreeperConsentState.clear(creeper.getUUID());
            creeper.discard();

            player.sendSystemMessage(
                    Component.translatable("creeper-consent-mod.message.creeper_deleted")
            );
        }
    }

}