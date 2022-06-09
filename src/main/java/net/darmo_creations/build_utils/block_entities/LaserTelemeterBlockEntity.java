package net.darmo_creations.build_utils.block_entities;

import net.darmo_creations.build_utils.blocks.LaserTelemeterBlock;
import net.darmo_creations.build_utils.blocks.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

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

  private Vec3i size;
  private BlockPos offset;

  public LaserTelemeterBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.LASER_TELEMETER, pos, state);
    this.size = Vec3i.ZERO;
    this.offset = BlockPos.ORIGIN;
  }

  public Vec3i getSize() {
    return this.size;
  }

  public void setSize(Vec3i size) {
    this.size = size;
    this.markDirty();
  }

  public BlockPos getOffset() {
    return this.offset;
  }

  public void setOffset(BlockPos offset) {
    this.offset = offset;
    this.markDirty();
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.put(SIZE_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.size)));
    nbt.put(OFFSET_TAG_KEY, NbtHelper.fromBlockPos(this.offset));
  }

  @Override
  public void readNbt(NbtCompound nbt) {
    super.readNbt(nbt);
    this.size = NbtHelper.toBlockPos(nbt.getCompound(SIZE_TAG_KEY));
    this.offset = NbtHelper.toBlockPos(nbt.getCompound(OFFSET_TAG_KEY));
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
