package su.plugin.gparty.bungee.api.object;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.player.PlayerKey;

public class GPartyPlayer {
	
	@Getter
	private final PlayerKey playerKey;
	
	@Setter
	@Getter
	private GParty party, invitedParty;
	
	@Setter
	@Getter
	private boolean moving, partyChat, chatSpy;

	public GPartyPlayer(PlayerKey playerKey) {
		this.playerKey = playerKey;
		party = new GParty(playerKey);
	}

	public ProxiedPlayer getPlayer() {
		return ProxyServer.getInstance().getPlayer(playerKey.getName());
	}

	public String getDisplayName() {
		return playerKey.getUPlayer().getName();
	}
	
	public boolean hasParty() {
		return party != null;
	}
	
	public boolean hasInvitedParty() {
		return invitedParty != null;
	}
	
}