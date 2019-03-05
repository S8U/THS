package su.plugin.prefixer;

import org.bukkit.Bukkit;

import com.comphenix.protocol.ProtocolLibrary;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

import lombok.Getter;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.listener.other.HologramListener;

public class PrefixerPlugin extends UKPlugin {
	
	@Getter
	private  static PrefixerPlugin instance;
	
	@Getter
	private static PrefixerAPI api = new PrefixerAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§d[ U-Prefixer ]");
		setColor(ChatColor.LIGHT_PURPLE);
		api.init();
		
		if(!api.getSQLManager().connect(this)) {
			wlog("MySQL에 연결할 수 없어 비활성화됩니다.");
			disable();
			
			return;
		}
		
		registerListeners();
		registerCommands();
		
		registerPermissions();
		
		api.registerPlugins();
		
		api.loadConfig(this);
		
		if(api.isUseHologram()) {
			HologramListener hl = new HologramListener();
			Bukkit.getPluginManager().registerEvents(hl, this);
			ProtocolLibrary.getProtocolManager().addPacketListener(hl);
			
			if(api.isHideHologramOnMove()) {
				api.getHologramManager().getHologramShowTask().runTaskTimerAsynchronously(10, 10);
			}
		}
		
		api.getPlayerManager().registerAllPlayer();
	}
	
	@Override
	public void onUDisable() {
		api.getSQLManager().close();
		
		if(api.isUseHologram()) {
			for(Hologram holo : api.getHologramManager().getHolograms().values()) {
				holo.delete();
			}
		}
	}
	
}