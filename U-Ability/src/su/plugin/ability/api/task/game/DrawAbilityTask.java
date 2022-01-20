package su.plugin.ability.api.task.game;

import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.permissions.PermissionAttachmentInfo;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.bukkit.api.scheduler.UKRunnable;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;

public class DrawAbilityTask extends UKRunnable {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private int drawDelay, count;
	
	public DrawAbilityTask() {
		super(AbilityPlugin.getInstance());
	}
	
	public void init(int drawDelay) {
		count = -1;
		this.drawDelay = drawDelay;
	}
	
	public void run() {
		count++;
		if(count == 0) {
			api.playSoundToAll(Sound.ITEM_PICKUP, 1, 1);
			Core.nbc(" ");
			Core.cbc(ChatColor.DARK_GREEN, "§a잠시 후 능력 추첨이 시작됩니다.");
			api.getBarManager().getBossBar().setText("잠시 후 능력 추첨이 시작됩니다.");
			api.getBarManager().getBossBar().setProgress(100);
		} else if(count == 1) {
			Core.nbc(" ");
			Core.cbc(ChatColor.DARK_AQUA, "§b할당될 능력 플러그인 목록");
			if(api.isUseThisPluginAbility()) {
				Core.cbc(ChatColor.DARK_AQUA, "U-Ability (ShaDow_Uni) (" + api.getAbilityManager().getAbilities("U-Ability").size() + "개)");
			}
			if(api.isUseBitAbility()) {
				Core.cbc(ChatColor.DARK_AQUA, "BitAbility (Heart_bit) (" + api.getAbilityManager().getAbilities("BitAbility").size() + "개)");
			}
			if(api.isUsePhysicalFighters()) {
				Core.cbc(ChatColor.DARK_AQUA, "Physical Fighters (염료) (" + api.getAbilityManager().getAbilities("PhysicalFighters").size() + "개)");
			}

			api.getAbilityManager().getPluginAbilities().forEach((pluginName, abilities) -> {
				if (pluginName.equals("U-Ability") || pluginName.equals("BitAbility") || pluginName.equals("PhysicalFighters")) return;
				Core.cbc(ChatColor.DARK_AQUA, pluginName + " (" + abilities.size() +"개)");
			});
		} else if(count == drawDelay) {
			api.playSoundToAll(Sound.LEVEL_UP, 1, 1);
			api.getGameManager().setGameState(GameState.DRAWING);
			
			if(api.getPlayerManager().getJoinedPlayers().size() > api.getAbilityManager().getAbilities().size() && !api.isUseOverlap()) {
				Core.cbc(ChatColor.RED, "§c능력이 부족하여 능력을 할당할 수 없습니다!");
				Core.cbc(ChatColor.RED, "§c게임이 중단되었습니다.");
				api.getGameManager().stopGame();
				return;
			}
			
			api.getAbilityManager().giveRandomAbilityToAll(api.isUseOverlap());
			api.getBarManager().updateSideBarAllPlayer();
			
			Core.nbc(" ");
			Core.cbc(ChatColor.DARK_GREEN, "§b임시 능력이 할당되었습니다.");
			Core.cbc(ChatColor.DARK_GREEN, "§b'/a help' 또는 '/능력' 명령어로 능력을 확인해보세요!");
			Core.cbc(ChatColor.DARK_GREEN, "§b'/yes' 또는 '/확정' 명령어로 능력을 확정할 수 있습니다.");
			
			for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
				gp.setRedrawCount(api.getRedrawCount(gp.getRank()));

				for (PermissionAttachmentInfo pai : gp.getPlayer().getEffectivePermissions()) {
					if (pai.getPermission().startsWith("ability.drawcount.")) {
						gp.setRedrawCount(Integer.parseInt(pai.getPermission().substring(pai.getPermission().lastIndexOf(".") + 1, pai.getPermission().length())));
						break;
					}
				}

				Core.cmsg(gp.getPlayer(), ChatColor.DARK_GREEN, "§b'/no' 또는 '/재추첨' 명령어로 능력을 §f" + gp.getRedrawCount() + "§a회 재추첨할 수 있습니다.");
			}
			
			Core.cbc(ChatColor.DARK_GREEN, "§b능력을 추첨 중입니다.. (" + api.getAbilityManager().getConfirmationCount() + " / " + api.getPlayerManager().getOnlineJoinedPlayers().size() + ")");
			if(!api.getGameManager().isAutoMode()) {
				api.getBarManager().getBossBar().setText("능력을 추첨 중입니다.. (" + api.getAbilityManager().getConfirmationCount() + " / " + api.getPlayerManager().getOnlineJoinedPlayers().size() + ")");
				api.getBarManager().getBossBar().setProgress((float) api.getAbilityManager().getConfirmationCount() / (float) api.getPlayerManager().getOnlineJoinedPlayers().size() * 100);
			}
			
			if(api.getGameManager().isAutoMode() && api.isUseDrawTimeLimit()) {
				api.getTaskManager().runDrawSkipTask(20 * 2);
			}
			
			api.getTaskManager().stopDrawAbilityTask();
		}
	}
	
}