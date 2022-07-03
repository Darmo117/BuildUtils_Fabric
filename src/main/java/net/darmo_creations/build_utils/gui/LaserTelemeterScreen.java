package net.darmo_creations.build_utils.gui;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.darmo_creations.build_utils.network.ClientToServerPacketFactory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Screen for the laser telemeter block.
 */
public class LaserTelemeterScreen extends Screen {
  // Layout
  private static final int TITLE_MARGIN = 30;
  private static final int MARGIN = 4;
  private static final int BUTTON_WIDTH = 150;
  private static final int BUTTON_HEIGHT = 20;

  // Widgets
  private TextFieldWidget lengthXTextField;
  private TextFieldWidget lengthYTextField;
  private TextFieldWidget lengthZTextField;
  private TextFieldWidget xOffsetTextField;
  private TextFieldWidget yOffsetTextField;
  private TextFieldWidget zOffsetTextField;
  private TextFieldWidget fillerBlockStateTextField;

  // Data
  private final LaserTelemeterBlockEntity blockEntity;
  private final Vec3i size;
  private final Vec3i offset;
  private final BlockState fillerBlockState;

  /**
   * Creates a GUI for the given tile entity.
   *
   * @param blockEntity The tile entity.
   */
  public LaserTelemeterScreen(LaserTelemeterBlockEntity blockEntity) {
    super(new TranslatableText("gui.build_utils.laser_telemeter.title"));
    this.blockEntity = blockEntity;
    this.size = blockEntity.getSize();
    this.offset = blockEntity.getOffset();
    this.fillerBlockState = blockEntity.getFillerBlockState();
  }

  @Override
  protected void init() {
    final int middle = this.width / 2;
    final int leftButtonX = middle - BUTTON_WIDTH - MARGIN;
    final int rightButtonX = middle + MARGIN;

    int y = this.height / 2 - 4 * BUTTON_HEIGHT - MARGIN / 2;
    int btnW = (int) (BUTTON_WIDTH * 0.75);

    //noinspection ConstantConditions
    this.xOffsetTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, (int) (middle - btnW * 1.5), y, btnW, BUTTON_HEIGHT, null));
    this.xOffsetTextField.setText("" + this.offset.getX());
    this.yOffsetTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle - btnW / 2, y, btnW, BUTTON_HEIGHT, null));
    this.yOffsetTextField.setText("" + this.offset.getY());
    this.zOffsetTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle + btnW / 2, y, btnW, BUTTON_HEIGHT, null));
    this.zOffsetTextField.setText("" + this.offset.getZ());

    y += 2 * BUTTON_HEIGHT;

    this.lengthXTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, (int) (middle - btnW * 1.5), y, btnW, BUTTON_HEIGHT, null));
    this.lengthXTextField.setText("" + this.size.getX());
    this.lengthYTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle - btnW / 2, y, btnW, BUTTON_HEIGHT, null));
    this.lengthYTextField.setText("" + this.size.getY());
    this.lengthZTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle + btnW / 2, y, btnW, BUTTON_HEIGHT, null));
    this.lengthZTextField.setText("" + this.size.getZ());

    y += 2 * BUTTON_HEIGHT;

    this.fillerBlockStateTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle - (3 * btnW) / 2, y, 3 * btnW, BUTTON_HEIGHT, null));
    this.fillerBlockStateTextField.setMaxLength(32500);
    if (this.fillerBlockState != null) {
      this.fillerBlockStateTextField.setText(Utils.blockStateToString(this.fillerBlockState));
    }

    y += 2 * BUTTON_HEIGHT;

    this.addDrawableChild(new ButtonWidget(
        middle - BUTTON_WIDTH / 2, y,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new TranslatableText("gui.build_utils.laser_telemeter.fill_area.label"),
        b -> this.onDone(true)
    ));

    y += BUTTON_HEIGHT + 2 * MARGIN;

    this.addDrawableChild(new ButtonWidget(
        leftButtonX, y,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new TranslatableText("gui.done"),
        b -> this.onDone(false)
    ));

    this.addDrawableChild(new ButtonWidget(
        rightButtonX, y,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new TranslatableText("gui.cancel"),
        b -> this.onCancel()
    ));
  }

  /**
   * Update client-side block entity and send update packet to server.
   *
   * @param fillArea Whether to fill the selected area from the server.
   */
  private void onDone(final boolean fillArea) {
    Vec3i size = new Vec3i(getValue(this.lengthXTextField), getValue(this.lengthYTextField), getValue(this.lengthZTextField));
    BlockPos offset = new BlockPos(getValue(this.xOffsetTextField), getValue(this.yOffsetTextField), getValue(this.zOffsetTextField));
    BlockState fillerBlockState = Utils.stringToBlockState(this.fillerBlockStateTextField.getText());
    if (fillerBlockState == null) {
      if (this.fillerBlockState == null) {
        fillerBlockState = Blocks.AIR.getDefaultState();
      } else {
        fillerBlockState = this.fillerBlockState;
      }
    }
    this.blockEntity.setSize(size);
    this.blockEntity.setOffset(offset);
    this.blockEntity.setFillerBlockState(fillerBlockState);
    ClientPlayNetworking.send(ClientToServerPacketFactory.LASER_TELEMETER_DATA_PACKET_ID,
        ClientToServerPacketFactory.createLaserTelemeterBEPacketByteBuffer(this.blockEntity.getPos(), offset, size, fillerBlockState, fillArea));
    //noinspection ConstantConditions
    this.client.setScreen(null);
  }

  private static int getValue(final TextFieldWidget TextFieldWidget) {
    try {
      return Integer.parseInt(TextFieldWidget.getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Discard changes and close this GUI.
   */
  private void onCancel() {
    //noinspection ConstantConditions
    this.client.setScreen(null);
  }

  @Override
  public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
    //noinspection ConstantConditions
    final TextRenderer font = this.client.textRenderer;
    final int fontHeight = font.fontHeight;

    this.renderBackground(matrices);

    drawCenteredText(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.title"),
        this.width / 2, (TITLE_MARGIN - fontHeight) / 2, 0xffffff);

    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.x_offset_field.label"),
        this.xOffsetTextField.x, this.xOffsetTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.y_offset_field.label"),
        this.yOffsetTextField.x, this.yOffsetTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.z_offset_field.label"),
        this.zOffsetTextField.x, this.zOffsetTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);

    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_x_field.label"),
        this.lengthXTextField.x, this.lengthXTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_y_field.label"),
        this.lengthYTextField.x, this.lengthYTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_z_field.label"),
        this.lengthZTextField.x, this.lengthZTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);

    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.filler_block_state.label"),
        this.fillerBlockStateTextField.x, this.fillerBlockStateTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);

    super.render(matrices, mouseX, mouseY, delta);
  }
}
