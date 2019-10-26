package su.plugin.core.bukkit.api.util;

import java.lang.reflect.Method;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class CraftItemUtil {

  // org.bukkit → nms
  @SneakyThrows(Exception.class)
  public Object asNMSCopy(ItemStack bukkitItemStack) {
    Class craftItemStackClass = KReflectionUtil.getCraftBukkitClass("inventory.CraftItemStack");
    Method asNMSCopyMethod = KReflectionUtil.getMethod(craftItemStackClass,"asNMSCopy");
    return asNMSCopyMethod.invoke(null, bukkitItemStack);
  }

  // nms → org.bukkit
  @SneakyThrows(Exception.class)
  public Object asBukkitCopy(Object nmsItemStack) {
    Class craftItemStackClass = KReflectionUtil.getCraftBukkitClass("inventory.CraftItemStack");
    Method asBukkitCopyMethod = KReflectionUtil.getMethod(craftItemStackClass,"asBukkitCopy");
    return asBukkitCopyMethod.invoke(null, nmsItemStack);
  }

}