package su.plugin.gessentials.bungee.command;

import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.util.NumberUtil;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.EMute;

public class MuteCommand implements UCommandListener {
	
	@CommandHandler(
			name = "mute",
			additional = "<플레이어> (<d:일 / h:시간 / m:분 / s:초>) (<사유>)",
			minArgs = 1,
			usage = "플레이어를 채팅 금지 상태로 만듭니다.",
			permission = "gessentials.mute"
			)
	public void mute(UCommandSender sender, String[] args) {
		PlayerKey tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(tpk == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}

		UPlayer tup = Core.getUPlayer(tpk);
		String targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();

		String[] arr = getTimeReason(args);

		long duration = Long.parseLong(arr[0]), cTime = System.currentTimeMillis() + duration;
		String reason = arr[1];

		EMute mute = GGEssentialsAPI.getChatManager().mute(tpk, sender, reason, duration);
		if(mute == null) {
			sender.wmsg("이미 채팅이 금지된 플레이어입니다.");
		} else {
			String timeStr = duration < 1 ? "" : StringUtil.buildDateString(mute.getUnMuteTime(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + "까지 ";

			if(tpk.getUPlayer() != null && tpk.getUPlayer().isOnline()) {
				GGEssentialsAPI.getPlayerManager().getEPlayer(tpk).setMute(mute);
				tpk.getUPlayer().msg("§c" + timeStr + "채팅이 금지되었습니다. " + (reason == null ? "" : "(사유: " + reason + ") ") + "[처리자: " + sender.getDisplayName() + "]");
			}

			sender.msg("§7" + targetDisplayName + " 님의 채팅을 " + (reason == null ? "" : "[" + reason + "] 사유로 ") + timeStr + "금지시켰습니다.");
		}
	}

	@CommandHandler(
			name = "unMute",
			additional = "<플레이어>",
			minArgs = 1,
			usage = "플레이어의 채팅 금지를 해제합니다.",
			permission = "gessentials.unmute"
	)
	public void unMute(UCommandSender sender, String[] args) {
		PlayerKey tpk = PlayerKey.getPlayerKeyByDisplayName(args[0]);
		if(tpk == null) {
			sender.wmsg("존재하지 않는 플레이어입니다.");
			return;
		}

		UPlayer tup = Core.getUPlayer(tpk);
		String targetDisplayName = tup == null ? Core.getDisplayName(tpk) : tup.getDisplayName();

		if(GGEssentialsAPI.getChatManager().unMute(tpk, sender)) {
			if(tpk.getUPlayer() != null && tpk.getUPlayer().isOnline()) {
				tpk.getUPlayer().msg("§a채팅 금지가 해제되었습니다. [처리자: " + sender.getDisplayName() + "]");
			}

			sender.msg("§7" + targetDisplayName + " 님의 채팅 금지를 해제했습니다.");
		} else {
			sender.wmsg("채팅이 금지되지 않은 플레이어입니다.");
		}
	}

	@CommandHandler(
			name = "muteall",
			usage = "전체 채팅을 금지시키거나 해제합니다.",
			permission="gessentials.muteall"
			)
	public void muteAll(UCommandSender sender, String[] args) {
		GGEssentialsAPI.getChatManager().setMuteAll(!GGEssentialsAPI.getChatManager().isMutedAll());
		
		Core.bc((GGEssentialsAPI.getChatManager().isMutedAll() ? "§c" : "§a") + "전체 채팅" + (
				GGEssentialsAPI.getChatManager().isMutedAll() ? "이 금지" : " 금지가 해제") + "되었습니다. [처리자: " + sender.getDisplayName() + "]");
	}

	private String[] getTimeReason(String[] args) {
		String[] arr = new String[2];

		long time = 0;
		int length = 0;

		for(int i = 0; i < 4; i++) {
			if(args.length < i + 1 || args[i].length() < 3) break;

			String ts = args[i].substring(0, 2);
			Integer num = NumberUtil.getInteger(args[i].substring(2, args[i].length()));

			if(num == null) continue;

			time += num * (ts.equalsIgnoreCase("d:") ? 86400000 : (ts.equalsIgnoreCase("h:") ? 3600000 : (ts.equalsIgnoreCase("m:") ? 60000 : (ts.equalsIgnoreCase("s:") ? 1000 : 0))));
			length++;
		}

		String reason = args.length > length + 1 ? StringUtil.connectString(args, length + 1, " ") : null;

		arr[0] = String.valueOf(time);
		arr[1] = reason;

		return arr;
	}
	
}