package su.plugin.ability.api.event;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import su.plugin.ability.api.category.KillType;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

public class KillEvent extends UKCancellableEvent {
	
	@Setter
	@Getter
	private boolean cancelled = false;
	
	@Getter
	private boolean firstBlood;
	
	@Getter
	private final Player killer, deathPlayer;
	
	@Setter
	@Getter
	private double killMoney, regularKillMoney, firstBloodMoney, assistMoney;
	
	@Getter
	private KillType killType;
	
	@Getter
	private List<GamePlayer> assists;
	
	@Getter
	private final PlayerDeathEvent playerDeathEvent;
	
	public KillEvent(Player killer, Player deathPlayer, List<GamePlayer> assists, boolean firstBlood, double killMoney, double regularKillMoney,
			double firstBloodMoney, double assistMoney, KillType killType, PlayerDeathEvent playerDeathEvent) {
		this.firstBlood = firstBlood;
		this.killer = killer;
		this.deathPlayer = deathPlayer;
		this.assists = assists;
		this.killMoney = killMoney;
		this.regularKillMoney = regularKillMoney;
		this.firstBloodMoney= firstBloodMoney;
		this.assistMoney = assistMoney;
		this.killType = killType;
		this.playerDeathEvent = playerDeathEvent;
	}
	
}