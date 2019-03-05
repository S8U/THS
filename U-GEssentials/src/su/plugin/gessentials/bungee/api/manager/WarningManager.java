package su.plugin.gessentials.bungee.api.manager;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.StringUtil;
import su.plugin.gessentials.bungee.GGEssentialsPlugin;
import su.plugin.gessentials.bungee.api.GGEssentialsAPI;
import su.plugin.gessentials.bungee.api.object.EPlayer;
import su.plugin.gessentials.bungee.api.object.WarningEvent;
import su.plugin.gessentials.bungee.task.WarningInitTask;

public class WarningManager {
	
	private GGEssentialsAPI api = GGEssentialsPlugin.getApi();
	
	@Setter
	@Getter
	private String warningDisplayName, warningMessage;
	
	@Getter
	private WarningInitTask warningInitTask = new WarningInitTask();
	
	@Setter
	@Getter
	private HashMap<PlayerKey, Integer> warnings = new HashMap<>();
	
	@Setter
	@Getter
	private HashMap<Integer, WarningEvent> warningEvents = new HashMap<>();
	
	public void setWarning(PlayerKey playerKey, int count) {
		warnings.put(playerKey, count);
	}
	
	public void giveWarning(PlayerKey playerKey, int count) {
		setWarning(playerKey, getWarning(playerKey) + count);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public int giveWarning(PlayerKey playerKey, List<String> replacedChat) {
		if(replacedChat.size() < 2) return 0;
		
		int count = 0;
		for(int i = 1; i < replacedChat.size(); i++) {
			count += api.getChatManager().getBanWords().get(replacedChat.get(i));
		}
		
		giveWarning(playerKey, count);
		
		return count;
	}
	
	public boolean hasWarning(PlayerKey playerKey) {
		return warnings.containsKey(playerKey);
	}
	
	public int getWarning(PlayerKey playerKey) {
		return warnings.get(playerKey);
	}
	
	public void setWarningEvent(int count, WarningEvent event) {
		warningEvents.put(count, event);
	}
	
	public boolean existsEvent(int count) {
		return warningEvents.containsKey(count);
	}
	
	public WarningEvent getWarningEvent(int count) {
		return warningEvents.get(count);
	}
	
	public void executeWarningEvent(EPlayer ep) {
		WarningEvent we = getWarningEvent(ep.getWarning());
		if(we == null) return;
		
		switch(we.getType()) {
		case KICK:
			ep.kickPlayer(null, we.getReason());
			Core.nbc("§c" + ep.getName()+ " 님께서 [" + we.getReason() + "] 사유로 강제 퇴장되었습니다. [처리자: " + warningDisplayName + "]");
			break;
		case BAN:
			ep.banPlayerKey(null, we.getReason());
			ep.kickPlayer(null, we.getReason());
			Core.nbc("§c" + ep.getName() + " 님께서 [" + we.getReason()+ "] 사유로 차단되었습니다. [처리자: " + warningDisplayName + "]");
			break;
		case TIME_BAN:
			ep.banPlayerKey(null, we.getReason(), we.getDuration());
			ep.kickPlayer(null, we.getReason());
			Core.nbc("§c" + ep.getName() + " 님께서 [" + we.getReason()+ "] 사유로 [" + StringUtil.buildDateString(System.currentTimeMillis() + we.getDuration(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + "] 까지 차단되었습니다. [처리자: " + warningDisplayName + "]");
			break;
		case IP_BAN:
			ep.banIp(null, we.getReason());
			ep.kickPlayer(null, we.getReason());
			Core.nbc("§c" + ep.getName() + " 님의 IP가 [" + we.getReason() + "] 사유로 차단되었습니다. [처리자: " + warningDisplayName + "]");
			break;
		case TIME_IP_BAN:
			ep.banIp(null, we.getReason(), we.getDuration());
			ep.kickPlayer(null, we.getReason());
			Core.nbc("§c" + ep.getName() + " 님의 IP가 [" + we.getReason() + "] 사유로 [" + StringUtil.buildDateString(System.currentTimeMillis() + we.getDuration(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + "] 까지 차단되었습니다. [처리자: " + warningDisplayName + "]");
			break;
			case MUTE:
				ep.mute(null, we.getReason(), 0);
				ep.getUPlayer().msg("§c채팅이 금지되었습니다. " + (we.getReason() == null ? "" : "(사유: " + we.getReason() + ") ") + "[처리자: " + warningDisplayName + "]");
				Core.log("§c" + ep.getName() + " 님의 채팅을 " + (we.getReason() == null ? "" : "[" + we.getReason() + "] 사유로 ") + "금지했습니다.");
				break;
			case TIME_MUTE:
				ep.mute(null, we.getReason(), we.getDuration());
				ep.getUPlayer().msg("§c" + StringUtil.buildDateString(System.currentTimeMillis() + we.getDuration(), "yyyy년 MM월 dd일 a h시 mm분 ss초") + "까지 " + "채팅이 금지되었습니다. " + (we.getReason() == null ? "" : "(사유: " + we.getReason() + ") ") +  "[처리자: " + warningDisplayName + "]");
				Core.log("§c" + ep.getName() + " 님의 채팅을 " + we.getReason() == null ? "" : we.getReason() + "까지 " + (we.getReason() == null ? "" : "[" + we.getReason() + "] 사유로 ") + "금지했습니다.");
				break;
		}
	}
	
	public void startWarningInitTask() {
		if(warningInitTask.isScheduled()) return;
		
		Calendar c = Calendar.getInstance();
		c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE) + 1, 0, 0, 0);
		
		warningInitTask.schedule(c.getTime(), 86400000, TimeUnit.MILLISECONDS);
	}
	
}