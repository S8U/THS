package su.plugin.lobbysystem.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.gui.SideBar;
import su.plugin.core.bukkit.api.lib.VaultHandler;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.lobbysystem.LobbySystemPlugin;
import su.plugin.lobbysystem.api.task.SideBarTask;
import su.plugin.lobbysystem.api.task.TimeLockTask;

public class LobbySystemAPI {
	
	@Setter
	@Getter
	private static String joinMessage, quitMessage, sideBarTitle;
	
	@Setter
	@Getter
	private static int joinSpeed, maxSpeed, lockTime, lockInterval;

	@Setter
	@Getter
	private static double doubleJumpForward, doubleJumpUpward;
	
	@Setter
	@Getter
	private static boolean useChannel, invincibilityOnJoin, useTimeLock, blockProtect, craftProtect, usePortal, useSideBar, useDoubleJump;

	@Setter
	@Getter
	private static SideBarTask sideBarTask;
	@Setter
	@Getter
	private static TimeLockTask timeLockTask;
	
	@Setter
	@Getter
	private static List<String> invincbilityPlayers = new ArrayList<>();
	@Setter
	@Getter
	private static List<String> invincbilityExceptionWorlds = new ArrayList<>();
	@Setter
	@Getter
	private static List<String> protectExceptionWorlds = new ArrayList<>();
	@Setter
	@Getter
	private static List<String> craftProtectExceptionWorlds = new ArrayList<>();
	@Setter
	@Getter
	private static List<Integer> breakExceptions = new ArrayList<>();
	@Setter
	@Getter
	private static List<Integer> placeExceptions = new ArrayList<>();
	@Setter
	@Getter
	private static List<String> sideBarTexts = new ArrayList<>();

	public static void registerPlugins() {
		if(useChannel = PluginUtil.existsPlugin("U-Channel")) {
			Core.log("U-Channel 플러그인과 연동되었습니다.");
		}
	}

	public static void createConfig() {
		LobbySystemPlugin plugin = LobbySystemPlugin.getInstance();
		
		plugin.getJsonConfig().addDefault("메시지.접속", "null");
		plugin.getJsonConfig().addDefault("메시지.퇴장", "null");
		
		plugin.getJsonConfig().addDefault("이동 속도.접속", 2);
		plugin.getJsonConfig().addDefault("이동 속도.최대", 3);

		plugin.getJsonConfig().addDefault("더블 점프.사용", false);
		plugin.getJsonConfig().addDefault("더블 점프.가로 속도", 16);
		plugin.getJsonConfig().addDefault("더블 점프.세로 속도", 10);
		
		plugin.getJsonConfig().addDefault("무적.접속 시 활성화", false);
		plugin.getJsonConfig().addDefault("무적.예외 월드", Arrays.asList("exception_world"));
		
		plugin.getJsonConfig().addDefault("시간 고정.사용", true);
		plugin.getJsonConfig().addDefault("시간 고정.주기(Tick)", 100);
		plugin.getJsonConfig().addDefault("시간 고정.시간", 4000);
		
		plugin.getJsonConfig().addDefault("블럭 보호.사용", true);
		plugin.getJsonConfig().addDefault("블럭 보호.파괴 예외 블럭", Arrays.asList(14, 15, 16, 21, 56, 129, 73));
		plugin.getJsonConfig().addDefault("블럭 보호.설치 보호 예외 블럭", Arrays.asList(0));
		plugin.getJsonConfig().addDefault("블럭 보호.예외 월드", Arrays.asList("exception_world"));
		
		plugin.getJsonConfig().addDefault("조합 방지.사용", false);
		plugin.getJsonConfig().addDefault("조합 방지.예외 월드", Arrays.asList("exception_world"));
		
		plugin.getJsonConfig().addDefault("포탈 사용", false);

		plugin.getJsonConfig().addDefault("사이드바.사용", true);
		plugin.getJsonConfig().addDefault("사이드바.타이틀", "&e&l정보");
		plugin.getJsonConfig().addDefault("사이드바.텍스트", Arrays.asList("&7<time:a h시 mm분>"));

		plugin.getJsonConfig().save();
	}
	
	@SuppressWarnings("unchecked")
	public static void loadConfig() {
		createConfig();
		
		LobbySystemPlugin plugin = LobbySystemPlugin.getInstance();
		
		joinMessage = plugin.getJsonConfig().getString("메시지.접속");
		quitMessage = plugin.getJsonConfig().getString("메시지.퇴장");
		
		joinSpeed = plugin.getJsonConfig().getInt("이동 속도.접속");
		maxSpeed = plugin.getJsonConfig().getInt("이동 속도.최대");

		useDoubleJump = plugin.getJsonConfig().getBoolean("더블 점프.사용");
		doubleJumpForward = plugin.getJsonConfig().getDouble("더블 점프.가로 속도") / 10;
		doubleJumpUpward = plugin.getJsonConfig().getDouble("더블 점프.세로 속도") / 10;
		
		invincibilityOnJoin = plugin.getJsonConfig().getBoolean("무적.접속 시 활성화");
		invincbilityExceptionWorlds = (ArrayList<String>) plugin.getJsonConfig().getStringList("무적.예외 월드");
		
		useTimeLock = plugin.getJsonConfig().getBoolean("시간 고정.사용");
		lockInterval = plugin.getJsonConfig().getInt("시간 고정.주기(Tick)");
		lockTime = plugin.getJsonConfig().getInt("시간 고정.시간");
		
		blockProtect = plugin.getJsonConfig().getBoolean("블럭 보호.사용");
		breakExceptions = (ArrayList<Integer>) plugin.getJsonConfig().getList("블럭 보호.파괴 보호 예외 블럭");
		placeExceptions = (ArrayList<Integer>) plugin.getJsonConfig().getList("블럭 보호.설치 보호 예외 블럭");
		protectExceptionWorlds = (ArrayList<String>) plugin.getJsonConfig().getStringList("블럭 보호.예외 월드");
		
		craftProtect = plugin.getJsonConfig().getBoolean("조합 방지.사용");
		craftProtectExceptionWorlds = plugin.getJsonConfig().getStringList("조합 방지.예외 월드");

		useSideBar = plugin.getJsonConfig().getBoolean("사이드바.사용");
		sideBarTitle = ChatColor.translateAlternateColorCodes('&', plugin.getJsonConfig().getString("사이드바.타이틀"));
		sideBarTexts = StringUtil.translateAlternateColorCodes(plugin.getJsonConfig().getStringList("사이드바.텍스트"));
		
		usePortal = plugin.getJsonConfig().getBoolean("포탈 사용");
		
		Core.log("설정을 불러왔습니다.");
	}
	
	public static void setSpeed(Player p, int speed) {
		p.setWalkSpeed(speed * 0.2F);
	}
	
	public static void setInvincibility(Player p, boolean enable) {
		if(enable && !invincbilityPlayers.contains(p.getName().toLowerCase())) {
			invincbilityPlayers.add(p.getName().toLowerCase());
		} else if(!enable && invincbilityPlayers.contains(p.getName().toLowerCase())) {
			invincbilityPlayers.remove(p.getName().toLowerCase());
		}
	}
	
	public static boolean isInvincibility(Player p) {
		return invincbilityPlayers.contains(p.getName().toLowerCase());
	}
	
	public static boolean canBreak(Block b) {
		if(b == null) return true;
		else if(protectExceptionWorlds.contains(b.getWorld().getName())) return true;
		return breakExceptions.contains(b.getTypeId());
	}
	
	public static boolean canPlace(Block b) {
		if(b == null) return true;
		else if(protectExceptionWorlds.contains(b.getWorld().getName())) return true;
		return placeExceptions.contains(b.getTypeId());
	}
	
	public static boolean canCraft(Player p) {
		if(!craftProtect) return true;
		return craftProtectExceptionWorlds.contains(p.getWorld().getName());
	}
	
	public static boolean isInvincibilityWorld(String world) {
		return !invincbilityExceptionWorlds.contains(world);
	}

	public static Scoreboard makeScoreBoard(Player player) {
		SideBar sb = new SideBar(replaceSideBarText(player, sideBarTitle));

		for(String text : sideBarTexts) {
			sb.addText(replaceSideBarText(player, text));
		}

		return sb.updateScoreboard();
	}

	@SneakyThrows(Exception.class)
	private static String replaceSideBarText(Player player, String text) {
		Method method = LobbySystemPlugin.getApi().getClass().getDeclaredMethod("makeTimeString", String.class);
		method.setAccessible(true);

		text = StringUtil.replaceValue("time", text, method)
				.replace("<player_count>", KCore.getOnlinePlayers().size() + "")
				.replace("<max_player_count>", Bukkit.getMaxPlayers() + "");

		if(KCore.isUseVault()) {
			text = text.replace("<money>", VaultHandler.getMoney(player) + "");
		}
		if(useChannel) {
			text = text.replace("<channel_name>", KChannelAPI.getChannelName())
					.replace("<channel_displayname>", KChannelAPI.getCurrentChannel().getDisplayName()).replace("<channel_displayname>", KChannelAPI.getCurrentChannel().getDisplayName())
					.replace("<channel_group_name>", KChannelAPI.getCurrentChannel().getGroup().getName())
					.replace("<channel_group_displayname>", KChannelAPI.getCurrentChannel().getGroup().getDisplayName())
					.replace("<channel_group_player_count>", KChannelAPI.getCurrentChannel().getGroup().getPlayerCount() + "")
					.replace("<channel_all_player_count>", KChannelAPI.getAllPlayerCount() + "");
		}

		// KillState

		return text;
	}

	private static String makeTimeString(String value) {
		return StringUtil.buildDateString(System.currentTimeMillis(), value);
	}

}