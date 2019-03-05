package su.plugin.gessentials.bungee.api.object.ban;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class EIpBan extends EBan {
	
	private final String ip;
	
	public EIpBan(String ip, int adminId, long time, long duration, String reason) {
		super(adminId, time, duration, reason);
		
		this.ip = ip;
	}
	
}