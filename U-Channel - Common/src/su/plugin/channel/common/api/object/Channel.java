package su.plugin.channel.common.api.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import su.plugin.channel.common.api.ChannelAPI;

@ToString
@RequiredArgsConstructor
@Setter
@Getter
public class Channel {
	
	private final String name;
	
	private String displayName, groupName;
	
	private boolean online;
	
	private int playerCount, maxPlayerCount;
	
	private List<String> playerList = new ArrayList<>();
	
	private LinkedTreeMap<String, Object> ETCs = new LinkedTreeMap<>();

	public ChannelGroup getGroup() {
		return ChannelAPI.getChannelGroupManager().getChannelGroup(groupName);
	}

	public boolean hasPlayer(String playerName) {
		for(String pn : playerList) {
			if(pn.equalsIgnoreCase(playerName)) return true;
		}
		
		return false;
	}
	
	public void setETC(String key, Object value) {
		ETCs.put(key, value);
	}
	
	public boolean existsETC(String key) {
		return ETCs.containsKey(key);
	}
	
	public Object getETC(String key) {
		return ETCs.get(key);
	}
	
	public String saveToJson() {
		HashMap<String, Object> values = new HashMap<>();
		
		values.put("online", online);
		values.put("player_count", playerCount);
		values.put("max_player_count", maxPlayerCount);
		values.put("player_list", playerList);
		values.put("etc", ETCs);
		
		return new Gson().toJson(values);
	}
	
	public void loadFromJson(String json) {
		HashMap<String, Object> values = new Gson().fromJson(json, HashMap.class);
		
		online = (boolean) values.get("online");
		playerCount = (int) (double) values.get("player_count");
		maxPlayerCount = (int) (double) values.get("max_player_count");
		playerList = (List<String>) values.get("player_list");
		ETCs = (LinkedTreeMap<String, Object>) values.get("etc");
	}
	
	public boolean sendToChannel(String name) {
		return ChannelAPI.getPlatformProvider().sendToChannel(this, name);
	}
	
}