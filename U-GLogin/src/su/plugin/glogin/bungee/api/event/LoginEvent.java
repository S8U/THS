package su.plugin.glogin.bungee.api.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Event;

@RequiredArgsConstructor
public class LoginEvent extends Event {

	@Getter
	private final ProxiedPlayer player;
	
}