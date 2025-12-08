package de.maxhenkel.inhabitor.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import de.maxhenkel.inhabitor.Inhabitor;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ChunkMap.class)
public class ChunkMapMixin {

    @WrapOperation(method = "scheduleUnload", at = @At(value = "INVOKE", target = "Ljava/util/concurrent/CompletableFuture;thenRunAsync(Ljava/lang/Runnable;Ljava/util/concurrent/Executor;)Ljava/util/concurrent/CompletableFuture;"))
    private CompletableFuture<Void> scheduleUnload(CompletableFuture instance, Runnable action, Executor executor, Operation<CompletableFuture<Void>> original) {
        return original.call(instance, (Runnable) () -> {
            Inhabitor.IS_SCHEDULE_UNLOAD.set(true);
            action.run();
            Inhabitor.IS_SCHEDULE_UNLOAD.set(false);
        }, executor);
    }

}
