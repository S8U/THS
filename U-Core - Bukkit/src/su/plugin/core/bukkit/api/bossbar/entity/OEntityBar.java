package su.plugin.core.bukkit.api.bossbar.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.Location;

import lombok.SneakyThrows;
import su.plugin.core.bukkit.api.util.KReflectionUtil;

public class OEntityBar extends EntityBar {
	
	private static Class<?> craftWorld, nmsWorld, entityWither, entityLiving, spawnPacket, destroyPacket, teleportPacket;

	
    //
    
    public OEntityBar(UUID uuid) {
		super(uuid, null, null);
	}
    
    public OEntityBar(UUID uuid, Location location, String text) {
    	super(uuid, location, text);
    	
        craftWorld = KReflectionUtil.getCraftBukkitClass("CraftWorld");
        nmsWorld = KReflectionUtil.getNMSClass("World");
        entityWither = KReflectionUtil.getNMSClass("EntityWither");
        entityLiving = KReflectionUtil.getNMSClass("EntityLiving");
        spawnPacket = KReflectionUtil.getNMSClass("Packet24MobSpawn");
        destroyPacket = KReflectionUtil.getNMSClass("Packet29DestroyEntity");
        teleportPacket = KReflectionUtil.getNMSClass("Packet34EntityTeleport");
    }
    
    //
    
    @Override
    @SneakyThrows(Exception.class)
    protected Object[] getCreatePacket() {
    	if(location == null) {
    		throw new NullPointerException("좌표가 설정되지 않았습니다.");
    	}
    	
        entityObject = entityWither.getConstructor(nmsWorld).newInstance(craftWorld.getMethod("getHandle").invoke(craftWorld.cast(location.getWorld())));
        Object id = entityObject.getClass().getMethod("getId").invoke(entityObject);

        configureBossBar();

        return new Object[]{spawnPacket.getConstructor(entityLiving).newInstance(entityObject), id};
    }
    
    @Override
    @SneakyThrows(Exception.class)
    protected Object getDestroyPacket() {
        return destroyPacket.getConstructor(int[].class).newInstance(new int[]{entityId});
    }
    
    @Override
    @SneakyThrows(Exception.class)
	protected Object getTeleportPacket(Location location) {
    	Object packet = teleportPacket.newInstance();
    	
    	Field a = KReflectionUtil.getField(teleportPacket, "a");
    	a.setAccessible(true);
    	a.set(packet, entityId);
    	
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
    
    @Override
    @SneakyThrows(Exception.class)
    protected void configureBossBar() {
        Method setCustomName = KReflectionUtil.getMethod(entityObject.getClass(), "setCustomName");
        Method setCustomNameVisible = KReflectionUtil
            .getMethod(entityObject.getClass(), "setCustomNameVisible");
        Method setLocation = KReflectionUtil.getMethod(entityObject.getClass(), "setLocation");
        Method setHealth = KReflectionUtil.getMethod(entityObject.getClass(), "setHealth");
        Method setInvisible = KReflectionUtil.getMethod(entityObject.getClass(), "setInvisible");
        Method setNoGravity = KReflectionUtil.getMethod(entityObject.getClass(), "setNoGravity");

        setCustomName.invoke(entityObject, text == null ? "" : text);
        setCustomNameVisible.invoke(entityObject, true);
        setLocation.invoke(entityObject, location.getX(), location.getY(), location.getZ(), 0.0F, 0.0F);
        setHealth.invoke(entityObject, health);
        setInvisible.invoke(entityObject, true);
        setNoGravity.invoke(entityObject, true);
    }
	
}