package s8u.plugin.nokeepinventory;

import org.bukkit.Bukkit;
import org.bukkit.World;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.Core;

public class NoKeepInventoryPlugin extends UKPlugin {

  @Override
  public void onUEnable() {
    setPrefix("§7[ U-NoKeepInventory ]");
    
    Bukkit.getScheduler().runTaskLater(this, () -> {
      for (World world : Bukkit.getWorlds()) {
        world.setGameRuleValue("keepInventory", "false");
        Core.log(world.getName() + " 월드의 keepInventroy를 false로 변경했습니다.");
      }
    }, 1L);
  }

}