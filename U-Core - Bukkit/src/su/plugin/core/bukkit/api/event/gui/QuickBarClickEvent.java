package su.plugin.core.bukkit.api.event.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.ClickAction;
import su.plugin.core.bukkit.api.event.UKEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.QuickBar;

@Getter
public class QuickBarClickEvent extends UKEvent {
	
	private final Player player;
	
	private final ClickAction clickAction;
	
	private QuickBar quickBar;
	
	private PlayerInteractEvent playerInteractEvent;
	
	public QuickBarClickEvent(PlayerInteractEvent playerInteractEvent) {
		this.playerInteractEvent = playerInteractEvent;
		player = playerInteractEvent.getPlayer();
		clickAction = (playerInteractEvent.getAction() == Action.LEFT_CLICK_AIR || playerInteractEvent.getAction() == Action.LEFT_CLICK_BLOCK) ? ClickAction.LEFT_CLICK : ClickAction.RIGHT_CLICK;
		quickBar = KCore.getGUIManager().getQuickBar(player);
	}
	
	public ItemStack getClickedItem() {
		return player.getInventory().getItemInHand();
	}
	
	public Icon getClickedIcon() {
		return quickBar.getIcon(getClickedX());
	}
	
	public int getClickedX() {
		return  player.getInventory().getHeldItemSlot() + 1;
	}
	
	public boolean isIconClicked() {
		return getClickedIcon() != null;
	}
	
}