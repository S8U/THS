package s8u.plugin.minigamecore.api.event;

import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKEvent;

@RequiredArgsConstructor
public class GameEndedEvent extends UKEvent {

    private final boolean autoGame;

}