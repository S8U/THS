package su.plugin.effect.api.object;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.enumeration.Particle;
import su.plugin.core.common.player.PlayerKey;
import su.plugin.effect.api.object.effect.Effect;

@RequiredArgsConstructor
@Getter
public class EffectPlayer {
	
	private final PlayerKey playerKey;
	
	private List<Particle> particles = new ArrayList<>();
	
	private List<Effect> effects = new ArrayList<>();
	
}