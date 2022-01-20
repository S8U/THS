package su.plugin.itemtools.listener;

import java.util.EnumSet;
import java.util.Set;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import su.plugin.core.bukkit.api.event.entity.EntityDamageByPlayerEvent;
import su.plugin.core.common.api.util.DebugUtil;
import su.plugin.itemtools.api.ItemToolsAPI;

public class ItemPointListener implements Listener {

  private static final Set<DamageCause> NO_REDUCE_CAUSE = EnumSet.of(
      DamageCause.FIRE_TICK,
      DamageCause.VOID,
      DamageCause.SUFFOCATION,
      DamageCause.DROWNING,
      DamageCause.STARVATION,
      DamageCause.FALL,
      DamageCause.MAGIC,
      DamageCause.LIGHTNING
  );

  @EventHandler (priority = EventPriority.MONITOR)
  public void onEntityDamage(EntityDamageEvent e) {
    Entity entity = e.getEntity();
    if(!(entity instanceof LivingEntity)) return;
    LivingEntity le = (LivingEntity) entity;

    //

    if (NO_REDUCE_CAUSE.contains(e.getCause())) return;

    double armourDamageReduction = e.getDamage(EntityDamageEvent.DamageModifier.ARMOR);
    // double armourPoints = le.getAttribute(Attribute.GENERIC_ARMOR).getValue() + ItemToolsAPI.getArmourPoint(le);
    double armourPoints = getArmourPoint(le) + ItemToolsAPI.getArmourPoint(le);
    if (le instanceof Player && ((Player) le).isBlocking()) {
      armourPoints *= 2.4 / 4.2;
    }


    DebugUtil.log("armourDamageReduction: " + armourDamageReduction);
    DebugUtil.log("armourPoints: " + armourPoints);

    double newReduction = armourPoints * 0.04 * - e.getDamage();

    if (e.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)) {
      e.setDamage (EntityDamageEvent.DamageModifier.ARMOR, newReduction);

      DebugUtil.log("newReduction: " + newReduction);
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

  private int getArmourPoint(LivingEntity entity) {
    int point = 0;

    ItemStack[] armours = entity.getEquipment().getArmorContents();
    for (ItemStack item : armours) {
      if (item == null) continue;

      switch (item.getType()) {
        case LEATHER_HELMET:
          point += 1; break;
        case GOLD_HELMET:
          point += 2; break;
        case CHAINMAIL_HELMET:
          point += 2; break;
        case IRON_HELMET:
          point += 2; break;
        case DIAMOND_HELMET:
          point += 3; break;

        case LEATHER_CHESTPLATE:
          point += 3; break;
        case GOLD_CHESTPLATE:
          point += 5; break;
        case CHAINMAIL_CHESTPLATE:
          point += 5; break;
        case IRON_CHESTPLATE:
          point += 6; break;
        case DIAMOND_CHESTPLATE:
          point += 8; break;

        case LEATHER_LEGGINGS:
          point += 2; break;
        case GOLD_LEGGINGS:
          point += 3; break;
        case CHAINMAIL_LEGGINGS:
          point += 4; break;
        case IRON_LEGGINGS:
          point += 5; break;
        case DIAMOND_LEGGINGS:
          point += 6; break;

        case LEATHER_BOOTS:
          point += 1; break;
        case GOLD_BOOTS:
          point += 1; break;
        case CHAINMAIL_BOOTS:
          point += 1; break;
        case IRON_BOOTS:
          point += 2; break;
        case DIAMOND_BOOTS:
          point += 3; break;
      }
    }

    return point;
  }

}