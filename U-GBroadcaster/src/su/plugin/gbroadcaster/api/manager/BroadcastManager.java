package su.plugin.gbroadcaster.api.manager;

import java.util.HashMap;

import lombok.Getter;
import su.plugin.gbroadcaster.api.object.BroadcastData;

public class BroadcastManager {
	
	@Getter
	private HashMap<String, BroadcastData> broadCastDatas = new HashMap<>();
	
	public void setBroadCastData(String channel, BroadcastData data) {
		broadCastDatas.put(channel.toLowerCase(), data);
	}
	
	public BroadcastData getBroadCastData(String channel) {
		return broadCastDatas.get(channel.toLowerCase());
	}
	
	public boolean existsBroadCastData(String channel) {
		return broadCastDatas.containsKey(channel.toLowerCase());
	}
	
	public void startAllTasks() {
		broadCastDatas.values().stream().filter(bd -> bd.isUse()).forEach(bd -> bd.startTask());
	}
	
	public void stopAllTasks() {
		broadCastDatas.forEach((s, data) -> data.getTask().cancel());
	}
	
}