package su.plugin.core.common.api;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import su.plugin.core.PackageNameProvider;
import su.plugin.core.common.api.command.UCommandManager;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.command.UConsoleSender;
import su.plugin.core.common.api.event.UEventManager;
import su.plugin.core.common.api.option.OptionManager;
import su.plugin.core.common.api.option.OptionSQLManager;
import su.plugin.core.common.api.platform.PlatformType;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.api.player.UPlayerManager;
import su.plugin.core.common.api.plugin.UPlugin;
import su.plugin.core.common.api.plugin.UPluginManager;
import su.plugin.core.common.api.sql.SQLManager;
import su.plugin.core.common.platform.PlatformHandler;

public abstract class Core {
	
	@Getter
	private static final String corePrefix = "§e[ U-Core ]";

	@Setter
	@Getter
	private static String allowNicknameRegex;
	
	@Getter
	protected static PlatformType platformType;
	
	protected static PlatformHandler platformProvider;
	
	@Getter
	protected static UConsoleSender UConsoleCommandSender;
	
	@Getter
	private static OptionManager optionManager = new OptionManager();
	@Getter
	private static OptionSQLManager optionSQLManager = new OptionSQLManager();
	@Getter
	private static UPlayerManager UPlayerManager = new UPlayerManager();
	@Getter
	protected static UCommandManager commandManager;
	@Getter
	private static UEventManager UEventManager = new UEventManager();
	@Getter
	private static UPluginManager UPluginManager = new UPluginManager();
	@Getter
	private static SQLManager SQLManager = new SQLManager();
	
	public static String getLastClassName() {
		StackTraceElement[] ste = new Throwable().getStackTrace();
		
		for(int i = 0; i < ste.length; i++) {
			if(!ste[i].getClassName().startsWith(PackageNameProvider.class.getPackage().getName())) return ste[i].getClassName();
		}
		
		return null;
	}
	
	public static UCommandSender getUCommandSender(@NonNull Object sender) {
		return platformProvider.getUCommandSender(sender);
	}
	
	public static UPlayer getUPlayerByPlatformPlayer(Object player) {
		return platformProvider.getUPlayer(player);
	}
	
	public static UPlayer getUPlayer(PlayerKey playerKey) {
		return UPlayerManager.getUPlayer(playerKey);
	}
	
	public static UPlayer getUPlayer(int playerId) {
		return UPlayerManager.getUPlayer(playerId);
	}
	
	public static UPlayer getUPlayer(String name) {
		return UPlayerManager.getUPlayer(name);
	}
	
	public static UPlayer getUPlayerByDisplayName(String displayName) {
		return UPlayerManager.getUPlayerByDisplayName(displayName);
	}
	
	public static UPlayer getUPlayer(UUID uuid) {
		return UPlayerManager.getUPlayer(uuid);
	}
	
	public static List<UPlayer> getOnlineUPlayers() {
		return UPlayerManager.getOnlineUPlayers();
	}

	public static void setDisplayName(PlayerKey playerKey, String displayName) {
		UPlayer up = getUPlayer(playerKey);
		if (up == null) {
			if(playerKey.getName().equals(displayName)) {
				Core.getSQLManager().deleteDisplayName(playerKey);
			} else {
				Core.getSQLManager().setDisplayName(playerKey, displayName);
			}
		} else {
			up.setDisplayName(displayName);
		}
	}
	
	public static String getDisplayName(PlayerKey playerKey) {
		String displayName = SQLManager.getDisplayName(playerKey);
		
		return displayName == null ? playerKey.getName() : displayName;
	}
	
	public static String getPlatformPlayerName(Object platformPlayer) {
		return platformProvider.getPlatformPlayerName(platformPlayer);
	}
	
	@SneakyThrows(Exception.class)
	public static UUID getUUID(Object obj) {
		for(Method method : obj.getClass().getMethods()) {
			if(method.getName().equals("getUniqueId")) return (UUID) method.invoke(obj, null);
		}
		
		return null;
	}
	
	public static void log(Object message) {
		logc(getLastClassName(), message);
	}
	
	public static void logc(String className, Object message) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		nlog(String.format((plugin == null ? "%2$s §f%1$s" : plugin.getLogFormat()), plugin == null || plugin.isUseLogColor() ? message.toString() : ChatColor.stripColor(message.toString()), (plugin == null ? corePrefix : plugin.getPrefix())));
	}
	
	public static void wlog(Object message) {
		wlogc(getLastClassName(), message);
	}
	
	public static void wlogc(String className, Object message) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		nlog(String.format((plugin == null ? "%2$s §c%1$s" : plugin.getWarningLogFormat()), plugin == null || plugin.isUseWarningLogColor() ? message.toString() : ChatColor.stripColor(message.toString()), (plugin == null ? corePrefix : plugin.getPrefix())));
	}
	
	public static void nlog(Object message) {
		platformProvider.nlog(message);
	}
	
	public static void msg(@NonNull Object sender, Object...messages) {
		msgc(sender, getLastClassName(), messages);
	}
	
	public static void msgc(@NonNull Object sender, String className, Object... messages) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		
		String format = plugin == null ? "%2$s §f%1$s" : plugin.getMessageFormat();
		format = String.format(format, "%1$s", plugin == null ? corePrefix : plugin.getPrefix());
		
		boolean useColor = plugin == null ? true : plugin.isUseMessageColor();
		
		nmsg(sender, makeComponentArr(useColor, format, messages));
	}
	
	public static void cmsg(@NonNull Object sender, ChatColor color, Object... messages) {
		cmsgc(sender, getLastClassName(), color, messages);
	}
	
	public static void cmsgc(@NonNull Object sender, String className, ChatColor color, Object... messages) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		
		String format = plugin == null ? "%2$s §f%3$s%1$s" : plugin.getColorMessageFormat();
		format = String.format(format, "%1$s", plugin == null ? corePrefix : plugin.getPrefix(), plugin == null ? "§f" : color);

		boolean useColor = plugin == null ? true : plugin.isUseColorMessageColor();
		
		nmsg(sender, makeComponentArr(useColor, format, messages));
	}
	
	public static void wmsg(@NonNull Object sender, Object... messages) {
		wmsgc(sender, getLastClassName(), messages);
	}
	
	public static void wmsgc(@NonNull Object sender, String className, Object... messages) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		
		String format = plugin == null ? "%2$s §c%1$s" : plugin.getWarningMessageFormat();
		format = String.format(format, "%1$s", plugin == null ? corePrefix : plugin.getPrefix());
		
		boolean useColor = plugin == null ? true : plugin.isUseWarningMessageColor();

		nmsg(sender, makeComponentArr(useColor, format, messages));
	}
	
	public static void nmsg(@NonNull Object sender, Object messages) {
		platformProvider.nmsg(sender, messages);
	}
	
	public static void nmsg(@NonNull Object sender, Object...messages) {
		nmsg(sender, platformProvider.makeComponent(true, messages)); 
	}
	
	public static void bc(Object... messages) {
		bcc(getLastClassName(), messages);
	}
	
	public static void bcc(String className, Object... messages) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		
		String format = plugin == null ? "%2$s §f%1$s" : plugin.getBroadcastFormat();
		format = String.format(format, "%1$s", plugin == null ? corePrefix : plugin.getPrefix());
		
		boolean useColor = plugin == null ? true : plugin.isUseBroadcastColor();

		nbc(makeComponentArr(useColor, format, messages));
	}
	
	public static void cbc(ChatColor color, Object... messages) {
		cbcc(getLastClassName(), color, messages);
	}
	
	public static void cbcc(String className, ChatColor color, Object... messages) {
		UPlugin plugin = UPluginManager.getUPluginByPackage(className);
		
		String format = plugin == null ? "%2$s §f%3$s%1$s" : plugin.getColorBroadcastFormat();
		format = String.format(format, "%1$s", plugin == null ? corePrefix : plugin.getPrefix(), plugin == null ? "§f" : color);
		
		boolean useColor = plugin == null ? true : plugin.isUseColorBroadcastColor();
		
		nbc(makeComponentArr(useColor, format, messages));
	}
	
	public static void nbc(Object message) {
		platformProvider.nbc(message);
	}
	
	public static void nbc(Object...components) {
		nbc(makeComponent(true, components));
	}
	
	//

	public static Object[] makeComponentArr(boolean useColor, String format, Object... messages) {
		List<Object> componentList = new ArrayList<>();

		Object messageComponent = makeComponent(useColor, messages);

		if (!format.contains("%1$s")) {
			componentList.add(format);
		} else {
			boolean contains = true;

			while(true) {
				String prefix = "";
				if(contains = format.contains("%1$s")) {
					prefix = format.substring(0, format.indexOf("%1$s"));
					format = format.substring(format.indexOf("%1$s") + 4);
				} else {
					prefix = format;
				}

				componentList.add(prefix);
				if(contains) {
					componentList.add(messageComponent);
				} else {
					break;
				}
			}
		}

		return componentList.toArray(new Object[componentList.size()]);
	}

	/*private static Object[] makeComponentArr(boolean useColor, String format, Object... messages) {
		Object[] componentArr = new Object[StringUtil.countMatches(format, "%1$s") * 2];
		
		Object messageComponent = makeComponent(useColor, messages);
		
		if(componentArr.length < 1) {
			componentArr = new Object[1];
			componentArr[0] = format;
		} else {
			for (int i = 0; format.contains("%1$s"); i++) {
				String prefix = format.substring(0, format.indexOf("%1$s"));
				format = format.substring(format.indexOf("%1$s") + 4);
				
				componentArr[i * 2] = prefix;
				componentArr[i * 2 + 1] = messageComponent;
			}
		}
		
		
		return componentArr;
	}*/

	public static Object makeComponent(boolean useColor, Object... messages) {
		return platformProvider.makeComponent(useColor, messages);
	}

}