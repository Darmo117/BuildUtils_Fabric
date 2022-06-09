package net.darmo_creations.build_utils.items;

import net.darmo_creations.build_utils.BuildUtils;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * This class declares all items for this mod.
 */
@SuppressWarnings("unused")
public final class ModItems {
  public static final Item RULER = register("ruler", new RulerItem(new FabricItemSettings().group(BuildUtils.ITEM_GROUP)));
  public static final Item CREATIVE_WAND = register("creative_wand", new CreativeWandItem(new FabricItemSettings().group(BuildUtils.ITEM_GROUP)));

  /**
   * Registers an item.
   *
   * @param name Item’s registry name.
   * @param item Item instance.
   * @param <T>  Item’s type.
   * @return The registered item.
   */
  private static <T extends Item> T register(final String name, final T item) {
    return Registry.register(Registry.ITEM, new Identifier(BuildUtils.MOD_ID, name), item);
  }

  /**
   * Dummy method called from {@link BuildUtils#onInitialize()} to register items:
   * it forces the class to be loaded during mod initialization, while the registries are unlocked.
   * <p>
   * Must be called on both clients and server.
   */
  public static void init() {
  }

  private ModItems() {
  }
}
