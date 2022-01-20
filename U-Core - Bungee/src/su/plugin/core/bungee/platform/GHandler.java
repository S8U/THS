package su.plugin.core.bungee.platform;

import lombok.ToString;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.UPlayer;
import su.plugin.core.common.platform.PlatformHandler;

@ToString
public class GHandler implements PlatformHandler {
	
	@Override
	public UCommandSender getUCommandSender(Object sender) {
		return sender instanceof UCommandSender ? (UCommandSender) sender : (CommandSender.class.isAssignableFrom(sender.getClass()) ? (sender instanceof ProxiedPlayer ? Core.getUPlayerByPlatformPlayer(sender) : Core.getUConsoleCommandSender()) : null);
	}

	@Override
	public UPlayer getUPlayer(Object player) {
		return player instanceof UPlayer ? (UPlayer) player : (player instanceof ProxiedPlayer ? Core.getUPlayerManager().getUPlayer(((ProxiedPlayer) player).getUniqueId()) : null);
	}
	
	@Override
	public String getPlatformPlayerName(Object platformPlayer) {
		return platformPlayer == null ? null : ((ProxiedPlayer) platformPlayer).getName();
	}
	
	@Override
	public void nlog(Object message) {
		ProxyServer.getInstance().getConsole().sendMessage(message.toString());
	}

	@Override
	public void nmsg(Object sender, Object message) {
		if(sender instanceof UCommandSender) {
			sender = ((UCommandSender) sender).getPlatformSender();
		}
		
		if(message instanceof BaseComponent) {
			((CommandSender) sender).sendMessage((BaseComponent) message);
			return;
		}
		
		((CommandSender) sender).sendMessage(message.toString());
	}

	@Override
	public void nbc(Object message) {
		if(message instanceof BaseComponent) {
			ProxyServer.getInstance().broadcast((BaseComponent) message);
			return;
		}
		
		ProxyServer.getInstance().broadcast(message.toString());
	}
	
	@Override
	public Object makeComponent(boolean useColor, Object... messages) {
		TextComponent tc = new TextComponent();
		ChatColor lastColor = ChatColor.WHITE;

		for(Object obj : messages) {
			String text = null;

		if(obj instanceof TextComponent) {
			TextComponent bc = (TextComponent) obj;

			if(useColor) {
				bc.setColor(lastColor);
			} else {
				bc.setColor(ChatColor.RESET);
				bc.setText(ChatColor.stripColor(bc.getText()));
			}

			text = bc.getText();

			tc.addExtra(bc);
		} else if(obj instanceof BaseComponent[]) {
			for(BaseComponent bca : (BaseComponent[]) obj) {
				TextComponent bc = (TextComponent) bca;

				if(useColor) {
					bc.setColor(lastColor);
				} else {
					bc.setColor(ChatColor.RESET);
					bc.setText(ChatColor.stripColor(bc.getText()));
				}

				text = bc.getText();

				tc.addExtra(bc);
			}
		} else {
			text = useColor ? obj.toString() : ChatColor.stripColor(obj.toString());

			tc.addExtra(text);
		}

		String colorStr = su.plugin.core.common.api.ChatColor.getLastColors(text);
		lastColor = colorStr.isEmpty() ? ChatColor.WHITE : ChatColor.getByChar(colorStr.charAt(1));
	}

		return tc;
	}
	
}