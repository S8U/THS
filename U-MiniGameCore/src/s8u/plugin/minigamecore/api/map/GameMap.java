package s8u.plugin.minigamecore.api.map;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import s8u.plugin.minigamecore.api.map.limit.MapLimiter;

@Getter
public class GameMap {

  @Setter
  private String name;

  @Setter
  private Location location;

  @Setter
  private MapLimiter mapLimiter;

  public boolean isInMap(Player player) {
    return isInMap(player.getLocation());
  }

  public boolean isInMap(Location location) {
    return mapLimiter != null && mapLimiter.isInMap(location);
  }

  public Location getRandomTopLocation() {
    if (mapLimiter == null) return null;

    return mapLimiter.getRandomTopLocation();
  }

}