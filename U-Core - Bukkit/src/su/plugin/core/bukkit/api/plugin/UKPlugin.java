package su.plugin.core.bukkit.api.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URLClassLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.config.json.KJsonConfig;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.plugin.UPlugin;

@RequiredArgsConstructor
public class UKPlugin extends JavaPlugin implements UPlugin {

	@Getter
	private String pluginPackage,
	version,
	prefix;
	
	@Setter
	@Getter
	private String logFormat = "%2$s §f%1$s", // {prefix} §f{message}
	warningLogFormat = "%2$s §c%1$s", // {prefix} §c{message}
	messageFormat = "%2$s §f%1$s", // {prefix} §f{message}
	colorMessageFormat = "%2$s §f%3$s%1$s", // {prefix} §f{color}{message}
	warningMessageFormat = "%2$s §c%1$s", // {prefix} §c{message}
	colorBroadcastFormat = "%2$s §f%3$s%1$s", // {prefix} §f{color}{message}
	broadcastFormat = "%2$s §f%1$s"; // {prefix} §f{message}
	
	@Setter
	@Getter
	private boolean useLogColor = true,
	useWarningLogColor = true,
	useMessageColor = true,
	useColorMessageColor = true,
	useWarningMessageColor = true,
	useBroadcastColor = true,
	useColorBroadcastColor = true;
	
	@Setter
	@Getter
	private ChatColor color = ChatColor.WHITE;
	
	@Setter
	@Getter
	private boolean deleteOnExit;
	
	private boolean disable;
	
	@Getter
	private KJsonConfig jsonConfig, messageFormatConfig;
	
	public void onEnable() {
		pluginPackage = getDescription().getMain().substring(0, getDescription().getMain().lastIndexOf("."));
		version = getDescription().getVersion();
		
		Core.getUPluginManager().registerUPlugin(this);
		
		jsonConfig = (KJsonConfig) new KJsonConfig(new File(getDataFolder(), "config.json")).load();
		
		disable = false;
		
		onUEnable();
		
		if(disable) return;

		messageFormatConfig = new KJsonConfig(new File(getDataFolder(), "message-format-config.json"));

		loadMessageFormatConfig(Core.getUConsoleCommandSender());

		log("플러그인이 활성화되었습니다. (v" + version + ")");
	}
	
	public void loadMessageFormatConfig(UCommandSender sender) {
		messageFormatConfig.load();

		messageFormatConfig.addDefault("로그.포맷", "%2$s &f%1$s");
		messageFormatConfig.addDefault("로그.색깔 사용", useLogColor);
		messageFormatConfig.addDefault("경고 로그.포맷", "%2$s &f%1$s");
		messageFormatConfig.addDefault("경고 로그.색깔 사용", useWarningLogColor);
		messageFormatConfig.addDefault("메시지.포맷", "%2$s &f%1$s");
		messageFormatConfig.addDefault("메시지.색깔 사용", useMessageColor);
		messageFormatConfig.addDefault("색깔 메시지.포맷", "%2$s &f%3$s%1$s");
		messageFormatConfig.addDefault("색깔 메시지.색깔 사용", useColorMessageColor);
		messageFormatConfig.addDefault("경고 메시지.포맷", "%2$s &c%1$s");
		messageFormatConfig.addDefault("경고 메시지.색깔 사용", useWarningMessageColor);
		messageFormatConfig.addDefault("공지.포맷", "%2$s &f%1$s");
		messageFormatConfig.addDefault("공지.색깔 사용", useBroadcastColor);
		messageFormatConfig.addDefault("색깔 공지.포맷", "%2$s &f%3$s%1$s");
		messageFormatConfig.addDefault("색깔 공지.색깔 사용", useColorBroadcastColor);

		messageFormatConfig.saveDefaults();
		
		logFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("로그.포맷"));
		useLogColor = messageFormatConfig.getBoolean("로그.색깔 사용");
		warningLogFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("경고 로그.포맷"));
		useWarningLogColor = messageFormatConfig.getBoolean("경고 로그.색깔 사용");
		messageFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("메시지.포맷"));
		useMessageColor = messageFormatConfig.getBoolean("메시지.색깔 사용");
		colorMessageFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("색깔 메시지.포맷"));
		useColorMessageColor = messageFormatConfig.getBoolean("색깔 메시지.색깔 사용");
		warningMessageFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("경고 메시지.포맷"));
		useWarningMessageColor = messageFormatConfig.getBoolean("경고 메시지.색깔 사용");
		broadcastFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("공지.포맷"));
		useBroadcastColor = messageFormatConfig.getBoolean("공지.색깔 사용");
		colorBroadcastFormat = ChatColor.translateAlternateColorCodes('&', messageFormatConfig.getString("색깔 공지.포맷"));
		useColorBroadcastColor = messageFormatConfig.getBoolean("색깔 공지.색깔 사용");
		
		log("메시지 포맷 설정을 불러왔습니다.");
		if(!sender.isConsole()) {
			sender.msg("메시지 포맷 설정을 불러왔습니다.");
		}
	}
	
	@SneakyThrows(IOException.class)
	public void onDisable() {
		onUDisable();
		
		disable = true;
		
		if(isDeleteOnExit()) {
			((URLClassLoader) getClass().getClassLoader()).close();
			
			getFile().delete();
		}
		
		log("플러그인이 비활성화되었습니다. (v" + version + ")");
		
		Core.getUPluginManager().unRegisterUPlugin(this);
	}
	
	public void onUEnable() { }
	public void onUDisable() { }
	public void onConfigLoad(UCommandSender sender) { }
	public void onConfigLoaded(UCommandSender sender) { }

	@Override
	public void loadConfig() {
		loadConfig(Core.getUConsoleCommandSender());
	}

	@Override
	public void loadConfig(UCommandSender sender) {
		try {
			long time = System.currentTimeMillis();

			jsonConfig.load();

			onConfigLoad(sender);

			time = System.currentTimeMillis() - time;

			log("설정을 불러왔습니다. (" + time + "ms)");
			if(!sender.isConsole()) {
				sender.msg("설정을 불러왔습니다. (" + time + "ms)");
			}

			onConfigLoaded(sender);
		} catch(Exception e) {
			e.printStackTrace();

			wlog("설정을 불러오는 데 실패했습니다.");
			if(!sender.isConsole()) {
				sender.wmsg("설정을 불러오는 데 실패했습니다.");
			}
		}
	}

	public void disable() {
		disable = true;
		
		PluginUtil.disablePlugin(this);
	}
	
	public Plugin getPlatformPlugin() {
		return this;
	}
	
	public File getFile() {
		return PluginUtil.getFile(this);
	}
	
	protected void log(Object message) {
		Core.logc(pluginPackage, message);
	}
	
	protected void wlog(Object message) {
		Core.wlogc(pluginPackage, message);
	}
	
	protected void nlog(Object message) {
		Core.nlog(message);
	}
	
	protected int registerListeners() {
		return PluginUtil.registerListeners(this);
	}
	
	protected int registerListeners(String pack) {
		return PluginUtil.registerListeners(this, pack);
	}
	
	protected void registerListener(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this);
	}
	
	protected int registerUEventListeners() {
		return Core.getUEventManager().registerListeners(this);
	}
	
	protected int registerUEventListeners(String pack) {
		return Core.getUEventManager().registerListeners(this, pack);
	}
	
	protected void registerUEventListener(UEventListener listener) {
		Core.getUEventManager().registerListener(this, listener);
	}
	
	protected void registerCommand(CommandExecutor executor, String... commands) {
		PluginUtil.registerCommand(this, executor, commands);
	}
	
	protected int registerCommands() {
		return Core.getCommandManager().registerCommands(this);
	}
	
	protected int registerCommands(String pack) {
		return Core.getCommandManager().registerCommands(this, pack);
	}
	
	protected void registerCommands(UCommandListener listener) {
		Core.getCommandManager().registerCommands(this, listener);
	}
	
	protected boolean registerPermissions() {
		return KCore.getPermissionManager().registerPermissions(this);
	}
	
	protected boolean registerPermissions(String pack) {
		return KCore.getPermissionManager().registerPermissions(this, pack);
	}
	
	protected void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	protected void setPluginPackage(String pluginPackage) {
		Core.getUPluginManager().unRegisterUPlugin(this);
		
		this.pluginPackage = pluginPackage;
		
		Core.getUPluginManager().registerUPlugin(this);
	}
	
	protected Plugin getPlugin(String name) {
		return Bukkit.getPluginManager().getPlugin(name);
	}
	
	protected boolean existsPlugin(String name) {
		return getPlugin(name) != null;
	}
	
}