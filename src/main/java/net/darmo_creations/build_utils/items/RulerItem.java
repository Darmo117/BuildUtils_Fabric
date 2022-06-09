package net.darmo_creations.build_utils.items;

import net.darmo_creations.build_utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Style;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

/**
 * Item used to measure lengths, areas and volumes.
 * <p>
 * Usage:
 * <li>Right-click on block to select first position.
 * <li>Right-click on another block to select second position.
 * The size, areas and volume the selected 3D rectangle will appear in the chat.
 */
public class RulerItem extends Item {
  private static final String POS_TAG_KEY = "Pos";

  public RulerItem(Settings settings) {
    super(settings.maxCount(1));
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    PlayerEntity player = context.getPlayer();
    //noinspection ConstantConditions
    ItemStack heldItem = player.getStackInHand(context.getHand());
    BlockPos pos = context.getBlockPos();
    World world = context.getWorld();
    RulerData data = RulerData.fromTag(heldItem.getNbt());

    if (data.position == null) {
      data.position = pos;
      Utils.sendMessage(world, player, new TranslatableText("item.build_utils.ruler.action_bar.first_selection",
          Utils.blockPosToString(pos)).setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
    } else {
      Utils.sendMessage(world, player, new TranslatableText("item.build_utils.ruler.action_bar.second_selection",
          Utils.blockPosToString(pos)).setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA)), true);

      Vec3i lengths = Utils.getLengths(data.position, pos);
      int lengthX = lengths.getX();
      int lengthY = lengths.getY();
      int lengthZ = lengths.getZ();
      Utils.sendMessage(world, player, new TranslatableText("item.build_utils.ruler.feedback.size",
          lengthX, lengthY, lengthZ).setStyle(Style.EMPTY.withColor(Formatting.GREEN)), false);

      // Do not display any area if at least two dimensions have a length of 1 (single line of blocks selected)
      if (lengthX + lengthY != 2 && lengthX + lengthZ != 2 && lengthY + lengthZ != 2) {
        Vec3i areas = Utils.getAreas(data.position, pos);
        int areaX = areas.getX();
        int areaY = areas.getY();
        int areaZ = areas.getZ();
        // Only display relevent area if player selected a 1-block-thick volume
        if (lengthX == 1 || lengthY == 1 || lengthZ == 1) {
          int area;
          if (lengthX == 1) {
            area = areaX;
          } else if (lengthZ == 1) {
            area = areaZ;
          } else {
            area = areaY;
          }
          Utils.sendMessage(world, player, new TranslatableText("item.build_utils.ruler.feedback.area", area)
              .setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)), false);
        } else {
          Utils.sendMessage(world, player, new TranslatableText("item.build_utils.ruler.feedback.area_full",
              areaX, areaY, areaZ).setStyle(Style.EMPTY.withColor(Formatting.DARK_GREEN)), false);
        }
      }

      int volume = Utils.getVolume(data.position, pos);
      Utils.sendMessage(world, player, new TranslatableText("item.build_utils.ruler.feedback.volume", volume)
          .setStyle(Style.EMPTY.withColor(Formatting.GOLD)), false);

      data.position = null;
    }

    heldItem.setNbt(data.toTag());
    return ActionResult.SUCCESS;
  }

  /**
   * Class holding data for the ruler that can serialize/deserialize NBT tags.
   */
  private static class RulerData {
    /**
     * Create a data instance from the given NBT tags.
     * If tag is null, an empty instance is returned.
     *
     * @param data NBT tag to deserialize.
     * @return The RulerData object.
     */
    static RulerData fromTag(NbtCompound data) {
      if (data != null) {
        NbtCompound tag = data.getCompound(POS_TAG_KEY);
        return new RulerData(!tag.isEmpty() ? NbtHelper.toBlockPos(tag) : null);
      } else {
        return new RulerData();
      }
    }

    BlockPos position;

    /**
     * Create an empty object.
     */
    RulerData() {
      this(null);
    }

    /**
     * Create an object for the given positions.
     */
    RulerData(BlockPos position) {
      this.position = position;
    }

    /**
     * Convert this data object to NBT tags.
     */
    NbtCompound toTag() {
      NbtCompound root = new NbtCompound();

      if (this.position != null) {
        root.put(POS_TAG_KEY, NbtHelper.fromBlockPos(this.position));
      }

      return root;
    }
  }
}
