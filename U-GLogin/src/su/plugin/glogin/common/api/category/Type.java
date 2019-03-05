package su.plugin.glogin.common.api.category;

import lombok.Getter;

public enum Type {
	REGISTER("register"), UNREGISTER("unregister"), LOGIN("login"), LOGOUT("logout"), PASSWORD_CHANGE("passwordchange");
	
	@Getter
	private String text;
	
	private Type(String text) {
		this.text = text;
	}
	
}