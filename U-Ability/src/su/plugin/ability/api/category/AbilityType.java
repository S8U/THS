package su.plugin.ability.api.category;

public enum AbilityType {
	
	PASSIVE("패시브"),
	ACTIVE_CONTINUE("액티브"),
	ACTIVE("액티브");
	
	private String text = null;
	
	private AbilityType(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
}
