package su.plugin.core.bukkit.api.event;

import org.bukkit.event.Cancellable;

import lombok.Getter;
import lombok.Setter;

public class UKCancellableEvent extends UKEvent implements Cancellable {
	
	@Setter
	@Getter
	private boolean cancelled = false;
	
}