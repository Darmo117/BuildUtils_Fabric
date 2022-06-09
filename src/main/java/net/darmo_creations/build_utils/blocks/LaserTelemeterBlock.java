package net.darmo_creations.build_utils.blocks;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.darmo_creations.build_utils.gui.LaserTelemeterScreen;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

/**
 * A block that draws a box of desired size and position to measure distances, areas and volumes.
 *
 * @see LaserTelemeterBlockEntity
 */
public class LaserTelemeterBlock extends BlockWithEntity {
  public LaserTelemeterBlock() {
    // Same settings as command block
    super(FabricBlockSettings
        .of(Material.METAL, MapColor.RED)
        .sounds(BlockSoundGroup.METAL)
        .strength(-1, 3_600_000)
        .dropsNothing());
  }

  @SuppressWarnings("deprecation")
  @Override
  public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
    Optional<LaserTelemeterBlockEntity> be = Utils.getBlockEntity(LaserTelemeterBlockEntity.class, world, pos);
    if (be.isPresent() && player.isCreativeLevelTwoOp()) {
      if (world.isClient()) {
        MinecraftClient.getInstance().setScreen(new LaserTelemeterScreen(be.get()));
      }
      return ActionResult.SUCCESS;
    } else {
      return ActionResult.FAIL;
    }
  }

  @Override
  public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
    return new LaserTelemeterBlockEntity(pos, state);
  }

  @Override
  public BlockRenderType getRenderType(BlockState state) {
    return BlockRenderType.MODEL;
  }
}
