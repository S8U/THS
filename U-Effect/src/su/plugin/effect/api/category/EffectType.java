package su.plugin.effect.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EffectType {
	
	PLAYER("플레이어"),
	PROJECTILE("투사체");
	
	@Getter
	private final String name;
	
	public static EffectType getByName(String name) {
		for(EffectType shape : values()) {
			if(shape.getName().equals(name)) return shape;
		}
		
		return null;
	}
	
}