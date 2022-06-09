package net.darmo_creations.build_utils.blocks;

import net.darmo_creations.build_utils.BuildUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;

/**
 * This class declares all blocks for this mod.
 */
@SuppressWarnings("unused")
public final class ModBlocks {
  public static final Block LASER_TELEMETER = register("laser_telemeter", new BlockLaserTelemeter(), BuildUtils.ITEM_GROUP);

  /**
   * Registers a block and its item, and puts it in the given item group.
   *
   * @param name      Block’s name.
   * @param block     Block to register.
   * @param itemGroup Item group to put the block in.
   * @param <T>       Type of the block to register.
   * @return The registered block.
   */
  private static <T extends Block> T register(final String name, final T block, @Nullable final ItemGroup itemGroup) {
    Registry.register(Registry.BLOCK, new Identifier(BuildUtils.MOD_ID, name), block);
    if (itemGroup != null) {
      Registry.register(Registry.ITEM, new Identifier(BuildUtils.MOD_ID, name), new BlockItem(block, new FabricItemSettings().group(itemGroup)));
    }
    return block;
  }

  /**
   * Dummy method called from {@link BuildUtils#onInitialize()} to register blocks:
   * it forces the class to be loaded during mod initialization, while the registries are unlocked.
   * <p>
   * Must be called on both clients and server.
   */
  public static void init() {
  }

  private ModBlocks() {
  }
}
