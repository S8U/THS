package s8u.plugin.cash.api.sql;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Type {
  SET("Set"),
  ADD("Add"),
  SUBTRACT("Subtract");

  private final String text;
}