package su.plugin.core.bukkit.api.player;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.KCorePlugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.task.PluginMessageTask;
import su.plugin.core.bukkit.api.util.BungeeUtil;
import su.plugin.core.bukkit.api.util.KReflectionUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

@Getter
public class KPlayer extends UPlayer {
	
	private final PlayerKey playerKey;
	
	private final Player player;

	@Setter
	private LivingEntity lastHit;

	private boolean hide;
	
	public KPlayer(PlayerKey playerKey, Player player) {
		this.playerKey = playerKey;
		this.player = player;
		
		name = player.getName();
	}
	
	@Override
	public void setDisplayName(String displayName, boolean sql) {
		this.displayName = displayName;

		if(isOnline()) {
			player.setDisplayName(displayName);
		}

		if(sql) {
			BungeeUtil.sendMessageToBungeeCord(KCorePlugin.getInstance(), "ucore:main", "SetDisplayName", name, displayName);

			if(playerKey.getName().equals(displayName)) {
				Core.getSQLManager().deleteDisplayName(playerKey);
			} else {
				Core.getSQLManager().setDisplayName(playerKey, displayName);
			}
		}

		this.displayName = this.displayName.equalsIgnoreCase(getPlayerKey().getName()) ? null : displayName;
	}
	
	@Override
	public boolean isConsole() {
		return false;
	}
	
	@Override
	public boolean hasPermission(String node) {
		return player.hasPermission(node);
	}
	
	@Override
	public Player getPlatformSender() {
		return player;
	}
	
	@Override
	public String getIp() {
		return player.getAddress().getAddress().getHostAddress();
	}
	
	@Override
	public boolean isOnline() {
		return player != null && player.isOnline();
	}

	@Override
	public void kickPlayer(String message) {
		player.kickPlayer(message);
	}
	
	@Override
	public void sendPluginMessage(String channel, byte... data) {
		new PluginMessageTask(KCorePlugin.getInstance(), getPlatformSender(), channel, data);
	}

	//

	public boolean hidePlayer() {
		if(hide || player == null || !isOnline()) return false;

		Bukkit.getScheduler().runTask(KCorePlugin.getInstance(), () -> {
			for(Player ap : KCore.getOnlinePlayers()) {
				if(player.equals(ap)) continue;

				ap.hidePlayer(player);
			}
		});

		return hide = true;
	}

	public boolean showPlayer() {
		if(!hide || player == null || !isOnline()) return false;

		Bukkit.getScheduler().runTask(KCorePlugin.getInstance(), () -> {
			for(Player ap : KCore.getOnlinePlayers()) {
				if(player.equals(ap)) continue;

				ap.showPlayer(player);
			}
		});

		return hide = false;
	}

	@SneakyThrows (Exception.class)
	public void setAbsorptionHearts(float health) {
		/*Object entityPlayer = KReflectionUtil.getHandle(getPlayer());
		ReflectionUtil.getMethod(entityPlayer.getClass(), "setAbsorptionHearts").invoke(entityPlayer, health);*/

		((CraftPlayer) getPlayer()).getHandle().setAbsorptionHearts(health);
	}

	@SneakyThrows (Exception.class)
	public float getAbsorptionHearts() {
		/*Object entityPlayer = KReflectionUtil.getHandle(getPlayer());
		return (float) ReflectionUtil.getMethod(entityPlayer.getClass(), "getAbsorptionHearts").invoke(entityPlayer, null);*/

		return ((CraftPlayer) getPlayer()).getHandle().getAbsorptionHearts();
	}

	//

	public void sendPacket(Object packet) {
		KReflectionUtil.sendPacket(getPlatformSender(),packet);
	}

}