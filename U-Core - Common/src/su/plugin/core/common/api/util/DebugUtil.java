package su.plugin.core.common.api.util;

import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.plugin.UPlugin;

public class DebugUtil {
	
	@Getter
	private static HashMap<String, Long> times = new HashMap<>();

	@Getter
	private static List<Integer> debugModes = new NotDuplicatedArrayList<>();

	public static void setDebugMode(int id, boolean toggle) {
		if(toggle) {
			debugModes.add(id);
		} else {
			debugModes.remove((Object) id);
		}
	}

	public static boolean isDebugMode(int id) {
		return debugModes.contains(id);
	}

	public static void log(Object obj) {
		if(debugModes.size() < 1) return;
		
		String className = Core.getLastClassName();
		
		UPlugin plugin = Core.getUPluginManager().getUPluginByPackage(className);
		for(int id : debugModes) {
			if(id == -1) {
				Core.nlog((plugin == null ? "[ Debug ] " : plugin.getColor() + "[ " + plugin.getName() + " | Debug ] §f") + obj);
			} else {
				UPlayer up = Core.getUPlayer(id);
				if(up == null) continue;

				up.nmsg((plugin == null ? "[ Debug ] " : plugin.getColor() + "[ " + plugin.getName() + " | Debug ] §f") + obj);
			}
		}
	}
	
	public static void printStackTraceElement() {
		if(debugModes.size() < 1) return;

		StackTraceElement[] ste = new Throwable().getStackTrace();
		for(int i = 0; i < ste.length; i++) {
			log(ste[i].getClassName());
		}
	}
	
	public static void startTimeMeasurement(boolean printLog) {
		if(debugModes.size() < 1) return;
		
		String className = Core.getLastClassName();
		if(printLog) {
			Core.log("시간 측정 시작: " + className);
		}
		times.put(className, System.currentTimeMillis());
	}
	
	public static void exitTimeMeasurement(boolean printLog) {
		if(debugModes.size() < 1) return;

		String className = Core.getLastClassName();
		if(printLog) {
			log("시간 측정 종료: " + (System.currentTimeMillis() - times.get(className)) + "ms : " + className);
		}
		times.remove(className);
	}
	
}