package net.darmo_creations.build_utils.network;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Gathers all server-side packet handlers.
 */
public final class ServerPacketHandlers {
  /**
   * Handler for laser telemeter block entity packets.
   *
   * @param server Server instance.
   * @param player Player that sent the packet.
   * @param buf    Packet’s payload.
   * @see ClientToServerPacketFactory#createLaserTelemeterBEPacketByteBuffer(BlockPos, BlockPos, Vec3i)
   */
  public static void handleLaserTelemeterBEPacket(MinecraftServer server, final ServerPlayerEntity player, final PacketByteBuf buf) {
    BlockPos pos = buf.readBlockPos();
    BlockPos offset = buf.readBlockPos();
    Vec3i size = PacketBufUtil.readVec3i(buf);
    server.execute(() -> Utils.getBlockEntity(LaserTelemeterBlockEntity.class, player.world, pos)
        .ifPresent(be -> {
          be.setOffset(offset);
          be.setSize(size);
        })
    );
  }

  private ServerPacketHandlers() {
  }
}
