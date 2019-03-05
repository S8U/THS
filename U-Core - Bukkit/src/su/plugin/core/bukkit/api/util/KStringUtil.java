package su.plugin.core.bukkit.api.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KStringUtil {
	
	public static String locationToString(Location location) {
		return location.getWorld().getName() + ", " + location.getX() + ", " + location.getY() + ", " + location.getZ() + ", " + location.getYaw() + ", " + location.getPitch();
	}
	
	public static Location stringToLocation(String str) {
		String[] strs = str.split(", ");
		return new Location(Bukkit.getWorld(strs[0]), Double.parseDouble(strs[1]), Double.parseDouble(strs[2]), Double.parseDouble(strs[3]), Float.parseFloat(strs[4]), Float.parseFloat(strs[5]));
	}
	
}