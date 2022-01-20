package s8u.plugin.physicalfighterspack;

import lombok.Getter;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.Core;

public class PhysicalFightersPackPlugin extends UKPlugin {

  @Getter
  private static PhysicalFightersPackPlugin instance;;

  @Override
  public void onUEnable() {
    instance = this;
    setPrefix("§c[ U-PhysicalFightersPack ]");

    Core.log(AbilityAPI.getAbilityManager().registerAbilities(this) + "개의 능력이 등록되었습니다.");
  }

}