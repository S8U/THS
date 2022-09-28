package com.github.s8u.gamereplay;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import su.plugin.ability.api.event.GameStartedEvent;
import su.plugin.ability.api.event.GameStoppedEvent;
import su.plugin.ability.api.event.WinEvent;

public class ReplayListener implements Listener {

    /**
     * 게임이 시작되면 리플레이 녹화 시작 (시작 카운트 끝나고 능력 추첨 전)
     */
    @EventHandler
    public void onGameStarted(GameStartedEvent event) {
        // 맵으로 텔레포트된 이후 녹화하기 위해 딜레이 후 녹화 시작
        Bukkit.getScheduler().runTaskLater(GameReplayPlugin.getInstance(), () -> GameReplayApi.startRecord(), 1L);
    }

    /**
     * 게임 중단 시 리플레이 녹화 중단
     */
    @EventHandler
    public void onGameStopped(GameStoppedEvent event) {
        GameReplayApi.stopRecord();
    }

    /**
     * 게임 종료 시 리플레이 녹화 중단
     */
    @EventHandler
    public void onWin(WinEvent event) {
        GameReplayApi.stopRecord();
    }

}