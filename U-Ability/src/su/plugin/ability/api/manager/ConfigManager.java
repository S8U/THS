package su.plugin.ability.api.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.bukkit.api.config.json.KJsonConfig;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.Core;

public class ConfigManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private File configFile = new File(AbilityPlugin.getInstance().getDataFolder(), "config.json");
	@Getter
	private File barConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "bar-config.json");
	@Getter
	private File autoConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "auto-config.json");
	@Getter
	private File startItemConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "startitem-config.json");
	@Getter
	private File rankItemConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "rankitem-config.json");
	@Getter
	private File supplyConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "supply-config.json");
	@Getter
	private File mapConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "map-config.json");
	@Getter
	private File spawnConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "spawn-config.json");
	@Getter
	private File blackListConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "blacklist-config.json");
	@Getter
	private File injectConfigFile = new File(AbilityPlugin.getInstance().getDataFolder(), "inject-config.json");
	
	@Getter
	private KJsonConfig config = new KJsonConfig(configFile).load();
	@Getter
	private KJsonConfig barConfig = new KJsonConfig(barConfigFile).load();
	@Getter
	private KJsonConfig autoConfig = new KJsonConfig(autoConfigFile).load();
	@Getter
	private KJsonConfig startItemConfig = new KJsonConfig(startItemConfigFile).load();
	@Getter
	private KJsonConfig rankItemConfig = new KJsonConfig(rankItemConfigFile).load();
	@Getter
	private KJsonConfig supplyConfig = new KJsonConfig(supplyConfigFile).load();
	@Getter
	private KJsonConfig mapConfig = new KJsonConfig(mapConfigFile).load();
	@Getter
	private KJsonConfig spawnConfig = new KJsonConfig(spawnConfigFile).load();
	@Getter
	private KJsonConfig blackListConfig = new KJsonConfig(blackListConfigFile).load();
	@Getter
	private KJsonConfig injectConfig = new KJsonConfig(injectConfigFile).load();
	
	public void createConfig() {
		if(configFile.exists()) return;
		config.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("config.yml"))).saveToString());
		config.save();
		
		Core.log("설정 파일이 생성되었습니다.");
	}
	
	
	public void createBarConfig() {
		if(barConfigFile.exists()) return;
		
		barConfig.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("bar-config.yml"))).saveToString());
		barConfig.save();
		
		Core.log("설정 파일이 생성되었습니다.");
	}
	
	public void createAutoConfig() {
		if(autoConfigFile.exists()) return;
		
		autoConfig.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("auto-config.yml"))).saveToString());
		autoConfig.save();
		
		Core.log("자동 게임 설정 파일이 생성되었습니다.");
	}
	
	public void createSupplyConfig() {
		if(supplyConfigFile.exists() && supplyConfigFile.length() > 1) return;
		
		supplyConfig.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("supply-config.yml"))).saveToString());
		supplyConfig.save();
		
		Core.log("보급품 설정 파일이 생성되었습니다.");
	}
	
	public void createStartItemConfig() {
		if(startItemConfigFile.exists() && startItemConfigFile.length() > 0) return;
		
		startItemConfig.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("startitem-config.yml"))).saveToString());
		startItemConfig.save();
		
		Core.log("시작 아이템 설정 파일이 생성되었습니다.");
	}
	
	public void createRankItemConfig() {
		if(rankItemConfigFile.exists() && rankItemConfigFile.length() > 0) return;

		rankItemConfig.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("rankitem-config.yml"))).saveToString());
		rankItemConfig.save();

		Core.log("등급 아이템 설정 파일이 생성되었습니다.");
	}
	
	@SneakyThrows(IOException.class)
	public void createMapConfig() {
		if(mapConfigFile.exists()) return;
		
		mapConfigFile.createNewFile();
		
		Core.log("맵 설정 파일이 생성되었습니다.");
	}
	
	@SneakyThrows(IOException.class)
	public void createSpawnConfig() {
		if(spawnConfigFile.exists()) return;
		
		spawnConfigFile.createNewFile();
		
		Core.log("스폰 설정 파일이 생성되었습니다.");
	}
	
	public void createBlackListConfig() {
		if(blackListConfigFile.exists()) return;
		
		blackListConfig.loadDefaultFromYaml(YamlConfiguration.loadConfiguration(new InputStreamReader(AbilityPlugin.getInstance().getResource("blacklist-config.yml"))).saveToString());
		blackListConfig.save();
		
		Core.log("능력 블랙리스트 설정 파일이 생성되었습니다.");
	}

	public void loadConfig() {
		createConfig();
		
		api.setRedrawCount(config.getInt("게임.능력 재추첨 기회"));
		api.setUseOverlap(config.getBoolean("게임.능력 중복 추첨"));
		/*if(api.isUseOverlap() && (api.isUseBitAbility() || api.isUsePhysicalFighters())) {
			api.setUseOverlap(false);
			Core.log("VisualAbility 기반 플러그인을 사용 할 경우 중복 추첨 기능을 사용할 수 없습니다.");
			Core.log("중복 추첨 기능이 비활성화됩니다.");
		}*/

		api.setQuitDeathHealth(config.getInt("게임.퇴장 시 탈락 체력(이하)"));
		api.setUseInfinityFoodLevel(config.getBoolean("게임.배고픔 무한"));
		api.setUseInfinityDurability(config.getBoolean("게임.내구도 무한"));
		api.setUseThisPluginAbility(config.getBoolean("게임.이 플러그인 능력 사용"));
		api.setUseSoundEffect(config.getBoolean("게임.효과음 사용"));
		api.setRainOff(config.getBoolean("게임.비 끄기"));

		api.setWaitingMOTD(ChatColor.translateAlternateColorCodes('&', config.getString("MOTD.대기 중")));
		api.setPlayingMOTD(ChatColor.translateAlternateColorCodes('&', config.getString("MOTD.게임 중")));
		
		api.setUseStartInvincibility(config.getBoolean("초반 무적.사용"));
		api.setStartInvincibilityCount(config.getInt("초반 무적.지속 시간"));
		
		api.setKickOnDeath(config.getBoolean("사망.강제 퇴장"));
		api.setBanOnDeath(config.getBoolean("사망.차단"));
		api.setEliminateOnDeath(config.getBoolean("사망.탈락"));
		api.setEliminateOnNatureDeath(config.getBoolean("사망.자연사 시 탈락 처리"));
		
		api.setAllowReconnect(config.getBoolean("재접속.허용"));
		api.setUseReconnectTimeLimit(config.getBoolean("재접속.재접속 허용 시간 제한"));
		api.setReconnectAllowCount(config.getInt("재접속.허용 시간(초)"));
		
		api.setUseInvincibilityOnWait(config.getBoolean("게임 시작 전 조작.무적"));
		api.setUsePvpProtectOnWait(config.getBoolean("게임 시작 전 조작.PVP 방지"));
		api.setUseBlockProtectOnWait(config.getBoolean("게임 시작 전 조작.블럭 보호"));
		api.setUseCommandProtectOnWait(config.getBoolean("게임 시작 전 조작.명령어 금지"));
		api.setProtectExceptionCommands(config.getStringList("게임 시작 전 조작.예외 명령어"));
		
		api.setUseSupply(config.getBoolean("보급품.사용"));
		api.setUseSupplyFirework(config.getBoolean("보급품.폭죽 사용"));
		api.setSupplyCreateCount(config.getInt("보급품.생성 간격(초)"));
		
		api.setUseLocationNotifyMessage(config.getBoolean("좌표 알림.메시지 사용"));
		api.setUseLocationNotifyFirework(config.getBoolean("좌표 알림.폭죽 사용"));
		api.setLocationNotifyCount(config.getInt("좌표 알림.간격(초)"));
		
		api.setUseFirstBlood(config.getBoolean("연속 킬.퍼스트 블러드 사용"));
		api.setUseDouble(config.getBoolean("연속 킬.더블 킬 사용"));
		api.setUseTriple(config.getBoolean("연속 킬.트리플 킬 사용"));
		api.setUseQuadra(config.getBoolean("연속 킬.쿼드라 킬 사용"));
		api.setUsePenta(config.getBoolean("연속 킬.펜타 킬 사용"));
		api.setDoubleCount(config.getInt("연속 킬.더블 킬 시간"));
		api.setTripleCount(config.getInt("연속 킬.트리플 킬 시간"));
		api.setQuadraCount(config.getInt("연속 킬.쿼드라 킬 시간"));
		api.setPentaCount(config.getInt("연속 킬.펜타 킬 시간"));
		
		api.setUseAssist(config.getBoolean("어시스트.사용"));
		api.setAssistCount(config.getInt("어시스트.시간(초)"));
		
		api.setKillMoney(config.getDouble("보상.킬"));
		api.setAssistMoney(config.getDouble("보상.어시스트"));
		api.setWinMoneyFormula(config.getString("보상.우승 보상 공식"));
		try {
			api.getScriptManager().getScriptEngine().eval("function getWinMoney(player_count) { return " + api.getWinMoneyFormula() + "; }");
		} catch (Exception e) {
			e.printStackTrace();
		}

		api.setFirstBloodMoney(config.getDouble("보상.퍼스트 블러드"));
		api.setDoubleMoney(config.getDouble("보상.더블 킬"));
		api.setTripleMoney(config.getDouble("보상.트리플 킬"));
		api.setQuadraMoney(config.getDouble("보상.쿼드라 킬"));
		api.setPentaMoney(config.getDouble("보상.펜타 킬"));
		
		api.setUseCommandProtectOnWatchMode(getConfig().getBoolean("관전.명령어 금지"));
		api.setWatchExceptionCommands(getConfig().getStringList("관전.예외 명령어"));
		
		api.setUseBungeeCord(getConfig().getBoolean("번지코드.사용"));
		api.setBungeeLobby(getConfig().getString("번지코드.로비"));
		
		Core.log("설정을 불러왔습니다.");
	}
	
	public void loadBarConfig() {
		createBarConfig();
		
		api.setUseBossBar(barConfig.getBoolean("보스바 사용"));
		api.setUseSideBar(barConfig.getBoolean("사이드바.사용"));
		api.setUseSideBarGameInfo(barConfig.getBoolean("사이드바.게임 정보 표시"));
		api.setUseWaitingQuickBar(barConfig.getBoolean("퀵바.대기 중 사용"));
		api.setUseWatchModeQuickBar(barConfig.getBoolean("퀵바.관전 중 사용"));
	}
	
	public void loadAutoConfig() {
		createAutoConfig();
		
		api.setUseAutoStart(autoConfig.getBoolean("자동 시작.사용"));
		api.setAutoStartCount(autoConfig.getInt("자동 시작.시작 인원"));
		api.setWinMinCount(autoConfig.getInt("자동 시작.우승 최소 시간"));
		
		api.setUseGameStartVote(autoConfig.getBoolean("시작 투표.사용"));
		api.setVoteTimeoutCount(autoConfig.getInt("시작 투표.시간 제한(초)"));
		api.setRevotePeriod(autoConfig.getInt("시작 투표.재투표 간격(초)"));

		api.setUseMapVote(autoConfig.getBoolean("맵 투표.사용"));
		
		api.setUseDrawTimeLimit(autoConfig.getBoolean("능력 확정 시간 제한.사용"));
		api.setDrawSkipCount(autoConfig.getInt("능력 확정 시간 제한.제한 시간(초)"));
		
		api.setUseAutoTeleport(autoConfig.getBoolean("텔레포트.사용"));
		api.setAutoTeleportCount(autoConfig.getInt("텔레포트.초반 무적 후 첫 텔레포트 시간"));
		api.setUseAutoTeleportRepeat(autoConfig.getBoolean("텔레포트.반복"));
		api.setAutoTeleportRepeatCount(autoConfig.getInt("텔레포트.반복 시간"));
		api.setTeleportToMapOnManyPlayer(autoConfig.getBoolean("인원이 많을 경우 맵으로 텔레포트"));
		api.setMapTeleportPlayerCount(autoConfig.getInt("맵 텔레포트 인원(이상)"));
		
		api.setUseAutoMapLimit(autoConfig.getBoolean("자동 맵 제한.맵 제한 사용"));
		api.setMapLimitRange(autoConfig.getInt("자동 맵 제한.맵 제한 범위 (반지름)"));
		api.setUseAutoTpAllMapLimit(autoConfig.getBoolean("자동 맵 제한.티피올 맵 제한 사용"));
		api.setTpAllLimitRange(autoConfig.getInt("자동 맵 제한.티피올 맵 제한 범위 (반지름)"));
		api.setUseMapLimitParticle(autoConfig.getBoolean("자동 맵 제한.파티클 사용"));
		api.setMapLimitParticle(autoConfig.getString("자동 맵 제한.파티클"));
		
		Core.log("자동 게임 설정을 불러왔습니다.");
	}
	
	public void loadKit() {
		try {
			File kits = new File(AbilityPlugin.getInstance().getDataFolder(), "kit");
			if(!kits.exists()) {
				kits.mkdir();
				Core.log("킷 폴더를 생성했습니다.");
				return;
			}
			
			if(kits.listFiles().length < 1) return;
			
			int load = 0;
			for(File kit : kits.listFiles()) {
				KJsonConfig kitconfig = (KJsonConfig) new KJsonConfig(kit).load();
				String invname = kit.getName().substring(0, kit.getName().length() - 5);
				Inventory inv = Bukkit.createInventory(null, 27, invname);
				for(int i = 0; i < 27; i++) {
					ItemStack item = kitconfig.getItemStack("kit." + i);
					if(item == null) continue;
					
					inv.setItem(i, item);
				}
				
				api.getKitManager().setKit(invname, inv);
				
				load++;
			}
			
			Core.log(load + "개의 킷을 불러왔습니다.");
		} catch(Exception e) {
			Core.log("킷을 불러올 수 없습니다.");
		}
	}
	
	public void loadStartItem() {
		createStartItemConfig();
		
		api.getItemManager().getStartItems().clear();
		
		api.setUseStartItem(startItemConfig.getBoolean("사용"));
		
		api.getItemManager().setStartLevel(startItemConfig.getInt("레벨"));
		
		api.getItemManager().setStartHelmet(ItemUtil.getItem(startItemConfig.getString("갑옷.투구")));
		api.getItemManager().setStartChestplate(ItemUtil.getItem(startItemConfig.getString("갑옷.갑옷")));
		api.getItemManager().setStartLeggings(ItemUtil.getItem(startItemConfig.getString("갑옷.레깅스")));
		api.getItemManager().setStartBoots(ItemUtil.getItem(startItemConfig.getString("갑옷.부츠")));
		
		api.getItemManager().setStartItems(getItems(startItemConfig.getStringList("아이템")));
		
		Core.log("시작 아이템을 불러왔습니다.");
	}
	
	public void loadRankItem() {
		createRankItemConfig();

		api.getItemManager().getRankItems().clear();
		
		api.setUseRankItem(rankItemConfig.getBoolean("사용"));
		
		for(String rank : rankItemConfig.getKeys("등급")) {
			api.getItemManager().setRankItem(rank, getItems(rankItemConfig.getStringList("등급." + rank)));
		}
		
		Core.log(api.getItemManager().getRankItems().size() + "개 등급의 아이템을 등록했습니다.");
	}
	
	public void loadSupply() {
		createSupplyConfig();
		
		api.getSupplyManager().getSupplies().clear();
		
		for(String name : getSupplyConfig().getKeys("보급품")) {
			api.getSupplyManager().setSupply(name, getItems(getSupplyConfig().getStringList("보급품." + name)));
		}
		
		Core.log(api.getSupplyManager().getSupplies().size() + "개의 보급품을 등록했습니다.");
	}
	
	private List<ItemStack> getItems(List<String> list) {
		List<ItemStack> itemList = new ArrayList<>();
		
		for(String items : list) {
			if(items.startsWith("<kit:")) {
				String kitName = items.substring(5, items.length() - 1);
				
				if(!api.getKitManager().existsKit(kitName)) {
					Core.log("'" + kitName + "' 킷을 찾을 수 없습니다.");
					continue;
				}
				
				for(ItemStack item : api.getKitManager().getKit(kitName)) {
					if(item == null) continue;
					itemList.add(item);
				}
			} else {
				String[] itemsp = items.split(" ");
				
				ItemStack item = ItemUtil.getItem(itemsp[0]);
				item.setAmount(Integer.parseInt(itemsp[1]));
				
				itemList.add(item);
			}
		}
		
		return itemList;
	}
	
	public void loadSpawn() {
		if(!spawnConfigFile.exists() || spawnConfigFile.length() < 1) return;
		
		api.getMapManager().setSpawn(spawnConfig.getLocation("spawn"));
		
		Core.log("스폰을 불러왔습니다.");
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void loadMap(String map) {
		if(!mapConfigFile.exists() || mapConfigFile.length() < 1) return;
		
		String loc = "맵." + map + ".";
		World world = Bukkit.getWorld(getMapConfig().getString(loc + "world"));
		if(world == null) {
			world = Bukkit.createWorld(new WorldCreator(getMapConfig().getString(loc + "world")));
		}
		GameMap m = new GameMap();
		m.setName(map.replace("@", "."));
		m.setRandomTeleport(mapConfig.getBoolean(loc + "랜덤 텔레포트"));
		double x;
		double y;
		double z;
		float yaw;
		float pitch;
		Location l;
		if(getMapConfig().getString(loc + "x").equals("<월드스폰>")) {
			world.getSpawnLocation().getChunk().load();
			world.getSpawnLocation().getChunk().unload();
			l = world.getHighestBlockAt((int) world.getSpawnLocation().getX(), (int) world.getSpawnLocation().getZ()).getLocation();
			if(l.getY() < 40 || world.getBlockAt(l).equals(Biome.OCEAN)) {
				Bukkit.shutdown();
				Core.log("플레이가 불가능한 월드가 생성되어 서버가 종료됩니다.");
				return;
			}
			l.setY(l.getY() + 1);
			m.setRandomTeleport(false);
		} else {
			x = getMapConfig().getDouble(loc + "x");
			y = getMapConfig().getDouble(loc + "y");
			z = getMapConfig().getDouble(loc + "z");
			yaw = (float) getMapConfig().getDouble(loc + "yaw");
			pitch = (float) getMapConfig().getDouble(loc + "pitch");
			l = new Location(world, x, y+1, z, yaw, pitch);
		}
		m.setMapLocation(l);
		
		if(api.isUseAutoMapLimit()) {
			int j = (int) Math.pow(api.getMapLimitRange() + 1, 2);
			for(int i = 0; i < j; i++) {
				double px = l.getX() + Math.cos(i) * api.getMapLimitRange();
				double pz = l.getZ() + Math.sin(i) * api.getMapLimitRange();
				Location location = world.getHighestBlockAt((int) px, (int) pz).getLocation();
				location.setY(location.getY() + 3);
				m.addMapPacketLocation(location);
			}
		}
		
		if(getMapConfig().getString(loc + "min.world") != null && getMapConfig().getString(loc + "max.world") != null) {
			World world1 = Bukkit.getWorld(getMapConfig().getString(loc + "min.world"));
			double x1 = getMapConfig().getDouble(loc + "min.x");
			double y1 = getMapConfig().getDouble(loc + "min.y");
			double z1 = getMapConfig().getDouble(loc + "min.z");
			Location loc1 = new Location(world1, x1, y1, z1);
			World world2 = Bukkit.getWorld(getMapConfig().getString(loc + "max.world"));
			double x2 = getMapConfig().getDouble(loc + "max.x");
			double y2 = getMapConfig().getDouble(loc + "max.y");
			double z2 = getMapConfig().getDouble(loc + "max.z");
			Location loc2 = new Location(world2, x2, y2, z2);
			m.setMapLimitLocation(loc1, loc2);
		}
		
		if(getMapConfig().getString(loc + "tpall.world") != null) {
			World tworld = Bukkit.getWorld(getMapConfig().getString(loc + "tpall.world"));
			double tx;
			double ty;
			double tz;
			float tyaw;
			float tpitch;
			Location tl = null;
			if(getMapConfig().getString(loc + "tpall.x").equals("<월드스폰>")) {
				tl = tworld.getHighestBlockAt((int) tworld.getSpawnLocation().getX(), (int) tworld.getSpawnLocation().getZ()).getLocation();
			} else {
				tworld = Bukkit.getWorld(mapConfig.getString(loc + "tpall.world"));
				tx = getMapConfig().getDouble(loc + "tpall.x");
				ty = getMapConfig().getDouble(loc + "tpall.y");
				tz = getMapConfig().getDouble(loc + "tpall.z");
				tyaw = (float) getMapConfig().getDouble(loc + "tpall.yaw");
				tpitch = (float) getMapConfig().getDouble(loc + "tpall.pitch");
				tl = new Location(tworld, tx, ty, tz, tyaw, tpitch);
			}
			m.setTPAllLocation(tl);
			
			if(api.isUseAutoTpAllMapLimit()) {
				int j = (int) Math.pow(api.getTpAllLimitRange() + 1, 2);
				for(int i = 0; i < j; i++) {
					double px = tl.getX() + Math.cos(i) * api.getTpAllLimitRange();
					double pz = tl.getZ() + Math.sin(i) * api.getTpAllLimitRange();
					Location location = world.getHighestBlockAt((int) px, (int) pz).getLocation();
					location.setY(location.getY() + 3);
					m.addTpAllPacketLocation(location);
				}
			}
			
			if(getMapConfig().getString(loc + "tpall.min.world") != null && getMapConfig().getString(loc + "tpall.max.world") != null) {
				World world1 = Bukkit.getWorld(getMapConfig().getString(loc + "tpall.min.world"));
				double x1 = getMapConfig().getDouble(loc + "tpall.min.x");
				double y1 = getMapConfig().getDouble(loc + "tpall.min.y");
				double z1 = getMapConfig().getDouble(loc + "tpall.min.z");
				Location loc1 = new Location(world1, x1, y1, z1);
				World world2 = Bukkit.getWorld(getMapConfig().getString(loc + "tpall.max.world"));
				double x2 = getMapConfig().getDouble(loc + "tpall.max.x");
				double y2 = getMapConfig().getDouble(loc + "tpall.max.y");
				double z2 = getMapConfig().getDouble(loc + "tpall.max.z");
				Location loc2 = new Location(world2, x2, y2, z2);
				m.setTpAllLimitLocation(loc1, loc2);
			}
		}
		
		api.getMapManager().setMap(map, m);
	}
	
	public void loadAllMap() {
		if(!mapConfigFile.exists() || mapConfigFile.length() < 1) return;
		
		int i = 0;
		for(String map : getMapConfig().getKeys("맵")) {
			loadMap(map);
			i++;
		}
		
		Core.log(i + "개의 맵을 불러왔습니다.");
	}
	
	public void loadBlackList() {
		createBlackListConfig();
		
		for(String line : blackListConfig.getStringList("블랙리스트")) {
			String abilityName = line.substring(0, line.lastIndexOf(" "));
			String pluginName = line.substring(line.indexOf(" ") + 1, line.length());
			
			api.getAbilityManager().addBlackList(abilityName, pluginName);
		}
		
		Core.log(api.getAbilityManager().getBlackList().size() + "개의 능력이 블랙리스트에 등록되었습니다.");
	}
	
	public void saveSpawn() {
		getSpawnConfig().set("spawn", api.getMapManager().getSpawn());
		
		getSpawnConfig().save();
	}
	
	public void saveMap(GameMap map) {
		createMapConfig();
		String loc = "맵." + map.getName().replace(".", "@");
		getMapConfig().set(loc + ".랜덤 텔레포트", map.isRandomTeleport());
		getMapConfig().set(loc, map.getMapLocation());
		
		if(map.getMinMapLocation() != null) {
			getMapConfig().set(loc + ".min.world", map.getMinMapLocation().getWorld().getName());
			getMapConfig().set(loc + ".min.x", map.getMinMapLocation().getX());
			getMapConfig().set(loc + ".min.y", map.getMinMapLocation().getY());
			getMapConfig().set(loc + ".min.z", map.getMinMapLocation().getZ());
		}
		
		if(map.getMaxMapLocation() != null) {
			getMapConfig().set(loc + ".max.world", map.getMaxMapLocation().getWorld().getName());
			getMapConfig().set(loc + ".max.x", map.getMaxMapLocation().getX());
			getMapConfig().set(loc + ".max.y", map.getMaxMapLocation().getY());
			getMapConfig().set(loc + ".max.z", map.getMaxMapLocation().getZ());
		}
		
		if(map.getTPAllLocation() != null) {
			getMapConfig().set(loc + ".tpall", map.getTPAllLocation());
		}
		
		if(map.getMinTPAllLocation() != null) {
			getMapConfig().set(loc + ".tpall.min.world", map.getMinTPAllLocation().getWorld().getName());
			getMapConfig().set(loc + ".tpall.min.x", map.getMinTPAllLocation().getX());
			getMapConfig().set(loc + ".tpall.min.y", map.getMinTPAllLocation().getY());
			getMapConfig().set(loc + ".tpall.min.z", map.getMinTPAllLocation().getZ());
		}
		if(map.getMaxTPAllLocation() != null) {
			getMapConfig().set(loc + ".tpall.max.world", map.getMaxTPAllLocation().getWorld().getName());
			getMapConfig().set(loc + ".tpall.max.x", map.getMaxTPAllLocation().getX());
			getMapConfig().set(loc + ".tpall.max.y", map.getMaxTPAllLocation().getY());
			getMapConfig().set(loc + ".tpall.max.z", map.getMaxTPAllLocation().getZ());
		}
		
		getMapConfig().save();
	}
	
	public void deleteMap(GameMap map) {
		createMapConfig();
		
		String loc = "맵." + map.getName().replace(".", "@");
		if(getMapConfig().getString(loc + ".world") == null) return;
		
		getMapConfig().set(loc, null);
		getMapConfig().save();
	}
	
	public void saveKit(Inventory inv) {
		try {
			File kitfile = new File(AbilityPlugin.getInstance().getDataFolder(), "kit/" + inv.getName() + ".yml");
			if(kitfile.exists()) {
				kitfile.delete();
			}
			kitfile.createNewFile();
			
			KJsonConfig kitconfig = (KJsonConfig) new KJsonConfig(kitfile);
			int i = 0;
			for(ItemStack item : inv) {
				kitconfig.set("kit." + i, item);
				
				i++;
			}
			kitconfig.save();
			
			Core.log(inv.getName() + " 킷을 저장했습니다.");
		} catch(Exception e) {
			Core.log(inv.getName() + " 킷을 저장할 수 없습니다.");
			e.printStackTrace();
		}
	}

	public void loadInjectConfig() {
		injectConfig.addDefault("사용", false);
		injectConfig.addDefault("플러그인 이름", Arrays.asList("pluginName.jar"));

		injectConfig.save();

		api.getAbilityPluginManager().setUseInject(injectConfig.getBoolean("사용"));
		api.getAbilityPluginManager().getInjectPluginNames().clear();
		api.getAbilityPluginManager().getInjectPluginNames().addAll(injectConfig.getStringList("플러그인 이름"));

		Core.log("Inject 설정을 불러왔습니다.");
	}

}
