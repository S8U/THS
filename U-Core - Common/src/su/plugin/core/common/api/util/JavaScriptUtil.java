package su.plugin.core.common.api.util;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JavaScriptUtil {

  private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

  static {
    try {
      engine.eval("function eval2(formula) { return eval(formula); }");
    } catch (ScriptException e) {
      e.printStackTrace();
    }
  }

  @SneakyThrows(Exception.class)
  public static Object getValue(String formula) {
    return ((Invocable) engine).invokeFunction("eval2", formula);
  }

}