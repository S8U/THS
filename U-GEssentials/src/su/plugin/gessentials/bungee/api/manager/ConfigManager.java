package su.plugin.gessentials.bungee.api.manager;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.ChatHandlingLocation;
import su.plugin.gessentials.bungee.api.category.ListeningChannel;
import su.plugin.gessentials.bungee.api.category.WarningEventType;
import su.plugin.gessentials.bungee.api.object.EChannel;
import su.plugin.gessentials.bungee.api.object.WarningEvent;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;

public class ConfigManager {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@Getter
	private File configFile = new File(GGEssentialsPlugin.getInstance().getDataFolder(), "config.json");
	@Getter
	private File channelConfigFile = new File(GGEssentialsPlugin.getInstance().getDataFolder(), "channel-config.json");
	@Getter
	private File chatFilterConfigFile = new File(GGEssentialsPlugin.getInstance().getDataFolder(), "chatfilter-config.json");
	
	@Getter
	private JsonConfig config = new JsonConfig(configFile).load();
	@Getter
	private JsonConfig channelConfig = new JsonConfig(channelConfigFile).load();
	@Getter
	private JsonConfig chatFilterConfig = new JsonConfig(chatFilterConfigFile).load();
	
	//
	
	public void createConfigFile() {
		if(configFile.exists()) return;
		
		config.addDefault("강제 퇴장 서버 표기", "&f마인크래프트 서버");
		config.addDefault("기본 채팅 처리 위치", "번지코드");
		
		config.addDefault("로비.사용", false);
		config.addDefault("로비.그룹 이름", "lobby");
		config.addDefault("로비.접속 시 로비로 이동", true);
		
		config.save();
	}
	
	public void createChannelConfigFile() {
		if(channelConfigFile.exists()) return;
		
		channelConfig.createFile();
	}
	
	public void createChatFilterConfigFile() {
		if(chatFilterConfigFile.exists()) return;
		
		chatFilterConfig.addDefault("채팅 필터.사용", false);
		chatFilterConfig.addDefault("채팅 필터.닉네임이 포함된 단어 필터링 무시", true);
		chatFilterConfig.addDefault("채팅 필터.단어", Arrays.asList("야자"));
		
		chatFilterConfig.addDefault("경고.사용", false);
		chatFilterConfig.addDefault("경고.처리자 표기", "U-GEssentials");
		chatFilterConfig.addDefault("경고.메시지", "&c금지어를 사용하여 경고 {count}회를 받았습니다. (누적 경고: {total_count}회)");
		chatFilterConfig.addDefault("경고.이벤트", Arrays.asList(
				"3 timemute d:1 경고 누적",
				"6 kick 경고가 누적되어 강제 퇴장되었습니다.",
				"9 tempban d:1 경고가 누적되어 서버에서 차단되었습니다.",
				"12 ban 경고가 누적되어 서버에서 차단되었습니다.",
				"15 tempipban d:1 경고가 누적되어 서버에서 아이피가 차단되었습니다.",
				"20 ipban 경고가 누적되어 서버에서 아이피가 차단되었습니다."));
		
		chatFilterConfig.save();
	}
	
	//
	
	public void loadConfig() {
		createConfigFile();
		
		api.setKickServerMark(ChatColor.translateAlternateColorCodes('&', config.getString("강제 퇴장 서버 표기")));
		
		api.setUseLobby(config.getBoolean("로비.사용"));
		api.getChannelManager().setLobbyGroupName(config.getString("로비.그룹 이름"));
		api.setSendToLobbyOnConnect(config.getBoolean("로비.접속 시 로비로 이동"));
	}
	
	public void loadChannelConfig() {
		createChannelConfigFile();
		
		api.getChannelManager().getChannels().clear();
		
		HashMap<String, String> chatHandlingLocations = new HashMap<>();

		api.getChannelManager().getChannels().clear();
		for(String channelName : ProxyServer.getInstance().getServers().keySet()) {
			channelConfig.addDefault(channelName + ".듣기 채널", "채널");
			channelConfig.addDefault(channelName + ".채팅 양식", "&7[{channel_displayname}] &f{prefixer_prefix}&f{permission_prefix}&f{name}&f : {message}");
			channelConfig.addDefault(channelName + ".채팅 처리 위치", "번지코드");
			
			EChannel channel = new EChannel(channelName);
			
			String lcs = channelConfig.getString(channelName + ".듣기 채널");
			channel.setListeningChannel(lcs != null && lcs.equalsIgnoreCase("전체") ? ListeningChannel.GLOBAL : ListeningChannel.LOCAL);
			
			String chatForm = ChatColor.translateAlternateColorCodes('&', channelConfig.getString(channelName + ".채팅 양식"));
			channel.setChatForm(chatForm == null ? "§7[{channel_displayname}] §f{prefixer_prefix}§f{permission_prefix}§f{name}§f : {message}" : chatForm);
			
			String chatHandling = channelConfig.getString(channelName + ".채팅 처리 위치");
			channel.setChatHandlingLocation(chatHandling != null ? (chatHandling.equalsIgnoreCase("번지코드") ? ChatHandlingLocation.BUNGEECORD : ChatHandlingLocation.BUKKIT) : null);
			chatHandlingLocations.put(channelName, chatHandling);
			
			api.getChannelManager().setEChannel(channelName, channel);
		}
		
		Core.getOptionSQLManager().setServerOption("gessentials_chat_handling_location", chatHandlingLocations);
		
		channelConfig.save();
		
		Core.log("채널 설정을 불러왔습니다.");
	}
	
	public void loadChatFilterConfig() {
		createChatFilterConfigFile();
		
		api.setUseChatFilter(chatFilterConfig.getBoolean("채팅 필터.사용"));
		api.setIgnoreFilterWordWithNames(chatFilterConfig.getBoolean("채팅 필터.닉네임이 포함된 단어 필터링 무시"));

		api.getChatManager().getBanWords().clear();
		for(String str : chatFilterConfig.getStringList("채팅 필터.단어")) {
			String word = str.contains(" ") ? str.split(" ")[0] : str;
			int warningCount = str.contains(" ") ? Integer.parseInt(str.split(" ")[1]) : 1;
			
			api.getChatManager().getBanWords().put(word, warningCount);
		}
		
		api.setUseWarning(chatFilterConfig.getBoolean("경고.사용"));
		api.getWarningManager().setWarningDisplayName(chatFilterConfig.getString("경고.처리자 표기"));
		api.getWarningManager().setWarningMessage(ChatColor.translateAlternateColorCodes('&', chatFilterConfig.getString("경고.메시지")));
		
		api.getWarningManager().getWarningEvents().clear();
		for(String event : chatFilterConfig.getStringList("경고.이벤트")) {
			String tasks[] = event.split(" ");
			int count = Integer.parseInt(tasks[0]);
			String task = tasks[1];
			WarningEvent e = new WarningEvent();
			if(task.equalsIgnoreCase("kick")) {
				e.setType(WarningEventType.KICK);
				StringBuilder sb = new StringBuilder();
				for(int i = 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setReason(sb.toString());
			} else if(task.equalsIgnoreCase("ban")) {
				e.setType(WarningEventType.BAN);
				StringBuilder sb = new StringBuilder();
				for(int i = 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setReason(sb.toString());
			} else if(task.equalsIgnoreCase("tempban") || task.equalsIgnoreCase("timeban")) {
				int d = 0, h = 0, m = 0, s = 0, l = 0;
				for(int i = 2; i < 6; i++) {
					if(tasks.length < i + 1 || tasks[i].length() < 3)  break;
					if(tasks[i].substring(0, 2).equalsIgnoreCase("d:")) {
						d = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("h:")) {
						h = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("m:")) {
						m = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("s:")) {
						s = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					}
				}
				StringBuilder sb = new StringBuilder();
				for(int i = l + 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setDuration((d * 86400 + h * 3600 + m * 60 + s) * 1000);
				e.setReason(ChatColor.translateAlternateColorCodes('&', sb.toString()));
				e.setType(WarningEventType.TIME_BAN);
			} else if(task.equalsIgnoreCase("ipban")) {
				e.setType(WarningEventType.IP_BAN);
				StringBuilder sb = new StringBuilder();
				for(int i = 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setReason(sb.toString());
			} else if(task.equalsIgnoreCase("tempipban") || task.equalsIgnoreCase("timeipban")) {
				int d = 0, h = 0, m = 0, s = 0, l = 0;
				for(int i = 2; i < 6; i++) {
					if(tasks.length < i + 1 || tasks[i].length() < 3)  break;
					if(tasks[i].substring(0, 2).equalsIgnoreCase("d:")) {
						d = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("h:")) {
						h = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("m:")) {
						m = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("s:")) {
						s = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					}
				}
				StringBuilder sb = new StringBuilder();
				for(int i = l + 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setDuration((d * 86400 + h * 3600 + m * 60 + s) * 1000);
				e.setReason(ChatColor.translateAlternateColorCodes('&', sb.toString()));
				e.setType(WarningEventType.TIME_IP_BAN);
			} else if(task.equalsIgnoreCase("mute")) {
				e.setType(WarningEventType.MUTE);
				StringBuilder sb = new StringBuilder();
				for(int i = 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setReason(sb.toString());
			} else if(task.equalsIgnoreCase("tempmute") || task.equalsIgnoreCase("timemute")) {
				int d = 0, h = 0, m = 0, s = 0, l = 0;
				for(int i = 2; i < 6; i++) {
					if(tasks.length < i + 1 || tasks[i].length() < 3)  break;
					if(tasks[i].substring(0, 2).equalsIgnoreCase("d:")) {
						d = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("h:")) {
						h = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("m:")) {
						m = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					} else if(tasks[i].substring(0, 2).equalsIgnoreCase("s:")) {
						s = Integer.parseInt(tasks[i].substring(2, tasks[i].length())); l++;
					}
				}
				StringBuilder sb = new StringBuilder();
				for(int i = l + 2; i < tasks.length; i++) {
					if(sb.length() < 1) {
						sb.append(tasks[i]);
					} else {
						sb.append(" " + tasks[i]);
					}
				}
				e.setDuration((d * 86400 + h * 3600 + m * 60 + s) * 1000);
				e.setReason(ChatColor.translateAlternateColorCodes('&', sb.toString()));
				e.setType(WarningEventType.TIME_MUTE);
			}
			api.getWarningManager().setWarningEvent(count, e);
		}

		Core.log("필터 설정을 불러왔습니다.");
	}
	
	public void loadOption() {
		Object muteAll = Core.getOptionSQLManager().getServerOption("gessentials_mute_all");
		if(muteAll != null) {
			api.getChatManager().setMuteAll((boolean) muteAll);
		}
		
		Core.log("서버 옵션을 불러왔습니다.");
	}
	
}