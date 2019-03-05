package su.plugin.core.bukkit.api.bossbar.entity;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import protocolsupport.api.ProtocolSupportAPI;
import protocolsupport.api.ProtocolVersion;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.enumeration.NMSVersion;
import su.plugin.core.bukkit.api.util.KReflectionUtil;

@RequiredArgsConstructor
public abstract class EntityBar {
	
    @Getter
    protected final UUID uuid;
    
    @Getter
    protected String text;
    
    @Getter
    protected int health;
    
    @Getter
    protected final int maxHealth = 300;
    
    @Getter
    protected Location location;
    
    @Getter
    protected int entityId = -1;
    
    @Getter
    protected Object entityObject;
    
    //
    
    public EntityBar(UUID uuid, Location location, String text) {
    	this.uuid = uuid;
    	
    	this.location = location;
    	this.text = text == null ? null : ChatColor.translateAlternateColorCodes('&', text);
    	
    	health = maxHealth;
    }
    
    //
    
    public Player getPlayer() {
    	return Bukkit.getPlayer(uuid);
    }
    
    //
    
    public void setText(String text) {
    	this.text = ChatColor.translateAlternateColorCodes('&', text);
    	
    	sendPacket(true);
    }
    
    public void setHealth(int health) {
    	this.health = health;
    	
    	sendPacket(true);
    }
    
    public void setProgress(double progress) {
    	setHealth((int) (progress * maxHealth));
    }
    
    public void setLocation(Location location) {
    	boolean wc = !this.location.getWorld().equals(location.getWorld());
    	
    	this.location = getWitherLocation();
    	
    	sendPacket(wc);
    }
    
    //
    
    private Location getWitherLocation() {
    	Player player = getPlayer();
    	
    	Location loc = player.getLocation();
    	
    	boolean isBefore1_8 = false;
    	boolean useEntity = false;
		if(KCore.isUseProtocolSupport()) {
    		ProtocolVersion pv = ProtocolSupportAPI.getProtocolVersion(player);
    		
    		isBefore1_8 = pv.isBefore(ProtocolVersion.MINECRAFT_1_8);
    		useEntity = pv.isBefore(ProtocolVersion.MINECRAFT_1_9);
    	} else {
    		isBefore1_8 = KCore.getNMSVersion().isBefore(NMSVersion.v1_8_R1);
    		useEntity = KCore.getNMSVersion().isBefore(NMSVersion.v1_9_R1);
    	}
		
		if(!useEntity) return null;
		
		else if(isBefore1_8) {
			loc.subtract(0, 300, 0);
		} else {
			loc.add(loc.getDirection().multiply(Bukkit.getViewDistance() * 11));
		}
		
    	return loc;
    }
    
    //
    
    public void sendPacket(boolean refresh) {
    	if(refresh) {
    		sendDestroyPacket();
    	} else if(hasPacket()) {
    		if(getPlayer().getWorld().equals(location.getWorld())) {
    			sendTeleportPacket(location);
    			return;
    		}
    		
    		sendDestroyPacket();
    	}
    	
    	Object[] pks = getCreatePacket();
    	
    	entityId = (int) pks[1];
    	KReflectionUtil.sendPacket(getPlayer(), pks[0]);
    }
    
    public void sendDestroyPacket() {
    	if(!hasPacket()) return;
    	
    	KReflectionUtil.sendPacket(getPlayer(), getDestroyPacket());
    	
    	entityId = -1;
    }
    
    public void sendTeleportPacket(Location location) {
    	if(!hasPacket()) return;
    	
    	KReflectionUtil.sendPacket(getPlayer(), getTeleportPacket(location));
    }

    public boolean hasPacket() {
    	return entityId != -1;
    }
    
    //
	
	protected abstract Object[] getCreatePacket();
	
    protected abstract Object getDestroyPacket();
    
	protected abstract Object getTeleportPacket(Location location);
	
    protected abstract void configureBossBar();
    
}