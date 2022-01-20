package s8u.plugin.physicalfighterspack.ability;

import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import s8u.plugin.physicalfighterspack.PhysicalFightersPackPlugin;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.core.common.api.Core;

public class Gaara extends PFPAbility {

  public Gaara() {
    super();

    initAbility("가아라",
        AbilityType.ACTIVE,
        AbilityRank.A,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 보고 있는 장소에 모래를 떨어뜨리고 4초 후 폭발시킵니다.");
    setCoolTime(40);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    Block targetBlock = e.getPlayer().getTargetBlock((Set<Material>) null, 40);
    if (targetBlock == null) {
      Core.wmsg(getPlayer(), "거리가 너무 멉니다.");
      return;
    }

    Location location = targetBlock.getLocation();

    Bukkit.getScheduler().runTaskLater(PhysicalFightersPackPlugin.getInstance(), () -> {
      Location explosionLoc = location.add(0, 2, 0);

      explosionLoc.getWorld().createExplosion(explosionLoc, 5F);
      explosionLoc.getWorld().createExplosion(explosionLoc, 5F);
      explosionLoc.getWorld().createExplosion(explosionLoc, 5F);
    }, 80L);

    Location sandLocation = location.clone();
    for (int y = 4; y <= 8; y++) {
      sandLocation.setY(location.getY() + y);

      for (int x = -3; x <= 3; x++) {
        for (int z = -3; z <= 3; z++) {
          sandLocation.setX(location.getX() + x);
          sandLocation.setZ(location.getZ() + z);

          if (sandLocation.getBlock().getType() == Material.BEDROCK || sandLocation.getBlock().getType() == Material.BARRIER) continue;
          sandLocation.getBlock().setType(Material.SAND);
        }
      }
    }
  }

}