package su.plugin.ability.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.script.Invocable;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.manager.AbilityManager;
import su.plugin.ability.api.manager.AbilityPluginManager;
import su.plugin.ability.api.manager.BarManager;
import su.plugin.ability.api.manager.BungeeManager;
import su.plugin.ability.api.manager.ConfigManager;
import su.plugin.ability.api.manager.GUIManager;
import su.plugin.ability.api.manager.GameManager;
import su.plugin.ability.api.manager.ItemManager;
import su.plugin.ability.api.manager.KitManager;
import su.plugin.ability.api.manager.MapManager;
import su.plugin.ability.api.manager.PlayerManager;
import su.plugin.ability.api.manager.ScriptManager;
import su.plugin.ability.api.manager.SupplyManager;
import su.plugin.ability.api.manager.TaskManager;
import su.plugin.ability.api.manager.VoteManager;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.SubCommand;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.util.StringUtil;

public class AbilityAPI {
	
	@Setter
	@Getter
	private static String mapLimitParticle, bungeeLobby, waitingMOTD, playingMOTD, voteStartingCondition, voteStartingConditionMessage;
	
	@Setter
	@Getter
	private static int redrawCount, startInvincibilityCount, maxPartyPlayerCount, supplyCreateCount, reconnectAllowCount, locationNotifyCount,
	assistCount, doubleCount, tripleCount, quadraCount, pentaCount, autoStartCount, drawSkipCount, autoTeleportCount,
	autoTeleportRepeatCount, mapTeleportPlayerCount, winMinCount, voteTimeoutCount, mapLimitRange, TpAllLimitRange, quitDeathHealth, revotePeriod;
	
	@Setter
	@Getter
	private static boolean useThisPluginAbility, useBitAbility, usePhysicalFighters, useBungeeCord, useGParty, useChannel, usePrefixer, usePermission, usePVPStats;
	
	@Setter
	@Getter
	private  static boolean useBossBar, useOverlap, useSoundEffect, useStartInvincibility, useInvincibilityOnWait, useBlockProtectOnWait, usePvpProtectOnWait,
	useSupply, useSupplyFirework, useCommandProtectOnWait, useCommandProtectOnWatchMode, invincibilityTime, eliminateOnDeath,
	eliminateOnNatureDeath, kickOnDeath, banOnDeath, allowReconnect, useReconnectTimeLimit, useLocationNotifyMessage, useLocationNotifyFirework,
	useAssist, useFirstBlood, useDouble, useTriple, useQuadra, usePenta, useAutoStart, useDrawTimeLimit, useAutoTeleport, useAutoTeleportRepeat,
	useAutoMapLimit, useAutoTpAllMapLimit, useMapLimitParticle, useWatchModeQuickBar, useWaitingQuickBar,
	useSideBar, useSideBarGameInfo, useInfinityDurability, useInfinityFoodLevel, useGameStartVote, useMapVote, useInvSkipVote, useStartItem, useRankItem,
	teleportToMapOnManyPlayer, rainOff, useEndBroadcastMessage,
	firstBlood = true;
	
	@Setter
	@Getter
	private static double killMoney, assistMoney, firstBloodMoney, doubleMoney, tripleMoney, quadraMoney, pentaMoney;

	@Setter
	@Getter
	private static String winMoneyFormula, endBroadcastSound;

	@Setter
	@Getter
	private static List<String> protectExceptionCommands = new ArrayList<>();
	
	@Setter
	@Getter
	private static List<String> watchExceptionCommands = new ArrayList<>();

	@Setter
	@Getter
	private static List<String> endBroadcastMessages = new ArrayList<>();

	@Setter
	@Getter
	private static HashMap<String, Integer> rankRedrawCount = new HashMap<>(); // <Rank, Count>
	
	@Getter
	private static AbilityManager abilityManager;
	@Getter
	private static AbilityPluginManager abilityPluginManager;
	@Getter
	private static ConfigManager configManager;
	@Getter
	private static GameManager gameManager;
	@Getter
	private static KitManager kitManager;
	@Getter
	private static MapManager mapManager;
	@Getter
	private static PlayerManager playerManager;
	@Getter
	private static ItemManager itemManager;
	@Getter
	private static SupplyManager supplyManager;
	@Getter
	private static GUIManager GUIManager;
	@Getter
	private static BarManager barManager;
	@Getter
	private static VoteManager voteManager;
	@Getter
	private static TaskManager taskManager;
	@Getter
	private static BungeeManager bungeeManager;
	@Getter
	private static ScriptManager scriptManager;
	
	public void init() {
		abilityManager = new AbilityManager();
		abilityPluginManager = new AbilityPluginManager();
		configManager = new ConfigManager();
		gameManager = new GameManager();
		kitManager = new KitManager();
		mapManager = new MapManager();
		playerManager = new PlayerManager();
		itemManager = new ItemManager();
		supplyManager = new SupplyManager();
		GUIManager = new GUIManager();
		barManager = new BarManager();
		voteManager = new VoteManager();
		taskManager = new TaskManager();
		bungeeManager = new BungeeManager();
		scriptManager = new ScriptManager();
	}

	@SneakyThrows(Exception.class)
	public double getWinMoney(int playerCount) {
		Invocable invEngine = (Invocable) scriptManager.getScriptEngine();

		return (double) invEngine.invokeFunction("getWinMoney", playerCount);
	}

	@SneakyThrows(Exception.class)
	public boolean canStartVote() {
		int lobbyCount = 0;
		if (bungeeLobby.toLowerCase().startsWith("<channel:")) {
			List<String> arr = StringUtil.getValue("channel", bungeeLobby);
			Channel channel = null;

			if(arr.size() > 0 && (channel = ChannelAPI.getChannelManager().getChannel(arr.get(0))) != null) {
				lobbyCount = channel.getPlayerCount();
			}
		} else if (bungeeLobby.toLowerCase().startsWith("<channelgroup:")) {
			List<String> arr = StringUtil.getValue("channelgroup", bungeeLobby);
			ChannelGroup group = null;

			if(arr.size() > 0 && (group = ChannelAPI.getChannelGroupManager().getChannelGroup(arr.get(0))) != null) {
				lobbyCount = group.getPlayerCount();
			}
		}

		return (boolean) ((Invocable) scriptManager.getScriptEngine()).invokeFunction("voteStartCondition",
				useChannel ? ChannelAPI.getAllPlayerCount() : 0,
				lobbyCount,
				playerManager.getOnlineJoinedPlayers().size());
	}

	public boolean isAdmin(CommandSender sender, boolean msg) {
		if(sender.isOp() || sender.hasPermission("ability.admin")) return true;
		else if(msg) {
			Core.wmsg(sender, ChatColor.RED, "권한이 없습니다.");
		}
		return false;
	}

	public void playSound(Player p, Sound sound, float volume, float speed) {
		if(!isUseSoundEffect() || p == null || sound == null) return;
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> p.playSound(p.getLocation(), sound, volume * 0.6F, speed));
	}
	
	public void playSoundToAll(Sound sound, float volume, float speed) {
		if(!isUseSoundEffect()) return;
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> {
			for(Player p : KCore.getOnlinePlayers()) {
				playSound(p, sound, volume, speed);
			}
		});
	}
	
	public void shutdown(int count) {
		Bukkit.getScheduler().runTaskTimerAsynchronously(AbilityPlugin.getInstance(), new Runnable() {
			int i = 0;
			
			public void run() {
				i++;
				
				if(i == (count - 3) && useBungeeCord) {
					for(Player ap : KCore.getOnlinePlayers()) {
						bungeeManager.sendToLobby(ap);
					}
				} else if(i == count) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "stop");
				}
			}
		}, 0, 20);
	}

	public static boolean sendUsageIfHasPermission(SubCommand sc, UCommandSender sender, ChatColor color) {
		if(sc.getPermission() != null && !sender.hasPermission(sc.getPermission())) return false;

		String r = "§f/" + sc.getCommand() + (sc.getAdditional() == null ? "" : " " + sc.getAdditional()) + color + " - " + sc.getUsage();

		sender.cmsg(color, r);

		return true;
	}

	public static int getRedrawCount(String rank) {
		return rank != null && rankRedrawCount.containsKey(rank.toLowerCase()) ? rankRedrawCount.get(rank.toLowerCase()) : redrawCount;
	}
	
}