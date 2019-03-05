package su.plugin.core.common.api.event;

import lombok.Getter;
import lombok.Setter;

public class UCancellableEvent extends UEvent {
	
	@Setter
	@Getter
	private boolean cancelled = false;
	
}