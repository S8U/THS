package su.plugin.gparty.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.api.object.KPartyPlayer;
import su.plugin.gparty.bukkit.api.task.PartyParticleTask;
import su.plugin.gparty.bukkit.listener.ConnectListener;
import su.plugin.gparty.bukkit.listener.PluginListener;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.player.PlayerKey;

public class KGPartyPlugin extends UKPlugin {
	
	@Getter
	private static KGPartyPlugin instance;
	@Getter
	private static KGPartyAPI api = new KGPartyAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§a[ U-GParty ]");
		setColor(ChatColor.GREEN);
		
		api.init();
		
		loadConfig();

		registerListeners(new ConnectListener().getClass().getPackage().getName());
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "U-GParty", new PluginListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "U-GParty");
		
		if(api.isUseParticle()) {
			Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new PartyParticleTask(), 0, 2);
		}
	}
	
	public void loadConfig() {
		getJsonConfig().addDefault("파티끼리 PVP 허용", false);
		getJsonConfig().addDefault("파티클.사용", true);
		getJsonConfig().addDefault("파티클.이름", "flame");

		getJsonConfig().save();

		api.setAllowPartyPVP(getJsonConfig().getBoolean("파티끼리 PVP 허용"));
		api.setUseParticle(getJsonConfig().getBoolean("파티클.사용"));
		api.setParticle(getJsonConfig().getString("파티클.이름"));

		log("설정을 불러왔습니다.");
	}
	
	public void initPlayers() {
		for(Player ap : KCore.getOnlinePlayers()) {
			KPartyPlayer pp = new KPartyPlayer(PlayerKey.getPlayerKey(ap.getName()));
			api.getPlayerManager().setPartyPlayer(ap, pp);

			api.getPartyManager().sendPartyDataRequest(pp.getPlayerKey());
		}
	}

}