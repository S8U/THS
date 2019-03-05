package su.plugin.ability.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.entity.PlayerDeathEvent;
import su.plugin.ability.api.category.DeathReason;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.event.UKEvent;

@RequiredArgsConstructor
@Getter
public class DeathEvent extends UKEvent {
	
	private final GamePlayer player, killer;
	
	private final DeathReason reason;
	
	private final PlayerDeathEvent playerDeathEvent;
	
}