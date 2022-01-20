package s8u.plugin.minigamecore.api.map.limit;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Setter
@Getter
public class SquareLimiter implements MapLimiter {

  private boolean ignoreY;

  private Location minLocation, maxLocation;

  public void setLimitLocation(Location minLocation, Location maxLocation) {
    double lx = minLocation.getX();
    double lz = minLocation.getZ();
    double rx = maxLocation.getX();
    double rz = maxLocation.getZ();
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

    minLocation.setX(lx);
    minLocation.setZ(lz);
    maxLocation.setX(rx);
    maxLocation.setZ(rz);

    this.minLocation = minLocation;
    this.maxLocation = maxLocation;
  }

  @Override
  public boolean isInMap(Player player) {
    return isInMap(player.getLocation());
  }

  @Override
  public boolean isInMap(Location location) {
    if (minLocation == null || maxLocation == null) return false;

    return location.getWorld().equals(location.getWorld()) &&
        minLocation.getX() <= location.getX() &&
        minLocation.getZ() <= location.getZ() &&
        maxLocation.getX() >= location.getX() &&
        maxLocation.getZ() >= location.getZ();
  }

  @Override
  public Location getRandomTopLocation() {
    return getRandomTopLocation(Arrays.asList(
        Material.WATER, Material.STATIONARY_WATER,
        Material.LAVA, Material.STATIONARY_LAVA,
        Material.BEDROCK, Material.AIR));
  }

  @Override
  public Location getRandomTopLocation(List<Material> excludeBlocks) {
    double ran = Math.random();

    double ranX = minLocation.getX() + ran * (maxLocation.getX() - minLocation.getX());
    double ranZ = minLocation.getZ() + ran * (maxLocation.getZ() - minLocation.getZ());

    Block block = minLocation.getWorld().getHighestBlockAt((int) ranX, (int) ranZ);

    if (block == null) return null;
    else if (excludeBlocks != null && excludeBlocks.contains(block.getType())) return getRandomTopLocation(excludeBlocks);

    return block.getLocation();
  }

}
