package su.plugin.channel.common.api.manager;

import com.google.gson.Gson;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.script.ScriptEngine;
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.core.common.api.sql.SQLManagerBase;
import su.plugin.core.common.api.sql.SQLTable;
import su.plugin.core.common.api.util.StringUtil;

@Getter
public abstract class SQLManager extends SQLManagerBase {
	
	private SQLTable channelInfoTable, channelGroupTable;
	
	//
	
	private boolean load, upload;
	
	private int loadInterval;
	
	//

	public SQLManager() {
		// setSQLType(su.plugin.core.common.api.sql.SQLType.MySQL);
	}
	
	@Override
	public void createTable() {
		channelGroupTable = new SQLTable(this, "Channel_Group", "name varchar(255) primary key, display_name varchar(255), script text").createTable();
		channelInfoTable = new SQLTable(this, "Channel_Info", "name varchar(255) primary key, display_name varchar(255), group_name varchar(255), info_json text").createTable();
	}
	
	@Override
	public void createJsonConfigOthers() {
		getJsonConfig().addDefault("불러오기", true);
		getJsonConfig().addDefault("불러오기 주기(s)", 1);
		getJsonConfig().addDefault("채널 정보 업로드", true);
	}
	
	@Override
	public void loadJsonConfigOthers() {
		load = getJsonConfig().getBoolean("불러오기");
		loadInterval = getJsonConfig().getInt("불러오기 주기(s)");
		upload = getJsonConfig().getBoolean("채널 정보 업로드");
	}
	
	//
	
	public void saveChannel(Channel channel) {
		channelInfoTable.insertDuplicate(channel.getName(), channel.getDisplayName(), channel.getGroupName(), channel.saveToJson());
	}
	
	public void saveAllChannel() {
		for(Channel channel : ChannelAPI.getChannelManager().getChannels().values()) {
			saveChannel(channel);
		}
	}
	
	@SneakyThrows(SQLException.class)
	public Channel getChannel(String name) {
		@Cleanup PreparedStatement state = channelInfoTable.select("*", "where name ='" + name + "'");
		@Cleanup ResultSet result = state.executeQuery();
		
		if(!result.next()) return null;
		
		name = result.getString("name");
		
		Channel channel = ChannelAPI.getChannelManager().getChannel(name);
		if(channel == null) {
			channel = new Channel(name);
		}
		
		if(channel.getDisplayName() == null) {
			channel.setDisplayName(result.getString("display_name"));
		}
		if(channel.getGroupName() == null) {
			channel.setGroupName(result.getString("group_name"));
		}
		
		String json = result.getString("info_json");
		channel.loadFromJson(json);
		
		return channel;
	}
	
	@SneakyThrows(SQLException.class)
	public HashMap<String, Channel> getAllChannel() {
		@Cleanup PreparedStatement state = channelInfoTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		HashMap<String, Channel> channels = new HashMap<>();
		
		while(result.next()) {
			String name = result.getString("name");
			
			Channel channel = ChannelAPI.getChannelManager().getChannel(name);
			if(channel == null) {
				channel = new Channel(name);
			}
			
			if(channel.getDisplayName() == null) {
				channel.setDisplayName(result.getString("display_name"));
			}
			if(channel.getGroupName() == null) {
				channel.setGroupName(result.getString("group_name"));
			}
			
			String json = result.getString("info_json");
			channel.loadFromJson(json);
			
			channels.put(name, channel);
		}
		
		return channels;
	}
	
	public void loadChannel(String name) {
		Channel channel = getChannel(name);
		if(channel == null) return;
		
		ChannelAPI.getChannelManager().setChannel(name, channel);
		
		onChannelLoaded(channel);
	}
	
	protected abstract void onChannelLoaded(Channel channel);
	
	public void loadAllChannel() {
		HashMap<String, Channel> channels = getAllChannel();
		
		channels.forEach((name, channel) -> ChannelAPI.getChannelManager().setChannel(name, channel));
		
		onAllChannelLoaded(channels);
	}
	
	protected abstract void onAllChannelLoaded(HashMap<String, Channel> channels);
	
	//
	
	public void saveChannelGroup(ChannelGroup channelGroup) {
		channelGroupTable.insertDuplicate(channelGroup.getName(), channelGroup.getDisplayName(), new Gson().toJson(channelGroup.getScript()));
	}
	
	public void saveAllChannelGroup() {
		for(ChannelGroup group : ChannelAPI.getChannelGroupManager().getChannelGroups().values()) {
			saveChannelGroup(group);
		}
	}
	
	@SneakyThrows(Exception.class)
	public void loadAllChannelGroup() {
		@Cleanup PreparedStatement state = channelGroupTable.select("*");
		@Cleanup ResultSet result = state.executeQuery();
		
		while(result.next()) {
			String name = result.getString("name");
			
			ChannelGroup channelGroup = ChannelAPI.getChannelGroupManager().getChannelGroup(name);
			if(channelGroup == null) {
				channelGroup = new ChannelGroup(name);
			}
			
			channelGroup.setDisplayName(result.getString("display_name"));
			
			List<String> script = new Gson().fromJson(result.getString("script"), List.class);
			ChannelAPI.getScriptManager().getScripts().put(channelGroup, script);

			ScriptEngine se = ChannelAPI.getScriptManager().getScriptEngineManager().getEngineByName("JavaScript");
			se.eval(StringUtil.connectString(script, " "));
			ChannelAPI.getScriptManager().getScriptEngines().put(channelGroup, se);
			
			ChannelAPI.getChannelGroupManager().setChannelGroup(name, channelGroup);
		}
	}
	
	
}