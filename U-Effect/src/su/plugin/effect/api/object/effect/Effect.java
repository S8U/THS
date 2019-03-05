package su.plugin.effect.api.object.effect;

import org.bukkit.Location;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import su.plugin.core.bukkit.enumeration.Particle;
import su.plugin.effect.api.category.EffectType;

@NoArgsConstructor
@Setter
@Getter
public abstract class Effect {
	
	protected String name = "이펙트";
	
	protected boolean enable = true;
	
	protected EffectType type;
	
	protected Particle particle;
	
	//
	
	public Effect(String name, Particle particle) {
		this.name = name;
		this.particle = particle;
	}
	
	public abstract void show(Location location);
	
}