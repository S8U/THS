package su.plugin.ability.api.event;

import lombok.Getter;
import org.bukkit.command.CommandSender;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class GameStartEvent extends UKCancellableEvent {
	
	@Getter
	private final CommandSender starter;
	
	public GameStartEvent(CommandSender starter) {
		this.starter = starter;
	}	
	
}