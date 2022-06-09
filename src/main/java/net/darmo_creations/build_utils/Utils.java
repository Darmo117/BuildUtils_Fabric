package net.darmo_creations.build_utils;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class defines various utility functions.
 */
public final class Utils {
  /**
   * Returns the block entity of the given class at the given position.
   *
   * @param blockEntityClass Block entity’s class.
   * @param world            The world.
   * @param pos              Block entity’s position.
   * @param <T>              Block entity’s type.
   * @return The block entity if found, an empty optional otherwise.
   */
  public static <T extends BlockEntity> Optional<T> getBlockEntity(final Class<T> blockEntityClass, final World world, final BlockPos pos) {
    BlockEntity be = world.getBlockEntity(pos);
    if (blockEntityClass.isInstance(be)) {
      return Optional.of(blockEntityClass.cast(be));
    }
    return Optional.empty();
  }

  /**
   * Converts a block position to a string.
   */
  public static String blockPosToString(final BlockPos pos) {
    return "%d %d %d".formatted(pos.getX(), pos.getY(), pos.getZ());
  }

  /**
   * Serializes a block state to a string.
   *
   * @param blockState Block state to serialize.
   * @return Serialized string.
   */
  public static String blockStateToString(final BlockState blockState) {
    //noinspection OptionalGetWithoutIsPresent
    String message = Registry.BLOCK.getKey(blockState.getBlock()).get().getValue().toString();
    Collection<Property<?>> properties = blockState.getProperties();
    if (!properties.isEmpty()) {
      message += "[" + properties.stream()
          .map(p -> p.getName() + "=" + blockState.get(p).toString())
          .collect(Collectors.joining(",")) + "]";
    }
    return message;
  }

  /**
   * Sends a chat message to a player. Does nothing if the world is remote (i.e. client-side).
   *
   * @param world       The world the player is in.
   * @param player      The player to send the message to.
   * @param text        Message’s text.
   * @param inActionBar Whether to send the message in the player’s action bar.
   */
  public static void sendMessage(final World world, PlayerEntity player, final Text text, final boolean inActionBar) {
    if (!world.isClient()) {
      player.sendMessage(text, inActionBar);
    }
  }

  /**
   * Returns the blocks length along each axis of the volume defined by the given positions.
   *
   * @return A {@link Vec3i} object. Each axis holds the number of blocks along itself.
   */
  public static Vec3i getLengths(final BlockPos pos1, final BlockPos pos2) {
    Pair<BlockPos, BlockPos> positions = normalizePositions(pos1, pos2);
    BlockPos posMin = positions.getLeft();
    BlockPos posMax = positions.getRight();

    return new Vec3i(
        posMax.getX() - posMin.getX() + 1,
        posMax.getY() - posMin.getY() + 1,
        posMax.getZ() - posMin.getZ() + 1
    );
  }

  /**
   * Returns the blocks area of each face of the volume defined by the given positions.
   *
   * @return A {@link Vec3i} object. Each axis holds the area of the face perpendicular to itself.
   */
  public static Vec3i getAreas(final BlockPos pos1, final BlockPos pos2) {
    Vec3i size = getLengths(pos1, pos2);

    return new Vec3i(
        size.getZ() * size.getY(),
        size.getX() * size.getZ(),
        size.getX() * size.getY()
    );
  }

  /**
   * Returns the total number of blocks inside the volume defined by the given positions.
   */
  public static int getVolume(final BlockPos pos1, final BlockPos pos2) {
    Vec3i size = getLengths(pos1, pos2);
    return size.getX() * size.getY() * size.getZ();
  }

  /**
   * Returns the highest and lowest block coordinates for the volume defined by the given two positions.
   *
   * @param pos1 First position.
   * @param pos2 Second position.
   * @return A pair with the lowest and highest positions.
   */
  public static Pair<BlockPos, BlockPos> normalizePositions(final BlockPos pos1, final BlockPos pos2) {
    BlockPos posMin = new BlockPos(
        Math.min(pos1.getX(), pos2.getX()),
        Math.min(pos1.getY(), pos2.getY()),
        Math.min(pos1.getZ(), pos2.getZ())
    );
    BlockPos posMax = new BlockPos(
        Math.max(pos1.getX(), pos2.getX()),
        Math.max(pos1.getY(), pos2.getY()),
        Math.max(pos1.getZ(), pos2.getZ())
    );
    return new ImmutablePair<>(posMin, posMax);
  }

  private Utils() {
  }
}
