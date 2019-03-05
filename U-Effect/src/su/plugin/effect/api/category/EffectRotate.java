package su.plugin.effect.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EffectRotate {
	
	LEFT("왼쪽"),
	RIGHT("오른쪽"),
	NONE("회전 안 함");
	
	@Getter
	private final String name;
	
	public static EffectRotate getByName(String name) {
		for(EffectRotate shape : values()) {
			if(shape.getName().equals(name)) return shape;
		}
		
		return null;
	}
	
}