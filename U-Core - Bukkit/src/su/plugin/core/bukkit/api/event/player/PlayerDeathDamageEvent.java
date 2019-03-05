package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

@RequiredArgsConstructor
@Getter
public class PlayerDeathDamageEvent  extends UKCancellableEvent {
	
	private final Player player, killer;
	
	private final EntityDamageEvent entityDamageEvent;
	
}