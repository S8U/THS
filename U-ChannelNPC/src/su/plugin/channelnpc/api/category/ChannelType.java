package su.plugin.channelnpc.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ChannelType {
	
	CHANNEL("채널"),
	CHANNEL_GROUP("그룹");
	
	@Getter
	private final String name;
	
}