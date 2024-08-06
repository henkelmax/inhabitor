package de.maxhenkel.inhabitor.mixin;

import de.maxhenkel.inhabitor.Inhabitor;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @Inject(method = "processUnloads", at = @At("HEAD"))
    private void setTickSaveState(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        Inhabitor.IS_TICK_SAVE.set(true);
    }

    @Inject(method = "processUnloads", at = @At("RETURN"))
    private void unsetTickSaveState(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        Inhabitor.IS_TICK_SAVE.set(false);
    }

}
