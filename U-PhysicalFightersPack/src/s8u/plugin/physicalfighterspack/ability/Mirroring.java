package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import s8u.plugin.physicalfighterspack.PhysicalFightersPackPlugin;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class Mirroring extends PFPAbility implements Listener {

  public Mirroring() {
    super();

    initAbility("미러링",
        AbilityType.PASSIVE,
        AbilityRank.S,
        "당신을 죽인 사람을 함께 저승으로 끌고갑니다.",
        "자신이 죽을경우 죽인 사람 역시 죽게됩니다.");
  }

  @EventHandler
  public void onDeath(PlayerDeathEvent e) {
    if (!getPlayer().equals(e.getEntity())) return;

    LivingEntity killer = ((KPlayer) KCore.getUPlayerByPlatformPlayer((Player) e.getEntity())).getLastHit();
    if (killer == null || !(killer instanceof Player)) return;

    GamePlayer kp = api.getPlayerManager().getGamePlayer((Player) killer);
    if (kp.isWatchMode() || kp.isEliminate()) return;

    Core.cbc(ChatColor.YELLOW, getGamePlayer().getDisplayName() + "님의 미러링 능력이 발동되었습니다.");

    for (Ability ability : kp.getAbilities()) {
      if (ability.getName().equals("이지스")
          && (ability.getPluginName().equals("PhysicalFighters") || ability.getPluginName().equals(PhysicalFightersPackPlugin.getInstance().getName()))) {
        Core.cbc(ChatColor.YELLOW, "미러링 능력이 무효화 되었습니다.");
        break;
      }
    }

    Bukkit.getScheduler().runTaskLater(PhysicalFightersPackPlugin.getInstance(), () -> killer.damage(5000), 1L);
  }

}