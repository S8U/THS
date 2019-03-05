package su.plugin.gessentials.bungee.command;

import java.util.ArrayList;
import java.util.List;

import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.category.Allow;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.Command;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class IgnoreCommand implements UCommandListener {
	
	@CommandHandler(
			name = "ignoreChat",
			aliases = {"ic", "채팅차단", "채팅무시"},
			additional = "<플레이어>",
			permission = "gessentials.ignorechat",
			minArgs = 1,
			usage = "플레이어의 채팅을 차단하거나 해제합니다.")
	public void ignoreChat(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		PlayerKey targetPlayerKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(targetPlayerKey == null) {
			p.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		boolean ignore = !ep.getChatIgnoreList().contains(targetPlayerKey);
		if(ignore) {
			ep.getChatIgnoreList().add(targetPlayerKey);
		} else {
			ep.getChatIgnoreList().remove(targetPlayerKey);
		}
		
		if(ep.getChatIgnoreList().size() < 1) {
			Core.getOptionManager().deletePlayerOption(p.getPlayerKey(), "gessentials_chat_ignore");
			Core.getOptionSQLManager().deletePlayerOption(p.getPlayerKey(), "gessentials_chat_ignore");
		} else {
			List<Integer> list = new ArrayList<>();
			for(PlayerKey pk : ep.getChatIgnoreList()) {
				list.add(pk.getId());
			}
			
			Core.getOptionManager().setPlayerOption(p.getPlayerKey(), "gessentials_chat_ignore", list);
			Core.getOptionSQLManager().setPlayerOption(p.getPlayerKey(), "gessentials_chat_ignore", list);
		}
		
		String targetDisplayName = Core.getDisplayName(targetPlayerKey);
		
		p.msg((ignore ? "§c" : "§a") + targetDisplayName + " 님의 채팅" + (ignore ? "을 차단" : " 차단을 해제") + "했습니다.");
	}
	
	@CommandHandler(
			name = "ignoreAllChat",
			aliases = {"iac", "채팅전체차단", "채팅전체무시"},
			usage = "모든 채팅을 차단하거나 해제합니다.",
			permission = "gessentials.ignoreallchat"
			)
	public void ignoreAllChat(UPlayer p, String[] args) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		ep.setIgnoreAllChat(!ep.isIgnoreAllChat());
		
		if(ep.isIgnoreAllChat()) {
			Core.getOptionManager().setPlayerOption(p.getPlayerKey(), "gessentials_chat_ignore_all", ep.isIgnoreAllChat());
			Core.getOptionSQLManager().setPlayerOption(p.getPlayerKey(), "gessentials_chat_ignore_all", ep.isIgnoreAllChat());
		} else {
			Core.getOptionManager().deletePlayerOption(p.getPlayerKey(), "gessentials_chat_ignore_all");
			Core.getOptionSQLManager().deletePlayerOption(p.getPlayerKey(), "gessentials_chat_ignore_all");
		}
		
		p.msg((ep.isIgnoreAllChat() ? "§c" : "§a") + "모든 채팅" + (ep.isIgnoreAllChat() ? "을 차단" : " 차단을 해제") + "했습니다.");
	}
	
	@CommandHandler(
			name = "ignoreWhisper",
			aliases = {"iw", "귓속말차단", "귓속말무시"},
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어의 귓속말을 차단하거나 해제합니다.",
			permission="gessentials.ignorewhisper"
			)
	public void ignoreWhisper(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		PlayerKey targetPlayerKey = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(targetPlayerKey == null) {
			p.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}
		
		boolean ignore = !ep.getWhisperIgnoreList().contains(targetPlayerKey);
		if(ignore) {
			ep.getWhisperIgnoreList().add(targetPlayerKey);
		} else {
			ep.getWhisperIgnoreList().remove(targetPlayerKey);
		}
		
		if(ep.getWhisperIgnoreList().size() < 1) {
			Core.getOptionManager().deletePlayerOption(p.getPlayerKey(), "gessentials_whisper_ignore");
			Core.getOptionSQLManager().deletePlayerOption(p.getPlayerKey(), "gessentials_whisper_ignore");
		} else {
			List<Integer> list = new ArrayList<>();
			for(PlayerKey pk : ep.getWhisperIgnoreList()) {
				list.add(pk.getId());
			}
			
			Core.getOptionManager().setPlayerOption(p.getPlayerKey(), "gessentials_whisper_ignore", list);
			Core.getOptionSQLManager().setPlayerOption(p.getPlayerKey(), "gessentials_whisper_ignore", list);
		}
		
		String targetDisplayName = Core.getDisplayName(targetPlayerKey);
		
		p.msg((ignore ? "§c" : "§a") + targetDisplayName + " 님의 귓속말" + (ignore ? "을 차단" : "차단을 해제") + "했습니다.");
	}
	
	@CommandHandler(
			name = "ignoreAllWhisper",
			aliases = {"iaw", "귓속말전체차단", "귓속말전체무시"},
			usage = "모든 귓속말을 차단하거나 해제합니다.",
			permission = "gessentials.ignoreallwhisper"
			)
	public void ignoreAllWhisper(UPlayer p, String[] args) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		if(ep.getWhisperAllow() == Allow.BLOCK) {
			ep.setWhisperAllow(Allow.ALL);
		} else {
			ep.setWhisperAllow(Allow.BLOCK);
		}
		
		p.msg((ep.getWhisperAllow() == Allow.BLOCK ? "§c" : "§a") + "모든 귓속말" + (ep.getWhisperAllow() == Allow.BLOCK ? "을 차단" : " 차단을 해제") + "했습니다.");
	}
	
	@CommandHandler(
			name = "chatIgnoreList",
			aliases = {"cil", "채팅차단목록", "채팅무시목록" },
			usage = "채팅 차단 목록을 확인합니다.",
			permission = "gessentials.chatignorelist"
			)
	public void chatIgnoreList(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		p.nmsg("§7[ 채팅 차단 목록 (" + ep.getChatIgnoreList().size() +" ]");
		for(PlayerKey pk : ep.getChatIgnoreList()) {
			String displayName = Core.getDisplayName(pk);
			
			p.nmsg(displayName + (displayName.equalsIgnoreCase(pk.getName()) ? "" : " (" + pk.getName() + ")"));
		}
	}
	
	@CommandHandler(
			name = "whisperIgnoreList",
			aliases = {"wil", "귓속말차단목록", "귓속말무시목록"},
			usage = "귓속말 차단 목록을 확인합니다.",
			permission = "gessentials.whisperignorelist"
			)
	public void whisperIgnoreList(UPlayer p, String[] args, Command command) {
		EPlayer ep = GGEssentialsAPI.getPlayerManager().getEPlayer(p.getPlayerKey());
		
		p.nmsg("§7[ 귓속말 차단 목록 (" + ep.getWhisperIgnoreList().size() +" ]");
		for(PlayerKey pk : ep.getWhisperIgnoreList()) {
			String displayName = Core.getDisplayName(pk);
			
			p.nmsg(displayName + (displayName.equalsIgnoreCase(pk.getName()) ? "" : " (" + pk.getName() + ")"));
		}
	}
	
}