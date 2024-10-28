package de.maxhenkel.inhabitor.mixin;

import de.maxhenkel.inhabitor.Inhabitor;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkAccess.class)
public class ChunkAccessMixin {

    @Unique
    private volatile boolean saveNeeded;

    @Inject(method = "incrementInhabitedTime", at = @At("RETURN"))
    private void incrementInhabitedTime(long l, CallbackInfo ci) {
        saveNeeded = true;
    }


    @Inject(method = "tryMarkSaved", at = @At("RETURN"))
    private void tryMarkSaved(CallbackInfoReturnable<Boolean> cir) {
        saveNeeded = false;
    }

    @Inject(method = "isUnsaved", at = @At("RETURN"), cancellable = true)
    private void isUnsaved(CallbackInfoReturnable<Boolean> cir) {
        if (saveNeeded && !cir.getReturnValue()) {
            cir.setReturnValue(Inhabitor.IS_TICK_SAVE.get());
        }
    }

}
