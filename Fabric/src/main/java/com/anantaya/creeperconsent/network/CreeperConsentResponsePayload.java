package com.anantaya.creeperconsent.network;

import com.anantaya.creeperconsent.CreeperConsent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

/**
 * C2S payload.
 * Sent by the client when the player presses ALLOW or DENY on the consent screen.
 */
public record CreeperConsentResponsePayload(int creeperEntityId, boolean allow)
        implements CustomPacketPayload {

    public static final Identifier CREEPER_CONSENT_RESPONSE_PAYLOAD_ID =
            Identifier.fromNamespaceAndPath(
                    CreeperConsent.MOD_ID,
                    "creeper_consent_response"
            );

    public static final CustomPacketPayload.Type<CreeperConsentResponsePayload> TYPE =
            new CustomPacketPayload.Type<>(CREEPER_CONSENT_RESPONSE_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, CreeperConsentResponsePayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT,
                    CreeperConsentResponsePayload::creeperEntityId,
                    ByteBufCodecs.BOOL,
                    CreeperConsentResponsePayload::allow,
                    CreeperConsentResponsePayload::new
            );

    @Override
    public CustomPacketPayload.@NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}