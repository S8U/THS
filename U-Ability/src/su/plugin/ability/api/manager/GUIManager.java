package su.plugin.ability.api.manager;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.GUI;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.player.UPlayer;

public class GUIManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Setter
	@Getter
	private GUI teleportGUI, gameStartVoteGUI, mapVoteGUI;
	
	public void updateTeleportGUI() {
		List<GamePlayer> gamePlayers = api.getPlayerManager().getOnlineJoinedPlayers();
		int row = (int) Math.ceil(gamePlayers.size() / 9) + 1;
		
		List<Player> n = new ArrayList<>();
		if(teleportGUI == null || teleportGUI.getRow() != row) {
			n = KCore.getGUIManager().getPlayers(teleportGUI);
			teleportGUI = new GUI("U-Ability", "플레이어 선택", row);
		}
		
		teleportGUI.getInventory().clear();
		teleportGUI.getIcons().clear();
		
		for(int i = 0; i < gamePlayers.size(); i++) {
			final int j = i;
			
			Icon icon = new Icon() {
				private GamePlayer gp = gamePlayers.get(j);
				
				@Override
				public ItemStack updateItem() {
					ItemStack item = ItemUtil.makeItem(397, (short) 3, "§f" + gp.getDisplayName(), "§f클릭 시 이동됩니다.");

					ItemMeta meta = item.getItemMeta();
					((SkullMeta) meta).setOwner(gp.getName());
					item.setItemMeta(meta);

					return item;
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					if(gp == null || gp.getPlayer() == null) {
						Core.wmsg(e.getPlayer(), "선택한 플레이어가 접속 중이 아닙니다.");
						return;
					}

					e.getPlayer().closeInventory();

					KCore.teleport(e.getPlayer(), gp.getPlayer().getLocation());
				}
			};
			
			teleportGUI.setIcon(i, icon);
		}
		
		teleportGUI.update();
		
		if(n.size() > 0) {
			n.forEach(gp -> teleportGUI.open(gp));
		}
	}

	//

	public void updateGameStartVoteGUI() {
		if(gameStartVoteGUI == null) {
			gameStartVoteGUI = new GUI("U-Ability GameStart", "게임 시작 투표", 1);

			Icon agreeIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return new ItemBuilder("351:10")
							.amount(api.getVoteManager().getGameStartVoteAgree().size() < 1 ? 1 : api.getVoteManager().getGameStartVoteAgree().size())
							.displayName("§l§a찬성")
							.lore("§f클릭 시 투표에 §a찬성§f합니다.", "", "§b인원: " + api.getVoteManager().getGameStartVoteAgree().size() + " §b명")
							.build();
				}

				@Override
				public void onIconClick(IconClickEvent e) {
					UPlayer up = Core.getUPlayerByPlatformPlayer(e.getPlayer());

					if(api.getGameManager().isGameStarted()) {
						up.wmsg("이미 게임이 시작되었습니다.");
					} else if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
						up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
					} else if(!api.getVoteManager().isGameStartVoting()) {
						up.wmsg("투표 중이 아닙니다.");
					} else if(api.getVoteManager().isGameStartVoted(up.getPlayerKey())) {
						up.wmsg("이미 투표에 참여했습니다.");
					} else if(api.getPlayerManager().getTeamAmount() < 2) {
						Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

						api.getVoteManager().stopVote();
					} else {
						api.getVoteManager().joinGameStartVote(up.getPlayerKey(), true);

						up.cmsg(ChatColor.DARK_AQUA, "§a투표에 찬성했습니다.");
					}

					e.getPlayer().closeInventory();
				}
			};

			Icon disagreeIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return new ItemBuilder("351:13")
							.amount(api.getVoteManager().getGameStartVoteDisagree().size() < 1 ? 1 : api.getVoteManager().getGameStartVoteDisagree().size())
							.displayName("§l§c반대")
							.lore("§f클릭 시 투표에 §c반대§f합니다.", "", "§b인원: " + api.getVoteManager().getGameStartVoteDisagree().size() + " §b명")
							.build();
				}

				@Override
				public void onIconClick(IconClickEvent e) {
					UPlayer up = Core.getUPlayerByPlatformPlayer(e.getPlayer());

					if(api.getGameManager().isGameStarted()) {
						up.wmsg("이미 게임이 시작되었습니다.");
					} else if(api.getPlayerManager().getGamePlayer(up.getPlayerKey()).isWatchMode()) {
						up.wmsg("관전 중에는 투표에 참여할 수 없습니다.");
					} else if(!api.getVoteManager().isGameStartVoting()) {
						up.wmsg("투표 중이 아닙니다.");
					} else if(api.getVoteManager().isGameStartVoted(up.getPlayerKey())) {
						up.wmsg("이미 투표에 참여했습니다.");
					} else if(api.getPlayerManager().getTeamAmount() < 2) {
						Core.cbc(ChatColor.RED, "§c인원이 부족하여 투표가 중단되었습니다.");

						api.getVoteManager().stopVote();
					} else {
						api.getVoteManager().joinGameStartVote(up.getPlayerKey(), false);

						up.cmsg(ChatColor.DARK_AQUA, "§c투표에 반대했습니다.");
					}

					e.getPlayer().closeInventory();
				}
			};

			gameStartVoteGUI.setIcon(4, 1, agreeIcon);
			gameStartVoteGUI.setIcon(6, 1, disagreeIcon);
		}

		gameStartVoteGUI.updateAsynchronously();
	}

	//

	public void updateMapVoteGUI() {
		updateMapVoteGUI(false);
	}

	public void updateMapVoteGUI(boolean recreate) {
		if(mapVoteGUI == null || recreate) {
			mapVoteGUI = new GUI("U-Ability Map", "맵 투표", Double.valueOf(Math.ceil((api.getMapManager().getMaps().size() + 1) / 9)).intValue() + 1);

			Icon randomIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					int count = api.getPlayerManager().getOnlineJoinedPlayers().size() - api.getVoteManager().getMapVote().size();

					return new ItemBuilder(Material.EMPTY_MAP)
							.amount(count < 1 ? 1 : count)
							.displayName("§f§l랜덤")
							.lore("§e클릭 시 §f랜덤§e에 투표합니다.", "", "§e인원: §f" + count + " §e명")
							.build();
				}

				@Override
				public void onIconClick(IconClickEvent e) {
					api.getVoteManager().getMapVote().remove(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()));

					e.getPlayer().closeInventory();

					if(api.isUseWaitingQuickBar()) {
						api.getBarManager().getWaitingQuickBar().update();
					}
					api.getGUIManager().updateMapVoteGUI();

					Core.cmsg(e.getPlayer(), ChatColor.DARK_AQUA, "랜덤§e에 투표했습니다.");
				}
			};

			mapVoteGUI.setIcon(0, randomIcon);

			int i = 1;
			for(GameMap map : api.getMapManager().getMaps().values()) {
				Icon mapIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						GameMap gameMap = (GameMap) getObject("map");
						int count = api.getVoteManager().getMapVoteCount(gameMap);

						return new ItemBuilder(Material.MAP)
								.amount(count < 1 ? 1 : count)
								.displayName("§f§l" + gameMap.getName())
								.lore("§e클릭 시 §f" + gameMap.getName() + " §e맵에 투표합니다.", "", "§e인원: §f" + count + " §e명")
								.build();
					}

					@Override
					public void onIconClick(IconClickEvent e) {
						api.getVoteManager().getMapVote().put(PlayerKey.getPlayerKeyByPlatformPlayer(e.getPlayer()), (GameMap) getObject("map"));

						e.getPlayer().closeInventory();

						if(api.isUseWaitingQuickBar()) {
							api.getBarManager().getWaitingQuickBar().update();
						}
						api.getGUIManager().updateMapVoteGUI();

						Core.cmsg(e.getPlayer(), ChatColor.DARK_AQUA, map.getName() + " §e맵에 투표했습니다.");
					}
				};

				mapIcon.setObject("map", map);

				mapVoteGUI.setIcon(i, mapIcon);

				i++;
			}
		}

		mapVoteGUI.updateAsynchronously();
	}
	
}