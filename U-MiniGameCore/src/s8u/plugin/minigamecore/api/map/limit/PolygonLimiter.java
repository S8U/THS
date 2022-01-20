package s8u.plugin.minigamecore.api.map.limit;

import java.util.Arrays;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PolygonLimiter implements MapLimiter {

  @Override
  public boolean isInMap(Player player) {
    return isInMap(player.getLocation());
  }

  @Override
  public boolean isInMap(Location location) {
    return true;
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
    return null;
  }

}