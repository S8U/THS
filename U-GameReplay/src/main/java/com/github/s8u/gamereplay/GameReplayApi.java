package com.github.s8u.gamereplay;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.jumper251.replay.api.ReplayAPI;
import me.jumper251.replay.replaysystem.Replay;
import org.bukkit.Bukkit;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.core.common.api.Core;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class GameReplayApi {

    @Setter
    @Getter
    private static Replay recodingReplay;


    @SneakyThrows
    public static void startRecord() {
        if (recodingReplay == null || !recodingReplay.isRecording()) {
            String path = new File("").getCanonicalPath();
            String folderName = path.substring(path.lastIndexOf(File.separator) + 1, path.length());

            // 서버폴더명:년월일시분초밀리초
            String replayName = folderName + ":" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmmssSSS"));

            recodingReplay = ReplayAPI.getInstance().recordReplay(replayName, Bukkit.getConsoleSender(),
                    AbilityAPI.getPlayerManager().getOnlineJoinedPlayers().stream()
                            .map(gamePlayer -> gamePlayer.getPlayer())
                            .collect(Collectors.toList()));

            Core.log(recodingReplay.getId() + " 리플레이 녹화가 시작되었습니다.");
        }
    }

    public static void stopRecord() {
        if (recodingReplay != null && recodingReplay.isRecording()) {
            ReplayAPI.getInstance().stopReplay(recodingReplay.getId(), true);
            Core.log(recodingReplay.getId() + " 리플레이가 녹화되었습니다.");
        }
    }

}