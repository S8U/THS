package s8u.plugin.physicalfighterspack.ability;

import java.util.Random;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class Zoro extends PFPAbility implements Listener {

  private int damage;

  public Zoro() {
    super();

    initAbility("조로",
        AbilityType.ACTIVE,
        AbilityRank.A,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 검의 피해량이 무작위로 설정됩니다.",
        "능력 사용으로 설정된 검의 피해량은 인챈트의 영향을 받지 않습니다",
        "설정 피해량 범위: 7~15");
    setCoolTime(45);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    damage = new Random().nextInt(9) + 7;

    Core.cmsg(getPlayer(), ChatColor.YELLOW, "검의 피해량이 " + damage + "로 설정되었습니다.");
  }

  @EventHandler
  public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
    if (!getPlayer().equals(e.getDamager())) return;

    if (getPlayer().getItemInHand() != null) {
      Material hand = getPlayer().getItemInHand().getType();
      if (!(hand == Material.WOOD_SWORD
          || hand == Material.STONE_SWORD
          || hand == Material.GOLD_SWORD
          || hand == Material.IRON_SWORD
          || hand == Material.DIAMOND_SWORD)) return;

      e.setDamage(damage);
    }
  }

}
