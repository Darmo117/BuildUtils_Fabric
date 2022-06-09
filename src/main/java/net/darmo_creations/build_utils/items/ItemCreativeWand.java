package net.darmo_creations.build_utils.items;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.darmo_creations.build_utils.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

/**
 * Item used to fill areas with blocks.
 * <p>
 * Usage:
 * <li>Right-click on block to select the first position.
 * <li>Right-click on another block to select the second position.
 * <li>Sneak-right-click on a block to select it as filler. If no block is targetted, air will be selected.
 */
public class ItemCreativeWand extends Item {
  private static final String POS1_TAG_KEY = "Pos1";
  private static final String POS2_TAG_KEY = "Pos2";
  private static final String STATE_TAG_KEY = "BlockState";

  public ItemCreativeWand(Settings settings) {
    super(settings.maxCount(1));
  }

  // TODO allow more than 32768 blocks
  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
    ItemStack heldItem = player.getStackInHand(hand);
    WandData data = WandData.fromTag(heldItem.getNbt());
    ActionResult result;
    if (player.isSneaking()) {
      setBlockState(Blocks.AIR.getDefaultState(), data, world, player);
      heldItem.setNbt(data.toTag());
      result = ActionResult.SUCCESS;
    } else if (data.isReady()) {
      if (world instanceof ServerWorld w) {
        String command = "fill %s %s %s".formatted(
            Utils.blockPosToString(data.firstPosition),
            Utils.blockPosToString(data.secondPosition),
            Utils.blockStateToString(data.blockState)
        );
        int blocksFilled;
        try {
          blocksFilled = w.getServer().getCommandManager().getDispatcher().execute(command, player.getCommandSource());
        } catch (CommandSyntaxException e) {
          Utils.sendMessage(world, player, new LiteralText(e.getMessage())
              .setStyle(Style.EMPTY.withColor(Formatting.RED)), false);
          return new TypedActionResult<>(ActionResult.FAIL, heldItem);
        }
      }
      result = ActionResult.SUCCESS;
    } else {
      Utils.sendMessage(world, player, new TranslatableText("item.build_utils.creative_wand.action_bar.error.cannot_fill")
          .setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
      result = ActionResult.FAIL;
    }
    return new TypedActionResult<>(result, heldItem);
  }

  @Override
  public ActionResult useOnBlock(ItemUsageContext context) {
    PlayerEntity player = context.getPlayer();
    World world = context.getWorld();
    //noinspection ConstantConditions
    if (!player.isCreativeLevelTwoOp()) {
      Utils.sendMessage(world, player, new TranslatableText(
          "item.build_utils.creative_wand.action_bar.error.permissions"
      ).setStyle(Style.EMPTY.withColor(Formatting.RED)), true);
    }
    BlockPos pos = context.getBlockPos();
    ItemStack heldItem = context.getStack();
    WandData data = WandData.fromTag(heldItem.getNbt());

    if (player.isSneaking()) {
      setBlockState(world.getBlockState(pos), data, world, player);
    } else {
      if (data.firstPosition == null || data.secondPosition != null) {
        data.firstPosition = pos;
        data.secondPosition = null;
        Utils.sendMessage(world, player, new TranslatableText(
            "item.build_utils.creative_wand.action_bar.pos1_selected",
            Utils.blockPosToString(pos)
        ).setStyle(Style.EMPTY.withColor(Formatting.AQUA)), true);
      } else {
        data.secondPosition = pos;
        Utils.sendMessage(world, player, new TranslatableText(
            "item.build_utils.creative_wand.action_bar.pos2_selected",
            Utils.blockPosToString(pos)
        ).setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA)), true);
      }
    }

    heldItem.setNbt(data.toTag());
    return ActionResult.SUCCESS;
  }

  @Override
  public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
    WandData data = WandData.fromTag(stack.getNbt());
    tooltip.add(new TranslatableText(
        "item.build_utils.creative_wand.tooltip.pos1",
        data.firstPosition != null ? Utils.blockPosToString(data.firstPosition) : "-"
    ).setStyle(Style.EMPTY.withColor(Formatting.AQUA)));
    tooltip.add(new TranslatableText(
        "item.build_utils.creative_wand.tooltip.pos2",
        data.secondPosition != null ? Utils.blockPosToString(data.secondPosition) : "-"
    ).setStyle(Style.EMPTY.withColor(Formatting.DARK_AQUA)));
    tooltip.add(new TranslatableText(
        "item.build_utils.creative_wand.tooltip.blockstate",
        data.blockState != null ? Utils.blockStateToString(data.blockState) : "-"
    ).setStyle(Style.EMPTY.withColor(Formatting.BLUE)));
  }

  /**
   * Set block state of tool then send a confirmation message to player.
   *
   * @param blockState Block state to use.
   * @param data       Wand data.
   * @param world      World the player is in.
   * @param player     Player to send chat message to.
   */
  private static void setBlockState(final BlockState blockState, WandData data, final World world, PlayerEntity player) {
    data.blockState = blockState;
    Utils.sendMessage(world, player, new TranslatableText(
        "item.build_utils.creative_wand.action_bar.blockstate_selected",
        Utils.blockStateToString(blockState)
    ).setStyle(Style.EMPTY.withColor(Formatting.BLUE)), true);
  }

  /**
   * Class holding data for the wand that can serialize/deserialize NBT tags.
   */
  private static class WandData {
    /**
     * Create a data instance from the given NBT tags.
     * If tag is null, an empty instance is returned.
     *
     * @param data NBT tag to deserialize.
     * @return The WandData object.
     */
    static WandData fromTag(NbtCompound data) {
      if (data != null) {
        NbtCompound tag1 = data.getCompound(POS1_TAG_KEY);
        NbtCompound tag2 = data.getCompound(POS2_TAG_KEY);
        NbtCompound tagState = data.getCompound(STATE_TAG_KEY);
        BlockPos pos1 = !tag1.isEmpty() ? NbtHelper.toBlockPos(tag1) : null;
        BlockPos pos2 = !tag2.isEmpty() ? NbtHelper.toBlockPos(tag2) : null;
        BlockState state = !tagState.isEmpty() ? NbtHelper.toBlockState(tagState) : null;
        return new WandData(pos1, pos2, state);
      } else {
        return new WandData();
      }
    }

    BlockPos firstPosition;
    BlockPos secondPosition;
    BlockState blockState;

    /**
     * Create an empty object.
     */
    WandData() {
      this(null, null, null);
    }

    /**
     * Create an object for the given positions and block state.
     */
    WandData(BlockPos firstPosition, BlockPos secondPosition, BlockState blockState) {
      this.firstPosition = firstPosition;
      this.secondPosition = secondPosition;
      this.blockState = blockState;
    }

    /**
     * Data object is considered ready when both positions and blockstate are set.
     */
    boolean isReady() {
      return this.firstPosition != null && this.secondPosition != null && this.blockState != null;
    }

    /**
     * Convert this data object to NBT tags.
     */
    NbtCompound toTag() {
      NbtCompound root = new NbtCompound();

      if (this.firstPosition != null) {
        root.put(POS1_TAG_KEY, NbtHelper.fromBlockPos(this.firstPosition));
      }
      if (this.secondPosition != null) {
        root.put(POS2_TAG_KEY, NbtHelper.fromBlockPos(this.secondPosition));
      }
      if (this.blockState != null) {
        root.put(STATE_TAG_KEY, NbtHelper.fromBlockState(this.blockState));
      }

      return root;
    }
  }
}
