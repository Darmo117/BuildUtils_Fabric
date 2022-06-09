package net.darmo_creations.build_utils.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.Vec3i;

/**
 * This class defines convenience methods to write to/read from {@link PacketByteBuf} objects.
 */
public final class PacketBufUtil {
  /**
   * Writes a {@link Vec3i} to a {@link PacketByteBuf}.
   *
   * @param byteBuf Destination buffer.
   * @param vec3i   A vector.
   */
  public static void writeVec3i(PacketByteBuf byteBuf, final Vec3i vec3i) {
    byteBuf.writeDouble(vec3i.getX());
    byteBuf.writeDouble(vec3i.getY());
    byteBuf.writeDouble(vec3i.getZ());
  }

  /**
   * Reads a {@link Vec3i} from a {@link PacketByteBuf}.
   *
   * @param byteBuf Source buffer.
   * @return A vector.
   */
  public static Vec3i readVec3i(final PacketByteBuf byteBuf) {
    double x = byteBuf.readDouble();
    double y = byteBuf.readDouble();
    double z = byteBuf.readDouble();
    return new Vec3i(x, y, z);
  }

  private PacketBufUtil() {
  }
}
