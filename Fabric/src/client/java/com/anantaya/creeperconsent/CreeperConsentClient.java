package com.anantaya.creeperconsent;

import com.anantaya.creeperconsent.network.OpenConsentScreenPayload;
import com.anantaya.creeperconsent.screen.CreeperConsentScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;


public class CreeperConsentClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		registerClientReceivers();
		CreeperConsent.LOGGER.info("Creeper Consent client initialised.");
	}

	private static void registerClientReceivers() {
		ClientPlayNetworking.registerGlobalReceiver(
				OpenConsentScreenPayload.TYPE,
				(payload, context) -> {
					int creeperEntityId = payload.creeperEntityId();

					context.client().execute(() -> {
						if (context.client().player == null) {
							return;
						}

						CreeperConsent.LOGGER.info(
								"Received consent screen request for creeper entity {}",
								creeperEntityId
						);

						context.client().gui.setScreen(
								new CreeperConsentScreen(creeperEntityId)
						);
					});
				}
		);
	}
}