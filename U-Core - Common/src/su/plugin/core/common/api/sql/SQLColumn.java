package su.plugin.core.common.api.sql;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class SQLColumn {
	
	private final String name, type;
	
	private final boolean notNull, autoIncrement;
	
	@Setter
	private boolean primaryKey;

}