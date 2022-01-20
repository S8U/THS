package s8u.plugin.test;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import su.plugin.core.bukkit.api.enumeration.Particle;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.UCommandListener;

public class TestCommand implements Listener, UCommandListener {

  private Location left, right;

  @CommandHandler(
      name = "test",
      additional = "<angle> <distance>"
  )
  public void test(Player p, String[] args) {
    int angle = Integer.parseInt(args[0]);
    int distance = Integer.parseInt(args[1]);

    double x = p.getLocation().getX() + (Math.cos(Math.toRadians(angle)) * distance);
    double z = p.getLocation().getZ() + (Math.sin(Math.toRadians(angle)) * distance);

    Particle.SPELL_INSTANT.spawn(p, x, p.getLocation().getY(), z, 0 ,0 ,0 ,1, 10);

    Core.bc("ㅇ");


  }

  @CommandHandler(
      name = "test2",
      usage = "<xz angle> <xy angle> <distance>"
  )
  public void test2(Player p, String[] args) {
    int xzAngle = Integer.parseInt(args[0]);
    int xyAngle = Integer.parseInt(args[1]);
    int distance = Integer.parseInt(args[2]);

    double x1 = p.getLocation().getX() + (Math.cos(Math.toRadians(xyAngle)) * distance);
    double y1 = p.getLocation().getY() + (Math.sin(Math.toRadians(xyAngle)) * distance);


    // Particle.SPELL_INSTANT.spawn(p, x, p.getLocation().getY(), z, 0 ,0 ,0 ,1, 10);

    Core.bc("ㅇ");


  }

  @EventHandler
  public void onInteract(PlayerInteractEvent e) {
    if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
      left = e.getClickedBlock().getLocation();
      Core.bc("Left Click");
    } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
      right = e.getClickedBlock().getLocation();
      Core.bc("Right Click");
    }
  }

}
