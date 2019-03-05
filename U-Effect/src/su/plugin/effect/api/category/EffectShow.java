package su.plugin.effect.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EffectShow {
	
	ALWAYS("항상 보기"),
	STAND("가만히 있을 때 사용"),
	MOVE("움직일 때 사용");
	
	@Getter
	private final String name;
	
	public static EffectShow getByName(String name) {
		for(EffectShow shape : values()) {
			if(shape.getName().equals(name)) return shape;
		}
		
		return null;
	}
	
}