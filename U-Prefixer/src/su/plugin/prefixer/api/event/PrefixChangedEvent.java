package su.plugin.prefixer.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.prefixer.api.category.ChangeAction;
import su.plugin.prefixer.api.object.PrefixPlayer;

@Getter
@AllArgsConstructor
public class PrefixChangedEvent extends UKEvent {
	
	private final PrefixPlayer prefixPlayer;
	
	private final String prefix;
	
	private final ChangeAction changeAction;
	
}