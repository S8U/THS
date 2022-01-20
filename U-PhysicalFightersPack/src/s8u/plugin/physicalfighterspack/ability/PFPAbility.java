package s8u.plugin.physicalfighterspack.ability;

import s8u.plugin.physicalfighterspack.PhysicalFightersPackPlugin;
import su.plugin.ability.api.object.Ability;

public class PFPAbility extends Ability {

  @Override
  public String getPluginName() {
    return PhysicalFightersPackPlugin.getInstance().getName();
  }

}