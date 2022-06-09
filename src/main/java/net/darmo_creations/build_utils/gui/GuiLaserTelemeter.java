package net.darmo_creations.build_utils.gui;

import net.darmo_creations.build_utils.block_entities.LaserTelemeterBlockEntity;
import net.darmo_creations.build_utils.network.ClientToServerPacketFactory;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * GUI for the laser telemeter block.
 */
public class GuiLaserTelemeter extends Screen {
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

  // Data
  private final LaserTelemeterBlockEntity blockEntity;
  private final Vec3i size;
  private final BlockPos offset;

  /**
   * Creates a GUI for the given tile entity.
   *
   * @param blockEntity The tile entity.
   */
  public GuiLaserTelemeter(LaserTelemeterBlockEntity blockEntity) {
    super(new TranslatableText("gui.build_utils.laser_telemeter.title"));
    this.blockEntity = blockEntity;
    this.size = blockEntity.getSize();
    this.offset = blockEntity.getOffset();
  }

  @Override
  protected void init() {
    final int middle = this.width / 2;
    final int leftButtonX = middle - BUTTON_WIDTH - MARGIN;
    final int rightButtonX = middle + MARGIN;

    int y = this.height / 2 - 2 * BUTTON_HEIGHT - MARGIN / 2;
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

    y += BUTTON_HEIGHT * 3 + MARGIN + this.textRenderer.fontHeight + 1;

    this.lengthXTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, (int) (middle - btnW * 1.5), y, btnW, BUTTON_HEIGHT, null));
    this.lengthXTextField.setText("" + this.size.getX());
    this.lengthYTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle - btnW / 2, y, btnW, BUTTON_HEIGHT, null));
    this.lengthYTextField.setText("" + this.size.getY());
    this.lengthZTextField = this.addDrawableChild(
        new TextFieldWidget(this.client.textRenderer, middle + btnW / 2, y, btnW, BUTTON_HEIGHT, null));
    this.lengthZTextField.setText("" + this.size.getZ());

    y += BUTTON_HEIGHT + 8 * MARGIN;

    this.addDrawableChild(new ButtonWidget(
        leftButtonX, y,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new TranslatableText("gui.done"),
        b -> this.onDone()
    ));

    this.addDrawableChild(new ButtonWidget(
        rightButtonX, y,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new TranslatableText("gui.cancel"),
        b -> this.onCancel()
    ));
  }

  private static int getValue(final TextFieldWidget TextFieldWidget) {
    try {
      return Integer.parseInt(TextFieldWidget.getText());
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Update client-side block entity and send packet to server.
   */
  private void onDone() {
    Vec3i size = new Vec3i(getValue(this.lengthXTextField), getValue(this.lengthYTextField), getValue(this.lengthZTextField));
    BlockPos offset = new BlockPos(getValue(this.xOffsetTextField), getValue(this.yOffsetTextField), getValue(this.zOffsetTextField));
    this.blockEntity.setSize(size);
    this.blockEntity.setOffset(offset);
    ClientPlayNetworking.send(ClientToServerPacketFactory.LASER_TELEMETER_DATA_PACKET_ID,
        ClientToServerPacketFactory.createLaserTelemeterBEPacketByteBuffer(this.blockEntity.getPos(), offset, size));
    //noinspection ConstantConditions
    this.client.setScreen(null);
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
    final int middle = this.width / 2;

    this.renderBackground(matrices);

    drawCenteredText(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.title"),
        middle, (TITLE_MARGIN - fontHeight) / 2, 0xffffff);

    int y = this.height / 2 - 2 * BUTTON_HEIGHT - MARGIN / 2 - fontHeight - 1;

    int btnW = (int) (BUTTON_WIDTH * 0.75);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.x_offset_field.label"),
        (int) (middle - btnW * 1.5), y, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.y_offset_field.label"),
        middle - btnW / 2, y, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.z_offset_field.label"),
        middle + btnW / 2, y, 0xa0a0a0);

    y += 3 * BUTTON_HEIGHT + MARGIN + fontHeight;

    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_x_field.label"),
        (int) (middle - btnW * 1.5), y, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_y_field.label"),
        middle - btnW / 2, y, 0xa0a0a0);
    drawTextWithShadow(matrices, font, new TranslatableText("gui.build_utils.laser_telemeter.length_z_field.label"),
        middle + btnW / 2, y, 0xa0a0a0);

    super.render(matrices, mouseX, mouseY, delta);
  }
}
