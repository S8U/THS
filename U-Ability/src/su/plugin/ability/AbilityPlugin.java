package su.plugin.ability;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.ability.command.AdminCommand;
import su.plugin.ability.command.KitCommand;
import su.plugin.ability.command.MainCommand;
import su.plugin.ability.command.MapCommand;
import su.plugin.ability.command.RankItemCommand;
import su.plugin.ability.command.VoteCommand;
import su.plugin.ability.command.SupplyCommand;
import su.plugin.ability.command.UserCommand;
import su.plugin.ability.listener.AbilityListener;
import su.plugin.ability.listener.other.BAbilityListener;
import su.plugin.ability.listener.other.PAbilityListener;
import su.plugin.ability.listener.other.PVPStatsListener;
import su.plugin.ability.listener.other.PrefixListener;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.ChatColor;

public class AbilityPlugin extends UKPlugin {
	
	@Getter
	private static AbilityPlugin instance;
	@Getter
	private static AbilityAPI api = new AbilityAPI();
	
	public void onUEnable() {
		instance = this;
		setPrefix("§e[ U-Ability ]");
		setColor(ChatColor.YELLOW);
		api.init();
		
		api.getConfigManager().loadConfig();
		api.getConfigManager().loadBarConfig();
		api.getConfigManager().loadAutoConfig();
		api.getConfigManager().loadSpawn();
		api.getConfigManager().loadAllMap();
		api.getConfigManager().loadKit();
		api.getConfigManager().loadStartItem();
		api.getConfigManager().loadRankItem();
		api.getConfigManager().loadSupply();
		api.getConfigManager().loadRankRedraw();
		api.getConfigManager().loadBlackList();
		api.getConfigManager().loadInjectConfig();

		if(api.isRainOff()) {
			for(World world : Bukkit.getWorlds()) {
				world.setStorm(false);
			}
		}

		if(api.getAbilityPluginManager().isUseInject()) {
			api.getAbilityPluginManager().injectPlugins();
		}

		registerPlugins();
		registerAbilities();
		registerAListeners();
		registerACommands();
		registerPermissions(PermissionList.class.getPackage().getName());
		registerPlayers();

		if(api.isUseGameStartVote()) {
			api.getGUIManager().updateGameStartVoteGUI();
		}
		if(api.isUseMapVote() && api.getMapManager().getMaps().size() > 0) {
			api.getGUIManager().updateMapVoteGUI();
		}
		if(api.isUseWaitingQuickBar() || api.isUseWatchModeQuickBar()) {
			api.getBarManager().initQuickBar();
		}

		Bukkit.getScheduler().runTaskLater(this, () -> api.getGameManager().setGameState(GameState.WAITING), 1);
	}
	
	@Override
	public void onUDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	public void registerAListeners() {
		registerListeners(new AbilityListener().getClass().getPackage().getName());

		Bukkit.getPluginManager().registerEvents(new PrefixListener(), this);

		if(api.isUsePVPStats()) {
			Bukkit.getPluginManager().registerEvents(new PVPStatsListener(), this);
		}

		if(Bukkit.getPluginManager().getPlugin("PhysicalFighters") != null) {
			api.setUsePhysicalFighters(true);
			Bukkit.getPluginManager().registerEvents(new PAbilityListener(), this);
		}
		
		if(Bukkit.getPluginManager().getPlugin("BitAbility") != null) {
			api.setUseBitAbility(true);
			Bukkit.getPluginManager().registerEvents(new BAbilityListener(), this);
		}
	}

	public void registerACommands() {
		registerCommands(new MainCommand());
		registerCommands(new UserCommand());
		registerCommands(new VoteCommand());
		registerCommands(new AdminCommand());
		registerCommands(new MapCommand());
		registerCommands(new KitCommand());
		registerCommands(new SupplyCommand());
		registerCommands(new RankItemCommand());
	}
	
	public void registerAbilities() {
		if(api.isUseThisPluginAbility()) {
			api.getAbilityManager().registerAbilities(this);
		}
		api.getAbilityPluginManager().loadAbilities();
		log(api.getAbilityManager().getAbilities().size() + "개의 능력이 등록되었습니다.");
	}
	
	public void registerPlugins() {
		if(PluginUtil.existsPlugin("U-Channel")) {
			api.setUseChannel(true);
			log("U-Channel 플러그인과 연동되었습니다.");
		}

		if(PluginUtil.existsPlugin("U-GParty")) {
			api.setUseGParty(true);
			log("U-GParty 플러그인과 연동되었습니다.");
		}

		if(PluginUtil.existsPlugin("U-Prefixer")) {
			api.setUsePrefixer(true);
			log("U-Prefixer 플러그인과 연동되었습니다.");
		}

		if(PluginUtil.existsPlugin("U-Permission")) {
			api.setUsePermission(true);
			log("U-Permission 플러그인과 연동되었습니다.");
		}

		if(PluginUtil.existsPlugin("U-PVPStats")) {
			api.setUsePVPStats(true);
			log("U-PVPStats 플러그인과 연동되었습니다.");
		}
	}
	
	public void registerPlayers() {
		for(Player p : KCore.getOnlinePlayers()) {
			GamePlayer gp = new GamePlayer(p);
			gp.setOnline(true);
			
			api.getPlayerManager().setGamePlayer(p, gp);
		}
		
		api.getBarManager().updateSideBarAllPlayer();
	}

}
