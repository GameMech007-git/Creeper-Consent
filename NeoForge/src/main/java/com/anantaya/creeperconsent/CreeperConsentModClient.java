package com.anantaya.creeperconsent;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = CreeperConsentMod.MOD_ID, value = Dist.CLIENT)
public class CreeperConsentModClient {
    // Keep this class for future client-only events if needed.
    // The screen-opening packet handler should be registered in CreeperConsentNetworking.
}