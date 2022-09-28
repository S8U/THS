package com.github.s8u.gamereplay;

import lombok.Getter;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;

public class GameReplayPlugin extends UKPlugin {

    @Getter
    private static GameReplayPlugin instance;


    @Override
    public void onUEnable() {
        instance = this;

        setPrefix("§7[ GameReplay ]");
        setColor(ChatColor.GRAY);

        registerListeners();
    }

}