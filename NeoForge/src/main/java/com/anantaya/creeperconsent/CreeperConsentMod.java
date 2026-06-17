package com.anantaya.creeperconsent;

import com.anantaya.creeperconsent.network.CreeperConsentNetworking;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(CreeperConsentMod.MOD_ID)
public class CreeperConsentMod {
	public static final String MOD_ID = "creeperconsentmod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public CreeperConsentMod(IEventBus modEventBus) {
		LOGGER.info("Creeper Consent Mod initialising...");

		modEventBus.register(CreeperConsentNetworking.class);

		LOGGER.info("Creeper Consent Mod ready. Creepers will now ask nicely.");
	}
}