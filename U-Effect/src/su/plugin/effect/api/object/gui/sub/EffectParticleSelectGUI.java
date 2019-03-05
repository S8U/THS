package su.plugin.effect.api.object.gui.sub;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import su.plugin.core.bukkit.enumeration.Particle;
import su.plugin.core.bukkit.event.gui.IconClickEvent;
import su.plugin.core.bukkit.gui.GUI;
import su.plugin.core.bukkit.gui.Icon;
import su.plugin.core.bukkit.util.ItemUtil;
import su.plugin.effect.api.EffectAPI;
import su.plugin.effect.api.object.effect.Effect;

@Getter
public class EffectParticleSelectGUI extends GUI {
	
	private Effect effect;
	
	private List<Particle> particles;
	
	public EffectParticleSelectGUI(Effect effect, List<Particle> particles) {
		super("EffectParticleSelect", "파티클 선택", (int) Math.ceil(EffectAPI.getParticles().size() / 9));
		
		this.effect = effect;
		this.particles = particles;
		
		//
		
		for(int i = 0; i < EffectAPI.getParticles().size(); i++) {
			final int n = i;
			
			Icon particleIcon = new Icon() {
				private Particle particle = EffectAPI.getParticles().get(n);
				
				@Override
				protected ItemStack updateItem() {
					return ItemUtil.makeItem(particles.contains(particle) ? 401 : 402, "§f§l" + particle.getName(), "§f§l클릭 시 변경됩니다.");
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					effect.setParticle(particle);
					
					// Open Effect Setting GUI
				}
			};
			
			setIcon(i, particleIcon);
		}
		
		//
		
		updateAsynchronously();
	}
	
}