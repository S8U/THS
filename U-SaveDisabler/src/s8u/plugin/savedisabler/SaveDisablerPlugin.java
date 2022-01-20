package s8u.plugin.savedisabler;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import su.plugin.core.bukkit.api.plugin.UKPlugin;

public class SaveDisablerPlugin extends UKPlugin implements Listener {

  @Override
  public void onUEnable() {
    setPrefix("§7[ U-SaveDisabler ]");
    Bukkit.getPluginManager().registerEvents(this, this);

    Bukkit.getScheduler().runTaskLater(this, () -> {
      Bukkit.getWorlds().forEach(w -> {
        if (!w.isAutoSave()) return;

        w.setAutoSave(false);
        log(w.getName() + " 월드 저장이 비활성화되었습니다.");
      });

      log("모든 월드 저장이 비활성화되었습니다.");
    }, 1L);
  }

  @EventHandler
  public void onWorldLoad(WorldLoadEvent e) {
    e.getWorld().setAutoSave(false);
    log(e.getWorld().getName() + " 월드 저장이 비활성화되었습니다.");
  }

}