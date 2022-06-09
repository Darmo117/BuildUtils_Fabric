package net.darmo_creations.build_utils.block_entities;

import net.darmo_creations.build_utils.BuildUtils;
import net.darmo_creations.build_utils.block_entities.renderers.LaserTelemeterBlockEntityRenderer;
import net.darmo_creations.build_utils.blocks.ModBlocks;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * Declares all block entity types added by this mod.
 */
public final class ModBlockEntities {
  public static final BlockEntityType<LaserTelemeterBlockEntity> LASER_TELEMETER =
      register("laser_telemeter", LaserTelemeterBlockEntity::new, ModBlocks.LASER_TELEMETER);

  /**
   * Registers a block entity type.
   *
   * @param name    Block entity’s name.
   * @param factory A factory for the block entity type.
   * @param blocks  Block to associate to the block entity.
   * @param <T>     Type of the block entity type.
   * @param <U>     Type of the associated block entity.
   * @return The registered block entity type.
   */
  private static <T extends BlockEntityType<U>, U extends BlockEntity> T register(
      final String name, FabricBlockEntityTypeBuilder.Factory<U> factory, final Block... blocks
  ) {
    //noinspection unchecked
    return (T) Registry.register(
        Registry.BLOCK_ENTITY_TYPE,
        new Identifier(BuildUtils.MOD_ID, name),
        FabricBlockEntityTypeBuilder.create(factory, blocks).build()
    );
  }

  /**
   * Dummy method called from {@link BuildUtils#onInitialize()} to register block entity types:
   * it forces the class to be loaded during mod initialization, while the registries are unlocked.
   * <p>
   * Must be called on both clients and server.
   */
  public static void init() {
  }

  /**
   * Registers block entity renderers.
   * Must be called on client only.
   */
  public static void registerRenderers() {
    BlockEntityRendererRegistry.register(LASER_TELEMETER, LaserTelemeterBlockEntityRenderer::new);
  }

  private ModBlockEntities() {
  }
}
