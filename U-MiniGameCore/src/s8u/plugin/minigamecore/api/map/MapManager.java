package s8u.plugin.minigamecore.api.map;

import java.util.LinkedHashMap;
import lombok.Getter;
import lombok.Setter;

public class MapManager {

  @Setter
  @Getter
  private GameMap spawnMap, playingMap;

  @Getter
  private LinkedHashMap<String, GameMap> maps = new LinkedHashMap<>(); // Name, Map

  public void setMap(String name, GameMap map) {
    maps.put(name.toLowerCase(), map);
  }

  public void removeMap(String name) {
    maps.remove(name);
  }

  public GameMap getMap(String name) {
    return maps.get(name.toLowerCase());
  }

  public boolean existsMap(String name) {
    return maps.containsKey(name.toLowerCase());
  }

  public GameMap getRandomMap() {
    return maps.values().toArray(new GameMap[maps.size()])[(int) (Math.random() * maps.size())];
  }

}