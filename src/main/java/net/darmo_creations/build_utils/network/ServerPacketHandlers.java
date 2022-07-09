package net.darmo_creations.build_utils.network;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.minecraft.block.BlockState;
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
   * @see C2SPacketFactory#createLaserTelemeterBEPacketByteBuffer(BlockPos, Vec3i, Vec3i, LaserTelemeterBlockEntity.Mode, BlockState, String, boolean, boolean)
   */
  public static void handleLaserTelemeterBEPacket(MinecraftServer server, final ServerPlayerEntity player, final PacketByteBuf buf) {
    BlockPos pos = buf.readBlockPos();
    Vec3i offset = PacketBufUtil.readVec3i(buf);
    Vec3i size = PacketBufUtil.readVec3i(buf);
    LaserTelemeterBlockEntity.Mode mode = LaserTelemeterBlockEntity.Mode.values()[buf.readInt()];
    BlockState fillerBlockState = Utils.stringToBlockState(buf.readString());
    String structureName = buf.readString().strip();
    boolean performAction = buf.readBoolean();
    boolean previewPaste = buf.readBoolean();
    server.execute(() -> Utils.getBlockEntity(LaserTelemeterBlockEntity.class, player.world, pos)
        .ifPresent(be -> {
          be.setOffset(offset);
          be.setSize(size);
          be.setMode(mode);
          be.setFillerBlockState(fillerBlockState);
          if ("".equals(structureName)) {
            be.setStructureName(null);
          } else {
            be.setStructureName(structureName);
          }
          if (performAction) {
            be.performAction(previewPaste);
          }
        })
    );
  }

  private ServerPacketHandlers() {
  }
}
