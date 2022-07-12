package net.darmo_creations.build_utils;

import net.darmo_creations.build_utils.block_entities.ModBlockEntities;
import net.darmo_creations.build_utils.gui.KeyBindings;
import net.fabricmc.api.ClientModInitializer;

/**
 * Client-side mod initializer.
 */
public class ClientInitializer implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    ModBlockEntities.registerRenderers();
    KeyBindings.registerKeyBindings();
  }
}
