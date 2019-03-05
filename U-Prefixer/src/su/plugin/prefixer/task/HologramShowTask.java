package su.plugin.prefixer.task;

import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class HologramShowTask extends UKRunnable {
	
	private PrefixerAPI api = PrefixerPlugin.getApi();
	
	public HologramShowTask() {
		super(PrefixerPlugin.getInstance());
	}

	@Override
	public void run() {
		for(PlayerKey pk : api.getHologramManager().getHolograms().keySet()) {
			PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(pk);
			if(!pp.isOnline() || pp.getHologram().getVisibilityManager().isVisibleTo(pp.getBukkitPlayer()) || !api.getHologramManager().hasMoveTime(pk) || System.currentTimeMillis() - api.getHologramManager().getMoveTime(pk) < api.getHologramShowInterval() || pp.getBukkitPlayer().isSneaking()) continue;

			pp.getHologram().teleport(pp.getMainPrefixLocation());
			pp.getHologram().getVisibilityManager().showTo(pp.getBukkitPlayer());
		}
	}
	
}