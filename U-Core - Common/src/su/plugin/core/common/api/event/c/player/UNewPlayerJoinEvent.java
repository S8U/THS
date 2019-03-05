package su.plugin.core.common.api.event.c.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.core.common.api.event.UEvent;
import su.plugin.core.common.api.player.UPlayer;

@RequiredArgsConstructor
@Setter
@Getter
public class UNewPlayerJoinEvent extends UEvent {

  private final UPlayer player;

}