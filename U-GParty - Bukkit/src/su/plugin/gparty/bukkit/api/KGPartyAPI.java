package su.plugin.gparty.bukkit.api;

import lombok.Getter;
import lombok.Setter;
import su.plugin.gparty.bukkit.api.manager.KPartyManager;
import su.plugin.gparty.bukkit.api.manager.KPlayerManager;

public class KGPartyAPI {
	
	@Setter
	@Getter
	private static String particle;
	
	@Setter
	@Getter
	private static boolean useParticle, allowPartyPVP;

	@Getter
	private static KPartyManager partyManager;
	@Getter
	private static KPlayerManager playerManager;
	
	public void init() {
		partyManager = new KPartyManager();
		playerManager = new KPlayerManager();
	}
	
}