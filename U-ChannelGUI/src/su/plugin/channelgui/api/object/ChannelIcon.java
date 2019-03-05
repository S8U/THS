package su.plugin.channelgui.api.object;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.inventory.ItemStack;
import su.plugin.channel.bukkit.api.KChannelAPI;
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

		if(getChannel() != null) {
			if(!getChannel().isOnline()) {
				Core.wmsg(e.getPlayer(), "오프라인 상태인 채널입니다.");
				return;
			} else if(KChannelAPI.getCurrentChannel().equals(getChannel())) {
				Core.wmsg(e.getPlayer(), "이미 접속 중인 채널입니다.");
				return;
			}

			Core.msg(e.getPlayer(), channel.getDisplayName() + " §e채널로 이동합니다.");

			getChannel().sendToChannel(e.getPlayer().getName());
		} else if(getChannelGroup() != null) {
			getChannelGroup().sendToOptimizeChannel(e.getPlayer().getName());
		}
	}

	private String makeItemCode() {
		if(getChannel() == null) return itemCode + "";

		if(type == CType.U_ABILITY) {
			if(getChannel().isOnline()) {
				Double state = (Double) getChannel().getETC("game_state");
				return api.getUabilityOnlineItemCode().get(state == null ? 0 : state.intValue()) + "";
			} else {
				return api.getUabilityOfflineItemCode() + "";
			}
		} else if(!getChannel().isOnline()) {
			return api.getOfflineItemCode() + "";
		}

		return itemCode + "";
	}

	private int makeAmount() {
		int amount = makeNAmount();
		return amount < 1 ? 1 : (amount > 128 ? 128 : amount);
	}

	private int makeNAmount() {
		if(!playerAmountIcon || (getChannel() == null && getChannelGroup() == null)) return amount;

		else if(getChannel() != null && getChannel().isOnline()) return getChannel().getPlayerCount();
		else if(getChannelGroup() != null) return getChannelGroup().getPlayerCount();

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
		if(getChannel() == null && getChannelGroup() == null) return ChatColor.translateAlternateColorCodes('&', text);

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

}