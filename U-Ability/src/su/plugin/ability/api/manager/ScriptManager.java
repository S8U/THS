package su.plugin.ability.api.manager;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import lombok.Getter;

@Getter
public class ScriptManager {

  private ScriptEngineManager scriptEngineManager = new ScriptEngineManager();

  private ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("JavaScript");

}
