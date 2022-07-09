package net.darmo_creations.build_utils.gui;

import net.darmo_creations.build_utils.Utils;
import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.darmo_creations.build_utils.network.C2SPacketFactory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
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
  private TextFieldWidget fileNameTextField;
  private ButtonWidget actionButton;
  private ButtonWidget modeButton;

  // Data
  private final LaserTelemeterBlockEntity blockEntity;
  private final Vec3i size;
  private final Vec3i offset;
  private LaserTelemeterBlockEntity.Mode mode;
  private final BlockState fillerBlockState;
  private final String fileName;

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
    this.mode = blockEntity.getMode();
    this.fillerBlockState = blockEntity.getFillerBlockState();
    this.fileName = blockEntity.getFileName();
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
    this.fillerBlockStateTextField.setMaxLength(400);
    if (this.fillerBlockState != null) {
      this.fillerBlockStateTextField.setText(Utils.blockStateToString(this.fillerBlockState));
    }

    this.fileNameTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle - (3 * btnW) / 2, y, 3 * btnW, BUTTON_HEIGHT, null));
    this.fileNameTextField.setMaxLength(50);
    this.fileNameTextField.setTextPredicate(text -> "".equals(text) || LaserTelemeterBlockEntity.FILE_NAME_PATTERN.matcher(text).find());
    if (this.fileName != null) {
      this.fileNameTextField.setText(this.fileName);
    }

    y += 2 * BUTTON_HEIGHT;

    this.modeButton = this.addDrawableChild(new ButtonWidget(
        middle - (3 * btnW) / 2, y,
        BUTTON_WIDTH / 2, BUTTON_HEIGHT,
        new LiteralText(""), // Set by setMode() method
        b -> this.onCycleMode()
    ));

    this.actionButton = this.addDrawableChild(new ButtonWidget(
        middle - BUTTON_WIDTH / 2, y,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new LiteralText(""), // Set by setMode() method
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

    this.updateFields();
  }

  private void onCycleMode() {
    LaserTelemeterBlockEntity.Mode[] values = LaserTelemeterBlockEntity.Mode.values();
    this.mode = values[(this.mode.ordinal() + 1) % values.length];
    this.updateFields();
  }

  private void updateFields() {
    this.lengthXTextField.setVisible(this.mode != LaserTelemeterBlockEntity.Mode.PASTE);
    this.lengthYTextField.setVisible(this.mode != LaserTelemeterBlockEntity.Mode.PASTE);
    this.lengthZTextField.setVisible(this.mode != LaserTelemeterBlockEntity.Mode.PASTE);
    this.fillerBlockStateTextField.setVisible(this.mode == LaserTelemeterBlockEntity.Mode.FILL);
    this.fileNameTextField.setVisible(this.mode == LaserTelemeterBlockEntity.Mode.COPY || this.mode == LaserTelemeterBlockEntity.Mode.PASTE);
    this.modeButton.setMessage(new TranslatableText("gui.build_utils.laser_telemeter.mode_button.%s.label".formatted(this.mode.name().toLowerCase())));
    this.actionButton.setMessage(new TranslatableText("gui.build_utils.laser_telemeter.action_button.%s.label".formatted(this.mode.name().toLowerCase())));
    this.actionButton.visible = this.mode != LaserTelemeterBlockEntity.Mode.BOX;
  }

  /**
   * Update client-side block entity and send update packet to server.
   *
   * @param performAction Whether to perform the selected action from the server.
   */
  private void onDone(final boolean performAction) {
    Vec3i size = new Vec3i(getValue(this.lengthXTextField), getValue(this.lengthYTextField), getValue(this.lengthZTextField));
    BlockPos offset = new BlockPos(getValue(this.xOffsetTextField), getValue(this.yOffsetTextField), getValue(this.zOffsetTextField));
    String fileName = this.fileNameTextField.getText().strip();
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
    this.blockEntity.setMode(this.mode);
    this.blockEntity.setFillerBlockState(fillerBlockState);
    this.blockEntity.setFileName(fileName.equals("") ? null : fileName);
    ClientPlayNetworking.send(C2SPacketFactory.LASER_TELEMETER_DATA_PACKET_ID,
        C2SPacketFactory.createLaserTelemeterBEPacketByteBuffer(this.blockEntity.getPos(), offset, size, this.mode, fillerBlockState, fileName, performAction));
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

    if (this.mode != LaserTelemeterBlockEntity.Mode.PASTE) {
      drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_x_field.label"),
          this.lengthXTextField.x, this.lengthXTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
      drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_y_field.label"),
          this.lengthYTextField.x, this.lengthYTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
      drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_z_field.label"),
          this.lengthZTextField.x, this.lengthZTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    }

    if (this.mode == LaserTelemeterBlockEntity.Mode.FILL) {
      drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.filler_block_state.label"),
          this.fillerBlockStateTextField.x, this.fillerBlockStateTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    } else if (this.mode == LaserTelemeterBlockEntity.Mode.COPY || this.mode == LaserTelemeterBlockEntity.Mode.PASTE) {
      drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.file_name.label"),
          this.fileNameTextField.x, this.fileNameTextField.y + BUTTON_HEIGHT + fontHeight / 2, 0xa0a0a0);
    }

    super.render(matrices, mouseX, mouseY, delta);
  }
}
