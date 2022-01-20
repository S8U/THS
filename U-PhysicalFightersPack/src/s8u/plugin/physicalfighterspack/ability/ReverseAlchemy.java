package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class ReverseAlchemy extends PFPAbility {

  public ReverseAlchemy() {
    super();

    initAbility("반 연금술",
        AbilityType.ACTIVE,
        AbilityRank.A,
        "철괴 좌클릭 시 금괴 1개를 철괴 1개로 교환합니다.",
        "철괴 우클릭 시 금괴 3개를 다이아몬드 1개로 교환합니다.",
        "금괴 클릭 시 금괴 3개를 소모하여 체력과 허기를 모두 회복합니다.");
    setCoolTime(30);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT), new ItemStack(Material.GOLD_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    int goldAmount = ItemUtil.getItemAmount(e.getPlayer().getInventory(), new ItemStack(Material.GOLD_INGOT));

    switch (castingItem.getType()) {
      case IRON_INGOT:
        if (clickType == ClickType.LEFT) {
          if (goldAmount < 1) {
            Core.wmsg(getPlayer(), "금괴가 부족합니다.");
            return;
          }

          ItemUtil.takeItem(e.getPlayer().getInventory(), new ItemStack(Material.GOLD_INGOT), 1);
          e.getPlayer().getInventory().addItem(new ItemStack(Material.IRON_INGOT)).forEach((i, item) -> e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), item));

          Core.cmsg(getPlayer(), ChatColor.YELLOW, "금괴 1개를 철괴 1개로 교환했습니다.");
        } else {
          if (goldAmount < 3) {
            Core.wmsg(getPlayer(), "금괴가 부족합니다.");
            return;
          }

          ItemUtil.takeItem(e.getPlayer().getInventory(), new ItemStack(Material.GOLD_INGOT), 3);
          e.getPlayer().getInventory().addItem(new ItemStack(Material.DIAMOND)).forEach((i, item) -> e.getPlayer().getWorld().dropItemNaturally(e.getPlayer().getLocation(), item));

          Core.cmsg(getPlayer(), ChatColor.YELLOW, "금괴 3개를 다이아몬드 1개로 교환했습니다.");
        }
        break;
      case GOLD_INGOT:
        if (goldAmount < 3) {
          Core.wmsg(getPlayer(), "금괴가 부족합니다.");
          return;
        }

        ItemUtil.takeItem(e.getPlayer().getInventory(), new ItemStack(Material.GOLD_INGOT), 3);

        getPlayer().setFoodLevel(20);
        getPlayer().setSaturation(5F);

        getPlayer().setHealth(getPlayer().getMaxHealth());

        Core.cmsg(getPlayer(), ChatColor.YELLOW, "금괴 3개를 소모하여 체력과 허기를 모두 회복했습니다.");
    }
  }

}
