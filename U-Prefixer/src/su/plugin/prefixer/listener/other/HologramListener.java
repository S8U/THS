package su.plugin.prefixer.listener.other;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.gmail.filoghost.holographicdisplays.api.Hologram;

import su.plugin.core.bukkit.api.event.player.PlayerMoveLocationEvent;
import su.plugin.core.common.api.event.UnregisterableListener;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.prefixer.PrefixerPlugin;
import su.plugin.prefixer.api.PrefixerAPI;
import su.plugin.prefixer.api.object.PrefixPlayer;

public class HologramListener extends PacketAdapter implements Listener, UnregisterableListener {
	
	private PrefixerAPI api = PrefixerPlugin.getApi();
	
	public HologramListener() {
		super(PrefixerPlugin.getInstance(), new PacketType[] {PacketType.Play.Server.REL_ENTITY_MOVE, PacketType.Play.Server.REL_ENTITY_MOVE_LOOK, PacketType.Play.Server.ENTITY_VELOCITY, PacketType.Play.Server.ENTITY_TELEPORT});
	}
	
    public void onPacketSending(PacketEvent packetEvent) {
        if(packetEvent.isAsync() || packetEvent.isAsynchronous()) return;
        
        int n = (Integer)packetEvent.getPacket().getIntegers().read(0);
        
        World world = packetEvent.getPlayer().getWorld();
        
        Entity entity = ProtocolLibrary.getProtocolManager().getEntityFromID(world, n);
        if(!(entity instanceof Player)) return;
        
        Player p = (Player) entity;
        
        PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(PlayerKey.getPlayerKey(p.getName()));
        if(pp == null || !pp.hasHologram()) return;
        
        pp.getHologram().teleport(pp.getMainPrefixLocation());
    }
    
    //
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	Player p = e.getPlayer();
    	PlayerKey playerKey = PlayerKey.getPlayerKey(p.getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	
    	Hologram holo = api.getHologramManager().getHologram(playerKey);
    	holo.delete();
    	
    	api.getHologramManager().removeHologram(playerKey);
    	api.getHologramManager().removeMoveTime(playerKey);
    }
    
    //
    
    @EventHandler
    public void onChangedWorld(PlayerChangedWorldEvent e) {
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	api.getHologramManager().getHologram(playerKey).teleport(PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey).getMainPrefixLocation());
    }
    
    @EventHandler
    public void onMove(PlayerMoveLocationEvent e) {
    	if(!api.isHideHologramOnMove()) return;
    	
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
		PrefixPlayer pp = api.getPlayerManager().getPrefixPlayer(playerKey);
		
		if(!pp.hasHologram() || !api.isHideHologramOnMove()) return;
		api.getHologramManager().updateMoveTime(playerKey);
		
		if(!pp.getHologram().getVisibilityManager().isVisibleTo(e.getPlayer())) return;
		pp.getHologram().getVisibilityManager().hideTo(e.getPlayer());
    }
    
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	api.getHologramManager().getHologram(playerKey).teleport(PrefixerAPI.getPlayerManager().getPrefixPlayer(playerKey).getMainPrefixLocation());
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getEntity().getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	api.getHologramManager().getHologram(playerKey).getVisibilityManager().setVisibleByDefault(false);
    	api.getHologramManager().getHologram(playerKey).getVisibilityManager().hideTo(e.getEntity());
    }
    
    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	api.getHologramManager().getHologram(playerKey).getVisibilityManager().setVisibleByDefault(true);
    	api.getHologramManager().getHologram(playerKey).getVisibilityManager().showTo(e.getPlayer());
    }
    
    @EventHandler
    public void onTogleSneak(PlayerToggleSneakEvent e) {
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	else if(e.isSneaking()) {
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().setVisibleByDefault(false);
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().hideTo(e.getPlayer());
    	} else {
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().setVisibleByDefault(true);
        	api.getPlayerManager().getPrefixPlayer(playerKey).getHologram().teleport(api.getPlayerManager().getPrefixPlayer(playerKey).getMainPrefixLocation());
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().showTo(e.getPlayer());
    	}
    }
    
    @EventHandler
    public void onGameModeChange(PlayerGameModeChangeEvent e) {
    	PlayerKey playerKey = PlayerKey.getPlayerKey(e.getPlayer().getName());
    	
    	if(!api.getHologramManager().existsHologram(playerKey)) return;
    	else if(e.getNewGameMode() == GameMode.SPECTATOR) {
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().setVisibleByDefault(false);
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().hideTo(e.getPlayer());
    	} else {
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().setVisibleByDefault(true);
        	api.getHologramManager().getHologram(playerKey).getVisibilityManager().showTo(e.getPlayer());
    	}
    }
    
}