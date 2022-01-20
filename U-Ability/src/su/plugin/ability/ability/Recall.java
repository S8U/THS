package su.plugin.ability.ability;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.category.AbilityRank;
import su.plugin.ability.api.category.AbilityType;
import su.plugin.ability.api.category.ClickType;
import su.plugin.ability.api.object.Ability;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

@Setter
@Getter
public class Recall extends Ability implements Listener {
	
	public static List<PlayerKey> using = new NotDuplicatedArrayList<>();
	
	private RecallTask recallTask;
	private RecordTask recordTask;
	private List<Location> locations = new ArrayList<>();
	private List<Double> health = new ArrayList<>();
	
	public Recall() {
		initAbility("시간 역행", AbilityType.ACTIVE, AbilityRank.S,
				"시간을 역행하여 3초 전의 체력 및 위치로 되돌아갑니다.",
				"시간 역행 중에는 데미지를 받지 않습니다.");
		setCoolTime(60);
		
		registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
		registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
	}
	
	@Override
	public void onAssign() {
		recordTask = new RecordTask(this);
		recordTask.runTaskTimer(0, 1);
	}
	
	@Override
	public void onResign() {
		if(recallTask != null) {
			recallTask.cancel();
			recallTask = null;
		}
		
		if(recordTask != null) {
			recordTask.cancel();
			recordTask = null;
		}
	}
	
	@Override
	public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
		if(isUsing(getPlayerKey())) return;
		api.playSound(getPlayer(), Sound.PORTAL_TRAVEL, 1, 1);

		playFireworkEffect(getPlayer().getLocation());

		getGamePlayer().getKPlayer().hidePlayer();

		setUse(getPlayerKey(), true);

		recallTask = new RecallTask(this);
		recallTask.runTaskTimer(1, 1);
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		if(getPlayer() == null || !e.getEntity().equals(getPlayer()) || !isUsing(getPlayerKey())) return;
		e.setCancelled(true);
	}
	
	@EventHandler
	public void onHealthRegain(EntityRegainHealthEvent e) {
		if(getPlayer() == null || !e.getEntity().equals(getPlayer()) || !isUsing(getPlayerKey())) return;
		e.setCancelled(true);
	}
	
	//

	public void setUse(PlayerKey playerKey, boolean toggle) {
		if(toggle) {
			using.add(playerKey);
		} else {
			using.remove(playerKey);
		}
	}
	
	public boolean isUsing(PlayerKey playerKey) {
		return using.contains(playerKey);
	}
	
	private void playFireworkEffect(Location location) {
		KCore.playFireworkEffect(location, false, false, Type.BALL, Color.RED, Color.RED, 1);
	}
	
class RecordTask extends UKRunnable {
	
	private Recall ability = null;
	
	public RecordTask(Recall ability) {
		super(AbilityPlugin.getInstance());
		this.ability = ability;
	}
	
	@Override
	public void run() {
		if(ability.getPlayer() == null || ability.isUsing(ability.getPlayerKey())) return;
		if(ability.getLocations().size() > 60) {
			ability.getLocations().remove(0);
			ability.getHealth().remove(0);
		}
		ability.getLocations().add(ability.getPlayer().getLocation());
		ability.getHealth().add(ability.getPlayer().getHealth());
	}
	
}

class RecallTask extends UKRunnable {
	
	private Recall ability = null;
	int i;
	
	public RecallTask(Recall ability) {
		super(AbilityPlugin.getInstance());
		this.ability = ability;
		i = ability.getLocations().size() - 1;
	}
	
	public void run() {
		ability.getPlayer().teleport(ability.getLocations().get(i));
		ability.getPlayer().setHealth(ability.getHealth().get(i));
		i = i - 2;
		if(i < 1) {
			setUse(ability.getPlayerKey(), false);

			ability.playFireworkEffect(ability.getPlayer().getLocation());

			ability.getGamePlayer().getKPlayer().hidePlayer();

			cancel();
		}
	}
	
}

}