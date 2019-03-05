package su.plugin.ability.api.category;

import lombok.Getter;

@Getter
public enum GameState {
	WAITING("대기 중", 1),
	PREPARING("준비 중", 2),
	DRAWING("능력 추첨 중", 3),
	STARTING("게임 시작 중", 4),
	PLAYING("게임 중", 5),
	END("끝", 6);
	
	private String text;
	private int progress;
	
	private GameState(String text, int progress) {
		this.text = text;
		this.progress = progress;
	}
	
}