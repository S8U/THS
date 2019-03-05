package su.plugin.core.bukkit.api.event;

import com.google.common.io.ByteArrayDataInput;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChannelMessageEvent extends UKEvent {
	
	@Getter
	private final String key, task;
	
	@Getter
	private final ByteArrayDataInput byteArrayDataInput;
	
}