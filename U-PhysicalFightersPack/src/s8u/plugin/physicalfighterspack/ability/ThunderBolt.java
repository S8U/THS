package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;

public class ThunderBolt extends PFPAbility {

  public ThunderBolt() {
    super();

    initAbility("썬더볼트",
        AbilityType.ACTIVE,
        AbilityRank.A,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 5칸 내의 적에게 6 데미지를 줍니다.");
    setCoolTime(5);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    Location loc = e.getPlayer().getLocation();

    e.getPlayer().getNearbyEntities(5, 5, 5).forEach(entity -> {
      if (!(entity instanceof LivingEntity)) return;

      loc.getWorld().strikeLightningEffect(entity.getLocation());
      ((LivingEntity) entity).damage(6);
    });
  }

}