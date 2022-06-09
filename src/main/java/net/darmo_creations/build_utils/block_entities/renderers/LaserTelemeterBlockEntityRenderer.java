package net.darmo_creations.build_utils.block_entities.renderers;

import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.darmo_creations.build_utils.blocks.LaserTelemeterBlock;
import net.darmo_creations.build_utils.blocks.ModBlocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;

/**
 * Renderer for the block entity associated to laser telemeters.
 * <p>
 * Renders the axes/box.
 *
 * @see LaserTelemeterBlockEntityRenderer
 * @see LaserTelemeterBlock
 * @see ModBlocks#LASER_TELEMETER
 */
public class LaserTelemeterBlockEntityRenderer implements BlockEntityRenderer<LaserTelemeterBlockEntity> {
  public LaserTelemeterBlockEntityRenderer(BlockEntityRendererFactory.Context ignored) {
  }

  @Override
  public void render(LaserTelemeterBlockEntity be, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
    PlayerEntity player = Objects.requireNonNull(MinecraftClient.getInstance().player);

    if (player.isCreativeLevelTwoOp() || player.isSpectator()) {
      Vec3i size = be.getSize();
      BlockPos offset = be.getOffset();
      int boxX1 = offset.getX();
      int boxY1 = offset.getY();
      int boxZ1 = offset.getZ();
      int boxX2 = boxX1 + size.getX();
      int boxY2 = boxY1 + size.getY();
      int boxZ2 = boxZ1 + size.getZ();
      WorldRenderer.drawBox(
          matrices, vertexConsumers.getBuffer(RenderLayer.getLines()),
          boxX1, boxY1, boxZ1, boxX2, boxY2, boxZ2,
          1, 1, 1, 1,
          0, 0, 0
      );
    }
  }

  @Override
  public boolean rendersOutsideBoundingBox(LaserTelemeterBlockEntity blockEntity) {
    return true;
  }

  @Override
  public int getRenderDistance() {
    return 1000;
  }
}
