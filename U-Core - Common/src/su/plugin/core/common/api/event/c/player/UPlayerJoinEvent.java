package su.plugin.core.common.api.event.c.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.event.UEvent;
import su.plugin.core.common.api.player.UPlayer;

@AllArgsConstructor
@Getter
public class UPlayerJoinEvent extends UEvent {
	
	private final UPlayer player;
	
	@Setter
	private String joinMessage;
	
}