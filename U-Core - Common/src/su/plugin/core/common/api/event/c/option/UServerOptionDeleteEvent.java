package su.plugin.core.common.api.event.c.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.common.api.event.UEvent;

@RequiredArgsConstructor
@Getter
public class UServerOptionDeleteEvent extends UEvent {
	
	private final String name;
	
}