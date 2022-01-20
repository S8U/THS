package su.plugin.lobbysystem.ncp;

import fr.neatmonster.nocheatplus.checks.CheckType;
import fr.neatmonster.nocheatplus.hooks.NCPExemptionManager;
import java.util.HashMap;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.lobbysystem.LobbySystemPlugin;

public class NCPHandler {

  @Getter
  private static boolean useNCP;

  private static HashMap<String, Integer> tasks = new HashMap<>();

  public static void checkPlugin() {
    useNCP = PluginUtil.existsPlugin("NoCheatPlus");
  }

  public static void allowDoubleJump(Player player) {
    if (tasks.containsKey(player.getName())) {
      Bukkit.getScheduler().cancelTask(tasks.get(player.getName()));
    }

    NCPExemptionManager.exemptPermanently(player, CheckType.MOVING_SURVIVALFLY);

    tasks.put(player.getName(), Bukkit.getScheduler().scheduleSyncDelayedTask(LobbySystemPlugin.getInstance(), () -> {
      NCPExemptionManager.unexempt(player, CheckType.MOVING_SURVIVALFLY);
      tasks.remove(player.getName());
    }, 60L));
  }

}