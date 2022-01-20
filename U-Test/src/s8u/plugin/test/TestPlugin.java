package s8u.plugin.test;

import java.io.FileOutputStream;
import java.io.PrintStream;
import su.plugin.core.bukkit.api.plugin.UKPlugin;

public class TestPlugin extends UKPlugin {

  @Override
  public void onUEnable() {
    registerListeners();
    registerCommands();

    try {
      System.setErr(new PrintStream(new FileOutputStream("error.log", true)));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}