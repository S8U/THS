package su.plugin.channelgui.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum CType {
	NORMAL("일반"), U_ABILITY("U-Ability");

	@Getter
	private final String name;

	public static CType getCTypeByName(String name) {
		if(name == null) return null;

		for(CType ct : values()) {
			if(ct.getName().equalsIgnoreCase(name)) return ct;
		}

		return null;
	}
}