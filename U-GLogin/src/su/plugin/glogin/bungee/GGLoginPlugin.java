package su.plugin.glogin.bungee;

import java.util.Arrays;

import lombok.Getter;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.bungee.command.UserCommand;
import su.plugin.glogin.bungee.listener.ControlListener;
import su.plugin.core.bungee.api.plugin.UGPlugin;
import su.plugin.core.common.api.ChatColor;

public class GGLoginPlugin extends UGPlugin {
	
	@Getter
	private static GGLoginPlugin instance;
	
	@Getter
	private static GGLoginAPI api = new GGLoginAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		
		setPrefix("§e[ U-GLogin ]");
		setColor(ChatColor.YELLOW);
		
		api.init();
		
		if(!api.getSQLManager().connect(this)) {
			log("MySQL에 연결할 수 없어 비활성화됩니다.");
			return;
		}
		
		api.getSQLManager().clearLogin();
		
		registerListeners(new ControlListener().getClass().getPackage().getName());
		registerUEventListeners(new ControlListener().getClass().getPackage().getName());
		
		registerCommands(new UserCommand().getClass().getPackage().getName());
		
		loadConfig();
	}
	
	public void createConfig() {
		getJsonConfig().addDefault("접속 시 강제 로그인", false);
		getJsonConfig().addDefault("정품일 경우 로그인 제외", true);
		getJsonConfig().addDefault("아이피당 최대 계정 수", 3);
		getJsonConfig().addDefault("로그인 타임 아웃(초)", 30);
		getJsonConfig().addDefault("닉네임 허용 문자", "[a-zA-Z0-9_]{1,16}");
		getJsonConfig().addDefault("로그인 전 허용 명령어", Arrays.asList("회원가입", "로그인"));
		
		getJsonConfig().save();
	}
	
	public void loadConfig() {
		createConfig();
		
		api.setForceLoginOnConnect(getJsonConfig().getBoolean("접속 시 강제 로그인"));
		api.setExcludeLoginIfOnlineMode(getJsonConfig().getBoolean("정품일 경우 로그인 제외"));
		api.setMaxAccountPerIp(getJsonConfig().getInt("아이피당 최대 계정 수"));
		api.setLoginTimeout(getJsonConfig().getInt("로그인 타임 아웃(초)"));
		api.setAllowNicknameRegex(getJsonConfig().getString("닉네임 허용 문자"));
		api.setExceptionCommands(getJsonConfig().getStringList("로그인 전 허용 명령어"));
		
		log("설정을 불러왔습니다.");
	}
	
}