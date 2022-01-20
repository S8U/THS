package su.plugin.core.bukkit.api.gui.sign;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.nbt.NbtCompound;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.KCore;

public class SignGUI {

  @Getter
  private String[] lines = new String[] { "", "", "", "" };

  public SignGUI() {

  }

  public SignGUI(String... lines) {
    for (int i = 0; i < 4; i++) {
      if (lines.length < i + 1) break;
      this.lines[i] = lines[i];
    }
  }

  public void setLine(int line, String text) {
    lines[line - 1] = text;
  }

  public String getLine(int line) {
    return lines[line - 1];
  }

  public void open(Player player) {
    player.closeInventory();

    KCore.getSignGUIManager().setPlayerGUI(player, this);

    Location location = new Location(player.getWorld(), 0, 0, 0);

    BlockPosition blockPosition = new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    player.sendBlockChange(blockPosition.toLocation(player.getLocation().getWorld()), Material.WALL_SIGN, (byte) 0);

    PacketContainer openSign = ProtocolLibrary.getProtocolManager().createPacket(Server.OPEN_SIGN_EDITOR);
    PacketContainer signData = ProtocolLibrary.getProtocolManager().createPacket(Server.TILE_ENTITY_DATA);

    openSign.getBlockPositionModifier().write(0, blockPosition);

    NbtCompound signNBT = (NbtCompound) signData.getNbtModifier().read(0);

    for (int i = 0; i < 4; i++) {
      signNBT.put("Text" + (i + 1), "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', lines[i]) + "\"}");
    }

    signNBT.put("x", location.getBlockX());
    signNBT.put("y", location.getBlockY());
    signNBT.put("z", location.getBlockZ());
    signNBT.put("id", "minecraft:sign");

    signData.getBlockPositionModifier().write(0, blockPosition);
    signData.getIntegers().write(0, 9);
    signData.getNbtModifier().write(0, signNBT);

    try {
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, signData);
      ProtocolLibrary.getProtocolManager().sendServerPacket(player, openSign);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // Event
  public void onSignComplete(Player player, String[] lines) { }

}