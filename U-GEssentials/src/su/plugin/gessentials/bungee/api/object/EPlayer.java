package su.plugin.gessentials.bungee.api.object;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.task.PluginMessageTask;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.Allow;
import su.plugin.gessentials.bungee.api.category.ListeningChannel;

@RequiredArgsConstructor
public class EPlayer {
	
	@Getter
	private final PlayerKey playerKey;
	
	@Setter
	@Getter
	private String name, ip;
	
	@Setter
	private String permissionPrefix;
	
	@Setter
	@Getter
	private long lastLogin, lastLogout;
	
	@Setter
	@Getter
	private boolean connected, ignoreAllChat;

	@Setter
	@Getter
	private EMute mute;

	public void setChatSpy(boolean toggle) {
		if(toggle) {
			Core.getOptionManager().setPlayerOption(playerKey, "gessentials_chat_spy", true);
			Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_chat_spy", true);
		} else {
			Core.getOptionManager().deletePlayerOption(playerKey, "gessentials_chat_spy");
			Core.getOptionSQLManager().deletePlayerOption(playerKey, "gessentials_chat_spy");
		}
	}

	public boolean isChatSpy() {
		return Core.getOptionManager().existsPlayerOption(playerKey, "gessentials_chat_spy");
	}

	public void setMoveSpy(boolean toggle) {
		if(toggle) {
			Core.getOptionManager().setPlayerOption(playerKey, "gessentials_move_spy", true);
			Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_move_spy", true);
		} else {
			Core.getOptionManager().deletePlayerOption(playerKey, "gessentials_move_spy");
			Core.getOptionSQLManager().deletePlayerOption(playerKey, "gessentials_move_spy");
		}
	}

	public boolean isMoveSpy() {
		return Core.getOptionManager().existsPlayerOption(playerKey, "gessentials_move_spy");
	}

	public void setWhisperAllow(Allow allow) {
		if(allow == Allow.ALL) {
			Core.getOptionManager().deletePlayerOption(playerKey, "gessentials_allow_whisper");
			Core.getOptionSQLManager().deletePlayerOption(playerKey, "gessentials_allow_whisper");
		} else if(allow == Allow.FRIEND) {
			Core.getOptionManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "friend");
			Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "friend");
		} else if(allow == Allow.BLOCK) {
			Core.getOptionManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "block");
			Core.getOptionSQLManager().setPlayerOption(playerKey, "gessentials_allow_whisper", "block");
		}
	}

	public Allow getWhisperAllow() {
		String option = (String) Core.getOptionManager().getPlayerOption(playerKey, "gessentials_allow_whisper");

		return option == null ? Allow.ALL : (option.equals("friend") ? Allow.FRIEND : Allow.BLOCK);
	}

	@Getter
	@Setter
	private ListeningChannel listeningChannel = ListeningChannel.LOCAL;
	
	@Setter
	@Getter
	private List<PlayerKey> chatIgnoreList = new ArrayList<>();
	
	@Setter
	@Getter
	private List<PlayerKey> whisperIgnoreList = new ArrayList<>();
	
	@Setter
	@Getter
	private Map<Integer, String> prefixerPrefixes = new HashMap<>();
	
	public ProxiedPlayer getProxiedPlayer() {
		return ProxyServer.getInstance().getPlayer(name);
	}
	
	public UPlayer getUPlayer() {
		return Core.getUPlayer(name);
	}
	
	public EChannel getEChannel() {
		return GGEssentialsAPI.getChannelManager().getEChannel(getProxiedPlayer().getServer().getInfo().getName());
	}
	
	public boolean isOnline() {
		return getProxiedPlayer() != null;
	}
	
	public String getDisplayName() {
		return getUPlayer() == null ? name : getUPlayer().getDisplayName();
	}

	public void setLastWhisper(int playerId) {
		GGEssentialsAPI.getChatManager().getLastWhispers().put(playerKey.getId(), playerId);
	}
	
	public int getLastWhisper() {
		return GGEssentialsAPI.getChatManager().getLastWhispers().containsKey(playerKey.getId()) ? GGEssentialsAPI
				.getChatManager().getLastWhispers().get(playerKey.getId()) : -2;
	}
	
	public boolean setAdminChat(boolean toggle) {
		if(toggle) return GGEssentialsAPI.getChatManager().getAdminChats().add(playerKey.getId());
		
		return GGEssentialsAPI.getChatManager().getAdminChats().remove(playerKey.getId());
	}
	
	public boolean isAdminChat() {
		return GGEssentialsAPI.getChatManager().getAdminChats().contains(playerKey.getId());
	}
	
	public boolean hasPrefixerPrefix() {
		return prefixerPrefixes.size() > 0;
	}
	
	public boolean hasPermissionPrefix() {
		return permissionPrefix != null;
	}
	
	public void setPrefixerPrefix(int priority, String prefix) {
		prefixerPrefixes.put(priority, prefix);
	}
	
	public void deletePrefixerPrefix(String prefix) {
		prefixerPrefixes.values().remove(prefix);
	}
	
	public boolean hasPrefixerPrefix(String prefix) {
		return prefixerPrefixes.values().contains(prefix);
	}
	
	public List<String> getPrefixerPrefixList() {
		List<String> list = new ArrayList<>();
		
		List<Integer> il = new ArrayList<>();
		il.addAll(prefixerPrefixes.keySet());
		Collections.sort(il);
		
		for(int i = 0; i < prefixerPrefixes.size(); i++) {
			list.add(prefixerPrefixes.get(il.get(i)));
		}
		
		return list;
	}
	
	public String getPrefixerPrefix() {
		return hasPrefixerPrefix() ? StringUtil.connectString(getPrefixerPrefixList(), "") : "";
	}
	
	public String getPermissionPrefix() {
		return hasPermissionPrefix() ? permissionPrefix : "";
	}

	public boolean isChatIgnored(PlayerKey playerKey) {
		return chatIgnoreList.contains(playerKey);
	}
	
	public boolean isWhisperIgnored(PlayerKey playerKey) {
		return whisperIgnoreList.contains(playerKey);
	}
	
	public boolean kickPlayer(UCommandSender sender, String reason) {
		return kickPlayer(sender, reason, 4);
	}
	
	public boolean kickPlayer(UCommandSender sender, String reason, int line) {
		if(!isOnline()) return false;
		
		getProxiedPlayer().disconnect(reason + "\n[처리자: " + (sender == null ? GGEssentialsAPI.getWarningManager().getWarningDisplayName() : sender.getDisplayName()) + "]" + StringUtil.repeatString("\n", line) + GGEssentialsAPI
				.getKickServerMark());
		
		ProxyServer.getInstance().getScheduler().runAsync(
				GGEssentialsPlugin.getInstance(), () -> GGEssentialsAPI
						.getSQLManager().writeKickLog(playerKey, sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId(), reason));
		return true;
	}
	
	public boolean banPlayerKey(UCommandSender sender, String reason) {
		if(!GGEssentialsAPI.getBanManager().banPlayerKey(playerKey, sender, reason)) return false;
		
		kickPlayer(sender, reason); return true;
	}
	
	public boolean banPlayerKey(UCommandSender sender, String reason, long duration) {
		if(!GGEssentialsAPI.getBanManager().banPlayerKey(playerKey, sender, reason, duration)) return false;
		
		reason += "\n(차단 해제 시간: " + StringUtil.buildDateString(
				GGEssentialsAPI.getBanManager().getBanData(playerKey.getId() + "").getUnBanTime(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + ")";
		
		kickPlayer(sender, reason, 3); return true;
	}
	
	public boolean banIp(UCommandSender sender, String reason) {
		if(!GGEssentialsAPI.getBanManager().banIp(ip, sender, reason)) return false;
		
		kickPlayer(sender, reason);
		
		for(UPlayer aup : Core.getOnlineUPlayers()) {
			if(name.equals(aup.getName()) || !aup.getIp().equals(ip)) continue;
			
			GGEssentialsAPI.getPlayerManager().getEPlayer(aup.getPlayerKey()).kickPlayer(sender, reason);
		}
		
		return true;
	}
	
	public boolean banIp(UCommandSender sender, String reason, long duration) {
		if(!GGEssentialsAPI.getBanManager().banIp(ip, sender, reason, duration)) return false;
		
		reason += "\n(차단 해제 시간: " + StringUtil.buildDateString(
				GGEssentialsAPI.getBanManager().getBanData(ip).getUnBanTime(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + ")";
		
		kickPlayer(sender, reason);
		
		for(UPlayer aup : Core.getOnlineUPlayers()) {
			if(name.equals(aup.getName()) || !aup.getIp().equals(ip)) continue;
			
			GGEssentialsAPI.getPlayerManager().getEPlayer(aup.getPlayerKey()).kickPlayer(sender, reason, 3);
		}
		
		return true;
	}

	public EMute mute(UCommandSender sender, String reason, long duration) {
		return GGEssentialsAPI.getChatManager().mute(playerKey, sender, reason, duration);
	}

	public boolean unMute(UCommandSender sender) {
		return GGEssentialsAPI.getChatManager().unMute(playerKey, sender);
	}

	public boolean isBanned() {
		return GGEssentialsAPI.getBanManager().isBannedPlayerKey(playerKey);
	}
	
	public boolean isIpBanned() {
		return GGEssentialsAPI.getBanManager().isBannedIp(ip);
	}

	public boolean isMuted() {
		return mute != null && mute.isEffective();
	}
	
	public void setWarning(int count) {
		GGEssentialsAPI.getWarningManager().setWarning(playerKey, count);
	}
	
	public void giveWarning(int count) {
		GGEssentialsAPI.getWarningManager().giveWarning(playerKey, count);
	}
	
	public boolean hasWarning() {
		return GGEssentialsAPI.getWarningManager().hasWarning(playerKey);
	}
	
	public int getWarning() {
		return GGEssentialsAPI.getWarningManager().getWarning(playerKey);
	}
	
	public void sendMoveSpyToServer() {
		if(!isOnline()) return;
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("MoveSpy");
		out.writeUTF(name);
		out.writeBoolean(isMoveSpy());
		
		new PluginMessageTask(GGEssentialsPlugin.getInstance(), getProxiedPlayer().getServer().getInfo(), "U-GEssentials", out.toByteArray()).runAsync();
	}
	
	public void sendChannelNameToServer() {
		if(!isOnline()) return;
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		
		out.writeUTF("ChannelName");
		out.writeUTF(getEChannel().getDisplayName());
		
		new PluginMessageTask(GGEssentialsPlugin.getInstance(), getProxiedPlayer().getServer().getInfo(), "U-GEssentials", out.toByteArray()).runAsync();
	}
	
}