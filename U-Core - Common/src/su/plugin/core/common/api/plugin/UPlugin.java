package su.plugin.core.common.api.plugin;

import java.io.File;

import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.config.json.JsonConfig;

public interface UPlugin {
	
	void onUEnable();
	
	void onUDisable();

	void loadConfig();

	void loadConfig(UCommandSender sender);

	void onConfigLoad(UCommandSender sender);

	void onConfigLoaded(UCommandSender sender);

	void loadMessageFormatConfig(UCommandSender sender);

	String getName();
	
	String getPluginPackage();
	
	String getVersion();
	
	boolean isEnabled();
	
	Object getPlatformPlugin();
	
	File getFile();
	
	File getDataFolder();
	
	JsonConfig getJsonConfig();
	
	String getPrefix();
	
	ChatColor getColor();
	
	String getLogFormat();
	
	boolean isUseLogColor();
	
	String getWarningLogFormat();
	
	boolean isUseWarningLogColor();
	
	String getMessageFormat();
	
	boolean isUseMessageColor();
	
	String getColorMessageFormat();
	
	boolean isUseColorMessageColor();
	
	String getWarningMessageFormat();
	
	boolean isUseWarningMessageColor();
	
	String getBroadcastFormat();

	boolean isUseBroadcastColor();
	
	String getColorBroadcastFormat();
	
	boolean isUseColorBroadcastColor();
	
}