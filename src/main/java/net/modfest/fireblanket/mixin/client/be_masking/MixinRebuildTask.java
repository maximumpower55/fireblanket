package net.modfest.fireblanket.mixin.client.be_masking;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkBuilder.BuiltChunk.RebuildTask.class)
public class MixinRebuildTask {
	@Inject(method = "addBlockEntity", at = @At("TAIL"))
	private <E extends BlockEntity> void fireblanket$addBEAnyway(ChunkBuilder.BuiltChunk.RebuildTask.RenderData renderData, E blockEntity, CallbackInfo ci) {
		if (!renderData.blockEntities.contains(blockEntity)) {
			renderData.blockEntities.add(blockEntity);
		}
	}
}
