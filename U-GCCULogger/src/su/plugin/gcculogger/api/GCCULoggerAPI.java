package su.plugin.gcculogger.api;

import lombok.Getter;
import lombok.Setter;
import su.plugin.gcculogger.api.manager.SQLManager;

public class GCCULoggerAPI {
	
	@Setter
	@Getter
	private static int maxCCU;

	@Getter
	private SQLManager SQLManager;
	
	public void init() {
		SQLManager = new SQLManager();
	}
	
}