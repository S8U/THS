package su.plugin.ability.api.manager;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.time.FastDateFormat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.ABossBar;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.builder.ItemBuilder;
import su.plugin.core.bukkit.api.enumeration.ClickAction;
import su.plugin.core.bukkit.api.event.gui.IconClickEvent;
import su.plugin.core.bukkit.api.gui.Icon;
import su.plugin.core.bukkit.api.gui.QuickBar;
import su.plugin.core.bukkit.api.gui.SideBar;
import su.plugin.core.bukkit.api.util.ItemUtil;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.StringUtil;

public class BarManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	private FastDateFormat df = FastDateFormat.getInstance("aa hh:mm");
	
	@Setter
	@Getter
	private QuickBar waitingQuickBar, watchModeQuickBar;
	
	@Getter
	private ABossBar bossBar = new ABossBar();
	
	public void initQuickBar() {
		Icon watchIcon = new Icon() {
			@Override
			protected ItemStack updateItem() {
				return ItemUtil.makeItem(Material.BED, "§e§l로비로 이동 §f§l(우클릭)", "§f우클릭 시 로비로 이동합니다.");
			}
			
			@Override
			public void onIconClick(IconClickEvent e) {
				if(e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;
				
				api.getBungeeManager().sendToLobby(e.getPlayer());
				
				Core.cmsg(e.getPlayer(), ChatColor.DARK_GREEN, "§e로비로 이동됩니다.");
			}
		};
		
		if(api.isUseWaitingQuickBar()) {
			waitingQuickBar = new QuickBar();
			waitingQuickBar.setIcon(9, watchIcon);

			if(api.isUseGameStartVote()) {
				Icon gameStartVoteIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						return new ItemBuilder(Material.PAPER)
								.amount(api.getVoteManager().getGameStartVotedCount() < 1 ? 1 : api.getVoteManager().getGameStartVotedCount())
								.displayName("§e§l시작 투표")
								.lore(api.getVoteManager().isGameStartVoting() ? "§f우클릭 시 시작 투표 GUI가 열립니다." : "§f우클릭 시 시작 투표를 시작합니다.")
								.build();
					}

					@Override
					public void onIconClick(IconClickEvent e) {
						if(e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;
						else if(api.getGameManager().isGameStarted()) {
							Core.wmsg(e.getPlayer(), "이미 게임이 시작되었습니다.");
						} else if(api.getVoteManager().isGameStartVoting()) {
							api.getGUIManager().getGameStartVoteGUI().open(e.getPlayer());
						} else if(api.getPlayerManager().getTeamAmount() < 2) {
							Core.wmsg(e.getPlayer(),"인원이 적어 투표를 진행할 수 없습니다.");
						} else if((System.currentTimeMillis() - api.getVoteManager().getLastGameStartVote()) < api.getRevotePeriod() * 1000) {
							Core.wmsg(e.getPlayer(), "아직 시작 투표를 진행할 수 없습니다.");
						} else {
							api.getVoteManager().startGameStartVote(api.getVoteTimeoutCount());

							Core.cbc(ChatColor.DARK_AQUA, "§b게임 시작 투표가 시작되었습니다.");
							Core.cbc(ChatColor.DARK_AQUA, "'/찬성' §b또는 §f'/반대' §b명령어를 사용하여 투표에 참여하세요!");
						}
					}
				};

				waitingQuickBar.setIcon(1, gameStartVoteIcon);
			}

			if(api.isUseMapVote()) {
				Icon mapVoteIcon = new Icon() {
					@Override
					protected ItemStack updateItem() {
						return new ItemBuilder(Material.MAP)
								.amount(api.getVoteManager().getMapVote().size() < 1 ? 1 : api.getVoteManager().getMapVote().size())
								.displayName("§b§l맵 투표")
								.lore(api.getVoteManager().isGameStartVoting() ? "§f우클릭 시 맵 투표 GUI가 열립니다." : "§f우클릭 시 맵 투표를 시작합니다.")
								.build();
					}

					@Override
					public void onIconClick(IconClickEvent e) {
						if(e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;
						else if(api.getMapManager().getMaps().size() < 1) {
							Core.wmsg(e.getPlayer(), "아직 맵이 생성되지 않았습니다.");
							return;
						}

						api.getGUIManager().getMapVoteGUI().open(e.getPlayer());
					}
				};

				waitingQuickBar.setIcon(api.isUseGameStartVote() ? 2 : 1, mapVoteIcon);
			}

			waitingQuickBar.update();
		}
		
		if(api.isUseWatchModeQuickBar()) {
			Icon teleportIcon = new Icon() {
				@Override
				protected ItemStack updateItem() {
					return new ItemBuilder(Material.COMPASS)
							.amount(api.getPlayerManager().getOnlineJoinedPlayers().size())
							.displayName("§e§l순간이동기 §f§l(우클릭)")
							.lore("§f우클릭 시 순간이동할 플레이어를 선택합니다.")
							.build();
				}
				
				@Override
				public void onIconClick(IconClickEvent e) {
					if(e.getQuickBarClickEvent().getClickAction() == ClickAction.LEFT_CLICK) return;
					
					if(api.getPlayerManager().getOnlineJoinedPlayers().size() < 1) {
						Core.wmsg(e.getPlayer(), "게임 중인 플레이어가 없습니다.");
						return;
					} else if(api.getGUIManager().getTeleportGUI() == null) {
						api.getGUIManager().updateTeleportGUI();
					}
					
					api.getGUIManager().getTeleportGUI().open(e.getPlayer());
				}
			};
			
			watchModeQuickBar = new QuickBar();
			watchModeQuickBar.setIcon(1, teleportIcon);
			watchModeQuickBar.setIcon(9, watchIcon);
			watchModeQuickBar.update();
		}
	}
	
	//
	
	public void updateSideBar(GamePlayer gp) {
		if(!api.isUseSideBar() && !gp.isOnline()) return;
		
		Bukkit.getScheduler().runTask(AbilityPlugin.getInstance(), () -> gp.getPlayer().setScoreboard(makeScoreboard(gp)));
	}
	
	public void updateSideBarAllPlayer() {
		if(!api.isUseSideBar()) return;
		
		for(GamePlayer agp : api.getPlayerManager().getOnlinePlayers()) {
			updateSideBar(agp);
		}
	}
	
	private Scoreboard makeScoreboard(GamePlayer gp) {
		SideBar sb = new SideBar("§e§lAbility");
		sb.addText("§7" + df.format(System.currentTimeMillis()));
		
		if(api.isUseSideBarGameInfo()) {
			if(!api.getGameManager().isGameStarted() && api.isUseAutoStart()) {
				sb.addText("§6게임 시작까지");
				sb.addText(api.getPlayerManager().getOnlineJoinedPlayers().size() + "/" + api.getAutoStartCount());
			} else if(api.getTaskManager().getDrawSkipTask() != null && api.getTaskManager().getDrawSkipTask().isRunning()) {
				sb.addText("§6능력 확정까지");
				sb.addText(StringUtil.buildTimeString(api.getTaskManager().getDrawSkipTask().getRemainingCount() * 1000));
				sb.addText("§f");
			} else if(api.getTaskManager().getInvincibilityTask() != null && api.getTaskManager().getInvincibilityTask().isRunning()) {
				sb.addText("§6무적 해제까지");
				sb.addText(StringUtil.buildTimeString(api.getTaskManager().getInvincibilityTask().getRemainingCount() * 1000));
				sb.addText("§f");
			} else if(api.getTaskManager().getTeleportAllTask() != null && api.getTaskManager().getTeleportAllTask().isRunning()) {
				sb.addText("§6텔레포트까지");
				sb.addText(StringUtil.buildTimeString(api.getTaskManager().getTeleportAllTask().getRemainingCount() * 1000));
				sb.addText("§f");
			}
		}
		
		if(api.getGameManager().isGameStarted()) {
			if(gp.hasAbility()) {
				List<String> abilityNames = new ArrayList<>();
				gp.getAbilities().forEach(ab -> abilityNames.add(ab.getName()));
				
				sb.addText("§c능력");
				sb.addText(StringUtil.connectString(abilityNames, " | "));
				sb.addText("§f§f");
				
				List<String> abilityCooltimes = new ArrayList<>();
				gp.getAbilities().forEach(ab -> {
					if(ab.getCoolTime() > 0) {
						abilityCooltimes.add(ab.getRemainingCoolTime() < 1 ? "사용 가능" : StringUtil.buildTimeString(ab.getRemainingCoolTime() * 1000));
					}
				});
				if(abilityCooltimes.size() > 0) {
					sb.addText("§e쿨타임");
					sb.addText(StringUtil.connectString(abilityCooltimes, " | "));
					sb.addText("§f§f§f");
				}
				
				List<String> abilityDurations = new ArrayList<>();
				gp.getAbilities().forEach(ab -> {
					if(ab.getRemainingDurationTime() > 0) {
						abilityDurations.add(ab.getRemainingDurationTime() < 1 ? "사용 가능" : StringUtil.buildTimeString((ab.getRemainingDurationTime() * 1000)));
					}
				});
				if(abilityDurations.size() > 0) {
					sb.addText("§a지속 시간");
					sb.addText(StringUtil.connectString(abilityDurations, " | "));
					sb.addText("§f§f§f§f");
				}
			}
			
			if(api.getGameManager().isAutoMode()) {
				sb.addText("§2남은 인원");
				sb.addText(api.getPlayerManager().getOnlineJoinedPlayers().size() + "명");
				sb.addText("§f§f§f§f§f");
			}
			
			sb.addText("§b진행 시간");
			sb.addText(StringUtil.buildTimeString(api.getGameManager().getPlayTime()));
		}
		
		return sb.updateScoreboard();
	}
	
}