package su.plugin.ability.api.event;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class WinEvent extends UKCancellableEvent {
	
	@Getter
	private final List<GamePlayer> players;
	
	@Setter
	@Getter
	private double winMoney;
	
	public WinEvent(List<GamePlayer> player, double winMoney) {
		this.players = player;
		this.winMoney = winMoney;
	}
	
}