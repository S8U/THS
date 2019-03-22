package su.plugin.itemtools.listener;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import su.plugin.core.bukkit.api.event.entity.EntityDamageByPlayerEvent;
import su.plugin.core.common.api.util.DebugUtil;
import su.plugin.itemtools.api.ItemToolsAPI;

public class ItemPointListener implements Listener {

  @EventHandler (priority = EventPriority.MONITOR)
  public void onEntityDamage(EntityDamageEvent e) {
    Entity entity = e.getEntity();
    if(!(entity instanceof LivingEntity)) return;
    LivingEntity le = (LivingEntity) entity;

    //

    double armourDamageReduction = e.getDamage(EntityDamageEvent.DamageModifier.ARMOR);
    double armourPoints = le.getAttribute(Attribute.GENERIC_ARMOR).getValue() + (ItemToolsAPI.getArmourPoint(le) == null ? 0 : ItemToolsAPI.getArmourPoint(le));

    DebugUtil.log("armourDamageReduction: " + armourDamageReduction);
    DebugUtil.log("armourPoints: " + armourPoints);

    double newReduction = armourPoints * 0.04 * -
        (e.getDamage() + e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING));

    if(e.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)){
      e.setDamage (EntityDamageEvent.DamageModifier.ARMOR, newReduction);
    }
  }

  @EventHandler
  public void onEntityDamageByPlayer(EntityDamageByPlayerEvent e) {
    Double ad = ItemToolsAPI.getAttackDamagePoint(e.getDamager());
    if(ad == null) return;

    DebugUtil.log("EntityDamageByEntityEvent.getDamage: " + e.getEntityDamageByEntityEvent().getDamage() + ad);
    DebugUtil.log("AD Bonus: " + ad);

    e.getEntityDamageByEntityEvent().setDamage(e.getEntityDamageByEntityEvent().getDamage() + ad);
  }

}