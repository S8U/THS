package su.plugin.channel.common.task;

import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.core.common.api.util.DebugUtil;

public class ChannelLoadTask implements Runnable {
	
	@Override
	public void run() {
		ChannelAPI.getSQLManager().loadAllChannel();
		
		DebugUtil.log("채널 정보를 불러왔습니다.");
	}
	
}