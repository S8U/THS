package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.ability.api.category.GameState;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class AttackReflex extends PFPAbility implements Listener {

  public AttackReflex() {
    super();

    initAbility("공격반사",
        AbilityType.ACTIVE_CONTINUE,
        AbilityRank.A,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 5초간 받는 데미지를 반사합니다.");
    setCoolTime(60);
    setDurationTime(5);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    Core.cbc(ChatColor.YELLOW, "지금부터 5초간 " + getGamePlayer().getDisplayName() + " 님에게 가한 데미지가 반사됩니다.");
  }

  @EventHandler
  public void onDamage(EntityDamageByEntityEvent e) {
    if (AbilityAPI.getGameManager().getGameState() != GameState.PLAYING || AbilityAPI.isInvincibilityTime());
    if (!getPlayer().equals(e.getEntity()) || getRemainingDurationTime() < 1 || !(e.getDamager() instanceof LivingEntity)) return;

    ((LivingEntity) e.getDamager()).damage(e.getDamage(), e.getEntity());

    e.setCancelled(true);
  }

}