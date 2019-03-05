package su.plugin.channelgui.api.manager;

import java.util.HashMap;

import lombok.Getter;
import su.plugin.channelgui.api.object.ChannelGUI;

public class GUIManager {
	
	@Getter
	private HashMap<String, ChannelGUI> GUIs = new HashMap<>();
	
	public void setGUI(String name, ChannelGUI GUI) {
		GUIs.put(name.toLowerCase(), GUI);
	}
	
	public boolean existsGUI(String name) {
		return GUIs.containsKey(name.toLowerCase());
	}
	
	public ChannelGUI getGUI(String name) {
		if(!existsGUI(name)) return null;
		return GUIs.get(name.toLowerCase());
	}
	
	/*public ChannelGUI getGUITitleOf(String title) {
		for(ChannelGUI menu : GUIs.values()) {
			if(menu.getName().equals(title)) return menu;
		}
		return null;
	}*/

}
