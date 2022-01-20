package su.plugin.core.bungee;

import java.util.UUID;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.core.bungee.api.GCore;
import su.plugin.core.bungee.api.player.GPlayer;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.bungee.listener.OptionListener;
import su.plugin.core.bungee.listener.PluginMessageListener;
import su.plugin.core.bungee.listener.UPlayerListener;
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

public class GCorePlugin extends UGPlugin {
	
	@Getter
	private static GCorePlugin instance;
	
	@Getter
	private static GCore api = new GCore();
	
	@Override
	public void onUEnable() {
		instance = this;
		
		api.init();
		
		setPrefix(Core.getCorePrefix());
		setColor(ChatColor.YELLOW);
		
		UPlayerListener upl = new UPlayerListener();
		ProxyServer.getInstance().getPluginManager().registerListener(this, upl);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessageListener());
		
		registerUEventListener(new OptionListener());

		registerCommands(new MainCommand());
		registerCommands(new PluginManagerCommand());
		registerCommands(new UPlayerManagerCommand());

		registerCommands(new DisplayNameCommand());
		registerCommands(new DebugCommand());
		registerCommands(new TestCommand());
		
		ProxyServer.getInstance().registerChannel("ucore:main");
		
		Core.getSQLManager().connect(this);
		
		ProxyServer.getInstance().getPlayers().forEach(p -> {
			PlayerKey playerKey = null;
			
			UUID uuid = Core.getUUID(p);

			if(uuid != null) {
				playerKey = Core.getSQLManager().getPlayerKey(uuid);
			}
			
			if(playerKey == null && (playerKey = Core.getSQLManager().getPlayerKey(p.getName())) == null) {
				Core.getSQLManager().createPlayerKey(p.getName(), uuid, GCore.getOnlineMode(p));
				playerKey = Core.getSQLManager().getPlayerKey(p.getName());
			}
			
			GPlayer gp = new GPlayer(playerKey, p.getName(), p.getAddress().getAddress().getHostAddress());
			gp.setProxiedPlayer(p);
			
			upl.getConnected().add(p.getName().toLowerCase());
			
			Core.getUPlayerManager().setUPlayer(playerKey, gp);
		});
		
		if(Core.getOptionSQLManager().connect(this)) {
			registerCommands(new PlayerOptionCommand());
			registerCommands(new ServerOptionCommand());
			
			Core.getOptionSQLManager().loadServerOptions();
		} else {
			Core.getOptionSQLManager().setUse(false);
			
			Core.wlog("옵션 SQL에 연결할 수 없어 옵션 SQL 기능이 비활성화됩니다.");
		}

		onConfigLoad();
	}
	
	@Override
	public void onUDisable() {
		Core.getSQLManager().close();
		Core.getOptionSQLManager().close();
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