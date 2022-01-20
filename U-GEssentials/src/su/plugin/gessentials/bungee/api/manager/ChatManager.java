package su.plugin.gessentials.bungee.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.Allow;
import su.plugin.gessentials.bungee.api.category.ListeningChannel;
import su.plugin.gessentials.bungee.api.object.EChannel;
import su.plugin.gessentials.bungee.api.object.EMute;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.gfriend.api.GFriendAPI;
import su.plugin.glogin.bungee.api.GGLoginAPI;
import su.plugin.glogin.common.api.object.Account;

public class ChatManager {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@Setter
	@Getter
	private HashMap<String, Integer> banWords = new HashMap<>();
	
	@Setter
	@Getter
	private HashMap<Integer, Integer> lastWhispers = new HashMap<>();
	
	@Setter
	@Getter
	private HashSet<Integer> adminChats = new HashSet<>();

	public void setMuteAll(boolean toggle) {
		if(toggle) {
			Core.getOptionManager().setServerOption("gessentials_mute_all", true);
		} else {
			Core.getOptionManager().deleteServerOption("gessentials_mute_all");
		}
	}

	public boolean isMutedAll() {
		return Core.getOptionManager().existsServerOption("gessentials_mute_all");
	}

	public String sendGlobalChat(EPlayer ep, String msg) {
		if(ep.getProxiedPlayer().hasPermission("gessentials.colorchat")) {
			msg = ChatColor.translateAlternateColorCodes('&', msg);
		}
		
		List<String> replacedChat = replaceChat(msg);
		String message= replacedChat.get(0);
		
		EChannel channel = ep.getEChannel();
		
		Core.nlog("§7[" + channel.getName() + "] §f" + ep.getPrefixerPrefix() + "§f" + ep.getPermissionPrefix() + "§f" + ep.getName() + " : §f" + msg);
		if(replacedChat.size() > 1){
			Core.nlog("§7[원본] §f" + msg);
		}

		String chat = channel.getChatForm().replace("{channel_name}", channel.getName()).replace("{channel_displayname}", channel.getDisplayName()).replace("{prefixer_prefix}", ep.getPrefixerPrefix()).replace("{permission_prefix}", ep.getPermissionPrefix()).replace("{name}", ep.getDisplayName()).replace("{message}", message);
		
		for(EPlayer aep : api.getPlayerManager().getOnlineEPlayers()) {
			if(aep.getEChannel().equals(ep.getEChannel()) || aep.getListeningChannel() == ListeningChannel.LOCAL || aep.isChatIgnored(ep.getPlayerKey())) continue;
			Core.nmsg(aep.getProxiedPlayer(), chat);
		}
		
		// Warning
		
		if(api.isUseWarning() && !ep.getProxiedPlayer().hasPermission("gessentials.bypasswarning")) {
			int count = api.getWarningManager().giveWarning(ep.getPlayerKey(), replacedChat);
			if(count > 0) {
				Core.nmsg(ep.getProxiedPlayer(), api.getWarningManager().getWarningMessage().replace("{count}", String.valueOf(count)).replace("{total_count}", String.valueOf(ep.getWarning())));
				
				api.getWarningManager().executeWarningEvent(ep);
				
				api.getSQLManager().saveWarning(ep.getPlayerKey(), api.getWarningManager().getWarning(ep.getPlayerKey()));
			}
		}
		
		return message;
	}
	
	public void sendAdminChat(UCommandSender sender, String msg) {
		List<String> replacedChat = replaceChat(msg);
		String message= replacedChat.get(0);

		String playerStr = "";
		if(!sender.isConsole()) {
			EPlayer ep = api.getEPlayer(((UPlayer) sender).getPlayerKey());
			EChannel channel = ep.getEChannel();

			playerStr = "[" + channel.getName() + "] §f" + ep.getPrefixerPrefix() + "§f" + ep.getPermissionPrefix();
		}

		String chat = "§7<AdminChat> " + playerStr + "§f" + sender.getDisplayName() + " §7: §f" + message;
		
		Core.nlog(chat);
		
		for(EPlayer aep : api.getPlayerManager().getOnlineEPlayers()) {
			if(!aep.getProxiedPlayer().hasPermission("gessentials.adminchat")) continue;

			Core.nmsg(aep.getProxiedPlayer(), chat);
		}
	}
	
	public void sendWhisper(UCommandSender sender, UCommandSender target, String msg) {
		if (sender.equals(target)) {
			sender.wmsg("자신에게는 귓속말을 보낼 수 없습니다.");
			return;
		}

		boolean ignored = false;

		if(!target.isConsole()) {
			if(api.isUseGLogin()) { // Target Login Check
				Account account = GGLoginAPI.getAccountManager().getAccount(((UPlayer) target).getPlayerKey());
				if(account == null || !account.isLogin()) {
					sender.wmsg("상대방이 로그인하지 않아 메시지를 보낼 수 없습니다.");
					return;
				}
			}

			if(!sender.isConsole() && !sender.hasPermission("gessentials.ignorewhisperbypass")) {
				EPlayer tp = GGEssentialsAPI.getPlayerManager().getEPlayer(target.getName());
				if(tp.getWhisperAllow() == Allow.BLOCK || (tp.getWhisperAllow() == Allow.FRIEND && api.isUseGFriend() && !GFriendAPI
						.getPlayerManager().getFriendPlayer(((UPlayer) sender).getPlayerKey()).isFriend(((UPlayer) target).getPlayerKey()))) {
					ignored = true;
					sender.wmsg("상대가 귓속말을 허용하지 않았습니다.");
				}
			}
		}

		//
		
		List<String> replacedChat = replaceChat(msg);
		String message= replacedChat.get(0);

		if(!target.isConsole()) {
			EPlayer tp = GGEssentialsAPI.getPlayerManager().getEPlayer(target.getName());
			ignored = ignored ? ignored : !sender.isConsole() && tp.isWhisperIgnored(PlayerKey.getPlayerKey(sender.getName()));
		}

		Core.nmsg(sender, "§7[§f나 → §e" + target.getDisplayName() + "§7] " + message);
		if(!ignored || sender.hasPermission("gessentials.ignorewhisperbypass")) {
			Core.nmsg(target, "§7[§e" + sender.getDisplayName() + " → §f나§7] " + message);
		}
		
		lastWhispers.put(target.isConsole() ? -1 : ((UPlayer) target).getPlayerKey().getId(), sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId());
		
		// Chat Spy
		
		String suffixMessage = "§7" + (ignored ? "[차단됨]" : "") + "[§e" + sender.getDisplayName() + "§7 → §e" + target.getDisplayName() + "§7]";
		String originalMessage = replacedChat.size() > 1 ? "§7[원본] " + suffixMessage + " " + msg : null;
		
		if(!sender.isConsole() && !target.isConsole()) {
			Core.nlog(suffixMessage + " " + msg);
		}
		if(originalMessage != null) {
			Core.nlog(suffixMessage + " " + originalMessage);
		}
		
		for(EPlayer ep : api.getPlayerManager().getOnlineChatSpys()) {
			if(sender.getName().equals(ep.getName()) || target.getName().equals(ep.getName())) continue;
			
			Core.nmsg(ep.getProxiedPlayer(), "§7<ChatSpy> " + suffixMessage + " " + message);
			
			if(originalMessage == null) continue;
			Core.nmsg(ep.getProxiedPlayer(), originalMessage);
		}
		
		// Warning
		
		if(!api.isUseWarning() || !(sender instanceof ProxiedPlayer) || sender.hasPermission("gessentials.bypasswarning")) return;
		
		EPlayer ep = api.getEPlayer((ProxiedPlayer) sender);
		
		int count = api.getWarningManager().giveWarning(ep.getPlayerKey(), replacedChat);
		if(count < 1) return;
		
		Core.nmsg(ep.getProxiedPlayer(), api.getWarningManager().getWarningMessage().replace("{count}", String.valueOf(count)).replace("{total_count}", String.valueOf(ep.getWarning())));
		
		api.getWarningManager().executeWarningEvent(ep);
		
		api.getSQLManager().saveWarning(ep.getPlayerKey(), api.getWarningManager().getWarning(ep.getPlayerKey()));
	}
	
	public List<String> replaceChat(String message) {
		String[] chars = new String[message.length()];
		for (int i = 0; i < chars.length; i++) {
			chars[i] = String.valueOf(message.charAt(i));
		}
		
		message = message.toLowerCase();
		for(ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			message = message.replace(player.getName().toLowerCase(), replaceWordTo(player.getName(), "-"));
		}
		
		List<String> list = new ArrayList<>();
		for(String word : banWords.keySet()) {
			List<Integer> rWord = replaceWord(message, word);
			for(int i : rWord) {
				chars[i] = "*";
			}
			if(rWord.size() < 1) continue;
			list.add(word);
		}
		
		list.add(0, StringUtil.connectString(chars, ""));
		return list;
	}
	
	public List<Integer> replaceWord(String message, String word) {
		String p = "[^a-zA-Zㄱ-ㅎ가-힣]";
		if(Pattern.matches("[a-zA-Z]*", word)) {
			p = "[^a-zA-Z]";
		} else if(Pattern.matches("[가-힣]*", word)) {
			p = "[^가-힣]";
		} else if(Pattern.matches("[ㄱ-ㅎ]*", word)) {
			p = "[^ㄱ-ㅎ]";
		}
		
		String[] chars = new String[message.length()];
		int[] num = new int[chars.length];
		int j = 0;
		for (int i = 0; i < chars.length; i++) {
			chars[i] = String.valueOf(message.charAt(i));
			if(Pattern.matches(p, String.valueOf(chars[i]))) continue;
			num[j++] = i;
		}
		
		String temp = message.replaceAll(p, "").replace(word, replaceWordTo(word, "*"));
		List<Integer> r = new ArrayList<>();
		for (int i = 0; i < temp.length(); i++) {
			if(!String.valueOf(temp.charAt(i)).equals("*")) continue;
			r.add(num[i]);
		}
		
		return r;
	}
	
	public String replaceWordTo(String word, String c) {
		return StringUtil.repeatString(c, word.length());
	}

	public boolean isMuted(PlayerKey playerKey) {
		EMute mute = GGEssentialsAPI.getPlayerManager().existsEPlayer(playerKey) ? GGEssentialsAPI.getPlayerManager().getEPlayer(playerKey).getMute() : GGEssentialsAPI.getSQLManager().getEMute(playerKey);

		return mute != null && mute.isEffective();
	}

	public EMute mute(PlayerKey playerKey, UCommandSender sender, String reason, long duration) {
		if(isMuted(playerKey)) return null;

		EMute mute = new EMute(playerKey, sender == null ? -2 : (sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId()), System.currentTimeMillis(), duration, reason);

		if(playerKey.getUPlayer() != null && playerKey.getUPlayer().isOnline()) {
			GGEssentialsAPI.getPlayerManager().getEPlayer(playerKey).setMute(mute);

			if(mute.isTimeMute()) {
				mute.startUnMuteTask();
			}
		}

		ProxyServer.getInstance().getScheduler().runAsync(GGEssentialsPlugin.getInstance(), () -> {
			api.getSQLManager().saveEMute(playerKey, mute);
			api.getSQLManager().writeMuteLog(playerKey, mute);
		});

		return mute;
	}

	public boolean unMute(PlayerKey playerKey, UCommandSender sender) {
		if(!isMuted(playerKey)) return false;

		if(playerKey.getUPlayer() != null && playerKey.getUPlayer().isOnline()) {
			GGEssentialsAPI.getPlayerManager().getEPlayer(playerKey).setMute(null);
		}

		ProxyServer.getInstance().getScheduler().runAsync(GGEssentialsPlugin.getInstance(), () -> {
			api.getSQLManager().deleteEMute(playerKey);
			api.getSQLManager().writeUnMuteLog(playerKey, sender == null ? -2 : (sender.isConsole() ? -1 : ((UPlayer) sender).getPlayerKey().getId()));
		});

		return true;
	}
	
}