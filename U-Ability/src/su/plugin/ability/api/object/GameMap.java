package su.plugin.ability.api.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.common.api.util.NumberUtil;

public class GameMap {

	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Setter
	@Getter
	private String name;
	
	@Setter
	@Getter
	private boolean randomTeleport = false;
	
	@Setter
	@Getter
	private Location mapLocation, TPAllLocation;
	@Getter
	private Location minMapLocation, maxMapLocation, minTPAllLocation, maxTPAllLocation;
	
	@Getter
	private List<Location> mapPacketLocations = new ArrayList<>(),
	tpAllPacketLocations = new ArrayList<>();
	
	public void setMapLimitLocation(Location loc1, Location loc2) {
		minMapLocation = loc1;
		maxMapLocation = loc2;
		replaceMinMax(getMinMapLocation(), getMaxMapLocation());
	}
	
	public void setTpAllLimitLocation(Location loc1, Location loc2) {
		minTPAllLocation = loc1;
		maxTPAllLocation = loc2;
		replaceMinMax(getMinTPAllLocation(), getMaxTPAllLocation());
	}
	
	private void replaceMinMax(Location min, Location max) {
		if(min == null || max == null) return;
		double lx = min.getX();
		double lz = min.getZ();
		double rx = max.getX();
		double rz = max.getZ();
		double temp;
		if(lx > rx) {
			temp = lx;
			lx = rx;
			rx = temp;
		}
		if(lz > rz) {
			temp = lz;
			lz = rz;
			rz = temp;
		}
		min.setX(lx);
		min.setZ(lz);
		max.setX(rx);
		max.setZ(rz);
	}
	
	public void addMapPacketLocation(Location loc) {
		mapPacketLocations.add(loc);
	}
	
	public void addTpAllPacketLocation(Location loc) {
		tpAllPacketLocations.add(loc);
	}
	
	public Location getRandomLocation(boolean tpAll) {
		int x = NumberUtil.random((int) (tpAll ? getMinTPAllLocation() : getMinMapLocation()).getX(), (int) (tpAll ? getMaxTPAllLocation() : getMaxMapLocation()).getX());
		int z = NumberUtil.random((int) (tpAll ? getMinTPAllLocation() : getMinMapLocation()).getZ(), (int) (tpAll ? getMaxTPAllLocation() : getMaxMapLocation()).getZ());
		World world = tpAll ? getTPAllLocation().getWorld() : getMapLocation().getWorld();
		Block b = world.getHighestBlockAt(x, z);
		Location l = b.getLocation();
		l.setY(l.getY() - 1);
		b = b.getWorld().getBlockAt(l);
		if(b == null) return getRandomLocation(tpAll);
		if(b.getType().equals(Material.WATER) || b.getType().equals(Material.STATIONARY_WATER) ||
				b.getType().equals(Material.LAVA) || b.getType().equals(Material.STATIONARY_LAVA) ||
				b.getType().equals(Material.BEDROCK) || b.getType().equals(Material.BARRIER) ||b.getType().equals(Material.AIR)) return getRandomLocation(tpAll);
		l.setY(l.getY() + 1);
		return l;
	}
	
	public boolean isInMap(Location location, boolean tpAll) {
		if(location.getY() < 1 ) return true;

		if(api.isUseAutoMapLimit() && !tpAll) {
			return isInMap(location, api.getMapLimitRange(), tpAll);
		} else if(api.isUseAutoTpAllMapLimit() && tpAll) {
			return isInMap(location, api.getTpAllLimitRange(), tpAll);
		}

		Location min = tpAll ? getMinTPAllLocation() : getMinMapLocation();
		Location max = tpAll ? getMaxTPAllLocation() : getMaxMapLocation();

		if(min == null || max == null) return true;

		World world = tpAll ? TPAllLocation.getWorld() : mapLocation.getWorld();

		return world.equals(location.getWorld()) &&
				min.getX() <= location.getX() &&
				min.getZ() <= location.getZ() &&
				max.getX() >= location.getX() &&
				max.getZ() >= location.getZ();
	}

	public boolean isInMap(Location location, int range, boolean tpAll) {
		if(location.getY() < 1) return true;

		Location l = tpAll ? getTPAllLocation() : getMapLocation();
		
		return (Math.pow(l.getX() - location.getX(), 2) + Math.pow(l.getZ() - location.getZ(), 2) <= Math.pow(range, 2));
	}
	
}