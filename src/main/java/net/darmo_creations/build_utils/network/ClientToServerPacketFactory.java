package net.darmo_creations.build_utils.network;

import io.netty.buffer.Unpooled;
import net.darmo_creations.build_utils.BuildUtils;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * A class that defines static methods that create packets to be sent from the client to the server.
 */
public class ClientToServerPacketFactory {
  public static final Identifier LASER_TELEMETER_DATA_PACKET_ID =
      new Identifier(BuildUtils.MOD_ID, "laser_telemeter_data_packet");

  /**
   * Creates a byte buffer to serve as payload for a packet to send
   * data of a laser telemeter block entity to the server.
   *
   * @see ServerPacketHandlers#handleLaserTelemeterBEPacket(MinecraftServer, ServerPlayerEntity, PacketByteBuf)
   */
  public static PacketByteBuf createLaserTelemeterBEPacketByteBuffer(final BlockPos pos, BlockPos offset, Vec3i size) {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBlockPos(pos);
    buf.writeBlockPos(offset);
    PacketBufUtil.writeVec3i(buf, size);
    return buf;
  }
}