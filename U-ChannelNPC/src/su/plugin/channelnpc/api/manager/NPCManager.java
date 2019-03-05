package su.plugin.channelnpc.api.manager;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;
import net.citizensnpcs.api.npc.NPC;
import su.plugin.channelnpc.api.object.ChannelNPC;

@Setter
@Getter
public class NPCManager {
	
	private int lastId = 0;
	
	private HashMap<Integer, ChannelNPC> channelNPCs = new HashMap<>();
	
	public ChannelNPC createChannelNPC(String name) {
		ChannelNPC npc = new ChannelNPC(++lastId);
		
		npc.setName(name);
		
		channelNPCs.put(npc.getId(), npc);
		
		return npc;
	}
	
	public boolean existsChannelNPC(int id) {
		return channelNPCs.containsKey(id);
	}
	
	public void removeChannelNPC(int id) {
		channelNPCs.remove(id);
	}
	
	public ChannelNPC getChannelNPC(int id) {
		return channelNPCs.get(id);
	}
	
	public ChannelNPC getChannelNPC(NPC citizensNPC) {
		for(ChannelNPC cn : channelNPCs.values()) {
			if(cn.getNPC().equals(citizensNPC)) return cn;
		}
		
		return null;
	}
	
}