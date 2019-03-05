package su.plugin.core.common.api.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface SubCommandHandler {
	
	String name();
	String[] parent();
	String[] aliases() default { "" };
	String additional() default "";
	String usage() default "";
	
	int minArgs() default 0;
	int maxArgs() default -1;
	
	String permission() default "";
	String noPermissionMessage() default "권한이 없습니다.";
	
	String playerOnlyMessage() default "콘솔에서는 사용할 수 없습니다.";
	String consoleOnlyMessage() default "콘솔에서만 사용할 수 있습니다.";

}