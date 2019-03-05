package su.plugin.gessentials.bungee.api.object.ban;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@RequiredArgsConstructor
public class EBan {
	
	private final int adminId;
	
	private final long time, duration;
	
	private final String reason;
	
	public boolean isTimeBan() {
		return duration > 0;
	}
	
	public boolean isEffective() {
		return !isTimeBan() || getRemainingBanTime() > 0;
	}
	
	public long getRemainingBanTime() {
		return getUnBanTime() - System.currentTimeMillis();
	}
	
	public long getUnBanTime() {
		return time + duration;
	}
	
}