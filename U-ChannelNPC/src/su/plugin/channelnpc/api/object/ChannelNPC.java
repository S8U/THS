package su.plugin.channelnpc.api.object;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.channelnpc.ChannelNPCPlugin;
import su.plugin.channelnpc.api.ChannelNPCAPI;
import su.plugin.channelnpc.api.category.ChannelType;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.ReflectionUtil;

@Setter
@Getter
public class ChannelNPC {
	
	private final int id, citizenId;
	
	private String name, skinName;
	
	private Location location;
	
	//
	
	private Channel channel;
	
	private ChannelGroup channelGroup;
	
	private ChannelType channelType;
	
	//
	
	private NPC NPC;
	
	private Hologram hologram;
	
	//
	
	private List<String> texts = new ArrayList<>();
	
	private List<String> rightCommands = new ArrayList<>();
	
	private List<String> shiftRightCommands = new ArrayList<>();
	
	//
	
	public ChannelNPC(int id) {
		this.id = id;
		texts = new ArrayList<>(ChannelNPCAPI.getNPCTexts());
		
		NPC = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, "");
		NPC.data().set("nameplate-visible", false);
		
		citizenId = NPC.getId();
	}
	
	public ChannelNPC(int id, int citizenId) {
		this.id = id;
		this.citizenId = citizenId;
		
		texts = new ArrayList<>(ChannelNPCAPI.getNPCTexts());
		
		NPC = CitizensAPI.getNPCRegistry().getById(citizenId);
		NPC.data().set("nameplate-visible", false);
	}
	
	public void setLocation(Location location) {
		this.location = location;
		
		if(!NPC.isSpawned()) return;
		
		NPC.teleport(location, TeleportCause.PLUGIN);
	}
	
	public void setSkinName(String skinName) {
		this.skinName = skinName;
		
		NPC.data().set("player-skin-name", skinName);
	}
	
	public void spawnNPC() {
		if(location == null) return;
		
		NPC.spawn(location);
	}
	
	public void destroyNPC() {
		NPC.destroy();
	}
	
	@SneakyThrows(Exception.class)
	public void saveCitizens() {
		Citizens c = (Citizens) CitizensAPI.getPlugin();
		c.storeNPCs();
		
		Field saves = c.getClass().getDeclaredField("saves");
		saves.setAccessible(true);
		Object npcRegistry = saves.get(c);
		
		Method saveToDiskImmediate = ReflectionUtil.getMethod(npcRegistry.getClass(), "saveToDiskImmediate");
		saveToDiskImmediate.invoke(npcRegistry, null);
	}
	
	//
	
	public void updateHologram(boolean create) {
		if(hologram == null || create) {
			if(hologram != null) {
				hologram.delete();
			}
			
			hologram = HologramsAPI.createHologram(ChannelNPCPlugin.getInstance(), location.clone().add(0, 2.5, 0));
		} else {
			hologram.clearLines();
		}
		
		for(String line : texts) {
			hologram.appendTextLine(line.replace("<npc_name>", name + "").replace("<player_count>", getPlayerCount() + ""));
			hologram.setAllowPlaceholders(true);
		}
	}
	
	public void removeHologram() {
		hologram.delete();
	}
	
	//
	
	public int getPlayerCount() {
		if(channelType == ChannelType.CHANNEL) return channel.getPlayerCount();
		else if (channelType == ChannelType.CHANNEL_GROUP) return channelGroup.getPlayerCount();
		
		return -1;
	}
	
	//
	
	public void join(Player player) {
		if(channelType == ChannelType.CHANNEL) {
			Core.msg(player, channel.getDisplayName() + " §e채널로 이동합니다.");

			channel.sendToChannel(player.getName());
		} else if (channelType == ChannelType.CHANNEL_GROUP) {
			channelGroup.sendToOptimizeChannel(player.getName());
		}
	}
	
	//
	
	private void executeCommand(Player player, String command) {
		if(command.equalsIgnoreCase("@join")) {
			if(channel != null && !channel.isOnline()) {
				Core.wmsg(player, "오프라인 상태인 채널입니다.");
				return;
			} else if(channel != null && channel.equals(KChannelAPI.getCurrentChannel())) {
				Core.wmsg(player, "이미 접속 중인 채널입니다.");
				return;
			}

			join(player);
		} else if(command.toLowerCase().startsWith("@cmd ")) {
			Bukkit.dispatchCommand(player, command.substring("@cmd ".length()));
		} else if(command.toLowerCase().startsWith("@cmdop ")) {
			boolean op = player.isOp();
			
			player.setOp(true);
			
			try {
				Bukkit.dispatchCommand(player, command.substring("@cmdop ".length()));
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if(op) return;
				player.setOp(false);
			}
		} else if(command.toLowerCase().startsWith("@cmdcon ")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring("@cmdcon ".length()));
		}
	}
	
	public void executeRightCommand(Player player) {
		for(String line : rightCommands) {
			executeCommand(player, line);
		}
	}
	
	public void executeShiftRightCommand(Player player) {
		for(String line : shiftRightCommands) {
			executeCommand(player, line);
		}
	}
	
}