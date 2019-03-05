package su.plugin.channelnpc.api.manager;

import java.io.File;
import java.util.Arrays;
import lombok.Getter;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channelnpc.ChannelNPCPlugin;
import su.plugin.channelnpc.api.ChannelNPCAPI;
import su.plugin.channelnpc.api.category.ChannelType;
import su.plugin.channelnpc.api.object.ChannelNPC;
import su.plugin.core.bukkit.api.util.KStringUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;
import su.plugin.core.common.api.util.StringUtil;

public class ConfigManager {
	
	private ChannelNPCAPI api = ChannelNPCPlugin.getApi();
	
	@Getter
	private JsonConfig NPCConfig = new JsonConfig(new File(ChannelNPCPlugin.getInstance().getDataFolder(), "npc-config.json"));
	
	public void loadNPC() {
		if(!NPCConfig.getFile().exists()) return;
		
		api.getNPCManager().getChannelNPCs().clear();
		
		NPCConfig.getValues().clear();
		NPCConfig.load();
		
		api.getNPCManager().setLastId(NPCConfig.getInt("마지막 ID"));
		
		for(String idStr : NPCConfig.getKeys("NPC")) {
			int id = Integer.parseInt(idStr);
			int citizenId = NPCConfig.getInt("NPC." + id + ".시티즌 ID");
			
			ChannelNPC npc = new ChannelNPC(id, citizenId);
			npc.setName(NPCConfig.getString("NPC." + id + ".이름"));
			npc.setLocation(KStringUtil.stringToLocation(NPCConfig.getString("NPC." + id + ".위치")));
			npc.setSkinName(NPCConfig.getString("NPC." + id + ".스킨 이름"));
			
			String cis = NPCConfig.getString("NPC." + npc.getId() + ".채널 정보");
			if(cis.startsWith("<channel:")) {
				String cn = StringUtil.getValue("channel", cis).get(0);
				
				npc.setChannelType(ChannelType.CHANNEL);
				npc.setChannel(ChannelAPI.getChannelManager().getChannel(cn));
			} else if(cis.startsWith("<channelgroup:")) {
				String cn = StringUtil.getValue("channelgroup", cis).get(0);
				
				npc.setChannelType(ChannelType.CHANNEL_GROUP);
				npc.setChannelGroup(ChannelAPI.getChannelGroupManager().getChannelGroup(cn));
			}
			
			npc.setTexts(StringUtil.translateAlternateColorCodes(NPCConfig.getStringList("NPC." + npc.getId() + ".텍스트")));
			
			npc.setRightCommands(NPCConfig.getStringList("NPC." + npc.getId() + ".우클릭"));
			npc.setShiftRightCommands(NPCConfig.getStringList("NPC." + npc.getId() + ".쉬프트 우클릭"));

			npc.updateHologram(true);
			
			api.getNPCManager().getChannelNPCs().put(id, npc);
		}
		
		Core.log(api.getNPCManager().getChannelNPCs().size() + "개의 NPC를 불러왔습니다.");
	}
	
	public void saveNPC() {
		NPCConfig.getValues().clear();
		
		NPCConfig.set("마지막 ID", api.getNPCManager().getLastId());
		
		for(ChannelNPC npc : api.getNPCManager().getChannelNPCs().values()) {
			NPCConfig.set("NPC." + npc.getId() + ".시티즌 ID", npc.getCitizenId());
			NPCConfig.set("NPC." + npc.getId() + ".이름", npc.getName());
			NPCConfig.set("NPC." + npc.getId() + ".위치", KStringUtil.locationToString(npc.getLocation()));
			NPCConfig.set("NPC." + npc.getId() + ".스킨 이름", npc.getSkinName());
			NPCConfig.set("NPC." + npc.getId() + ".채널 정보", npc.getChannelType() == ChannelType.CHANNEL ? "<channel:" + npc.getChannel().getName() + ">" : "<channelgroup:" + npc.getChannelGroup().getName() + ">");
			NPCConfig.set("NPC." + npc.getId() + ".텍스트", npc.getTexts());
			NPCConfig.set("NPC." + npc.getId() + ".우클릭", npc.getRightCommands().size() > 0 ? npc.getRightCommands() : Arrays.asList("@JOIN"));
			NPCConfig.set("NPC." + npc.getId() + ".쉬프트 우클릭", npc.getShiftRightCommands().size() > 0 ? npc.getShiftRightCommands() : Arrays.asList("@CMDOP " + (npc.getChannelType() == ChannelType.CHANNEL ? "channel info " + npc.getChannel().getName() : "channel group info " + npc.getChannelGroup().getName())));
		}
		
		NPCConfig.save();
		
		Core.log("NPC를 저장했습니다.");
	}
	
}