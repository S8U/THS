package su.plugin.core.common.api.option;

import java.util.HashMap;

import lombok.Getter;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.c.option.UPlayerOptionChangeEvent;
import su.plugin.core.common.api.event.c.option.UPlayerOptionDeleteEvent;
import su.plugin.core.common.api.event.c.option.UServerOptionChangeEvent;
import su.plugin.core.common.api.event.c.option.UServerOptionDeleteEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

@Getter
public class OptionManager {
	
	private HashMap<PlayerKey, HashMap<String, Object>> playerOptions = new HashMap<>();
	
	private HashMap<String, Object> serverOptions = new HashMap<>();
	
	//
	
	public boolean setPlayerOption(PlayerKey playerKey, String optionName, Object value) {
		return setPlayerOption(playerKey, optionName, value, true);
	}
	
	public boolean setPlayerOption(PlayerKey playerKey, String optionName, Object value, boolean event) {
		HashMap<String, Object> options = getPlayerOptions(playerKey);
		if(options == null) {
			options = new HashMap<>();
		}
		
		options.put(optionName, value);
		playerOptions.put(playerKey, options);
		
		if(event) {
			Core.getUEventManager().callEvent(new UPlayerOptionChangeEvent(Core.getUPlayer(playerKey), optionName, value));
		}
		
		return true;
	}
	
	public boolean existsPlayerOption(PlayerKey playerKey, String optionName) {
		return getPlayerOption(playerKey, optionName) != null;
	}
	
	public HashMap<String, Object> getPlayerOptions(PlayerKey playerKey) {
		return playerOptions.get(playerKey);
	}
	
	public Object getPlayerOption(PlayerKey playerKey, String optionName) {
		if(!playerOptions.containsKey(playerKey)) return null;

		HashMap<String, Object> options = getPlayerOptions(playerKey);
		for(String name : options.keySet()) {
			if(name.equalsIgnoreCase(optionName)) return options.get(name);
		}
		
		return null;
	}
	
	public void setPlayerOptions(PlayerKey playerKey, HashMap<String, Object> options) {
		setPlayerOptions(playerKey, options, true);
	}
	
	public void setPlayerOptions(PlayerKey playerKey, HashMap<String, Object> options, boolean event) {
		playerOptions.put(playerKey, options);
		
		if(event) {
			UPlayer player = Core.getUPlayer(playerKey);
			options.forEach((name, value) -> {
				Core.getUEventManager().callEvent(new UPlayerOptionChangeEvent(player, name, value));
			});
		}
	}
	
	public void deletePlayerOptions(PlayerKey playerKey) {
		deletePlayerOptions(playerKey, true);
	}
	
	public void deletePlayerOptions(PlayerKey playerKey, boolean event) {
		playerOptions.remove(playerKey);
		
		if(event) {
			UPlayer player = Core.getUPlayer(playerKey);
			getPlayerOptions(playerKey).forEach((name, value) -> {
				Core.getUEventManager().callEvent(new UPlayerOptionDeleteEvent(player, name));
			});
		}
	}
	
	public boolean deletePlayerOption(PlayerKey playerKey, String optionName) {
		return deletePlayerOption(playerKey, optionName, true);
	}
	
	public boolean deletePlayerOption(PlayerKey playerKey, String optionName, boolean event) {
		HashMap<String, Object> options = getPlayerOptions(playerKey);
		if(options == null) return false;
		
		if(event) {
			Core.getUEventManager().callEvent(new UPlayerOptionDeleteEvent(Core.getUPlayer(playerKey), optionName));
		}
		
		options.remove(optionName);
		return true;
	}
	
	//
	
	public void setServerOption(String optionName, Object value) {
		setServerOption(optionName, value, true);
	}
	
	public void setServerOption(String optionName, Object value, boolean event) {
		if(value == null) {
			deleteServerOption(optionName);
			return;
		}
		
		serverOptions.put(optionName, value);
		
		if(event) {
			Core.getUEventManager().callEvent(new UServerOptionChangeEvent(optionName, value));
		}
	}
	
	public boolean existsServerOption(String optionName) {
		for(String name : serverOptions.keySet()) {
			if(name.equalsIgnoreCase(optionName)) return true;
		}
		
		return false;
	}
	
	public Object getServerOption(String optionName) {
		for(String name : serverOptions.keySet()) {
			if(name.equalsIgnoreCase(optionName)) return serverOptions.get(name);
		}
		
		return null;
	}
	
	public boolean deleteServerOption(String optionName) {
		return deleteServerOption(optionName, true);
	}
	
	public boolean deleteServerOption(String optionName, boolean event) {
		for(String name : serverOptions.keySet()) {
			if(!name.equalsIgnoreCase(optionName)) continue;
			
			serverOptions.remove(optionName);
			
			if(event) {
				Core.getUEventManager().callEvent(new UServerOptionDeleteEvent(optionName));
			}
			
			return true;
		}
		
		return false;
	}
	
	public void setServerOptions(HashMap<String, Object> options) {
		setServerOptions(options, true);
	}
	
	public void setServerOptions(HashMap<String, Object> options, boolean event) {
		serverOptions = options;
		
		if(event) {
			options.forEach((name, value) -> {
				Core.getUEventManager().callEvent(new UServerOptionChangeEvent(name, value));
			});
		}
	}
	
}