package su.plugin.core.common.api.platform;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PlatformType {
	BUKKIT("버킷"), BUNGEECORD("번지코드");
	
	@Getter
	final String name;
}