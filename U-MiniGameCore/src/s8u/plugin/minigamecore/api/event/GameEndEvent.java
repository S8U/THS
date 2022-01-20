package s8u.plugin.minigamecore.api.event;

import lombok.RequiredArgsConstructor;
import su.plugin.core.bukkit.api.event.UKCancellableEvent;

@RequiredArgsConstructor
public class GameEndEvent extends UKCancellableEvent {

    private final boolean autoGame;

}