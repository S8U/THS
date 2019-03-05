package su.plugin.gessentials.bukkit;

import org.bukkit.Bukkit;

import com.google.gson.internal.LinkedTreeMap;

import lombok.Getter;
import su.plugin.gessentials.bukkit.api.KGEssentialsAPI;
import su.plugin.gessentials.bukkit.listener.MessageListener;
import su.plugin.gessentials.bukkit.listener.PermissionListener;
import su.plugin.gessentials.bukkit.listener.PrefixerListener;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class KGEssentialsPlugin extends UKPlugin {
	
	@Getter
	private static KGEssentialsPlugin instance;
	
	@Getter
	private static KGEssentialsAPI api = new KGEssentialsAPI();
	
	@Override
	public void onUEnable() {
		instance = this;
		setPrefix("§7[ U-GEssentials ]");
		setColor(ChatColor.GRAY);
		
		api.init();
		
		LinkedTreeMap<String, String> chatHandlingLocations = (LinkedTreeMap<String, String>) Core.getOptionManager().getServerOption("gessentials_chat_handling_location");
		api.setSendChat(chatHandlingLocations.containsKey(KChannelAPI.getChannelName()) && chatHandlingLocations.get(KChannelAPI.getChannelName()).equals("버킷"));
		if(api.isSendChat()) {
			log("채팅 처리 위치가 버킷으로 설정되었습니다.");
		}
		
		registerListeners(new MessageListener().getClass().getPackage().getName());
		if(api.isUsePermission()) {
			registerListener(new PermissionListener());
		}
		if(api.isUsePrefixer()) {
			registerListener(new PrefixerListener());
		}
		
		Bukkit.getMessenger().registerIncomingPluginChannel(this, "U-GEssentials", new MessageListener());
		Bukkit.getMessenger().registerOutgoingPluginChannel(this, "U-GEssentials");
	}
	
}