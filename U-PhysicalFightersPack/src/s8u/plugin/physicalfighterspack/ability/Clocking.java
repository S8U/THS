package s8u.plugin.physicalfighterspack.ability;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.core.bukkit.api.KCore;

public class Clocking extends PFPAbility {

  public Clocking() {
    super();

    initAbility("클로킹",
        AbilityType.ACTIVE_CONTINUE,
        AbilityRank.S,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 5초간 투명 상태가 됩니다.");
    setCoolTime(60);
    setDurationTime(5);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onDurationStart() {
    KCore.getOnlinePlayers().forEach(ap -> ap.hidePlayer(getPlayer()));
  }

  @Override
  public void onDurationEnd() {
    KCore.getOnlinePlayers().forEach(ap -> ap.showPlayer(getPlayer()));
  }

}