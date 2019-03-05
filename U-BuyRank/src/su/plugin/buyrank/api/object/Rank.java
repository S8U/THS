package su.plugin.buyrank.api.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@RequiredArgsConstructor
public class Rank {

  private final String name;

  private String permission;

  private int killCount;

  private double price;

}