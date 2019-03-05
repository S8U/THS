package su.plugin.channel.common.api.manager;

import java.util.HashMap;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import lombok.Getter;
import lombok.Setter;
import su.plugin.channel.common.api.object.ChannelGroup;

@Setter
@Getter
public class ScriptManager {
	
	private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
	
	private HashMap<ChannelGroup, ScriptEngine> scriptEngines = new HashMap<>();
	
	private HashMap<ChannelGroup, List<String>> scripts = new HashMap<>();
	
}