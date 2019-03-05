package su.plugin.glogin.common.api.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import su.plugin.core.common.api.player.PlayerKey;

@Setter
@Getter
@RequiredArgsConstructor
public class Account {
	
	private final PlayerKey playerKey;
	
	private String name;
	
	private String ip, password;
	
	private long lastLogin, lastLogout, registerTime;
	
	private boolean login;
	
}