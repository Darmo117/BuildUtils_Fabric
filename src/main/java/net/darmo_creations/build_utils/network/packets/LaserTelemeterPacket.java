package net.darmo_creations.build_utils.network.packets;

import io.netty.buffer.Unpooled;
import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.darmo_creations.build_utils.network.PacketBufUtil;
import net.darmo_creations.build_utils.network.ServerPacketHandler;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;

/**
 * A packet used to sync data of a {@link LaserTelemeterBlockEntity} from client to server.
 */
public class LaserTelemeterPacket implements Packet {
  private final BlockPos pos;
  private final Vec3i offset;
  private final Vec3i size;
  private final LaserTelemeterBlockEntity.Mode mode;
  private final BlockState fillerBlockState;
  private final String structureName;
  private final boolean performAction;
  private final boolean previewPaste;

  // Invoked by PacketRegistry through reflection
  @SuppressWarnings("unused")
  public LaserTelemeterPacket(final PacketByteBuf buf) {
    this.pos = buf.readBlockPos();
    this.offset = PacketBufUtil.readVec3i(buf);
    this.size = PacketBufUtil.readVec3i(buf);
    this.mode = LaserTelemeterBlockEntity.Mode.values()[buf.readInt()];
    this.fillerBlockState = Utils.stringToBlockState(buf.readString());
    this.structureName = buf.readString().strip();
    this.performAction = buf.readBoolean();
    this.previewPaste = buf.readBoolean();
  }

  public LaserTelemeterPacket(
      final BlockPos pos,
      final Vec3i offset,
      final Vec3i size,
      final LaserTelemeterBlockEntity.Mode mode,
      final BlockState fillerBlockState,
      final String structureName,
      final boolean performAction,
      final boolean previewPaste
  ) {
    this.pos = Objects.requireNonNull(pos);
    this.offset = Objects.requireNonNull(offset);
    this.size = Objects.requireNonNull(size);
    this.mode = Objects.requireNonNull(mode);
    this.fillerBlockState = Objects.requireNonNull(fillerBlockState);
    this.structureName = structureName;
    this.performAction = performAction;
    this.previewPaste = previewPaste;
  }

  @Override
  public PacketByteBuf getBuffer() {
    PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
    buf.writeBlockPos(this.pos);
    PacketBufUtil.writeVec3i(buf, this.offset);
    PacketBufUtil.writeVec3i(buf, this.size);
    buf.writeInt(this.mode.ordinal());
    buf.writeString(Utils.blockStateToString(this.fillerBlockState));
    buf.writeString(this.structureName);
    buf.writeBoolean(this.performAction);
    buf.writeBoolean(this.previewPaste);
    return buf;
  }

  public BlockPos pos() {
    return this.pos;
  }

  public Vec3i offset() {
    return this.offset;
  }

  public Vec3i size() {
    return this.size;
  }

  public LaserTelemeterBlockEntity.Mode mode() {
    return this.mode;
  }

  public BlockState fillerBlockState() {
    return this.fillerBlockState;
  }

  public String structureName() {
    return this.structureName;
  }

  public boolean performAction() {
    return this.performAction;
  }

  public boolean previewPaste() {
    return this.previewPaste;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }
    LaserTelemeterPacket that = (LaserTelemeterPacket) o;
    return this.performAction == that.performAction && this.previewPaste == that.previewPaste && this.pos.equals(that.pos) && this.offset.equals(that.offset) && this.size.equals(that.size) && this.mode == that.mode && this.fillerBlockState.equals(that.fillerBlockState) && Objects.equals(this.structureName, that.structureName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.pos, this.offset, this.size, this.mode, this.fillerBlockState, this.structureName, this.performAction, this.previewPaste);
  }

  @Override
  public String toString() {
    return "LaserTelemeterPacket[pos=%s, offset=%s, size=%s, mode=%s, fillerBlockState=%s, structureName=%s, performAction=%s, previewPaste=%s]"
        .formatted(this.pos, this.offset, this.size, this.mode, this.fillerBlockState, this.structureName, this.performAction, this.previewPaste);
  }

  /**
   * Server-side handler for this packet.
   */
  public static class ServerHandler implements ServerPacketHandler<LaserTelemeterPacket> {
    @Override
    public void onPacket(MinecraftServer server, ServerPlayerEntity player, final LaserTelemeterPacket packet) {
      server.execute(() -> Utils.getBlockEntity(LaserTelemeterBlockEntity.class, player.world, packet.pos())
          .ifPresent(be -> {
            be.setOffset(packet.offset());
            be.setSize(packet.size());
            be.setMode(packet.mode());
            be.setFillerBlockState(packet.fillerBlockState());
            if ("".equals(packet.structureName())) {
              be.setStructureName(null);
            } else {
              be.setStructureName(packet.structureName());
            }
            if (packet.performAction()) {
              be.performAction(packet.previewPaste());
            }
          })
      );
    }
  }
}
