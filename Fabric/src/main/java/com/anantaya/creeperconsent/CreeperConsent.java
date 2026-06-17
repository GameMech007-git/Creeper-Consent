package com.anantaya.creeperconsent;

import com.anantaya.creeperconsent.network.CreeperConsentNetworking;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreeperConsent implements ModInitializer {
	public static final String MOD_ID = "creeperconsentmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Creeper Consent Mod initialising...");
		CreeperConsentNetworking.register();
		LOGGER.info("Creeper Consent Mod ready. Creepers will now ask nicely.");
	}
}