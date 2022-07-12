package net.darmo_creations.build_utils.gui;

import net.darmo_creations.build_utils.network.C2SPacketFactory;
import net.darmo_creations.build_utils.network.packets.ToggleNightVisionPacket;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * This class declares all key bindings for this mod.
 */
public class KeyBindings {
  public static final KeyBinding TOGGLE_NIGHT_VISION_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
      "key.build_utils.toggle_night_vision",
      InputUtil.Type.KEYSYM,
      GLFW.GLFW_KEY_N,
      "category.build_utils"
  ));

  /**
   * Registers all key bindings.
   */
  public static void registerKeyBindings() {
    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (TOGGLE_NIGHT_VISION_KEY.wasPressed()) {
        if (client.player != null && client.player.hasPermissionLevel(2)
            && (client.player.isCreative() || client.player.isSpectator())) {
          C2SPacketFactory.sendPacket(new ToggleNightVisionPacket());
        }
      }
    });
  }
}
