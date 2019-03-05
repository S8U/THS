package su.plugin.channelnpc.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import su.plugin.channel.bukkit.api.event.KChannelLoadedEvent;
import su.plugin.channel.bukkit.api.event.KCurrentChannelUpdatedEvent;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channelnpc.ChannelNPCPlugin;
import su.plugin.channelnpc.api.ChannelNPCAPI;
import su.plugin.channelnpc.api.category.ChannelType;
import su.plugin.channelnpc.api.object.ChannelNPC;

public class NPCListener implements Listener {
	
	private ChannelNPCAPI api = ChannelNPCPlugin.getApi();
	
	@EventHandler
	public void onNPCClick(NPCRightClickEvent e) {
		Player p = e.getClicker();
		
		NPC ctz = e.getNPC();
		ChannelNPC npc = api.getNPCManager().getChannelNPC(ctz);
		if(npc == null) return;
		
		if(p.isSneaking()) {
			npc.executeShiftRightCommand(p);
			return;
		}
		
		npc.executeRightCommand(p);
	}
	
	@EventHandler
	public void onChannelLoad(KChannelLoadedEvent e) {
		for(ChannelNPC npc : api.getNPCManager().getChannelNPCs().values()) {
			if(npc.getChannelType() == ChannelType.CHANNEL) {
				if(!e.getLoadedChannel().contains(npc.getChannel())) continue;
				
				npc.updateHologram(false);
			} else if(npc.getChannelType() == ChannelType.CHANNEL_GROUP) {
				for(Channel channel : npc.getChannelGroup().getChannels()) {
					if(!e.getLoadedChannel().contains(channel)) continue;
					
					npc.updateHologram(false);
				}
			}
		}
	}
	
	@EventHandler
	public void onCurrentChannelUpdated(KCurrentChannelUpdatedEvent e) {
		for(ChannelNPC npc : api.getNPCManager().getChannelNPCs().values()) {
			if(npc.getChannelType() == ChannelType.CHANNEL) {
				if(!e.getCurrentChannel().equals(npc.getChannel())) continue;
				
				npc.updateHologram(false);
			} else if(npc.getChannelType() == ChannelType.CHANNEL_GROUP) {
				for(Channel channel : npc.getChannelGroup().getChannels()) {
					if(!e.getCurrentChannel().equals(channel)) continue;
					
					npc.updateHologram(false);
				}
			}
		}
	}
	
}