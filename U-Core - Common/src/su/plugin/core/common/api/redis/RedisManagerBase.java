package su.plugin.core.common.api.redis;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;
import su.plugin.core.common.api.plugin.UPlugin;

public class RedisManagerBase {
	
	@Setter
	@Getter
	private String redisAddress;
	
	@Setter
	@Getter
	private int redisPort;
	
	@Setter
	@Getter
	private boolean use, useUseOption;
	
	@Getter
	private JsonConfig jsonConfig;
	
	protected JedisPool pool;
	
	public void loadJsonConfig(UPlugin plugin) {
		createJsonConfig(plugin);
		
		if(useUseOption) {
			use = jsonConfig.getBoolean("사용");
		}
		redisAddress = jsonConfig.getString("주소");
		redisPort = jsonConfig.getInt("포트");
		loadJsonConfigOthers();
		
		Core.log("Redis 설정을 불러왔습니다.");
	}
	
	private void createJsonConfig(UPlugin plugin) {
		jsonConfig = new JsonConfig(new File(plugin.getDataFolder(), "redis-config.json")).load();
		
		if(useUseOption) {
			jsonConfig.addDefault("사용", false);
		}
		jsonConfig.addDefault("주소", "localhost");
		jsonConfig.addDefault("포트", 6379);
		createJsonConfigOthers();
		
		jsonConfig.save();
	}
	
	public void loadJsonConfigOthers() { }
	public void createJsonConfigOthers() { }
	
	public boolean connect(UPlugin plugin) {
		try {
			loadJsonConfig(plugin);
			if(useUseOption && !use) return false;
			
			pool = new JedisPool(redisAddress, redisPort);
			Core.log("Redis에 접속되었습니다.");
			
			return true;
		} catch(Exception e) {
			e.printStackTrace();
			
			Core.log("Redis에 연결할 수 없습니다,");
			return false;
		}
	}
	
	public void close() {
		try {
			if(!isConnected()) return;
			
			pool.close();
			
			Core.log("Redis과의 연결을 종료했습니다.");
		} catch(Exception e) {
			e.printStackTrace();
			
			Core.log("Redis과의 연결을 종료하는 중 오류가 발생했습니다.");
		}
	}
	
	public boolean isConnected() {
		return pool != null && !pool.isClosed();
	}
	
	public Jedis getResource() {
		return pool.getResource();
	}
	
}