package su.plugin.core.bukkit.api.gui;

import java.lang.reflect.Field;
import java.util.HashMap;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.util.CraftItemUtil;
import su.plugin.core.bukkit.api.util.KReflectionUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public abstract class FakeIcon extends Icon {

  @Getter
  private HashMap<PlayerKey, ItemStack> items = new HashMap<>();

  // Constructor
  public FakeIcon() {
    super();
  }

  public FakeIcon(ItemStack item) {
    super(item);
  }

  public FakeIcon(String key, ItemStack item) {
    super(key, item);
  }

  // Update
  protected abstract ItemStack updateItem(UPlayer up);

  protected ItemStack updateItem(Player p) {
    return updateItem(KCore.getUPlayerByPlatformPlayer(p));
  }

  @Override
  protected ItemStack updateItem() {
    return null;
  }

  public void update(UPlayer up) {
    items.put(up.getPlayerKey(), updateItem(up));

    sendTo(up);
  }

  public void update(Player p) {
    update(Core.getUPlayerByPlatformPlayer(p));
  }

  @Override
  public void update() { }

  // Send
  @SneakyThrows(Exception.class)
  public void sendTo(UPlayer up) {
    if (!items.containsKey(up.getPlayerKey())) return;

    GUI gui = KCore.getGUIManager().getPlayerGUI(up.getName());
    if (gui == null) return;

    for (int index : gui.getIcons().keySet()) {
      if (!gui.getIcon(index).equals(this)) continue;

      ItemStack bukkitItem = items.get(up.getPlayerKey());

      Player p = (Player) up.getPlatformSender();

      Object entityPlayerObject = KReflectionUtil.getHandle(p);
      Object activeContainerObject = entityPlayerObject.getClass().getField("activeContainer").get(entityPlayerObject);
      int windowId = activeContainerObject.getClass().getField("windowId").getInt(activeContainerObject);

      Object packetObject = KReflectionUtil.getNMSClass("PacketPlayOutSetSlot").newInstance();
      Field aField = packetObject.getClass().getDeclaredField("a");
      Field bField = packetObject.getClass().getDeclaredField("b");
      Field cField = packetObject.getClass().getDeclaredField("c");

      aField.setAccessible(true);
      bField.setAccessible(true);
      cField.setAccessible(true);

      aField.set(packetObject, windowId);
      bField.set(packetObject,index);
      cField.set(packetObject, CraftItemUtil.asNMSCopy(bukkitItem));

      KReflectionUtil.sendPacket((Player) up.getPlatformSender(), packetObject);
    }
  }

  public void sendTo(Player p) {
    sendTo(Core.getUPlayerByPlatformPlayer(p));
  }

}