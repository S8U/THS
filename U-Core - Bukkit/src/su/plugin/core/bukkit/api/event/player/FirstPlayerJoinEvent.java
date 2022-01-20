package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKEvent;

/**
 * 서버에 플레이어가 접속하여 1명이 됐을 경우 일어나는 이벤트
 */
@RequiredArgsConstructor
@Getter
public class FirstPlayerJoinEvent extends UKEvent {
	
	private final Player player;
	
	private final PlayerJoinEvent playerJoinEvent;
	
}