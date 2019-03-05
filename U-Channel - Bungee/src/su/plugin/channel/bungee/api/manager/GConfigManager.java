package su.plugin.channel.bungee.api.manager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.script.ScriptEngine;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import su.plugin.channel.bungee.GChannelPlugin;
import su.plugin.channel.bungee.api.GChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;
import su.plugin.core.common.api.util.StringUtil;

public class GConfigManager {
	
	private GChannelAPI api = GChannelPlugin.getApi();
	
	@Getter
	private JsonConfig channelConfig;
	@Getter
	private JsonConfig groupConfig;
	
	public GConfigManager() {
		channelConfig = new JsonConfig(new File(GChannelPlugin.getInstance().getDataFolder(), "channel-config.json"));
		groupConfig = new JsonConfig(new File(GChannelPlugin.getInstance().getDataFolder(), "group-config.json"));
	}
	
	public void createChannelConfig() {
		channelConfig.load();

		Set<String> serverNameKeySet = ProxyServer.getInstance().getServers().keySet();
		for(String key : channelConfig.getKeys("채널")) {
			if(!serverNameKeySet.contains(key.replace("@", "."))) {
				channelConfig.set("채널." + key, null);
			}
		}

		for(ServerInfo si : ProxyServer.getInstance().getServers().values()) {
			String name = si.getName().replace(".", "@");
			channelConfig.addDefault("채널." + name + ".표기", si.getName().toUpperCase().charAt(0));
			channelConfig.addDefault("채널." + name + ".그룹", "group1");
		}
		
		channelConfig.save();
	}
	
	public void loadChannelConfig() {
		createChannelConfig();
		
		for(String k : channelConfig.getKeys("채널")) {
			String name = k.replace("@", ".");

			Channel c = api.getChannelManager().getChannel(name);
			if(c == null) {
				c = new Channel(name);
			}
			
			c.setDisplayName(ChatColor.translateAlternateColorCodes('&', channelConfig.getString("채널." + k + ".표기")));
			c.setGroupName(channelConfig.getString("채널." + k + ".그룹"));
			
			api.getChannelManager().setChannel(c.getName(), c);
		}
		
		Core.log(api.getChannelManager().getChannels().size() + "개의 채널을 불러왔습니다.");
	}
	
	public void createGroupConfig() {
		if(!groupConfig.getFile().exists()) {
			groupConfig.addDefault("그룹.group1.표기", "그룹1");
			groupConfig.addDefault("그룹.group1.최적 채널 스크립트", Arrays.asList(
					"function getOptimizeChannel(currentChannel, channels) {",
					"return channels.get('name');",
					"}"
					));
			
			groupConfig.save();
		}
		
		groupConfig.load();
	}
	
	@SneakyThrows(Exception.class)
	public void loadGroupConfig() {
		createGroupConfig();
		
		for(String k : groupConfig.getKeys("그룹")) {
			ChannelGroup g = new ChannelGroup(k.replace("@", "."));
			
			g.setDisplayName(ChatColor.translateAlternateColorCodes('&', groupConfig.getString("그룹." + k + ".표기")));
			
			List<String> script = groupConfig.getStringList("그룹." + k + ".최적 채널 스크립트");
			api.getScriptManager().getScripts().put(g, script);

			ScriptEngine se = api.getScriptManager().getScriptEngineManager().getEngineByName("JavaScript");
			se.eval(StringUtil.connectString(script, " "));
			api.getScriptManager().getScriptEngines().put(g, se);

			api.getChannelGroupManager().setChannelGroup(g.getName(), g);
		}
		
		Core.log(api.getChannelGroupManager().getChannelGroups().size() + "개의 채널 그룹을 불러왔습니다.");
	}
	
}