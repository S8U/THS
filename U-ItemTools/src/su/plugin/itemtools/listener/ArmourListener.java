package su.plugin.itemtools.listener;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class ArmourListener implements Listener {

  @EventHandler (priority = EventPriority.MONITOR)
  public void onEntityDamage(EntityDamageEvent e) {
    Entity entity = e.getEntity();
    if(!(entity instanceof LivingEntity)) return;
    LivingEntity le = (LivingEntity) entity;

    double armourDamageReduction = e.getDamage(EntityDamageEvent.DamageModifier.ARMOR);
    //double armourPoints = le.getAttribute(Attribute.GENERIC_ARMOR).getValue();
    double armourPoints = getArmour(le);
    double newReduction = armourPoints * 0.04 * -
        (e.getDamage() + e.getDamage(EntityDamageEvent.DamageModifier.BLOCKING));

    if(e.isApplicable(EntityDamageEvent.DamageModifier.ARMOR)){
      e.setDamage (EntityDamageEvent.DamageModifier.ARMOR, newReduction);
    }
  }

  public double getArmour(LivingEntity entity) {
    double total = 0;

    for(ItemStack armour : entity.getEquipment().getArmorContents()) {
      if(armour == null) continue;
      if(!armour.hasItemMeta() || !armour.getItemMeta().hasLore()) continue;

      for(String line : armour.getItemMeta().getLore()) {
        if(!line.startsWith("§r§3§r§9방어력: §f")) continue;

        double damage = Double.parseDouble(line.substring("§r§3§r§9방어력: §f".length(), line.length()));

        total += damage;
      }
    }

    return total;
  }

}