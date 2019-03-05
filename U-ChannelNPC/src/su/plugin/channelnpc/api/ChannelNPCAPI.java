package su.plugin.channelnpc.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import su.plugin.channelnpc.api.manager.ConfigManager;
import su.plugin.channelnpc.api.manager.NPCManager;

public class ChannelNPCAPI {
	
	@Setter
	@Getter
	private static List<String> NPCTexts = new ArrayList<>();
	
	@Getter
	private static NPCManager NPCManager;
	
	@Getter
	private static ConfigManager configManager;
	
	public static void init() {
		NPCManager = new NPCManager();
		configManager = new ConfigManager();
	}
	
}