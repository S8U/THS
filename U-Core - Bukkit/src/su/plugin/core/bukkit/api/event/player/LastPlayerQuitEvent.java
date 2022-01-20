package su.plugin.core.bukkit.api.event.player;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKEvent;

/**
 * 서버에 마지막 플레이어가 퇴장하여 0명이 됐을 경우 일어나는 이벤트
 */
@RequiredArgsConstructor
@Getter
public class LastPlayerQuitEvent extends UKEvent {
	
	private final Player player;
	
	private final PlayerQuitEvent playerQuitEvent;
	
}