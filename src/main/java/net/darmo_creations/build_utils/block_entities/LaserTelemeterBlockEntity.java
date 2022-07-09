package net.darmo_creations.build_utils.block_entities;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.blocks.LaserTelemeterBlock;
import net.darmo_creations.build_utils.blocks.ModBlocks;
import net.minecraft.block.Block;
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
import net.minecraft.structure.Structure;
import net.minecraft.structure.StructureManager;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.Objects;
import java.util.Optional;

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
  private static final String STRUCTURE_NAME_KEY = "StructureName";
  private static final String MODE_KEY = "Mode";
  private static final String ROTATION_KEY = "Rotation";
  private static final String MIRROR_KEY = "Mirror";

  private Vec3i size;
  private Vec3i offset;
  private Mode mode;
  private BlockState fillerBlockState;
  private String structureName;
  private BlockRotation rotation;
  private BlockMirror mirror;

  public LaserTelemeterBlockEntity(final BlockPos pos, final BlockState state) {
    super(ModBlockEntities.LASER_TELEMETER, pos, state);
    this.setSize(Vec3i.ZERO);
    this.setOffset(Vec3i.ZERO);
    this.setFillerBlockState(Blocks.AIR.getDefaultState());
    this.setMode(Mode.BOX);
    this.setRotation(BlockRotation.NONE);
    this.setMirror(BlockMirror.NONE);
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

  public void setSize(final Vec3i size) {
    this.size = this.clamp(size);
    this.markDirty();
  }

  private Vec3i clamp(final Vec3i vec3i) {
    return new Vec3i(Math.max(0, vec3i.getX()), Math.max(0, vec3i.getY()), Math.max(0, vec3i.getZ()));
  }

  public Vec3i getOffset() {
    return this.offset;
  }

  public void setOffset(final Vec3i offset) {
    this.offset = Objects.requireNonNull(offset);
    this.markDirty();
  }

  public BlockState getFillerBlockState() {
    return this.fillerBlockState;
  }

  public void setFillerBlockState(final BlockState fillerBlockState) {
    this.fillerBlockState = Objects.requireNonNull(fillerBlockState);
    this.markDirty();
  }

  public String getStructureName() {
    return this.structureName;
  }

  public void setStructureName(String structureName) {
    this.structureName = structureName;
    this.markDirty();
  }

  public BlockRotation getRotation() {
    return this.rotation;
  }

  public void setRotation(BlockRotation rotation) {
    this.rotation = Objects.requireNonNull(rotation);
    this.markDirty();
  }

  public BlockMirror getMirror() {
    return this.mirror;
  }

  public void setMirror(BlockMirror mirror) {
    this.mirror = Objects.requireNonNull(mirror);
    this.markDirty();
  }

  public void performAction(boolean previewPaste) {
    if (this.world instanceof ServerWorld w) {
      if (this.mode == Mode.PASTE || this.size.getX() != 0 && this.size.getY() != 0 && this.size.getZ() != 0) {
        switch (this.mode) {
          case FILL -> this.fillArea(w);
          case COPY -> this.copyArea(w);
          case PASTE -> this.pasteStructure(w, previewPaste);
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
      BlockPos pos = this.getPos().add(this.offset);
      int blocksNb = Utils.fill(pos, pos.add(this.size).add(-1, -1, -1), this.fillerBlockState, world);
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.feedback.total_filled_volume", blocksNb), MessageType.CHAT, Util.NIL_UUID);
    } else {
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.error.cannot_fill_area")
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
    }
  }

  private void copyArea(final ServerWorld world) {
    if (this.structureName != null) {
      StructureManager structureManager = world.getStructureManager();
      Structure structure;
      try {
        structure = structureManager.getStructureOrBlank(new Identifier(this.structureName));
        structure.saveFromWorld(world, this.getPos().add(this.offset), this.size, false, Blocks.STRUCTURE_VOID);
        structureManager.saveStructure(new Identifier(this.structureName));
      } catch (InvalidIdentifierException invalidIdentifierException) {
        world.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.error.invalid_structure_name", this.structureName)
                .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
        return;
      }
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.feedback.copy_successfull", this.structureName), MessageType.CHAT, Util.NIL_UUID);
    } else {
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.error.cannot_copy_area")
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
    }
  }

  private void pasteStructure(ServerWorld world, boolean preview) {
    if (this.structureName != null) {
      Optional<Structure> optional;
      StructureManager structureManager = world.getStructureManager();
      try {
        optional = structureManager.getStructure(new Identifier(this.structureName));
      } catch (InvalidIdentifierException invalidIdentifierException) {
        world.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.error.invalid_structure_name", this.structureName)
                .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
        return;
      }
      if (optional.isEmpty()) {
        world.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.error.unknown_structure_name", this.structureName)
                .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
        return;
      }
      Structure structure = optional.get();
      this.updateSizeFromStructure(world, structure);
      if (!preview) {
        this.place(world, structure);
        world.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.feedback.paste_successfull", this.structureName), MessageType.CHAT, Util.NIL_UUID);
      } else {
        world.getServer().getPlayerManager().broadcast(
            new TranslatableText("block.build_utils.laser_telemeter.feedback.area_prepared", this.structureName), MessageType.CHAT, Util.NIL_UUID);
      }
    } else {
      world.getServer().getPlayerManager().broadcast(
          new TranslatableText("block.build_utils.laser_telemeter.error.cannot_paste_structure")
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), MessageType.CHAT, Util.NIL_UUID);
    }
  }

  private void updateSizeFromStructure(ServerWorld world, final Structure structure) {
    this.size = structure.getSize();
    // Update data on client side to draw correct box
    world.updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), Block.NOTIFY_LISTENERS);
    this.markDirty();
  }

  private void place(ServerWorld world, final Structure structure) {
    StructurePlacementData structurePlacementData = new StructurePlacementData()
        .setMirror(this.mirror)
        .setRotation(this.rotation)
        .setIgnoreEntities(true);
    BlockPos pos = this.getPos().add(this.offset);
    structure.place(world, pos, pos, structurePlacementData, world.getServer().getOverworld().getRandom(), Block.NOTIFY_LISTENERS);
  }

  @Override
  protected void writeNbt(NbtCompound nbt) {
    super.writeNbt(nbt);
    nbt.put(SIZE_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.size)));
    nbt.put(OFFSET_TAG_KEY, NbtHelper.fromBlockPos(new BlockPos(this.offset)));
    if (this.fillerBlockState != null) {
      nbt.put(FILLER_BLOCK_STATE_KEY, NbtHelper.fromBlockState(this.fillerBlockState));
    }
    if (this.structureName != null) {
      nbt.putString(STRUCTURE_NAME_KEY, this.structureName);
    }
    nbt.putInt(ROTATION_KEY, this.rotation.ordinal());
    nbt.putInt(MIRROR_KEY, this.mirror.ordinal());
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
    if (nbt.contains(STRUCTURE_NAME_KEY, NbtElement.STRING_TYPE)) {
      this.structureName = nbt.getString(STRUCTURE_NAME_KEY);
    } else {
      this.structureName = null;
    }
    this.rotation = BlockRotation.values()[nbt.getInt(ROTATION_KEY)];
    this.mirror = BlockMirror.values()[nbt.getInt(MIRROR_KEY)];
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
