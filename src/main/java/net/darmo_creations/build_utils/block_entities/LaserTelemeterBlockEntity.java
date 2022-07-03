package net.darmo_creations.build_utils.block_entities;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.blocks.LaserTelemeterBlock;
import net.darmo_creations.build_utils.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.MessageType;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;

/**
 * Block entity for laser telemeter.
 * <p>
 * Negative values for length fields mean that the line should be drawn in the negative direction along its axis.
 *
 * @see LaserTelemeterBlock
 * @see ModBlocks#LASER_TELEMETER
 */
public class LaserTelemeterBlockEntity extends BlockEntity {
  private static final String SIZE_TAG_KEY = "Size";
  private static final String OFFSET_TAG_KEY = "Offset";
  private static final String FILLER_BLOCK_STATE_KEY = "FillerBlockState";

  private Vec3i size;
  private Vec3i offset;
  private BlockState fillerBlockState;

  public LaserTelemeterBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.LASER_TELEMETER, pos, state);
    this.size = Vec3i.ZERO;
    this.offset = Vec3i.ZERO;
    this.fillerBlockState = Blocks.AIR.getDefaultState();
  }

  public Vec3i getSize() {
    return this.size;
  }

  public void setSize(Vec3i size) {
    this.size = Objects.requireNonNull(size);
    this.markDirty();
  }

  public Vec3i getOffset() {
    return this.offset;
  }

  public void setOffset(Vec3i offset) {
    this.offset = Objects.requireNonNull(offset);
    this.markDirty();
  }

  public BlockState getFillerBlockState() {
    return this.fillerBlockState;
  }

  public void setFillerBlockState(BlockState fillerBlockState) {
    this.fillerBlockState = Objects.requireNonNull(fillerBlockState);
    this.markDirty();
  }

  public void fillArea() {
    if (this.world instanceof ServerWorld w) {
      if (this.size.getX() != 0 && this.size.getY() != 0 && this.size.getZ() != 0 && this.fillerBlockState != null) {
        int xo1 = this.size.getX() > 0 ? 0 : -1;
        int yo1 = this.size.getY() > 0 ? 0 : -1;
        int zo1 = this.size.getZ() > 0 ? 0 : -1;
        int xo2 = this.size.getX() < 0 ? 0 : -1;
        int yo2 = this.size.getY() < 0 ? 0 : -1;
        int zo2 = this.size.getZ() < 0 ? 0 : -1;
        BlockPos pos1 = this.pos.add(this.offset).add(xo1, yo1, zo1);
        BlockPos pos2 = this.pos.add(this.offset).add(this.size).add(xo2, yo2, zo2);
        int blocksNb = Utils.fill(pos1, pos2, this.fillerBlockState, w);
        w.getServer().getPlayerManager().broadcast(
            new TranslatableText("item.build_utils.creative_wand.feedback.total_filled_volume", blocksNb), MessageType.CHAT, Util.NIL_UUID);
      } else {
        w.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.error.cannot_fill_area")
                .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
      }
    }
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.put(SIZE_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.size)));
    nbt.put(OFFSET_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.offset)));
    if (this.fillerBlockState != null) {
      nbt.put(FILLER_BLOCK_STATE_KEY, NbtHelper.fromBlockState(this.fillerBlockState));
    }
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    this.size = NbtHelper.toBlockPos(nbt.getCompound(SIZE_TAG_KEY));
    this.offset = NbtHelper.toBlockPos(nbt.getCompound(OFFSET_TAG_KEY));
    if (nbt.contains(FILLER_BLOCK_STATE_KEY, NbtElement.COMPOUND_TYPE)) {
      this.fillerBlockState = NbtHelper.toBlockState(nbt.getCompound(FILLER_BLOCK_STATE_KEY));
    } else {
      this.fillerBlockState = null;
    }
  }

  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return this.createNbt();
  }
}
