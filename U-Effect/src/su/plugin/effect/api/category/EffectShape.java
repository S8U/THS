package su.plugin.effect.api.category;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum EffectShape {
	
	ROUND("원"),
	POLYGON("각형"),
	STAR("별"),
	WING("날개");
	
	@Getter
	private final String name;
	
	public static EffectShape getByName(String name) {
		for(EffectShape shape : values()) {
			if(shape.getName().equals(name)) return shape;
		}
		
		return null;
	}
	
}