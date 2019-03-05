package su.plugin.ability.api.event;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class GameStopEvent extends UKCancellableEvent {
	
	@Getter
	private final CommandSender stopper;
	
	public GameStopEvent(CommandSender stopper) {
		this.stopper = stopper;
	}
	
}