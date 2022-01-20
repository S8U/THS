package s8u.plugin.minigamecore.api.map.limit;

import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface MapLimiter {

  boolean isInMap(Location location);

  boolean isInMap(Player player);

  Location getRandomTopLocation();

  Location getRandomTopLocation(List<Material> excludeBlocks);

}