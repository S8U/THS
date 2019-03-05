package su.plugin.core.common.api.event.c.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.event.UCancellableEvent;
import su.plugin.core.common.api.player.UPlayer;

@Setter
@Getter
@AllArgsConstructor
public class UPlayerChatEvent extends UCancellableEvent {

  private UPlayer player;

  private String message;

}
