package su.plugin.core.bukkit.api.gui.sign;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.KCore;

public class SignGUIManager {

  @Getter
  private HashMap<String, SignGUI> playerGUIs = new HashMap<>();

  public void registerListener() {
    ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(KCorePlugin.getInstance(),
        Client.UPDATE_SIGN) {
      @Override
      public void onPacketReceiving(PacketEvent e) {
        SignGUI gui = KCore.getSignGUIManager().getPlayerGUI(e.getPlayer());
        if (gui == null) return;

        String[] lines = new String[4];
        int i = 0;
        for (WrappedChatComponent comp : e.getPacket().getChatComponentArrays().read(0)) {
          lines[i] = comp.getJson().substring(1, comp.getJson().length() - 1);
          i++;
        }

        e.setCancelled(true);

        gui.onSignComplete(e.getPlayer(), lines);

        e.getPlayer().sendBlockChange(new Location(e.getPlayer().getWorld(), 0, 0, 0), Material.AIR, (byte) 0);
      }
    });
  }

  public void setPlayerGUI(String name, SignGUI gui) {
    playerGUIs.put(name.toLowerCase(), gui);
  }

  public void setPlayerGUI(Player p, SignGUI gui) {
    setPlayerGUI(p.getName(), gui);
  }

  public void removePlayerGUI(String name) {
    playerGUIs.remove(name.toLowerCase());
  }

  public void removePlayerGUI(Player p) {
    removePlayerGUI(p.getName());
  }

  public SignGUI getPlayerGUI(String name) {
    return playerGUIs.get(name.toLowerCase());
  }

  public SignGUI getPlayerGUI(Player p) {
    return getPlayerGUI(p.getName());
  }

  public boolean hasGUI(String name) {
    return playerGUIs.containsKey(name.toLowerCase());
  }

  public boolean hasGUI(Player p) {
    return hasGUI(p.getName());
  }

  public List<Player> getPlayers(SignGUI gui) {
    List<Player> players = new ArrayList<>();

    for(String name : playerGUIs.keySet()) {
      SignGUI g = getPlayerGUI(name);
      if(!gui.equals(g)) continue;
      players.add(Bukkit.getPlayer(name));
    }

    return players;
  }

}
