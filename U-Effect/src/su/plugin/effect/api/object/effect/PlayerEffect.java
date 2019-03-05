package su.plugin.effect.api.object.effect;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import su.plugin.core.bukkit.enumeration.Particle;
import su.plugin.effect.api.category.EffectRotate;
import su.plugin.effect.api.category.EffectShape;
import su.plugin.effect.api.category.EffectShow;
import su.plugin.effect.api.object.EffectPlayer;

@Setter
@Getter
public class PlayerEffect extends Effect {
	
	private final EffectPlayer effectPlayer;
	
	//
	
	private int amount = 1;
	
	private double angle, height = 1, size = 1, rotateSpeed = 1;
	
	private String[] wingShape = new String[] {
			"x x x x x x x x x",
			"x o o x x x o o x",
			"x x o o x o o x x",
			"x x x o o o x x x",
			"x x o o x o o x x",
			"x x x x x x x x x"
		};
		
	private EffectRotate rotate = EffectRotate.NONE;
	
	private EffectShape shape = EffectShape.ROUND;
	
	private EffectShow show = EffectShow.ALWAYS;
	
	public PlayerEffect(EffectPlayer effectPlayer, Particle particle, EffectShape shape, double size, int amount) {
		this.effectPlayer = effectPlayer;
		this.particle = particle;
		this.shape = shape;
		this.size = size;
		this.amount = amount;
	}
	
	//
	
	@Override
	public void show(Location loc) {
		if(shape == null || particle == null) return;
		
		switch(shape) {
		case ROUND:
			if(rotate != EffectRotate.NONE) {
				angle = angle == 360 ? 0 : angle + (rotate == EffectRotate.LEFT ? -(rotateSpeed / 5) : (rotateSpeed / 5)) / 100;
			}
			
			for(int i = 0; i < amount; i++) {
				particle.spawn(new Location(loc.getWorld(), loc.getX() + size / 5 * Math.cos(angle + i * Math.toRadians(360 / amount)), loc.getY() + (height - 1) / 5, loc.getZ() + size / 5 * Math.sin(angle + i * Math.toRadians(360 / amount))), 0, 1);
			}
			
			break;
		case POLYGON:
			Location[] pots = new Location[amount];
			
			if(rotate != EffectRotate.NONE) {
				angle = angle == 360 ? 0 : angle + (rotate == EffectRotate.LEFT? -(rotateSpeed / 5) : (rotateSpeed / 5)) / 100;
			}
			
			for(int i = 0; i < amount; i++) {
				pots[i] = new Location(loc.getWorld(), loc.getX() + size / 5 * Math.cos(angle + i * Math.toRadians(360 / amount)), loc.getY() + (height - 1) / 5, loc.getZ() + size / 5 * Math.sin(angle + i * Math.toRadians(360 / amount)));
				if(i > 0) {
					drawLine(pots[i - 1], pots[i]);
					if(i == amount - 1) {
						drawLine(pots[0], pots[i]);
					}
				}
			}
			
			break;
		case STAR:
			pots = new Location[5];
			if(rotate != EffectRotate.NONE) {
				angle = angle == 360 ? 0 : angle + (rotate == EffectRotate.LEFT ? -(rotateSpeed / 5) : (rotateSpeed / 5)) / 100;
				for(int i = 0; i < 5; i++) {
					pots[i] = new Location(loc.getWorld(), loc.getX() + size / 5 * Math.cos(angle + i * Math.toRadians(360 / 5)), loc.getY() + (height - 1) / 5, loc.getZ() + size / 5 * Math.sin(angle + i * Math.toRadians(360 / 5)));
				}
			} else {
				for(int i = 0; i < 5; i++) {
					pots[i] = new Location(loc.getWorld(), loc.getX() + size / 5 * Math.cos(i * Math.toRadians(360 / 5)), loc.getY() + (height - 1) / 5, loc.getZ() + size / 5 * Math.sin(i * Math.toRadians(360 / 5)));
				}
			}
			
			drawLine(pots[0], pots[2]);
			drawLine(pots[0], pots[3]);
			drawLine(pots[1], pots[3]);
			drawLine(pots[1], pots[4]);
			drawLine(pots[2], pots[4]);
			break;
		case WING:
			Location bLoc = loc;
			bLoc.setX(loc.getX() + 0.35 * Math.cos(Math.toRadians(loc.getYaw() - 90)));
			bLoc.setZ(loc.getZ() + 0.35 * Math.sin(Math.toRadians(loc.getYaw() - 90)));
			
			int length = wingShape[0].replace(" ", "").length();
			double ty = bLoc.getY() + wingShape.length / 2 * (size / 20);
			double y = 0;
			for(String s : wingShape) {
				String[] ss = s.split(" ");
				double x = - length / 2 * (size / 20);
				for(String t : ss) {
					if(t.equals("o")) {
						particle.spawn(bLoc.getX() + x * Math.cos(Math.toRadians(bLoc.getYaw())), ty + y + (height - 1) / 5, bLoc.getZ() + x * Math.sin(Math.toRadians(bLoc.getYaw())), 0, 1);
					}
					x += size / 20;
				}
				y -= size / 20;
			}
			break;
		}
	}
	
	private void drawLine(Location pos1, Location pos2) {
		double xLength = pos1.getX() - pos2.getX();
		double zLength = pos1.getZ() - pos2.getZ();
		for (int i = 0; i <= amount; i++) {
			particle.spawn(pos1.getX() - xLength / amount * i, pos1.getY() + (height - 1) / 5, pos1.getZ() - zLength / amount * i, 0, 1);
		}
	}
	
}