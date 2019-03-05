package su.plugin.core.bukkit.api.event.gui;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.QuickBar;

@Getter
public class IconClickEvent extends UKEvent {
	
	private boolean pickUpCancel = true;
	
	private final GUIClickEvent GUIClickEvent;
	private final QuickBarClickEvent quickBarClickEvent;
	
	public IconClickEvent(GUIClickEvent GUIClickEvent, QuickBarClickEvent quickBarClickEvent) {
		this.GUIClickEvent = GUIClickEvent;
		this.quickBarClickEvent = quickBarClickEvent;
		
		if(!isGUIClick()) return;
		pickUpCancel = GUIClickEvent.isPickUpCancel();
	}
	
	public Player getPlayer() {
		return isGUIClick() ? GUIClickEvent.getPlayer() : quickBarClickEvent.getPlayer();
	}
	
	public boolean isGUIClick() {
		return GUIClickEvent != null;
	}
	
	public boolean isQuickBarClick() {
		return quickBarClickEvent != null;
	}
	
	public GUI getGUI() {
		return GUIClickEvent.getGUI();
	}
	
	public QuickBar getQuickBar() {
		return quickBarClickEvent.getQuickBar();
	}
	
	public Icon getIcon() {
		return isGUIClick() ? GUIClickEvent.getClickedIcon() : quickBarClickEvent.getClickedIcon();
	}
	
}