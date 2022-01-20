package s8u.plugin.cash.listener.lib.citizen;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import s8u.plugin.cash.api.CashAPI;

public class NPCListener implements Listener {

  @EventHandler
  public void onNPC(NPCRightClickEvent e) {
    if (!e.getNPC().getName().equals("캐시상점")) return;

    CashAPI.getShopMainGUI().open(e.getClicker());
  }

}