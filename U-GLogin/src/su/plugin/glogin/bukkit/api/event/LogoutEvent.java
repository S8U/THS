package su.plugin.glogin.bukkit.api.event;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKEvent;

@RequiredArgsConstructor
public class LogoutEvent extends UKEvent {

	@Getter
	private final Player player;
	
}