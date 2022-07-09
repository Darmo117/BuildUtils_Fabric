package net.darmo_creations.build_utils;

import net.darmo_creations.build_utils.block_entities.ModBlockEntities;
import net.darmo_creations.build_utils.blocks.ModBlocks;
import net.darmo_creations.build_utils.items.ModItems;
import net.darmo_creations.build_utils.network.C2SPacketFactory;
import net.darmo_creations.build_utils.network.packets.LaserTelemeterPacket;
import net.darmo_creations.build_utils.network.PacketRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod’s main class. Common initializer for both client and server.
 */
public class BuildUtils implements ModInitializer {
  public static final String MOD_ID = "build_utils";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  // Creative mode’s item group
  public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(
      new Identifier(MOD_ID, "general"),
      () -> new ItemStack(ModBlocks.LASER_TELEMETER)
  );

  @Override
  public void onInitialize() {
    ModBlocks.init();
    ModItems.init();
    ModBlockEntities.init();
    this.registerPackets();
  }

  /**
   * Registers all packets.
   */
  private void registerPackets() {
    PacketRegistry.registerPacket(
        C2SPacketFactory.LASER_TELEMETER_DATA_PACKET_ID,
        LaserTelemeterPacket.class,
        new LaserTelemeterPacket.ServerHandler()
    );
  }
}
