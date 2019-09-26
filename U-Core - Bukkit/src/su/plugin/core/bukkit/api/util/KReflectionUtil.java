package su.plugin.core.bukkit.api.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;
import su.plugin.core.common.api.util.ReflectionUtil;

public class KReflectionUtil extends ReflectionUtil {
	
	@SneakyThrows(Exception.class)
	public static Class<?> getNMSClass(String name) {
		return Class.forName("net.minecraft.server." + KCore.getNMSVersion().getString() + "." + name);
	}
	
	@SneakyThrows(Exception.class)
	public static Class<?> getCraftBukkitClass(String name) {
		return Class.forName("org.bungee.craftbukkit." + KCore.getNMSVersion().getString() + "." + name);
	}
	
	@SneakyThrows(Exception.class)
	public static Object getHandle(Player player) {
		return getMethod(player.getClass(), "getHandle").invoke(player, null);
	}
	
	@SneakyThrows(Exception.class)
	public static Object getPlayerConnection(Player player) {
		Object handle = getHandle(player);
		
		return handle.getClass().getField("playerConnection").get(handle);
	}
	
	@SneakyThrows(Exception.class)
	public static Object getNetworkManager(Player player) {
		Object playerConnection = getPlayerConnection(player);
		
		return playerConnection.getClass().getField("networkManager").get(playerConnection);
	}
	
	@SneakyThrows(Exception.class)
	public static void sendPacket(Player player, Object packet) {
		if(packet == null) return;
		
		Object playerConnection = getPlayerConnection(player);
		
		getMethod(playerConnection.getClass(), "sendPacket").invoke(playerConnection, packet);
	}
	
	@SneakyThrows(Exception.class)
	public static int getProtocolVersion(Player player) {
		Object networkManager = getNetworkManager(player);
		
		return KCore.getNMSVersion().isBefore(NMSVersion.v1_8_R1) ? Integer.valueOf(networkManager.getClass().getMethod("getVersion").invoke(networkManager, null).toString()) : 10000;
	}
	
	@SneakyThrows(Exception.class)
	public static PluginCommand getCommand(String name, Plugin plugin) {
		Constructor<PluginCommand> c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
		c.setAccessible(true);
		
		return c.newInstance(name, plugin);
	}
	
	@SneakyThrows(Exception.class)
	public static CommandMap getCommandMap() {
		Field f = SimplePluginManager.class.getDeclaredField("commandMap");
		f.setAccessible(true);
		
		return (CommandMap) f.get(Bukkit.getPluginManager());
	}
	
}