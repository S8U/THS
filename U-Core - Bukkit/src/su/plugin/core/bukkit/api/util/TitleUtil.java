package su.plugin.core.bukkit.api.util;

import java.lang.reflect.Field;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;

@UtilityClass
public class TitleUtil {
	
	@SneakyThrows(Exception.class)
	public static void sendTitle(Player player, String message, int fadeIn, int fadeOut, int stay) {
		sendTitle(player, message, null, fadeIn, fadeOut, stay);
	}
	
	@SneakyThrows(Exception.class)
	public static void sendSubTitle(Player player, String message, int fadeIn, int fadeOut, int stay) {
		sendTitle(player, null, message, fadeIn, fadeOut, stay);
	}
	
	@SneakyThrows(Exception.class)
	public static void sendTitle(Player player, String title, String subTitle, int fadeIn, int fadeOut, int stay) {
		Object titleClass = KReflectionUtil.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null);
		Object titleComponent = KReflectionUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + (title == null ? "" : ChatColor.translateAlternateColorCodes('&', title)) + "\"}");
		Object titlePacket = KReflectionUtil.getNMSClass("PacketPlayOutTitle")
				.getConstructor(KReflectionUtil.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], KReflectionUtil
            .getNMSClass("IChatBaseComponent"))
				.newInstance(titleClass, titleComponent);
		Object timePacket = KReflectionUtil.getNMSClass("PacketPlayOutTitle")
				.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
		
		KReflectionUtil.sendPacket(player, timePacket);
		
		if(subTitle != null) {
			Object subTitleClass = KReflectionUtil.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null);
			Object subTitleComponent = KReflectionUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', subTitle) + "\"}");
			Object subTitlePacket = KReflectionUtil.getNMSClass("PacketPlayOutTitle")
					.getConstructor(KReflectionUtil.getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], KReflectionUtil
              .getNMSClass("IChatBaseComponent"))
					.newInstance(subTitleClass, subTitleComponent);
			
			KReflectionUtil.sendPacket(player, subTitlePacket);
		}
		
		KReflectionUtil.sendPacket(player, titlePacket);
	}
	
	@SneakyThrows(Exception.class)
	public static void sendTabTitle(Player player, String header, String footer) {
		Object headerComponent = KReflectionUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', header) + "\"}");
		Object footerComponent = KReflectionUtil.getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + ChatColor.translateAlternateColorCodes('&', footer) + "\"}");
		
		Object packet = KReflectionUtil.getNMSClass("PacketPlayOutPlayerListHeaderFooter")
				.getConstructor(KCore.getNMSVersion().isAfter(NMSVersion.v1_11_R1) || header == null ? new Class[] {} : new Class[] {
            KReflectionUtil.getNMSClass("IChatBaseComponent")})
				.newInstance(KCore.getNMSVersion().isAfter(NMSVersion.v1_11_R1) || header == null ? new Object[] {} : new Object[] {headerComponent});
		
		if(KCore.getNMSVersion().isAfter(NMSVersion.v1_11_R1) && header != null) {
			Field a = packet.getClass().getDeclaredField("a");
			a.setAccessible(true);
			a.set(packet, headerComponent);
		}
		
		if(footer != null) {
			Field b = packet.getClass().getDeclaredField("b");
			b.setAccessible(true);
			b.set(packet, footerComponent);
		}
		
		KReflectionUtil.sendPacket(player, packet);
	}
	
	@SneakyThrows(Exception.class)
	public static void sendActionBar(Player player, String message) {
		Object barMessage = KReflectionUtil.getNMSClass("ChatComponentText").getConstructor(String.class).newInstance(ChatColor.translateAlternateColorCodes('&', message));
		Object packet = KReflectionUtil.getNMSClass("PacketPlayOutChat")
				.getConstructor(KReflectionUtil.getNMSClass("IChatBaseComponent"), KCore.getNMSVersion().isAfter(NMSVersion.v1_11_R1) ? KReflectionUtil
            .getNMSClass("ChatMessageType") : Byte.TYPE)
				.newInstance(barMessage, KCore.getNMSVersion().isAfter(NMSVersion.v1_11_R1) ? KReflectionUtil
            .getNMSClass("ChatMessageType").getField("GAME_INFO").get(null) : (byte) 2);
		
		KReflectionUtil.sendPacket(player, packet);
	}
	
}