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
      // Adapted from StructureBlockBlockEntityRenderer#render
      Vec3i size = be.getSize();
      Vec3i offset = be.getOffset();
      double sizeX;
      double sizeZ;
      switch (be.getMirror()) {
        case LEFT_RIGHT -> {
          sizeX = size.getX();
          sizeZ = -size.getZ();
        }
        case FRONT_BACK -> {
          sizeX = -size.getX();
          sizeZ = size.getZ();
        }
        default -> {
          sizeX = size.getX();
          sizeZ = size.getZ();
        }
      }
      double offsetX = offset.getX();
      double offsetZ = offset.getZ();
      double boxX1;
      double boxY1 = offset.getY();
      double boxZ1;
      double boxX2;
      double boxY2 = boxY1 + (double) size.getY();
      double boxZ2;
      switch (be.getRotation()) {
        case CLOCKWISE_90 -> {
          boxX1 = sizeZ < 0 ? offsetX : offsetX + 1;
          boxZ1 = sizeX < 0 ? offsetZ + 1 : offsetZ;
          boxX2 = boxX1 - sizeZ;
          boxZ2 = boxZ1 + sizeX;
        }
        case CLOCKWISE_180 -> {
          boxX1 = sizeX < 0 ? offsetX : offsetX + 1;
          boxZ1 = sizeZ < 0 ? offsetZ : offsetZ + 1;
          boxX2 = boxX1 - sizeX;
          boxZ2 = boxZ1 - sizeZ;
        }
        case COUNTERCLOCKWISE_90 -> {
          boxX1 = sizeZ < 0 ? offsetX + 1 : offsetX;
          boxZ1 = sizeX < 0 ? offsetZ : offsetZ + 1;
          boxX2 = boxX1 + sizeZ;
          boxZ2 = boxZ1 - sizeX;
        }
        default -> {
          boxX1 = sizeX < 0 ? offsetX + 1 : offsetX;
          boxZ1 = sizeZ < 0 ? offsetZ + 1 : offsetZ;
          boxX2 = boxX1 + sizeX;
          boxZ2 = boxZ1 + sizeZ;
        }
      }
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
