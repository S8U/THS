package su.plugin.ability.api.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.GameMap;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.player.PlayerKey;

public class VoteManager {
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Setter
	@Getter
	private boolean gameStartVoting = false;

	@Setter
	@Getter
	private long lastGameStartVote;
	
	@Getter
	private List<PlayerKey> gameStartVoteAgree = new ArrayList<>();
	@Getter
	private List<PlayerKey> gameStartVoteDisagree = new ArrayList<>();

	@Setter
	@Getter
	private int gameStartVoteTask;

	//

	@Setter
	@Getter
	private boolean invSkipVoting = false;

	@Getter
	private List<PlayerKey> invSkipVoteAgree = new ArrayList<>();

	//

	@Getter
	private HashMap<PlayerKey, GameMap> mapVote = new HashMap<>();
	
	public void initGameStartVote() {
		gameStartVoting = false;
		gameStartVoteAgree.clear();
		gameStartVoteDisagree.clear();

		api.getGUIManager().updateGameStartVoteGUI();
	}
	
	public void addGameStartAgree(Player p) {
		addGameStartAgree(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public void addGameStartAgree(PlayerKey playerKey) {
		gameStartVoteAgree.add(playerKey);
	}
	
	public void addGameStartDisagree(Player p) {
		addGameStartDisagree(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public void addGameStartDisagree(PlayerKey playerKey) {
		gameStartVoteDisagree.add(playerKey);
	}
	
	public void joinGameStartVote(Player p, boolean agree) {
		joinGameStartVote(PlayerKey.getPlayerKeyByPlatformPlayer(p), agree);
	}

	public int getGameStartVotedCount() {
		return gameStartVoteAgree.size() + gameStartVoteDisagree.size();
	}

	public void joinGameStartVote(PlayerKey playerKey, boolean agree) {
		if(agree) {
			addGameStartAgree(playerKey);
		} else {
			addGameStartDisagree(playerKey);
		}

		Core.cbc(ChatColor.DARK_AQUA, this.gameStartVoteAgree.size() + gameStartVoteDisagree.size() + "§b명이 게임 시작 투표에 참여했습니다. (찬성: §f" + this.gameStartVoteAgree
				.size() + " §b/ 반대: §f" + gameStartVoteDisagree.size() + "§b)");
		int playerCount = api.getPlayerManager().getOnlineJoinedPlayers().size();
		if (playerCount == 2 && this.gameStartVoteAgree.size() >= playerCount) {
			stopGameStartVote();
			api.getGameManager().startGame(true);
		} else if(playerCount != 2 && playerCount % 2 == 0 && this.gameStartVoteAgree.size() >= playerCount / 2) {
			stopGameStartVote();
			api.getGameManager().startGame(true);
		} else if(playerCount != 2 && playerCount % 2 != 0 && this.gameStartVoteAgree.size()  >= (playerCount + 1) / 2) {
			stopGameStartVote();
			api.getGameManager().startGame(true);
		} else if(playerCount % 2 == 0 && this.gameStartVoteDisagree.size() >= playerCount / 2) {
			stopGameStartVote();
			Core.cbc(ChatColor.RED, "§c게임 시작 투표가 부결되었습니다.");
		} else if(playerCount % 2 != 0 && this.gameStartVoteDisagree.size()  >= (playerCount + 1) / 2) {
			stopGameStartVote();
			Core.cbc(ChatColor.RED, "§c게임 시작 투표가 부결되었습니다.");
		} else {
			api.getBarManager().getWaitingQuickBar().update();
			api.getGUIManager().updateGameStartVoteGUI();
		}
	}
	
	public boolean isGameStartAgree(Player p) {
		return isGameStartAgree(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean isGameStartAgree(PlayerKey playerKey) {
		return gameStartVoteAgree.contains(playerKey);
	}
	
	public boolean isGameStartDisagree(Player p) {
		return isGameStartDisagree(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean isGameStartDisagree(PlayerKey playerKey) {
		return gameStartVoteDisagree.contains(playerKey);
	}
	
	public boolean isGameStartVoted(Player p) {
		return isGameStartVoted(PlayerKey.getPlayerKeyByPlatformPlayer(p));
	}
	
	public boolean isGameStartVoted(PlayerKey playerKey) {
		return isGameStartAgree(playerKey) || isGameStartDisagree(playerKey);
	}
	
	public void startGameStartVote(int time) {
		initGameStartVote();
		gameStartVoting = true;
		api.getTaskManager().runGameStartVoteTask(time);
	}
	
	public void stopGameStartVote() {
		initGameStartVote();
		lastGameStartVote = System.currentTimeMillis();
		api.getTaskManager().stopGameStartVoteTask();
	}

	public int getMapVoteCount(GameMap map) {
		int i = 0;

		for (GameMap gameMap : mapVote.values()) {
			if(map.equals(gameMap)) i++;
		}

		return i;
	}

}
