package su.plugin.pvpstats;

import lombok.Getter;
import org.bukkit.Bukkit;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.pvpstats.api.PVPStatsAPI;
import su.plugin.pvpstats.command.AdminCommand;
import su.plugin.pvpstats.listener.PlayerListener;
import su.plugin.pvpstats.listener.other.AbilityListener;
import su.plugin.pvpstats.placeholder.PlaceHolderHook;

public class PVPStatsPlugin extends UKPlugin {
	
	@Getter
	private static PVPStatsPlugin instance;
	
	@Getter
	private static PVPStatsAPI api = new PVPStatsAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§c[ U-PVPStats ]");
		setColor(ChatColor.RED);

		api.init();
		
		if(!api.getSQLManager().connect(this)) {
			wlog("MySQL에 연결할 수 없어 비활성화됩니다.");
			disable();
			return;
		}

		api.registerPlugins();

		registerListeners(new PlayerListener().getClass().getPackage().getName());
		if(api.isUseAbility()) {
			registerListener(new AbilityListener());
		}
		registerCommands(new AdminCommand().getClass().getPackage().getName());
		if(KCore.isUsePlaceholderAPI()) {
			new PlaceHolderHook().hook();
		}
		registerPermissions(new PermissionList().getClass().getPackage().getName());

		loadConfig();

		api.getPlayerManager().registerAllPlayers();
	}
	
	@Override
	public void onUDisable() {
		api.stopRankingUpdateTimer();

		api.getSQLManager().close();
	}

	@Override
	public void onConfigLoad(UCommandSender sender) {
		api.loadConfig(this);
	}

	@Override
	public void onConfigLoaded(UCommandSender sender) {
		api.initAnotherPeriodStats(false);

		api.getRankingManager().updateRanking(sender);
		Bukkit.getScheduler().runTaskLater(this, () -> api.getRankingManager().updateRankingHologram(true), 1);
		api.getRankingManager().runRankingUpdateTask();

		api.runStatsInitTimer();
	}
}