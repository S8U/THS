package su.plugin.effect.api.object.effect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.enumeration.Particle;
import su.plugin.effect.api.category.EffectShape;

@Setter
@Getter
public class DecoEffect extends PlayerEffect {
	
	private List<Location> locations = new ArrayList<>();
	
	public DecoEffect(Particle particle, EffectShape shape, double size, int amount) {
		super(null, particle, shape, size, amount);
	}
	
}