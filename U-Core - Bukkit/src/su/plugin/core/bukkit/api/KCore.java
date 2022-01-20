package su.plugin.core.bukkit.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.command.KCommandManager;
import su.plugin.core.bukkit.api.command.KConsoleSender;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;
import su.plugin.core.bukkit.api.gui.GUIManager;
import su.plugin.core.bukkit.api.gui.sign.SignGUIManager;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.bukkit.api.permission.PermissionManager;
import su.plugin.core.bukkit.api.util.KReflectionUtil;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.bukkit.platform.KHandler;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.platform.PlatformType;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;

public class KCore extends Core {
	
	@Getter
	private static NMSVersion NMSVersion;
	
	@Setter
	@Getter
	private static boolean useVault, usePlaceholderAPI, useProtocolSupport, useProtocolLib;
	
	@Getter
	private static GUIManager GUIManager = new GUIManager();
	@Getter
	private static SignGUIManager signGUIManager;
	@Getter
	private static PermissionManager permissionManager = new PermissionManager();
	
	public static void init() {
		platformType = PlatformType.BUKKIT;
		platformProvider = new KHandler();
		
		//
		
		UConsoleCommandSender = new KConsoleSender();
		commandManager = new KCommandManager();
		
		String pg = Bukkit.getServer().getClass().getPackage().getName();
		NMSVersion = su.plugin.core.bukkit.api.enumeration.NMSVersion.getByName(pg.substring(pg.lastIndexOf(".") + 1));
		
		//
		
		if(useVault = Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			List<String> list = new ArrayList<>();
			if(VaultHandler.setupChat()) {
				list.add("Chat");
			}
			if(VaultHandler.setupEconomy()) {
				list.add("Economy");
			}
			if(VaultHandler.setupPermission()) {
				list.add("Permission");
			}
			
			log("Vault 플러그인과 연동되었습니다." + (list.size() < 1 ? "" : " (" + StringUtil.connectString(list, ", ") + ")"));
		}
		
		if(usePlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			log("PlaceholderAPI 플러그인과 연동되었습니다.");
		}
		
		if(useProtocolSupport = Bukkit.getPluginManager().isPluginEnabled("ProtocolSupport")) {
			log("ProtocolSupport 플러그인과 연동되었습니다.");
		}

		if (useProtocolLib = PluginUtil.existsPlugin("ProtocolLib")) {
			signGUIManager = new SignGUIManager();
			log("ProtocolLib 플러그인과 연동되었습니다.");
		}
	}
	
	public static KCommandManager getCommandManager() {
		return (KCommandManager) commandManager;
	}
	
	public static Player getPlayer(PlayerKey playerKey) {
		UPlayer up = getUPlayer(playerKey);
		return up == null ? null : (Player) up.getPlatformSender();
	}
	
	public static Player getPlayer(UUID UUID) {
		for(Player p : getOnlinePlayers()) {
			if(p.getUniqueId().equals(UUID)) return p;
		}
		
		return null;
	}
	
	public static boolean isSpigot() {
		try { return Class.forName("org.bukkit.Server.Spigot") != null; }
		catch (ClassNotFoundException e) { }
		return false;
	}
	
	@SneakyThrows(Exception.class)
	public static List<Player> getOnlinePlayers() {
		List<Player> players = new ArrayList<>();
		
		Object result = KReflectionUtil.getMethod(Bukkit.class, "getOnlinePlayers").invoke(null, null);
		players.addAll(result instanceof Player[] ? Arrays.asList((Player[]) result) : (Collection) result);
		
		return players;
	}
	
	public static void teleport(Player player, Location location) {
		Bukkit.getScheduler().runTask(KCorePlugin.getInstance(), () -> {
			if(player.getVehicle() != null) {
				player.leaveVehicle();
			}

			player.teleport(location);
		});
	}
	
	public static Firework spawnFirework(Location location, boolean flicker, boolean trail, Type type, Color color, Color fade, int power) {
		Firework firework = location.getWorld().spawn(location, Firework.class);
		
		FireworkMeta meta = firework.getFireworkMeta();
		FireworkEffect fe = FireworkEffect.builder().flicker(flicker).trail(trail).with(type).withColor(color).withFade(fade).build();
		meta.setPower(power);
		meta.addEffect(fe);
		
		firework.setFireworkMeta(meta);
		
		return firework;
	}
	
	public static Firework spawnFirework(Location location, FireworkEffect effect, int power) {
		Firework firework = location.getWorld().spawn(location, Firework.class);
		
		FireworkMeta meta = firework.getFireworkMeta();
		meta.setPower(power);
		meta.addEffect(effect);
		
		firework.setFireworkMeta(meta);
		
		return firework;
	}
	
	private static Method oldSendPacket, worldGetHandle, fireworkGetHandle, nmsWorldBroadcastEntityEffect;
	
	public static void playFireworkEffect(Location location, boolean flicker, boolean trail, Type type, Color color, Color fade, int power) {
		playFireworkEffect(location, spawnFirework(location, flicker, trail, type, color, fade, power));
	}
	
	public static void playFireworkEffect(Location location, FireworkEffect effect, int power) {
		playFireworkEffect(location, effect, power);
	}
	
	@SneakyThrows(Exception.class)
	public static void playFireworkEffect(Location location, Firework firework) {
		if(NMSVersion.isBefore(NMSVersion.v1_7_R1)) {
			Object packet = KReflectionUtil.getCraftBukkitClass("Packet38EntityStatus");
			
			packet.getClass().getField("a").set(packet, firework.getEntityId());
			packet.getClass().getField("b").set(packet, 17);
			
			for(Player ap : getOnlinePlayers()) {
				Object playerConnection = KReflectionUtil.getPlayerConnection(ap);
				
				if(oldSendPacket == null) {
					oldSendPacket = KReflectionUtil.getMethod(playerConnection.getClass(), "sendPacket");
				}
				oldSendPacket.invoke(playerConnection, packet);
			}
		} else {
	        Object nms_world = null;
	        Object nms_firework = null;
	        
	        if(worldGetHandle == null) {
	        	worldGetHandle = KReflectionUtil.getMethod(location.getWorld().getClass(), "getHandle");
	        	fireworkGetHandle = KReflectionUtil.getMethod(firework.getClass(), "getHandle");
	        }
	        
	        nms_world = worldGetHandle.invoke(location.getWorld(), (Object[]) null);
	        nms_firework = fireworkGetHandle.invoke(firework, (Object[]) null);
	        
	        if(nmsWorldBroadcastEntityEffect == null) {
	        	nmsWorldBroadcastEntityEffect = KReflectionUtil
                .getMethod(nms_world.getClass(), "broadcastEntityEffect");
	        }
	        
	        nmsWorldBroadcastEntityEffect.invoke(nms_world, new Object[] {nms_firework, (byte) 17});
		}
		
        firework.remove();
    }
	
}