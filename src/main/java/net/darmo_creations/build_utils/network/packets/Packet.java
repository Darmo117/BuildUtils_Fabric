package net.darmo_creations.build_utils.network.packets;

import net.minecraft.network.PacketByteBuf;

public interface Packet {
  PacketByteBuf getBuffer();
}
