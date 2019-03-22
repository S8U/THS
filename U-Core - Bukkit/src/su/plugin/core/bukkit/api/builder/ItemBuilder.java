package su.plugin.core.bukkit.api.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import su.plugin.core.bukkit.api.util.ItemUtil;

public class ItemBuilder {

  private ItemStack item;

  public ItemBuilder(int typeId) {
    item = new ItemStack(typeId);
  }

  public ItemBuilder(int typeId, short durability) {
    item = new ItemStack(typeId);
    item.setDurability(durability);
  }

  public ItemBuilder(Material type) {
    item = new ItemStack(type);
  }

  public ItemBuilder(String itemCode) {
    if(item == null) {
      item = new ItemStack(ItemUtil.getItem(itemCode));
    }

    if (itemCode.contains(":")) {
      String[] items = itemCode.split(":");
      item.setTypeId(Integer.parseInt(items[0]));
      item.setDurability(Short.parseShort(items[1]));
    } else {
      item = new ItemStack(Integer.parseInt(itemCode));
    }
  }

  public ItemBuilder(ItemStack item) {
    this.item = item;
  }

  //

  public ItemBuilder type(int typeId) {
    item.setTypeId(typeId);

    return this;
  }

  public ItemBuilder type(int typeId, short durability) {
    item.setTypeId(typeId);
    item.setDurability(durability);

    return this;
  }

  public ItemBuilder type(Material type) {
    item.setType(type);

    return this;
  }

  public ItemBuilder type(String itemCode) {
    if (itemCode.contains(":")) {
      String[] items = itemCode.split(":");
      item.setTypeId(Integer.parseInt(items[0]));
      item.setDurability(Short.parseShort(items[1]));
    } else {
      item.setTypeId(Integer.parseInt(itemCode));
    }

    return this;
  }

  public ItemBuilder data(MaterialData data) {
    item.setData(data);

    return this;
  }

  //

  public ItemBuilder amount(int amount) {
    item.setAmount(amount < 1 ? 1 : (amount > 128 ? 128 : amount));

    return this;
  }

  public ItemBuilder durability(short durability) {
    item.setDurability(durability);

    return this;
  }

  public ItemBuilder displayName(String displayName) {
    ItemMeta itemMeta = item.getItemMeta();

    itemMeta.setDisplayName(displayName);

    item.setItemMeta(itemMeta);

    return this;
  }

  public ItemBuilder lore(String... lore) {
    ItemMeta itemMeta = item.getItemMeta();

    List<String> list = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
    for(String line : lore) {
      list.add(line);
    }

    itemMeta.setLore(list);

    item.setItemMeta(itemMeta);

    return this;
  }

  public ItemBuilder lore(List<String> lore) {
    ItemMeta itemMeta = item.getItemMeta();

    List<String> newLore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<>();
    newLore.addAll(lore);

    itemMeta.setLore(newLore);

    item.setItemMeta(itemMeta);

    return this;
  }

  public ItemBuilder clearLore() {
    ItemMeta itemMeta = item.getItemMeta();

    itemMeta.setLore(new ArrayList<>());

    item.setItemMeta(itemMeta);

    return this;
  }

  //

  public ItemBuilder enchantment(Enchantment enchantment, int level) {
    item.addEnchantment(enchantment, level);

    return this;
  }

  public ItemBuilder enchantments(Map<Enchantment, Integer> enchantments) {
    item.addEnchantments(enchantments);

    return this;
  }

  public ItemBuilder unsafeEnchantment(Enchantment enchantment, int level) {
    item.addUnsafeEnchantment(enchantment, level);

    return this;
  }

  public ItemBuilder unsafeEnchantments(Map<Enchantment, Integer> enchantments) {
    item.addUnsafeEnchantments(enchantments);

    return this;
  }

  public ItemStack build() {
    return item;
  }

}