package su.plugin.prefixer.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.prefixer.api.object.PrefixPlayer;

@Getter
@AllArgsConstructor
public class MainPrefixChangeEvent extends UKEvent {
	
	private final PrefixPlayer prefixPlayer;
	
	@Setter
	private int priority;
	
	@Setter
	private String prefix;
	
}