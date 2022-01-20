package su.plugin.core.bukkit;

import java.util.UUID;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;
import su.plugin.core.bukkit.api.player.KPlayer;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.bukkit.command.QuickBarCommand;
import su.plugin.core.bukkit.listener.BungeeMessageListener;
import su.plugin.core.bukkit.listener.BungeeOptionListener;
import su.plugin.core.bukkit.listener.GUIListener;
import su.plugin.core.bukkit.listener.PlayerListener;
import su.plugin.core.bukkit.listener.UPlayerListener;
import su.plugin.core.bukkit.placeholder.PlayerOptionPlaceHolder;
import su.plugin.core.bukkit.placeholder.ServerOptionPlaceHolder;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.command.DebugCommand;
import su.plugin.core.common.command.DisplayNameCommand;
import su.plugin.core.common.command.MainCommand;
import su.plugin.core.common.command.PlayerOptionCommand;
import su.plugin.core.common.command.PluginManagerCommand;
import su.plugin.core.common.command.ServerOptionCommand;
import su.plugin.core.common.command.TestCommand;
import su.plugin.core.common.command.UPlayerManagerCommand;

public class KCorePlugin extends UKPlugin {
	
	@Getter
	private static KCorePlugin instance;
	
	@Getter
	private static KCore api = new KCore();
	
	@Override
	public void onUEnable() {
		instance = this;
		
		api.init();
		
		setPrefix(Core.getCorePrefix());
		setColor(ChatColor.YELLOW);
		
		Bukkit.getPluginManager().registerEvents(new GUIListener(), this);
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		Bukkit.getPluginManager().registerEvents(new UPlayerListener(), this);

		registerCommands(new MainCommand());
		registerCommands(new PluginManagerCommand());
		registerCommands(new UPlayerManagerCommand());

		registerCommands(new DisplayNameCommand());
		registerCommands(new DebugCommand());

		registerCommands(new QuickBarCommand());
		registerCommands(new TestCommand());

		registerCommands(new su.plugin.core.bukkit.command.TestCommand());

		Core.getSQLManager().connect(this);
		
		KCore.getOnlinePlayers().forEach(p -> {
			PlayerKey playerKey = null;
			
			if(KCore.getNMSVersion().isAfter(NMSVersion.v1_6_R2)) {
				UUID uuid = Core.getUUID(p);
				if(uuid != null) {
					playerKey = Core.getSQLManager().getPlayerKey(uuid);
				}
			}
			
			if(playerKey == null && (playerKey = Core.getSQLManager().getPlayerKey(p.getName())) == null) {
				Core.getSQLManager().createPlayerKey(p.getName(), Core.getUUID(p), Bukkit.getOnlineMode());
				playerKey = Core.getSQLManager().getPlayerKey(p.getName());
			}
			
			Core.getUPlayerManager().setUPlayer(playerKey, new KPlayer(playerKey, p));
		});

		if(Core.getOptionSQLManager().connect(this)) {
			registerCommands(new PlayerOptionCommand());
			registerCommands(new ServerOptionCommand());

			Core.getOptionSQLManager().loadServerOptions();

			if(Core.getOptionSQLManager().isUseBungeeSync()) {
				BungeeOptionListener bol = new BungeeOptionListener();

				Bukkit.getMessenger().registerIncomingPluginChannel(this, "ucore:main", bol);
				registerUEventListener(bol);
			}
		} else {
			Core.getOptionSQLManager().setUse(false);
		}

		registerChannel();
		
		if (api.isUsePlaceholderAPI()) {
			new PlayerOptionPlaceHolder().hook();
			new ServerOptionPlaceHolder().hook();
		}

		if (api.isUseProtocolLib()) {
			KCore.getSignGUIManager().registerListener();
		}
		
		onConfigLoad();
	}
	
	@Override
	public void onUDisable() {
		for(String name : KCore.getGUIManager().getPlayerGUIs().keySet()) {
			Player p = Bukkit.getPlayer(name);
			if(p == null) return;
			p.closeInventory();
		}
		
		Core.getSQLManager().close();
		Core.getOptionSQLManager().close();
	}
	
	public void registerChannel() {
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "ucore:main", new BungeeMessageListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "ucore:main");
		
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	}
	
	public void onConfigLoad() {
		getJsonConfig().addDefault("닉네임 허용 문자", "[a-zA-Z0-9_]{1,16}");
		getJsonConfig().addDefault("Tab 자동 완성.플레이어 닉네임 사용", true);
		getJsonConfig().addDefault("Tab 자동 완성.플레이어 표기 닉네임 사용", true);
		
		getJsonConfig().save();

		Core.setAllowNicknameRegex(getJsonConfig().getString("닉네임 허용 문자"));
		Core.getCommandManager().setPlayerNameTabComplete(getJsonConfig().getBoolean("Tab 자동 완성.플레이어 닉네임 사용"));
		Core.getCommandManager().setPlayerDisplayNameTabComplete(getJsonConfig().getBoolean("Tab 자동 완성.플레이어 표기 닉네임 사용"));
		
		log("설정을 불러왔습니다.");
	}
	
}