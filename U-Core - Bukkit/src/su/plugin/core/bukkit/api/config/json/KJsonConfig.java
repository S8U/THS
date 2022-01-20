package su.plugin.core.bukkit.api.config.json;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.yaml.snakeyaml.Yaml;

import su.plugin.core.common.api.config.json.JsonConfig;

public class KJsonConfig extends JsonConfig {
	
	private static final String[] BYPASS_CLASS = {"CraftMetaBlockState", "CraftMetaItem"
												  /*Glowstone Support*/ ,"GlowMetaItem"};
	
	public KJsonConfig(File file) {
		super(file);
	}
	
	@Override
	public KJsonConfig load() {
		return (KJsonConfig) super.load();
	}
	
	public void loadFromYaml(String yamlString) {
		HashMap<String, Object> tempValues = (HashMap<String, Object>) new Yaml().load(yamlString);
		
		tempValues.forEach((path, value) -> {
			if(value instanceof Map<?, ?>) {
				valuesToDot(path, values, (Map<String, Object>) value);
			} else {
				values.put(path, value);
			}
		});
	}
	
	public void loadDefaultFromYaml(String yamlString) {
		HashMap<String, Object> tempValues = (HashMap<String, Object>) new Yaml().load(yamlString);
		
		tempValues.forEach((path, value) -> {
			if(value instanceof Map<?, ?>) {
				valuesToDot(path, defaults, (Map<String, Object>) value);
			} else {
				defaults.put(path, value);
			}
		});
	}
	
	public void set(String path, Object value) {
		if(path == null) return;
		else if(value == null) {
			String dpath = path + ".";
			values.forEach((k, v) -> {
				if(k.length() > 1 && k.startsWith(dpath)) {
					values.remove(k);
				}
			});
			
			return;
		} else if(value instanceof ItemStack) {
			setItemStack(path, (ItemStack) value);
			
			return;
		} else if(value instanceof Location) {
			setLocation(path, (Location) value);
			
			return;
		}
		
		values.put(path, value);
	}
	
	private void setLocation(String path, Location location) {
		set(path + ".world", location.getWorld().getName());
		set(path + ".x", location.getX());
		set(path + ".y", location.getY());
		set(path + ".z", location.getZ());
		set(path + ".yaw", location.getYaw());
		set(path + ".pitch", location.getPitch());
	}
	
	public Location getLocation(String path) {
		String worldName = getString(path + ".world");
		World world = Bukkit.getWorld(worldName);
		if(worldName == null || world == null) return null;
		
		double x = getDouble(path + ".x");
		double y = getDouble(path + ".y");
		double z = getDouble(path + ".z");
		float yaw = (float) getDouble(path + ".yaw");
		float pitch = (float) getDouble(path + ".pitch");
		
		return new Location(world, x, y, z, yaw, pitch);
	}
	
	private void setItemStack(String path, ItemStack itemStack) {
		set(path + ".type", itemStack.getType().name());
		
		if(itemStack.getDurability() > 0) {
			set(path + ".storage", itemStack.getDurability());
		}
		
		set(path + ".amount", itemStack.getAmount());
		
		if(itemStack.hasItemMeta()) {
			ItemMeta meta = itemStack.getItemMeta();
			
			if(meta.hasDisplayName()) {
				set(path + ".item-meta.displayname", meta.getDisplayName());
			}
			if(meta.hasLore()) {
				set(path + ".item-meta.lore", meta.getLore());
			}
			if(meta.hasEnchants()) {
				List<String> l = new ArrayList<>();
				meta.getEnchants().forEach((em, i) -> l.add(em.getName() + ":" + i));
				set(path + ".item-meta.enchants", l);
			}
			if(!meta.getItemFlags().isEmpty()) {
				set(path + ".item-meta.flags", meta.getItemFlags().stream().map(ItemFlag::name));
			}

			for (String clazz : BYPASS_CLASS) {
				if(meta.getClass().getSimpleName().equals(clazz)) {
					return;
				}
			}

			if(meta instanceof SkullMeta) {
				SkullMeta skullMeta = (SkullMeta) meta;
				if(skullMeta.hasOwner()) {
					set(path + ".item-meta.extra-meta.owner", skullMeta.getOwner());
				}
			} else if(meta instanceof BannerMeta) {
				BannerMeta bannerMeta = (BannerMeta) meta;
				set(path + ".item-meta.extra-meta.base-color", bannerMeta.getBaseColor().name());

				if(bannerMeta.numberOfPatterns() > 0) {
					List<String> l = new ArrayList<>();
					bannerMeta.getPatterns().stream().map(p -> l.add(p.getColor().name() + ":" + p.getPattern().getIdentifier()));
					set(path + ".item-meta.extra-meta.patterns", l);
				}

			} else if(meta instanceof EnchantmentStorageMeta) {
				EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
				if(esmeta.hasStoredEnchants()) {
					List<String> l = new ArrayList<>();
					esmeta.getStoredEnchants().forEach((e, i) -> l.add(e.getName() + ":" + i));
					set(path + ".item-meta.extra-meta.stored-enchants", l);
				}
			} else if(meta instanceof LeatherArmorMeta) {
				LeatherArmorMeta lameta = (LeatherArmorMeta) meta;
				set(path + ".item-meta.extra-meta.color", Integer.toHexString(lameta.getColor().asRGB()));
			} else if(meta instanceof BookMeta) {
				BookMeta bmeta = (BookMeta) meta;
				if(bmeta.hasAuthor() || bmeta.hasPages() || bmeta.hasTitle()) {
					if(bmeta.hasTitle()) {
						set(path + ".item-meta.extra-meta.title", bmeta.getTitle());
					}
					if(bmeta.hasAuthor()) {
						set(path + ".item-meta.extra-meta.author", bmeta.getAuthor());
					}
					if(bmeta.hasPages()) {
						set(path + ".item-meta.extra-meta.pages", bmeta.getPages());
					}
				}
			} else if(meta instanceof PotionMeta) {
				PotionMeta pmeta = (PotionMeta) meta;
				if(pmeta.hasCustomEffects()) {
					List<String> l = new ArrayList<>();
					pmeta.getCustomEffects().forEach(pe -> l.add(pe.getType().getName() + ":" + pe.getAmplifier() + ":" + pe.getDuration() / 20));
					set(path + ".item-meta.extra-meta.custom-effects", l);
				}
			} else if(meta instanceof FireworkEffectMeta) {
				FireworkEffectMeta femeta = (FireworkEffectMeta) meta;
				if(femeta.hasEffect()) {
					FireworkEffect effect = femeta.getEffect();
					set(path + ".item-meta.extra-meta.type", effect.getType().name());
					if(effect.hasFlicker()) {
						set(path + ".item-meta.extra-meta.flicker", true);
					}
					if(effect.hasTrail()) {
						set(path + ".item-meta.extra-meta.trail", true);
					}

					if(!effect.getColors().isEmpty()) {
						List<String> l = new ArrayList<>();
						effect.getColors().forEach(c -> l.add(Integer.toHexString(c.asRGB())));
						set(path + ".item-meta.extra-meta.colors", l);
					}

					if(!effect.getFadeColors().isEmpty()) {
						List<String> l = new ArrayList<>();
						effect.getFadeColors().forEach(c -> l.add(Integer.toHexString(c.asRGB())));
						set(path + ".item-meta.extra-meta.fade-colors", l);
					}
				}
			} else if(meta instanceof FireworkMeta) {
				FireworkMeta fmeta = (FireworkMeta) meta;
				
				set(path + ".item-meta.extra-meta.power", fmeta.getPower());
				
				if(fmeta.hasEffects()) {
					fmeta.getEffects().forEach(effect -> {
						set(path + ".item-meta.extra-meta.effects.type", effect.getType().name());
						if(effect.hasFlicker()) {
							set(path + ".item-meta.extra-meta.effects.flicker", true);
						}
						if(effect.hasTrail()) {
							set(path + ".item-meta.extra-meta.effects.trail", true);
						}

						if(!effect.getColors().isEmpty()) {
							List<String> l = new ArrayList<>();
							effect.getColors().forEach(c -> l.add(Integer.toHexString(c.asRGB())));
							set(path + ".item-meta.extra-meta.effects.colors", l);
						}

						if(!effect.getFadeColors().isEmpty()) {
							List<String> l = new ArrayList<>();
							effect.getFadeColors().forEach(c -> l.add(Integer.toHexString(c.asRGB())));
							set(path + ".item-meta.extra-meta.effects.fade-colors", l);
						}
					});
				}
			} else if(meta instanceof MapMeta) {
				MapMeta mmeta = (MapMeta) meta;

				/*if(mmeta.hasLocationName()) {
					set(path + ".item-meta.extra-meta.location-name", mmeta.getLocationName());
				}
				if(mmeta.hasColor()) {
					set(path + ".item-meta.extra-meta.color", Integer.toHexString(mmeta.getColor().asRGB()));
				}*/
				set(path + ".item-meta.extra-meta.scaling", mmeta.isScaling());
			}
		}
	}

	public ItemStack getItemStack(String path) {
		String type = getString(path + ".type");
		if(type == null) return null;
		
		short data = (short) getInt(path + ".storage", 0);
		
		int amount = getInt(path + ".amount", 1);
		
		ItemStack itemStack = new ItemStack(Material.getMaterial(type));
		itemStack.setDurability(data);
		itemStack.setAmount(amount);
		
		ItemMeta meta = itemStack.getItemMeta();
		
		String displayName = getString(path + ".item-meta.displayname");
		if(displayName != null) {
			meta.setDisplayName(displayName);
		}
		
		List<String> lore = getStringList(path + ".item-meta.lore");
		if(lore != null && lore.size() > 0) {
			meta.setLore(lore);
		}
		
		List<String> enchants = getStringList(path + ".item-meta.enchants");
		if(enchants != null && enchants.size() > 0) {
			enchants.forEach(enchantString -> {
				if(enchantString.contains(":")) {
					String[] enchantSplit = enchantString.split(":");
					Enchantment enchantMent = Enchantment.getByName(enchantSplit[0]);
					int el = Integer.parseInt(enchantSplit[1]);
					if(enchantMent != null && el > 0) {
						meta.addEnchant(enchantMent, el, true);
					}
				}
			});
		}
		
		List<String> flags = getStringList(path + ".item-meta.flags");
		if(flags != null && flags.size() > 0) {
			flags.forEach(fs -> {
				for (ItemFlag flag : ItemFlag.values()) {
					if(!flag.name().equals(fs)) continue;
					meta.addItemFlags(flag);
				}
			});
		}
		
		if(meta instanceof SkullMeta) {
			String owner = getString(path + ".item-meta.extra-meta.owner");
			if(owner != null) {
				((SkullMeta) meta).setOwner(owner);
			}
		} else if(meta instanceof BannerMeta) {
			BannerMeta bmeta = (BannerMeta) meta;
			
			String baseColor = getString(path + ".item-meta.extra-meta.base-color");
			if(baseColor != null) {
				Optional<DyeColor> color = Arrays.stream(DyeColor.values())
						.filter(dyeColor -> dyeColor.name().equalsIgnoreCase(baseColor))
						.findFirst();
				if(color.isPresent()) {
					bmeta.setBaseColor(color.get());
				}
			}
			
			List<String> patternList = getStringList(path + ".item-meta.extra-meta.patterns");
			if(patternList != null && patternList.size() > 0) {
				List<Pattern> patterns = new ArrayList<>(patternList.size());
				
				patternList.forEach(patternString -> {
					if(patternString.contains(":")) {
						String[] patternSplit = patternString.split(":");
						Optional<DyeColor> color = Arrays.stream(DyeColor.values())
								.filter(dyeColor -> dyeColor.name().equalsIgnoreCase(patternSplit[0]))
								.findFirst();
						PatternType patternType = PatternType.getByIdentifier(patternSplit[1]);
						if(color.isPresent() && patternType != null) {
							patterns.add(new Pattern(color.get(), patternType));
						}
					}
				});
			}
		} else if(meta instanceof EnchantmentStorageMeta) {
			List<String> storedEnchants = getStringList(path + ".item-meta.extra-meta.stored-enchants");
			
			if(storedEnchants != null && storedEnchants.size() > 0) {
				EnchantmentStorageMeta esmeta = (EnchantmentStorageMeta) meta;
				storedEnchants.forEach(enchantString -> {
					if(enchantString.contains(":")) {
						String[] enchantSplit = enchantString.split(":");
						Enchantment enchantment = Enchantment.getByName(enchantSplit[0]);
						int level = Integer.parseInt(enchantSplit[1]);
						if(enchantment != null && level > 0) {
							esmeta.addStoredEnchant(enchantment, level, true);
						}
					}
				});
			}
		} else if(meta instanceof LeatherArmorMeta) {
			String color = getString(path + ".item-meta.extra-meta.color");
			if(color != null) {
				((LeatherArmorMeta) meta).setColor(Color.fromRGB(Integer.parseInt(color, 16)));
			}
		} else if(meta instanceof BookMeta) {
			BookMeta bmeta = (BookMeta) meta;
			
			String title = getString(path + ".item-meta.extra-meta.title");
			if(title != null) {
				bmeta.setTitle(title);
			}
			
			String author = getString(path + ".item-meta.extra-meta.author");
			if(author != null) {
				bmeta.setAuthor(author);
			}
			
			List<String> pageList = getStringList(path + ".item-meta.extra-meta.pages");
			if(pageList != null && pageList.size() > 0) {
				bmeta.setPages(pageList);
			}
		} else if(meta instanceof PotionMeta) {
			List<String> customEffectList = getStringList(path + ".item-meta.extra-meta.custom-effects");
			if(customEffectList != null && customEffectList.size() > 0) {
				PotionMeta pmeta = (PotionMeta) meta;
				customEffectList.forEach(enchantString -> {
					if(enchantString.contains(":")) {
						String[] potionSplit = enchantString.split(":");
						PotionEffectType potionType = PotionEffectType.getByName(potionSplit[0]);
						int amplifier = Integer.parseInt(potionSplit[1]);
						int duration = Integer.parseInt(potionSplit[2]) * 20;
						if(potionType != null) {
							pmeta.addCustomEffect(new PotionEffect(potionType, amplifier, duration), true);
						}
					}
				});
			}
		} else if(meta instanceof FireworkEffectMeta) {
			String effectTypeString = getString(path + ".item-meta.extra-meta.type");
			if(effectTypeString != null) {
				boolean flicker = getBoolean(path + ".item-meta.extra-meta.flicker");
				boolean trail = getBoolean(path + ".item-meta.extra-meta.trail");
				List<String> colorList = getStringList(path + ".item-meta.extra-meta.colors");
				List<String> fadeColorList = getStringList(path + ".item-meta.extra-meta.fade-colors");
				
				FireworkEffectMeta femeta = (FireworkEffectMeta) meta;
				
				FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeString);
				
				if(effectType != null) {
					FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);
					
					if(colorList != null && colorList.size() > 0) {
						List<Color> colors = new ArrayList<>();
						colorList.forEach(color -> {
							colors.add(Color.fromRGB(Integer.parseInt(color, 16)));
						});
						builder.withColor(colors);
					}
					
					if(fadeColorList != null && fadeColorList.size() > 0) {
						List<Color> fadeColors = new ArrayList<>();
						fadeColorList.forEach(color -> {
							fadeColors.add(Color.fromRGB(Integer.parseInt(color, 16)));
						});
						builder.withFade(fadeColors);
					}
					
					builder.flicker(flicker);
					builder.trail(trail);
					
					femeta.setEffect(builder.build());
				}
			}
		} else if(meta instanceof FireworkMeta) {
			FireworkMeta fmeta = (FireworkMeta) meta;
			
			String power = getString(path + ".item-meta.extra-meta.power");
			if(power != null) {
				fmeta.setPower(Integer.parseInt(power));
			}
			
			String effectTypeString = getString(path + ".item-meta.extra-meta.effect.type");
			if(effectTypeString != null) {
				boolean flicker = getBoolean(path + ".item-meta.extra-meta.effect.flicker");
				boolean trail = getBoolean(path + ".item-meta.extra-meta.effect.trail");
				List<String> colorList = getStringList(path + ".item-meta.extra-meta.effect.colors");
				List<String> fadeColorList = getStringList(path + ".item-meta.extra-meta.effect.fade-colors");
				
				FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effectTypeString);
				
				if(effectType != null) {
					FireworkEffect.Builder builder = FireworkEffect.builder().with(effectType);
					
					if(colorList != null && colorList.size() > 0) {
						List<Color> colors = new ArrayList<>();
						colorList.forEach(color -> {
							colors.add(Color.fromRGB(Integer.parseInt(color, 16)));
						});
						builder.withColor(colors);
					}
					
					if(fadeColorList != null && fadeColorList.size() > 0) {
						List<Color> fadeColors = new ArrayList<>();
						fadeColorList.forEach(color -> {
							fadeColors.add(Color.fromRGB(Integer.parseInt(color, 16)));
						});
						builder.withFade(fadeColors);
					}
					
					builder.flicker(flicker);
					builder.trail(trail);
					
					fmeta.addEffect(builder.build());
				}
			}
		} else if(meta instanceof MapMeta) {
			MapMeta mmeta = (MapMeta) meta;
			
			boolean scaling = getBoolean(path + ".item-meta.extra-meta.scaling");
			mmeta.setScaling(scaling);
			
			/*String locationName = getString(path + ".item-meta.extra-meta.location-name");
			if(locationName != null) {
				mmeta.setLocationName(locationName);	
			}
			
			String color = getString(path + ".item-meta.extra-meta.color");
			if(color != null) {
				mmeta.setColor(Color.fromRGB(Integer.parseInt(color, 16)));
			}*/
		}
		
		itemStack.setItemMeta(meta);
		
		return itemStack;
	}
}