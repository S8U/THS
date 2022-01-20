package s8u.plugin.minigamecore.api.map.limit;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Setter
@Getter
@RequiredArgsConstructor
public class RoundLimiter implements MapLimiter {

  private final Location centerLocation;

  private double limitRange;

  @Override
  public boolean isInMap(Player player) {
    return isInMap(player.getLocation());
  }

  @Override
  public boolean isInMap(Location location) {
    return Math.pow(location.getX() - this.centerLocation.getX(), 2) + Math.pow(location.getZ() - this.centerLocation.getX(), 2) <= Math.pow(limitRange, 2);
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

    double ranX = centerLocation.getX() + Math.sin(Math.toRadians(ran)) * limitRange;
    double ranZ = centerLocation.getZ() + Math.cos(Math.toRadians(ran)) * limitRange;

    Block block = centerLocation.getWorld().getHighestBlockAt((int) ranX, (int) ranZ);

    if (block == null) return null;
    else if (excludeBlocks != null && excludeBlocks.contains(block.getType())) return getRandomTopLocation(excludeBlocks);

    return block.getLocation();
  }
}