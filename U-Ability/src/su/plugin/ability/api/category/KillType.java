package su.plugin.ability.api.category;

import lombok.Getter;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;

public enum KillType {
	NORMAL(1, ""), DOUBLE(2, "더블 킬"), TRIPLE(3, "트리플 킬"), QUADRA(4, "쿼드라 킬"), PENTA(5, "펜타 킬");
	
	private AbilityAPI api = AbilityPlugin.getApi();
	
	@Getter
	private int number;
	@Getter
	private String text;
	
	private KillType(int number, String text) {
		this.number = number;
		this.text = text;
	}
	
	public static KillType getKillType(int number) {
		switch(number) {
		case 2:
			return DOUBLE;
		case 3:
			return TRIPLE;
		case 4:
			return QUADRA;
		case 5:
			return PENTA;
		}
		return NORMAL;
	}
	
	public int getKillCount() {
		switch(number) {
		case 2:
			return api.getDoubleCount();
		case 3:
			return api.getTripleCount();
		case 4:
			return api.getQuadraCount();
		case 5:
			return api.getPentaCount();
		}
		return 0;
	}

	public boolean useContinueKill() {
		switch(number) {
			case 2:
				return api.isUseDouble();
			case 3:
				return api.isUseTriple();
			case 4:
				return api.isUseQuadra();
			case 5:
				return api.isUsePenta();
		}

		return false;
	}
	
	public double getKillMoney() {
		switch(number) {
		case 2:
			return api.getDoubleMoney();
		case 3:
			return api.getTripleMoney();
		case 4:
			return api.getQuadraMoney();
		case 5:
			return api.getPentaMoney();
		}
		return 0;
	}

}