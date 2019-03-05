package su.plugin.core.common.api.event.c.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.common.api.event.UEvent;
import su.plugin.core.common.api.player.UPlayer;

@RequiredArgsConstructor
@Getter
public class UPlayerOptionChangeEvent extends UEvent {
	
	private final UPlayer player;
	
	private final String name;
	
	private final Object value;
	
}