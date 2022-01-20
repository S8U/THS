package su.plugin.channelgui.api.manager;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import su.plugin.channel.common.api.ChannelAPI;
import su.plugin.channelgui.ChannelGUIPlugin;
import su.plugin.channelgui.api.ChannelGUIAPI;
import su.plugin.channelgui.api.category.CType;
import su.plugin.channelgui.api.object.ChannelGUI;
import su.plugin.channelgui.api.object.ChannelIcon;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.config.json.JsonConfig;
import su.plugin.core.common.api.util.StringUtil;

public class ConfigManager {
	
	private ChannelGUIAPI api = ChannelGUIPlugin.getApi();
	
	@Getter
	private File GUIFolder = new File(ChannelGUIPlugin.getInstance().getDataFolder(), "gui");
	
	public void createFolder() {
		if(GUIFolder.exists()) return;
		GUIFolder.mkdirs();

		createExampleGUI();
	}
	
	public void createExampleGUI() {
		JsonConfig nexample = new JsonConfig(new File(GUIFolder, "example-normal.json"));

		nexample.set("이름", "서버 목록");
		nexample.set("줄", 1);
		nexample.set("타입", "일반");

		nexample.set("권한", "channelgui.example");
		nexample.set("아이템 개수를 플레이어 수로 사용", false);

		nexample.set("아이콘.채널1.아이템 코드", "1");
		nexample.set("아이콘.채널1.아이템 이름", "&f채널 &e[1]");
		nexample.set("아이콘.채널1.아이템 설명", Arrays.asList("&f클릭 시 이동됩니다."));
		nexample.set("아이콘.채널1.X", 1);
		nexample.set("아이콘.채널1.Y", 1);

		nexample.save();

		JsonConfig aexample = new JsonConfig(new File(GUIFolder, "example-uability.json"));

		aexample.set("이름", "서버 목록");
		aexample.set("줄", 1);

		aexample.set("타입", "U-Ability");

		aexample.set("권한", "channelgui.example");
		aexample.set("아이템 개수를 플레이어 수로 사용", false);

		aexample.set("아이콘.채널1.채널", "<channel:channel_name>");
		aexample.set("아이콘.채널1.아이템 코드", "1");
		aexample.set("아이콘.채널1.아이템 이름", "&f능력자 채널 &e[1]");
		aexample.set("아이콘.채널1.아이템 설명", Arrays.asList("<상태>", "&f클릭 시 이동됩니다."));
		aexample.set("아이콘.채널1.X", 1);
		aexample.set("아이콘.채널1.Y", 1);

		aexample.save();
	}

	public void loadGUIs() {
		api.getGUIManager().getGUIs().values().forEach(gui -> gui.closeAll());
		api.getGUIManager().getGUIs().clear();

		File[] files = GUIFolder.listFiles();
		if(files.length < 1) return;

		for(File file : files) {
			loadGUI(file);
		}

		Core.log(api.getGUIManager().getGUIs().size() + "개의 GUI를 불러왔습니다.");
	}
	
	public void loadGUI(File file) {
		try {
			JsonConfig config = new JsonConfig(file).load();

			String name = file.getName().substring(0, file.getName().length() - 5);

			//

			String title = ChatColor.translateAlternateColorCodes('&', config.getString("이름"));
			int row = config.getInt("줄");

			ChannelGUI gui = new ChannelGUI("U-ChannelGUI " + name, title, row);

			CType defaultType = CType.getCTypeByName(config.getString("타입"));
			if(defaultType == null) {
				Core.wlog(file.getName() + ": GUI를 불러올 수 없습니다 : 잘못된 타입");
				return;
			}

			gui.setDefaultType(defaultType);
			gui.setPermission(config.getString("권한"));
			gui.setPlayerAmountMenu(config.getBoolean("아이템 개수를 플레이어 수로 사용"));

			for(String key : config.getKeys("아이콘")) {
				String path = "아이콘." + key + ".";

				ChannelIcon icon = new ChannelIcon(gui);

				icon.setName(key);
				icon.setItemCode(config.getString(path + "아이템 코드") + "");
				icon.setAmount(config.getInt(path + "아이템 개수"));
				icon.setDynamicAmountFormula(config.getString(path + "동적 아이템 개수"));

				icon.setPlayerAmountIcon(gui.isPlayerAmountMenu());

				icon.setDisplayName(config.getString(path + "아이템 이름"));
				List<String> lore = config.getStringList(path + "아이템 설명");
				if(lore != null) {
					icon.setLore(lore);
				}

				icon.setLeftCommands(config.getStringList(path + "좌클릭"));
				icon.setRightCommands(config.getStringList(path + "우클릭"));
				icon.setShiftCommands(config.getStringList(path + "쉬프트 클릭"));

				String channel = config.getString(path + "채널");
				if(channel != null) {
					if(channel.startsWith("<channel:")) {
						icon.setChannel(ChannelAPI.getChannelManager().getChannel(StringUtil.getValue("channel", channel).get(0)));
					} else if (channel.startsWith("<channelgroup:")) {
						icon.setChannelGroup(ChannelAPI.getChannelGroupManager().getChannelGroup(StringUtil.getValue("channelgroup", channel).get(0)));
					}

					if(icon.getChannel() == null && icon.getChannelGroup() == null) {
						Core.wlog(file.getName() + ": GUI를 불러올 수 없습니다 : 잘못된 채널");
						return;
					}
				}

				CType type = CType.getCTypeByName(config.getString(path + "타입"));
				if(type == null) {
					icon.setType(defaultType);
				} else {
					icon.setType(type);
				}

				gui.setIcon((int) config.getDouble(path + "X"), (int) config.getDouble(path + "Y"), icon);
			}

			gui.updateAsynchronously();

			api.getGUIManager().setGUI(name, gui);
		} catch (Exception e) {
			e.printStackTrace();
			Core.wlog(file.getName() + ": GUI를 불러올 수 없습니다.");
		}
	}
	
}
