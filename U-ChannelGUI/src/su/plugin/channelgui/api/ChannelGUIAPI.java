package su.plugin.channelgui.api;

import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
import su.plugin.channelgui.api.manager.ConfigManager;
import su.plugin.channelgui.api.manager.GUIManager;

public class ChannelGUIAPI {

	@Setter
	@Getter
	private static String offlineItemCode,
			onlineMessage, offlineMessage;

	@Setter
	@Getter
	private static String uabilityOfflineItemCode, uabilityOfflineMessage;

	@Getter
	private static HashMap<Integer, String> uabilityOnlineItemCode = new HashMap<>();

	@Getter
	private static HashMap<Integer, String> uabilityOnlineMessage = new HashMap<>();

	@Getter
	private static GUIManager GUIManager;
	@Getter
	private static ConfigManager configManager;

	public void init() {
		GUIManager = new GUIManager();
		configManager = new ConfigManager();
	}
	
}