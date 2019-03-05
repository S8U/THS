package su.plugin.channel.bungee.task;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;

import com.google.gson.internal.LinkedTreeMap;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import su.plugin.channel.bungee.GChannelPlugin;
import su.plugin.channel.bungee.api.GChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.core.bungee.api.scheduler.UGRunnable;
import su.plugin.core.common.api.Core;

public class OfflineCheckTask extends UGRunnable {
	
	private GChannelAPI api = GChannelPlugin.getApi();
	
	public OfflineCheckTask() {
		super(GChannelPlugin.getInstance());
	}
	
	@Override
	public void run() {
		for(Channel channel : api.getChannelManager().getChannels().values()) {
			if(!channel.isOnline()) continue;
			
			ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(channel.getName());
			if(serverInfo == null) {
				Core.wlog(channel.getName() + "은 존재하지 않습니다.");
				return;
			}
			
			String ip = serverInfo.getAddress().getAddress().getHostAddress();
			int port = serverInfo.getAddress().getPort();
			
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(ip, port), 1500);
				socket.close();
			} catch(Exception e) {
				channel.setOnline(false);
				channel.setPlayerCount(0);
				channel.setMaxPlayerCount(0);
				channel.setPlayerList(Arrays.asList());
				channel.setETCs(new LinkedTreeMap<>());
				
				if(api.getSQLManager().isUpload()) {
					api.getSQLManager().saveChannel(channel);
				}
			}
		}
	}
	
}