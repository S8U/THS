package su.plugin.core.bungee.api.player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.GCorePlugin;
import su.plugin.core.bungee.api.task.PluginMessageTask;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

@RequiredArgsConstructor
@Getter
public class GPlayer extends UPlayer {
	
	private final PlayerKey playerKey;
	
	private final String name, ip;
	
	private ProxiedPlayer proxiedPlayer;
	
	public void setProxiedPlayer(ProxiedPlayer proxiedPlayer) {
		this.proxiedPlayer = proxiedPlayer;
	}
	
	@Override
	public void setDisplayName(String displayName, boolean sql) {
		this.displayName = displayName;
		
		if(proxiedPlayer != null) {
			proxiedPlayer.setDisplayName(displayName);
		}
		
		if(sql) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			
			out.writeUTF("SetDisplayName");
			out.writeUTF(name);
			out.writeUTF(displayName);
			
			new PluginMessageTask(GCorePlugin.getInstance(), proxiedPlayer.getServer().getInfo(), "U-Core", out.toByteArray()).runAsync();

			if(name.equals(displayName)) {
				Core.getSQLManager().deleteDisplayName(playerKey);
			} else {
				Core.getSQLManager().setDisplayName(playerKey, displayName);
			}
		}

		this.displayName = name.equalsIgnoreCase(displayName) ? null : displayName;
	}
	
	@Override
	public boolean isConsole() {
		return false;
	}
	
	@Override
	public boolean hasPermission(String node) {
		return proxiedPlayer.hasPermission(node);
	}
	
	@Override
	public ProxiedPlayer getPlatformSender() {
		return proxiedPlayer;
	}
	
	@Override
	public boolean isOnline() {
		return proxiedPlayer != null && proxiedPlayer.isConnected();
	}

	@Override
	public void kickPlayer(String message) {
		proxiedPlayer.disconnect(message);
	}
	
	@Override
	public void sendPluginMessage(String channel, byte... data) {
		getPlatformSender().sendData(channel, data);
	}

}