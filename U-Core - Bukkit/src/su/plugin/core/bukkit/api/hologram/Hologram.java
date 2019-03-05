package su.plugin.core.bukkit.api.hologram;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.util.KReflectionUtil;

@NoArgsConstructor
public class Hologram {
	
    private static Class<?> craftWorld, nmsWorld, armorStand, entityLiving, spawnPacket, destroyPacket, teleportPacket;

    static {
        craftWorld = KReflectionUtil.getCraftBukkitClass("CraftWorld");
        nmsWorld = KReflectionUtil.getNMSClass("World");
        armorStand = KReflectionUtil.getNMSClass("EntityArmorStand");
        entityLiving = KReflectionUtil.getNMSClass("EntityLiving");
        spawnPacket = KReflectionUtil.getNMSClass("PacketPlayOutSpawnEntityLiving");
        destroyPacket = KReflectionUtil.getNMSClass("PacketPlayOutEntityDestroy");
        teleportPacket = KReflectionUtil.getNMSClass("PacketPlayOutEntityTeleport");
    }
    
    @Getter
    private Location location;
    
    private static final double OFFSET = 0.23, Y = - 0.3;
    
    @Setter
    @Getter
    private Sort sortFrom = Sort.BOTTOM;
    
    @Getter
    private List<UUID> playerUuids = new ArrayList<>();
    
    @Getter
    private List<String> lines = new ArrayList<>();
    
    @Getter
    private HashMap<Integer, Object> packets = new HashMap<>();
    
    public Hologram(Location location, String... text) {
    	this(location, Sort.BOTTOM, text);
    }
    
    public Hologram(Location location, Sort sortFrom, String... text) {
    	this(location, sortFrom, Arrays.asList(text));
    }
    
    public Hologram(Location location, List<String> text) {
    	this(location, Sort.BOTTOM, text);
    }
    
    public Hologram(Location location, Sort sortFrom, List<String> text) {
    	this.location = location;
    	this.sortFrom = sortFrom;
    	
    	for(String t : text) {
    		lines.add(ChatColor.translateAlternateColorCodes('&', t));
    	}
    	
    	createNewPackets();
    }
    
    public void setLine(int num, String text) {
    	lines.set(num, ChatColor.translateAlternateColorCodes('&', text));
    	
    	refresh();
    }
    
    public void setLines(String... text) {
    	setLines(Arrays.asList(text));
    }
    
    public void setLines(List<String> text) {
    	lines.clear();
    	
    	for(String t : text) {
    		lines.add(ChatColor.translateAlternateColorCodes('&', t));
    	}
    	
    	refresh();
    }
    
    public void addLine(String text) {
    	lines.add(ChatColor.translateAlternateColorCodes('&', text));
    	
    	refresh();
    }
    
    public void addLine(String... text) {
    	for(String t : text) {
    		lines.add(ChatColor.translateAlternateColorCodes('&', t));
    	}
    	
    	refresh();
    }
    
    public void setLocation(Location location) {
    	this.location = location;
    	
    	teleport(location);
    }
    
    public boolean isWatching(Player player) {
    	return isWatching(player.getUniqueId());
    }
    
    public boolean isWatching(UUID uuid) {
    	return playerUuids.contains(uuid);
    }
    
    public List<Player> getPlayers() {
    	List<Player> list = new ArrayList<>();
    	
    	for(UUID uuid : playerUuids) {
    		Player p = Bukkit.getPlayer(uuid);
    		if(p != null) list.add(p);
    	}
    	
    	return list;
    }
    
    public void show() {
    	show(KCore.getOnlinePlayers());
    }
    
    public void show(Player... players) {
    	show(Arrays.asList(players));
    }
    
    public void show(List<Player> players) {
    	show(players, true);
    }
    
    private void show(List<Player> players, boolean save) {
    	if(location == null) {
    		throw new NullPointerException("좌표가 설정되지 않았습니다.");
    	} else if(lines.isEmpty()) {
    		throw new IllegalArgumentException("텍스트가 설정되지 않았습니다.");
    	}
    	
    	double oy = sortFrom == Sort.TOP ? 0 : (sortFrom == Sort.MIDDLE ? OFFSET * lines.size() / 2 : OFFSET * (lines.size() - 1));
    	
    	Location loc = location.clone().add(0, oy + Y, 0);
    	
    	createNewPackets();
    	
    	for(Object packet : packets.values()) {
            for(Player p : players) {
            	if(save && isWatching(p)) continue;
            	
            	KReflectionUtil.sendPacket(p, packet);
            }
            
            loc.subtract(0, OFFSET, 0);
    	}
        
    	if(!save) return;
    	
        for(Player p : players) {
        	if(isWatching(p)) continue;
        	
        	playerUuids.add(p.getUniqueId());
        }
    }
    
    public void hide() {
    	hide(getPlayers());
    }
    
    public void hide(Player... players) {
    	hide(Arrays.asList(players));
    }
    
    public void hide(List<Player> players) {
    	hide(players, true);
    }
    
    private void hide(List<Player> players, boolean save) {
        Object packet = null;
        
        for(int id : packets.keySet()) {
        	packet = getDestroyPacket(id);
        	if(packet == null) continue;
        	
        	for(Player p : players) {
            	if(save && !isWatching(p)) continue;
        		
        		KReflectionUtil.sendPacket(p, packet);
        	}
        }
        
		if(!save) return;
		
    	for(Player p : players) {
        	if(!isWatching(p)) continue;
        	
    		playerUuids.remove(p.getUniqueId());
    	}
    }
    
    public void teleport(Location location) {
    	double oy = sortFrom == Sort.TOP ? 0 : (sortFrom == Sort.MIDDLE ? OFFSET * lines.size() / 2 : OFFSET * (lines.size() - 1));
    	Location loc = location.clone().add(0, oy + Y, 0);
    	
    	Object packet = null;
    	
        for(int id : getPackets().keySet()) {
        	packet = getTeleportPacket(id, loc);
        	if(packet == null) continue;
        	
        	for(Player p : getPlayers()) {
        		KReflectionUtil.sendPacket(p, packet);
        	}
        	
            loc.subtract(0, OFFSET, 0);
        }
    }
    
    public void refresh() {
    	List<Player> players = getPlayers();
    	if(players.isEmpty()) return;
    	
    	show(players, false);
    }
    
    //
    
    public void createNewPackets() {
    	if(location == null) {
    		throw new NullPointerException("좌표가 설정되지 않았습니다.");
    	} else if(lines.isEmpty()) {
    		throw new IllegalArgumentException("텍스트가 설정되지 않았습니다.");
    	}
    	
    	for(int id : packets.keySet()) {
    		Object packet = getDestroyPacket(id);
    		
    		for(Player p : getPlayers()) {
    			KReflectionUtil.sendPacket(p, packet);
    		}
    	}
    	
        packets.clear();
        
    	double oy = sortFrom == Sort.TOP ? 0 : (sortFrom == Sort.MIDDLE ? OFFSET * lines.size() / 2 : OFFSET * (lines.size() - 1));
    	Location loc = location.clone().add(0, oy + Y, 0);
    	
    	for (String str : lines) {
            Object[] packet = getCreatePacket(loc, str);
            
            packets.put((Integer) packet[1], packet[0]);
            
    		for(Player p : getPlayers()) {
    			KReflectionUtil.sendPacket(p, packet[0]);
    		}
            
            loc.subtract(0, OFFSET, 0);
        }
    }
    
    @SneakyThrows(Exception.class)
    private Object[] getCreatePacket(Location location, String text) {
        Object entityObject = armorStand.getConstructor(nmsWorld).newInstance(craftWorld.getMethod("getHandle").invoke(craftWorld.cast(location.getWorld())));
        Object id = entityObject.getClass().getMethod("getId").invoke(entityObject);

        configureHologram(entityObject, text, location);

        return new Object[]{spawnPacket.getConstructor(entityLiving).newInstance(entityObject), id};
    }

    @SneakyThrows(Exception.class)
    private Object getDestroyPacket(int id) {
        return destroyPacket.getConstructor(int[].class).newInstance(new int[]{id});
    }
    
    @SneakyThrows(Exception.class)
	private Object getTeleportPacket(int id, Location location) {
    	Object packet = teleportPacket.newInstance();
    	
    	Field a = KReflectionUtil.getField(teleportPacket, "a");
    	a.setAccessible(true);
    	a.set(packet, id);
    	
    	Field b = KReflectionUtil.getField(teleportPacket, "b");
    	b.setAccessible(true);
    	b.set(packet, location.getX());
    	
    	Field c = KReflectionUtil.getField(teleportPacket, "c");
    	c.setAccessible(true);
    	c.set(packet, location.getY());
    	
    	Field d = KReflectionUtil.getField(teleportPacket, "d");
    	d.setAccessible(true);
    	d.set(packet, location.getZ());
    	
    	Field e = KReflectionUtil.getField(teleportPacket, "e");
    	e.setAccessible(true);
    	e.set(packet, (byte) ((int) location.getYaw() * 256 / 360));
    	
    	Field f = KReflectionUtil.getField(teleportPacket, "f");
    	f.setAccessible(true);
    	f.set(packet, (byte) ((int) location.getPitch() * 256 / 360));
    	
    	Field g = KReflectionUtil.getField(teleportPacket, "g");
    	g.setAccessible(true);
    	g.set(packet, false);
    	
    	return packet;
	}

    @SneakyThrows(Exception.class)
    private void configureHologram(Object entityObject, String text, Location location) {
        Method setCustomName = KReflectionUtil.getMethod(entityObject.getClass(), "setCustomName");
        Method setCustomNameVisible = KReflectionUtil
            .getMethod(entityObject.getClass(), "setCustomNameVisible");
        Method setNoGravity = KReflectionUtil.getMethod(entityObject.getClass(), "setNoGravity");
        Method setLocation = KReflectionUtil.getMethod(entityObject.getClass(), "setLocation");
        Method setInvisible = KReflectionUtil.getMethod(entityObject.getClass(), "setInvisible");
        Method setSmall = KReflectionUtil.getMethod(entityObject.getClass(), "setSmall");
        Method setBasePlate = KReflectionUtil.getMethod(entityObject.getClass(), "setBasePlate");
        Method setMarker = KReflectionUtil.getMethod(entityObject.getClass(), "setMarker");

        setCustomName.invoke(entityObject, text);
        setCustomNameVisible.invoke(entityObject, true);
        setNoGravity.invoke(entityObject, true);
        setLocation.invoke(entityObject, location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        setInvisible.invoke(entityObject, true);
        setSmall.invoke(entityObject, true);
        setBasePlate.invoke(entityObject, false);
        setMarker.invoke(entityObject, true);
    }
	
}