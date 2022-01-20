package su.plugin.core.bukkit.api.util;

import java.util.Arrays;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import su.plugin.core.bukkit.api.builder.ItemBuilder;

@UtilityClass
public class ItemUtil {

	public static Material getMaterialById(int typeId) {
		for (Material m : Material.values()) {
			if (m.getId() == typeId) return m;
		}

		return null;
	}

	public static ItemStack getItemById(int typeId) {
		for (Material m : Material.values()) {
			if (m.getId() == typeId) return new ItemStack(m);
		}

		return null;
	}

	public static ItemStack getItem(String itemCode) {
		ItemStack item = null;
		
		if(itemCode.contains(":")) {
			String[] items = itemCode.split(":");
			item = getItemById(Integer.parseInt(items[0]));
			item.setDurability(Short.parseShort(items[1]));
		} else {
			item = getItemById(Integer.parseInt(itemCode));
		}
		
		return item;
	}
	
	public static String getItemCode(ItemStack item) {
		return item.getDurability() == 0 ? String.valueOf(item.getType().getId()) : item.getType().getId() + ":" + item.getDurability();
	}
	
	public static String getItemCode(Block block) {
		return block.getData() == 0 ? String.valueOf(block.getType().getId()) : block.getType().getId() + ":" + block.getData();
	}
	
	public static ItemStack makeItem(int itemCode, short durability, String displayName, String...lore) {
		ItemStack item = getItemById(itemCode);
		item.setDurability(durability);
		ItemMeta im = item.getItemMeta();
		if(displayName != null) {
			im.setDisplayName(ChatColor.WHITE + displayName);
		}
		if(lore != null && lore.length > 0) {
			for(int i = 0; i < lore.length; i++) {
				lore[i] = ChatColor.WHITE + lore[i];
			}
			im.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack makeItem(int itemCode, String displayName, String...lore) {
		ItemStack item = getItemById(itemCode);
		ItemMeta im = item.getItemMeta();
		if(displayName != null) {
			im.setDisplayName(ChatColor.WHITE + displayName);
		}
		if(lore != null && lore.length > 0) {
			for(int i = 0; i < lore.length; i++) {
				lore[i] = ChatColor.WHITE + lore[i];
			}
			im.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack makeItem(String itemCode, String displayName, String...lore) {
		ItemStack item = null;
		
		if(itemCode.contains(":")) {
			String[] items = itemCode.split(":");
			item = getItemById(Integer.parseInt(items[0]));
			item.setDurability(Short.parseShort(items[1]));
		} else {
			item = getItemById(Integer.parseInt(itemCode));
		}
		
		ItemMeta im = item.getItemMeta();
		if(displayName != null) {
			im.setDisplayName(ChatColor.WHITE + displayName);
		}
		if(lore != null && lore.length > 0) {
			for(int i = 0; i < lore.length; i++) {
				lore[i] = ChatColor.WHITE + lore[i];
			}
			im.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(im);
		
		return item;
	}
	
	public static ItemStack makeItem(ItemStack item, String displayName, String...lore) {
		ItemMeta im = item.getItemMeta();
		if(displayName != null) {
			im.setDisplayName(ChatColor.WHITE + displayName);
		}
		if(lore != null && lore.length > 0) {
			for(int i = 0; i < lore.length; i++) {
				lore[i] = ChatColor.WHITE + lore[i];
			}
			im.setLore(Arrays.asList(lore));
		}
		item.setItemMeta(im);
		
		return item;
	}
	
	public static ItemStack makeItem(String itemCode, String displayName) {
		return makeItem(itemCode, displayName, null);
	}
	
	public static ItemStack makeItem(Material item, short durability, String displayName, String...lore) {
		return makeItem(item.getId(), durability, displayName, lore);
	}
	
	public static ItemStack makeItem(int itemCode, short durability, String displayName) {
		return makeItem(itemCode, durability, displayName, null);
	}
	
	public static ItemStack makeItem(Material item, short durability, String displayName) {
		return makeItem(item.getId(), durability, displayName, null);
	}
	
	public static ItemStack makeItem(Material item, String displayName, String...lore) {
		return makeItem(item.getId(), displayName, lore);
	}
	
	public static ItemStack makeItem(int itemCode, String displayName) {
		return makeItem(itemCode, displayName, null);
	}
	
	public static ItemStack makeItem(Material item, String displayName) {
		return makeItem(item.getId(), displayName, null);
	}
	
	public static int takeItem(Inventory inventory, ItemStack item, int amount) {
		int take = 0;
		
		for(int i = 0; i < inventory.getSize(); i++) {
			ItemStack ii = inventory.getItem(i);
			
			if(ii == null || !ii.isSimilar(item)) continue;
			
			if(ii.getAmount() > amount) {
				take += amount;
				
				ii.setAmount(ii.getAmount() - amount);
				break;
			} else {
				take += ii.getAmount();
				
				inventory.setItem(i, null);
				if((amount =- ii.getAmount()) < 1) break;
			}
		}
		
		return take;
	}
	
	public static int takeItemInHand(Player p, int amount) {
		ItemStack item = p.getItemInHand();
		
		if(item == null) return 0;
		
		int take = 0;
		
		if(item.getAmount() > amount) {
			take = amount;
			
			item.setAmount(item.getAmount() - amount);
		} else {
			take = item.getAmount();
			
			p.setItemInHand(null);
		}
		
		return take;
	}
	
	public static boolean equalsItem(ItemStack f, ItemStack s) {
		boolean m = f.hasItemMeta() == s.hasItemMeta();
		
		if(f.hasItemMeta() && m) {
			ItemMeta fmeta = f.getItemMeta();
			ItemMeta smeta = s.getItemMeta();
			
			if(m = fmeta.hasDisplayName() == smeta.hasDisplayName() && fmeta.hasLore() == smeta.hasLore()) {
				m = fmeta.hasDisplayName() ? fmeta.getDisplayName().equals(smeta.getDisplayName()) : m;
				m = fmeta.hasLore() ? fmeta.getLore().equals(smeta.getLore()) : m;
			}
		}
		
		return f.getType().equals(s.getType()) && f.getData().equals(s.getData()) && m;
	}
	
	public static boolean hasItem(Inventory inventory, ItemStack item) {
		for(ItemStack ii : inventory) {
			if(ii == null || equalsItem(ii, item)) return true;
		}
		return false;
	}
	
	public static int getItemAmount(Inventory inventory, ItemStack item) {
		int amount = 0;
		for(ItemStack ii : inventory) {
			if(ii == null || !equalsItem(ii, item)) continue;
			amount += ii.getAmount();
		}
		return amount;
	}
	
	public static int getEmptySlotAmount(Inventory inventory) {
		int amount = 0;
		for(ItemStack item : inventory) {
			if(!(item == null || item.getType() == Material.AIR)) continue;
			amount++;
		}
		return amount;
	}
	
	public static int getInventorySpace(Inventory inventory, ItemStack item) {
		int space = 0;

		if(inventory instanceof PlayerInventory) {
			for(ItemStack ii : ((PlayerInventory) inventory).getContents()) {
				if(ii == null || ii.getType() == Material.AIR) {
					space += 64;
				} else if(ii.isSimilar(item)) {
					space += 64 - ii.getAmount();
				}
			}
		} else {
			for(ItemStack ii : inventory) {
				if(ii == null || ii.getType() == Material.AIR) {
					space += 64;
				} else if(ii.isSimilar(item)) {
					space += 64 - ii.getAmount();
				}
			}
		}

		return space;
	}
	
	public static boolean hasInventorySpace(Inventory inventory, ItemStack item) {
		return getInventorySpace(inventory, item) >= item.getAmount();
	}
	
	public static ItemStack addEnchantOnEnchantBook(ItemStack enchantBook, Enchantment enchant, int level) {
		EnchantmentStorageMeta eMeta = (EnchantmentStorageMeta) enchantBook.getItemMeta();
		eMeta.addStoredEnchant(enchant, level, false);
		enchantBook.setItemMeta(eMeta);
		return enchantBook;
	}
	
	public static ItemStack addEnchantOnEnchantBook(ItemStack enchantBook, String enchantName, int level) {
		return addEnchantOnEnchantBook(enchantBook, Enchantment.getByName(enchantName), level);
	}

	public static ItemStack getSkull(String playerName) {
		ItemStack item = new ItemBuilder(397).durability((short) 3).build();

		ItemMeta meta = item.getItemMeta();
		((SkullMeta) meta).setOwner(playerName);
		item.setItemMeta(meta);

		return item;
	}

	public static ItemStack getSkull(ItemStack item, String playerName) {
		item.setType(getMaterialById(397));
		item.setDurability((short) 3);

		ItemMeta meta = item.getItemMeta();
		((SkullMeta) meta).setOwner(playerName);
		item.setItemMeta(meta);

		return item;
	}
	
}