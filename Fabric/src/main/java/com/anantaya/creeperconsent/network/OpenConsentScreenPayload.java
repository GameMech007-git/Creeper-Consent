package com.anantaya.creeperconsent.network;

import com.anantaya.creeperconsent.CreeperConsent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public record OpenConsentScreenPayload(int creeperEntityId) implements CustomPacketPayload {

    public static final Identifier OPEN_CONSENT_SCREEN_PAYLOAD_ID =
            Identifier.fromNamespaceAndPath(CreeperConsent.MOD_ID, "open_consent_screen");

    public static final Type<OpenConsentScreenPayload> TYPE =
            new Type<>(OPEN_CONSENT_SCREEN_PAYLOAD_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenConsentScreenPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    OpenConsentScreenPayload::creeperEntityId,
                    OpenConsentScreenPayload::new
            );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}