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
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Block entity for laser telemeter.
 * <p>
 * Negative values for length fields mean that the line should be drawn in the negative direction along its axis.
 *
 * @see LaserTelemeterBlock
 * @see ModBlocks#LASER_TELEMETER
 */
public class LaserTelemeterBlockEntity extends BlockEntity {
  public static final Pattern FILE_NAME_PATTERN = Pattern.compile("^[\\w.-]+$");

  private static final String SIZE_TAG_KEY = "Size";
  private static final String OFFSET_TAG_KEY = "Offset";
  private static final String FILLER_BLOCK_STATE_KEY = "FillerBlockState";
  private static final String FILE_NAME_KEY = "FileName";
  private static final String MODE_KEY = "Mode";

  private Vec3i size;
  private Vec3i offset;
  private Mode mode;
  private BlockState fillerBlockState;
  private String fileName;

  public LaserTelemeterBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.LASER_TELEMETER, pos, state);
    this.setSize(Vec3i.ZERO);
    this.setOffset(Vec3i.ZERO);
    this.setFillerBlockState(Blocks.AIR.getDefaultState());
    this.setMode(Mode.BOX);
  }

  public Mode getMode() {
    return this.mode;
  }

  public void setMode(Mode mode) {
    this.mode = Objects.requireNonNull(mode);
    this.markDirty();
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

  public String getFileName() {
    return this.fileName;
  }

  public void setFileName(String fileName) {
    if (fileName != null && !FILE_NAME_PATTERN.matcher(fileName).find()) {
      throw new IllegalArgumentException("invalid file name: " + fileName);
    }
    this.fileName = fileName;
    this.markDirty();
  }

  public void performAction() {
    if (this.world instanceof ServerWorld w) {
      if (this.size.getX() != 0 && this.size.getY() != 0 && this.size.getZ() != 0) {
        switch (this.mode) {
          case FILL -> this.fillArea(w);
          case COPY -> this.copyArea(w);
          case PASTE -> this.pasteStructure(w);
        }
      } else {
        w.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.error.cannot_perform_action")
                .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
      }
    }
  }

  private void fillArea(ServerWorld world) {
    if (this.fillerBlockState != null) {
      Pair<BlockPos, BlockPos> corners = this.getAreaCorners();
      int blocksNb = Utils.fill(corners.getLeft(), corners.getRight(), this.fillerBlockState, world);
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.feedback.total_filled_volume", blocksNb), MessageType.CHAT, Util.NIL_UUID);
    } else {
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.error.cannot_fill_area")
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
    }
  }

  private void copyArea(final ServerWorld world) {
    if (this.fileName != null) {
      Pair<BlockPos, BlockPos> corners = this.getAreaCorners();
      // TODO copy
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.feedback.copy_successfull", this.fileName), MessageType.CHAT, Util.NIL_UUID);
    } else {
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.error.cannot_copy_area")
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
    }
  }

  private void pasteStructure(ServerWorld world) {
    if (this.fileName != null) {
      // TODO paste
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.feedback.paste_successfull", this.fileName), MessageType.CHAT, Util.NIL_UUID);
    } else {
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.error.cannot_paste_structure")
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
    }
  }

  private Pair<BlockPos, BlockPos> getAreaCorners() {
    int xo1 = this.size.getX() > 0 ? 0 : -1;
    int yo1 = this.size.getY() > 0 ? 0 : -1;
    int zo1 = this.size.getZ() > 0 ? 0 : -1;
    int xo2 = this.size.getX() < 0 ? 0 : -1;
    int yo2 = this.size.getY() < 0 ? 0 : -1;
    int zo2 = this.size.getZ() < 0 ? 0 : -1;
    BlockPos pos1 = this.pos.add(this.offset).add(xo1, yo1, zo1);
    BlockPos pos2 = this.pos.add(this.offset).add(this.size).add(xo2, yo2, zo2);
    return new Pair<>(pos1, pos2);
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.put(SIZE_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.size)));
    nbt.put(OFFSET_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.offset)));
    if (this.fillerBlockState != null) {
      nbt.put(FILLER_BLOCK_STATE_KEY, NbtHelper.fromBlockState(this.fillerBlockState));
    }
    if (this.fileName != null) {
      nbt.putString(FILE_NAME_KEY, this.fileName);
    }
    nbt.putInt(MODE_KEY, this.mode.ordinal());
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
    if (nbt.contains(FILE_NAME_KEY, NbtElement.STRING_TYPE)) {
      this.fileName = nbt.getString(FILE_NAME_KEY);
    } else {
      this.fileName = null;
    }
    this.mode = Mode.values()[nbt.getInt(MODE_KEY)];
  }

  @Override
  public Packet<ClientPlayPacketListener> toUpdatePacket() {
    return BlockEntityUpdateS2CPacket.create(this);
  }

  @Override
  public NbtCompound toInitialChunkDataNbt() {
    return this.createNbt();
  }

  public enum Mode {
    BOX,
    FILL,
    COPY,
    PASTE
  }
}
