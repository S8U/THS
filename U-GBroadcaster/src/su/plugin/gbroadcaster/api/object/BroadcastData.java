package su.plugin.gbroadcaster.api.object;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;
import su.plugin.gbroadcaster.task.BroadcastTask;

@Setter
@Getter
public class BroadcastData {
	
	private final String channelName;
	
	private int interval = 30;
	
	private boolean use, random;
	
	private List<String> messages = new ArrayList<>();
	
	private BroadcastTask task;
	
	public BroadcastData(String channelName) {
		this.channelName = channelName;
		task = new BroadcastTask(this);
	}
	
	public void startTask() {
		task.schedule(0, interval, TimeUnit.SECONDS);
	}

}
