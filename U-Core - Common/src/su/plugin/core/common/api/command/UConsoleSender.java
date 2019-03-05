package su.plugin.core.common.api.command;

public abstract class UConsoleSender extends UCommandSender {
	
	@Override
	public boolean isConsole() {
		return true;
	}
	
	@Override
	public boolean hasPermission(String node) {
		return true;
	}
	
	@Override
	public String getName() {
		return "콘솔";
	}
	
}