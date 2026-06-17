package com.anantaya.creeperconsent.mixin;

import com.anantaya.creeperconsent.CreeperConsentState;
import com.anantaya.creeperconsent.CreeperEventHandler;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Creeper.class)
public class CreeperEntityMixin {

    @Inject(method = "explodeCreeper", at = @At("HEAD"), cancellable = true)
    private void onCreeperExplode(CallbackInfo ci) {
        Creeper creeper = (Creeper) (Object) this;

        // If player clicked ALLOW, allow the real vanilla explosion once.
        if (CreeperConsentState.consumeApproval(creeper.getUUID())) {
            return;
        }

        // Otherwise block vanilla explosion.
        ci.cancel();

        // Open consent GUI.
        CreeperEventHandler.checkAndTriggerConsent(creeper);
    }
}