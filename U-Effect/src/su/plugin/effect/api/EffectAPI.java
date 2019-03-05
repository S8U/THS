package su.plugin.effect.api;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import su.plugin.core.bukkit.enumeration.Particle;
import su.plugin.effect.api.manager.GUIManager;
import su.plugin.effect.api.manager.PlayerManager;
import su.plugin.effect.api.manager.SQLManager;

public class EffectAPI {
	
	@Getter
	private static PlayerManager playerManager;
	@Getter
	private static GUIManager GUIManager;
	@Getter
	private static SQLManager SQLManager;
	
	@Getter
	private static List<Particle> particles = new ArrayList<>();
	
	public EffectAPI() {
		playerManager = new PlayerManager();
		GUIManager = new GUIManager();
		SQLManager = new SQLManager();
	}
	
}