package su.plugin.channelgui.api.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import su.plugin.channel.bukkit.api.KChannelAPI;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channel.common.api.object.Channel;
import su.plugin.channel.common.api.object.ChannelGroup;
import su.plugin.channelgui.ChannelGUIPlugin;
import su.plugin.channelgui.api.ChannelGUIAPI;
import su.plugin.channelgui.api.category.CType;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

@ToString
public class ChannelIcon extends Icon {
	
	private ChannelGUIAPI api = ChannelGUIPlugin.getApi();

	@Getter
	private final ChannelGUI channelGUI;

	@Setter
	@Getter
	private String name,
			displayName, itemCode;

	@Setter
	@Getter
	private int amount = 1;
	@Setter
	@Getter
	private String dynamicAmountFormula;
	
	@Setter
	@Getter
	private boolean playerAmountIcon;

	@Setter
	@Getter
	private CType type;

	@Setter
	@Getter
	private Channel channel;

	@Setter
	@Getter
	private ChannelGroup channelGroup;

	@Setter
	@Getter
	private List<String> lore = new ArrayList<>();

	@Setter
	@Getter
	private List<String> leftCommands = new ArrayList<>();
	@Setter
	@Getter
	private List<String> rightCommands = new ArrayList<>();
	@Setter
	@Getter
	private List<String> shiftCommands = new ArrayList<>();

	public ChannelIcon(ChannelGUI channelGUI) {
		this.channelGUI = channelGUI;
	}

	@Override
	protected ItemStack updateItem() {
		return new ItemBuilder(makeItemCode())
				.amount(makeAmount())
				.displayName(replaceText(displayName))
				.lore(makeLore())
				.build();
	}

	@Override
	public void onIconClick(IconClickEvent e) {
		if(channelGUI.hasCooldown(e.getPlayer().getName())) return;

		channelGUI.addCooldown(e.getPlayer().getName(), 500);

		if (leftCommands.size() < 1 &&
				rightCommands.size() < 1 &&
				shiftCommands.size() < 1 &&
				(channel != null || channelGroup != null)) {
			join(e.getPlayer());
		}

		switch (e.getGUIClickEvent().getAction()) {
			case LEFT_HOLD:
				executeLeftCommand(e.getPlayer()); break;
			case RIGHT_HOLD:
				executeRightCommand(e.getPlayer()); break;
			case SHIFT_CLICK:
				executeShiftCommand(e.getPlayer()); break;
		}
	}

	private String makeItemCode() {
		if(getChannel() == null) return itemCode;

		if(type == CType.U_ABILITY) {
			if(getChannel().isOnline()) {
				Double state = (Double) getChannel().getETC("game_state");
				return api.getUabilityOnlineItemCode().get(state == null ? 0 : state.intValue()) + "";
			} else {
				return api.getUabilityOfflineItemCode();
			}
		} else if(!getChannel().isOnline()) {
			return api.getOfflineItemCode();
		}

		return itemCode;
	}

	private int makeAmount() {
		if (dynamicAmountFormula != null) {
			if(dynamicAmountFormula.startsWith("<channel:")) {
				return Integer.parseInt(ChannelAPI.getChannelManager().getChannel(StringUtil.getValue("channel", dynamicAmountFormula).get(0)).toString());
			} else if (dynamicAmountFormula.startsWith("<channelgroup:")) {
				return Integer.parseInt(ChannelAPI.getChannelGroupManager().getChannelGroup(StringUtil.getValue("channelgroup", dynamicAmountFormula).get(0)).toString());
			}
		} else if (!playerAmountIcon || (channel == null && channelGroup == null)) return amount;

		if (channel != null && channel.isOnline()) return getChannel().getPlayerCount();
		else if (channelGroup != null) return channelGroup.getPlayerCount();

		return amount;
	}

	private List<String> makeLore() {
		List<String> newLore = new ArrayList<>();

		for(String line : lore) {
			String repl = replaceText(line);
			if(repl == null) continue;

			newLore.add(repl);
		}

		return newLore;
	}

	private String replaceText(String text) {
		text = ChatColor.translateAlternateColorCodes('&', text);

		text = StringUtil.replaceValue("channel_player", text, (value) -> {
			Channel c = ChannelAPI.getChannelManager().getChannel(value);
			return c == null ? 1 : c.getPlayerCount();
		});

		text=  StringUtil.replaceValue("channelgroup_player", text, (value) -> {
			ChannelGroup c = ChannelAPI.getChannelGroupManager().getChannelGroup(value);
			return c == null ? 1 : c.getPlayerCount();
		});

		if(getChannel() == null && getChannelGroup() == null) return text;

		text = ChatColor.translateAlternateColorCodes('&', text);

		text = text.replace("<채널이름>", getChannel() == null ? getChannelGroup().getDisplayName() : getChannel().getDisplayName());

		if(getChannel() == null) { // isGroup
			text = text.replace("<플레이어수>", "" + getChannelGroup().getPlayerCount());

			return text;
		}

		if(text.contains("<플레이어수>")) {
			if(getChannel().isOnline()) {
				text = text.replace("<플레이어수>", "" + getChannel().getPlayerCount());
			} else {
				return null;
			}
		}

		if(text.contains("<최대플레이어수>")) {
			if(getChannel().isOnline()) {
				text = text.replace("<최대플레이어수>", "" + getChannel().getMaxPlayerCount());
			} else {
				return null;
			}
		}

		if(type == CType.U_ABILITY) {
			if(getChannel().isOnline()) {
				Double state = (Double) getChannel().getETC("game_state");
				text = text.replace("<상태>", api.getUabilityOnlineMessage().get(state == null ? 0 : state.intValue()));
			} else {
				text = text.replace("<상태>", api.getUabilityOfflineMessage());
			}
		} else {
			text = text.replace("<상태>", getChannel().isOnline() ? api.getOnlineMessage() : api.getOfflineMessage());
		}

		return text;
	}

	//

	public void join(Player player) {
		if(channel != null) {
			if(!channel.isOnline()) {
				Core.wmsg(player, "오프라인 상태인 채널입니다.");
				return;
			} else if(KChannelAPI.getCurrentChannel().equals(getChannel())) {
				Core.wmsg(player, "이미 접속 중인 채널입니다.");
				return;
			}

			Core.msg(player, channel.getDisplayName() + " §e채널로 이동합니다.");

			channel.sendToChannel(player.getName());
		} else if(channelGroup != null) {
			channelGroup.sendToOptimizeChannel(player.getName());
		}
	}

	private void executeCommand(Player player, String command) {
		if (command.equalsIgnoreCase("@none")) return;
		else if (command.equalsIgnoreCase("@join")) {
			if (channel != null && !channel.isOnline()) {
				Core.wmsg(player, "오프라인 상태인 채널입니다.");
				return;
			} else if (channel != null && channel.equals(KChannelAPI.getCurrentChannel())) {
				Core.wmsg(player, "이미 접속 중인 채널입니다.");
				return;
			}

			join(player);
		} else if (command.toLowerCase().startsWith("@cmd ")) {
			Bukkit.dispatchCommand(player, command.substring("@cmd ".length()));
		} else if (command.toLowerCase().startsWith("@cmdop ")) {
			boolean op = player.isOp();

			player.setOp(true);

			try {
				Bukkit.dispatchCommand(player, command.substring("@cmdop ".length()));
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				if (op) return;
				player.setOp(false);
			}
		} else if (command.toLowerCase().startsWith("@cmdcon ")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.substring("@cmdcon ".length()));
		} else if (command.toLowerCase().startsWith("@open ")) {
			String guiName = command.substring("@open ".length());
			ChannelGUI gui = api.getGUIManager().getGUI(guiName);
			if (gui == null) {
				Core.wmsg(player, "존재하지 않는 채널 GUI 입니다.");
				return;
			}

			gui.open(player);
		}
	}

	public void executeLeftCommand(Player player) {
		leftCommands.forEach(line -> executeCommand(player, line));
	}

	public void executeRightCommand(Player player) {
		rightCommands.forEach(line -> executeCommand(player, line));
	}

	public void executeShiftCommand(Player player) {
		shiftCommands.forEach(line -> executeCommand(player, line));
	}

}