package su.plugin.lobbysystem.listener;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import su.plugin.lobbysystem.api.LobbySystemAPI;

public class DoubleJumpListener implements Listener {

  @EventHandler
  public void onMove(PlayerMoveEvent e) {
    if(!LobbySystemAPI.isUseDoubleJump()) return;

    Block b = e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN);
    if((e.getPlayer().getGameMode() != GameMode.SURVIVAL && e.getPlayer().getGameMode() != GameMode.ADVENTURE)
        || b.getType() == Material.WATER || b.getType() == Material.WATER_LILY
        || b.getType() == Material.LAVA || b.getType() == Material.STATIONARY_LAVA
        || b.getType() == Material.AIR) return;

    e.getPlayer().setAllowFlight(true);
  }

  @EventHandler
  public void onFly(PlayerToggleFlightEvent e) {
    if(!LobbySystemAPI.isUseDoubleJump() || e.getPlayer().getGameMode() != GameMode.SURVIVAL && e.getPlayer().getGameMode() != GameMode.ADVENTURE) return;

    e.getPlayer().setAllowFlight(false);
    e.getPlayer().setFlying(false);
    e.getPlayer().setVelocity(e.getPlayer().getLocation().getDirection().multiply(LobbySystemAPI.getDoubleJumpForward()).setY(LobbySystemAPI.getDoubleJumpUpward()));

    e.setCancelled(true);
  }

}