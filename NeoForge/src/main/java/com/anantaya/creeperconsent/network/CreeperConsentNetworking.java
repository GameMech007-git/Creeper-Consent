package com.anantaya.creeperconsent.network;
import com.anantaya.creeperconsent.CreeperConsentMod;
import com.anantaya.creeperconsent.CreeperConsentState;
import com.anantaya.creeperconsent.screen.CreeperConsentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public final class CreeperConsentNetworking {

    public static final float EXPLOSION_RADIUS = 8.0f;

    private CreeperConsentNetworking() {}

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        registrar.playToClient(
                OpenConsentScreenPayload.TYPE,
                OpenConsentScreenPayload.CODEC,
                (payload, context) -> {
                    Minecraft minecraft = Minecraft.getInstance();

                    if (minecraft.player == null) {
                        return;
                    }

                    int creeperEntityId = payload.creeperEntityId();

                    CreeperConsentMod.LOGGER.info(
                            "Received consent screen request for creeper entity {}",
                            creeperEntityId
                    );

                    minecraft.gui.setScreen(new CreeperConsentScreen(creeperEntityId));
                }
        );

        registrar.playToServer(
                CreeperConsentResponsePayload.TYPE,
                CreeperConsentResponsePayload.CODEC,
                (payload, context) -> {
                    if (!(context.player() instanceof ServerPlayer player)) {
                        return;
                    }

                    handleConsentResponse(
                            player,
                            payload.creeperEntityId(),
                            payload.allow()
                    );
                }
        );

        CreeperConsentMod.LOGGER.info("Creeper Consent networking registered.");
    }

    private static void handleConsentResponse(ServerPlayer player,
                                              int creeperEntityId,
                                              boolean allow) {
        if (!(player.level() instanceof ServerLevel world)) {
            return;
        }

        Entity entity = world.getEntity(creeperEntityId);

        if (!(entity instanceof Creeper creeper)) {
            CreeperConsentMod.LOGGER.warn(
                    "Player {} sent consent response for unknown/non-creeper entity {}",
                    player.getName().getString(),
                    creeperEntityId
            );
            return;
        }

        double distanceSq = player.distanceToSqr(creeper);

        if (distanceSq > (EXPLOSION_RADIUS * EXPLOSION_RADIUS)) {
            CreeperConsentMod.LOGGER.warn(
                    "Player {} responded but is now out of range of creeper {}",
                    player.getName().getString(),
                    creeperEntityId
            );

            CreeperConsentState.clear(creeper.getUUID());
            return;
        }

        if (allow) {
            CreeperConsentMod.LOGGER.info(
                    "Player {} ALLOWED creeper {} to explode.",
                    player.getName().getString(),
                    creeperEntityId
            );

            CreeperConsentState.approveOnce(creeper.getUUID());

            creeper.setSwellDir(1);
            creeper.ignite();

            player.sendSystemMessage(
                    Component.translatable("creeperconsentmod.message.explosion_allowed")
            );
        } else {
            CreeperConsentMod.LOGGER.info(
                    "Player {} DENIED creeper {} — removing entity.",
                    player.getName().getString(),
                    creeperEntityId
            );

            CreeperConsentState.clear(creeper.getUUID());
            creeper.discard();

            player.sendSystemMessage(
                    Component.translatable("creeperconsentmod.message.creeper_deleted")
            );
        }
    }

    public static void sendConsentScreenToNearbyPlayers(Creeper creeper,
                                                        ServerLevel world) {
        AABB blastBox = new AABB(creeper.blockPosition()).inflate(EXPLOSION_RADIUS);

        List<ServerPlayer> nearby = world.getPlayers(
                player -> player.getBoundingBox().intersects(blastBox)
        );

        if (nearby.isEmpty()) {
            return;
        }

        creeper.setSwellDir(-1);

        OpenConsentScreenPayload payload = new OpenConsentScreenPayload(creeper.getId());

        for (ServerPlayer player : nearby) {
            PacketDistributor.sendToPlayer(player, payload);

            CreeperConsentMod.LOGGER.info(
                    "Sent consent screen to {} for creeper {}",
                    player.getName().getString(),
                    creeper.getId()
            );
        }
    }
}