package su.plugin.ability.api.object;

import java.util.List;
import org.bukkit.entity.Player;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.bukkit.api.bossbar.BossBar;

public class ABossBar extends BossBar {

	@Override
	public void addAllPlayers() {
		if(!AbilityAPI.isUseBossBar()) return;
		super.addAllPlayers();
	}
	
	@Override
	public void addPlayers(List<Player> players) {
		if(!AbilityAPI.isUseBossBar()) return;
		super.addPlayers(players);
	}
	
	@Override
	public void addPlayers(Player... players) {
		if(!AbilityAPI.isUseBossBar()) return;
		super.addPlayers(players);
	}
	
	@Override
	public void clearBar() {
		if(!AbilityAPI.isUseBossBar()) return;
		super.clearBar();
	}
	
	/*@Override
	public void setBarColor(BarColor color) {
		if(!AbilityAPI.isUseBossBar()) return;
		super.setBarColor(color);
	}
	
	@Override
	public void setBarStyle(BarStyle style) {
		if(!AbilityAPI.isUseBossBar()) return;
		super.setBarStyle(style);
	}*/
	
	@Override
	public void setProgress(double progress) {
		if(!AbilityAPI.isUseBossBar()) return;
		super.setProgress(progress);
	}
	
	@Override
	public void setText(String text) {
		if(!AbilityAPI.isUseBossBar()) return;
		super.setText(text);
	}
	
}