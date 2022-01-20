package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import s8u.plugin.physicalfighterspack.PhysicalFightersPackPlugin;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.event.GameStopEvent;
import su.plugin.ability.api.event.WinEvent;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.Core;

public class Yasuo extends PFPAbility implements Listener {

  private KPlayer kp;

  private UKRunnable regenTask;

  public Yasuo() {
    super();

    initAbility("야스오",
        AbilityType.PASSIVE,
        AbilityRank.A,
        "공격 시 2의 추가 데미지를 입힙니다.",
        "데미지를 흡수하는 6의 보호막을 얻습니다.",
        "보호막은 10초당 2씩 회복됩니다.");
  }

  @Override
  public void onAssign() {
    kp = (KPlayer) Core.getUPlayerByPlatformPlayer(getPlayer());
    kp.setAbsorptionHearts(6);

    regenTask = new YasuoTask(kp);
    regenTask.runTaskTimerAsynchronously(200, 200);
  }

  @Override
  public void onResign() {
    regenTask.cancel();

    kp.setAbsorptionHearts(0);
  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent e) {
    if (!getPlayer().equals(e.getDamager())) return;

    e.setDamage(e.getDamage() + 2);
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent e) {
    kp.setAbsorptionHearts(0);
  }

  @EventHandler
  public void onStop(GameStopEvent e) {
    kp.setAbsorptionHearts(0);
  }

  @EventHandler
  public void onEnd(WinEvent e) {
    kp.setAbsorptionHearts(0);
  }

}

class YasuoTask extends UKRunnable {

  private KPlayer kp;

  public YasuoTask(KPlayer kp) {
    super(PhysicalFightersPackPlugin.getInstance());

    this.kp = kp;
  }

  @Override
  public void run() {
    float heart = kp.getAbsorptionHearts();
    if (heart >= 6) return;

    kp.setAbsorptionHearts(Math.min(6, heart + 2));
  }

}