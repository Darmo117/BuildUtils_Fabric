package net.darmo_creations.build_utils.network.packets;

import io.netty.buffer.Unpooled;
import net.darmo_creations.build_utils.network.ServerPacketHandler;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public class ToggleNightVisionPacket implements Packet {
  // Invoked by PacketRegistry through reflection
  @SuppressWarnings("unused")
  public ToggleNightVisionPacket(final PacketByteBuf buf) {
  }

  public ToggleNightVisionPacket() {
  }

  @Override
  public PacketByteBuf getBuffer() {
    return new PacketByteBuf(Unpooled.buffer());
  }

  /**
   * Server-side handler for this packet.
   */
  public static class ServerHandler implements ServerPacketHandler<ToggleNightVisionPacket> {
    @Override
    public void onPacket(MinecraftServer server, ServerPlayerEntity player, final ToggleNightVisionPacket packet) {
      server.execute(() -> {
        if (player.hasStatusEffect(StatusEffects.NIGHT_VISION)) {
          player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        } else {
          player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 99999, 0, false, false));
        }
      });
    }
  }
}
