package su.plugin.gessentials.bungee.api.object;

import lombok.Getter;
import lombok.Setter;
import su.plugin.gessentials.bungee.api.category.WarningEventType;

@Setter
@Getter
public class WarningEvent {
	
	private long duration;
	
	private String reason;
	
	private WarningEventType type;
	
}