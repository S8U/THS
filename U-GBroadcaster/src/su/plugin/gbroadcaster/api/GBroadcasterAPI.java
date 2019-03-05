package su.plugin.gbroadcaster.api;

import lombok.Getter;
import lombok.Setter;
import su.plugin.gbroadcaster.api.manager.BroadcastManager;

public class GBroadcasterAPI {

	@Setter
	@Getter
	private static String prefix;

	@Getter
	private static BroadcastManager broadcastManager;

	public void init() {
		broadcastManager = new BroadcastManager();
	}
	
}