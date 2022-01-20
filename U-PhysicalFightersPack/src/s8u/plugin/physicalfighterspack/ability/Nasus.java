package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class Nasus extends PFPAbility implements Listener {

  private int stack;

  public Nasus() {
    super();

    initAbility("나서스",
        AbilityType.ACTIVE,
        AbilityRank.C,
        "괭이로 흙을 경작할 때마다 1 스택이 증가합니다.",
        "괭이의 피해량이 10 스택 당 1 증가합니다.",
        "최대 스택: 300");
    setCoolTime(3);
  }

  @Override
  public void onAssign() {
    getPlayer().getInventory().addItem(new ItemStack(Material.WOOD_HOE));
  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (!getPlayer().equals(e.getPlayer())
        || getRemainingCoolTime() > 0
        || getPlayer().getItemInHand() == null
        || e.getAction() != Action.RIGHT_CLICK_BLOCK
        || (e.getClickedBlock().getType() != Material.DIRT && e.getClickedBlock().getType() != Material.GRASS)) return;


    Material hand = getPlayer().getItemInHand().getType();
    if (!(hand == Material.WOOD_HOE
        || hand == Material.STONE_HOE
        || hand == Material.GOLD_HOE
        || hand == Material.IRON_HOE
        || hand == Material.DIAMOND_HOE)) return;

    runCoolDownTask();

    if (stack >= 300) {
      Core.wmsg(getPlayer(), "최대 스택인 300 스택에 도달하여 더 이상 스택을 쌓을 수 없습니다.");
      return;
    }

    stack++;
    Core.cmsg(getPlayer(), ChatColor.YELLOW, "1 스택이 증가했습니다. (현재 스택: " + stack + ")");
  }

  @EventHandler
  public void onHit(EntityDamageByEntityEvent e) {
    if (!e.getDamager().equals(getPlayer())
        || getPlayer().getItemInHand() == null) return;

    Material hand = getPlayer().getItemInHand().getType();
    if (!(hand == Material.WOOD_HOE
        || hand == Material.STONE_HOE
        || hand == Material.GOLD_HOE
        || hand == Material.IRON_HOE
        || hand == Material.DIAMOND_HOE)) return;

    e.setDamage(e.getDamage() + stack / 10);
  }

}
