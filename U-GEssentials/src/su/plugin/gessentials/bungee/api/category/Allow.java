package su.plugin.gessentials.bungee.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Allow {
	ALL("허용"), FRIEND("친구"), BLOCK("차단");

	@Getter
	private final String name;
}