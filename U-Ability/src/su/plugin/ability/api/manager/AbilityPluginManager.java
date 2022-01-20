package su.plugin.ability.api.manager;

import Physical.Fighters.MainModule.AbilityBase;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.object.other.BAbility;
import su.plugin.ability.api.object.other.PAbility;
import su.plugin.core.bukkit.api.util.PluginUtil;
import su.plugin.core.bukkit.api.util.KReflectionUtil;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.util.NotDuplicatedArrayList;

public class AbilityPluginManager {

  private AbilityAPI api = AbilityPlugin.getApi();

  @Getter
  private File pluginFolder = new File(AbilityPlugin.getInstance().getDataFolder(), "plugins");

  @Setter
  @Getter
  private boolean useInject;

  @Getter
  private List<String> injectPluginNames = new ArrayList<>();
  @Getter
  private List<String> injectedClassNames = new NotDuplicatedArrayList<>();

  public static Player[] getOnlinePlayers() {
    return (Player[]) Bukkit.getOnlinePlayers().toArray(new Player[Bukkit.getOnlinePlayers().size()]);
  }

  @SneakyThrows(Exception.class)
  public void injectPlugins() {
    Core.log("능력자 플러그인 변환을 시작합니다.");

    ClassPool classPool = ClassPool.getDefault();

    classPool.appendClassPath(Bukkit.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
    classPool.appendClassPath(AbilityPlugin.getInstance().getFile().getAbsolutePath());

    for (String fileName : injectPluginNames) {
      File file = new File(pluginFolder, fileName);
      if (!file.exists()) {
        Core.wlog(fileName + " 파일을 찾을 수 없습니다.");
        continue;
      }

      Core.log(fileName + " 플러그인 변환을 시작합니다.");

      classPool.appendClassPath(file.getAbsolutePath());

      File folder = new File(pluginFolder, "inject/" + fileName.substring(0, fileName.length() - 4));

      ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
      ZipEntry entry = null;
      while ((entry = zip.getNextEntry()) != null) {
        if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;

        String className = entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6);
        CtClass clazz = classPool.getCtClass(className);

        for(CtMethod method : clazz.getMethods()) {
          method.instrument(new ExprEditor() {
            public void edit(MethodCall m) throws CannotCompileException {
              if(!m.getMethodName().equals("getOnlinePlayers") || !m.getSignature().equals("()[Lorg/bungee/entity/Player;")) return;
              m.replace("{ $_ = su.plugin.ability.api.sql.AbilityPluginManager.getOnlinePlayers(); }");

              injectedClassNames.add(className);
            }
          });
        }

        if(!injectedClassNames.contains(className)) continue;

        clazz.writeFile(folder.getAbsolutePath());

        Core.log(className + "를 변환했습니다.");
      }

      Core.log(fileName + " 플러그인을 변환했습니다.");
    }

    Core.log("능력자 플러그인 변환이 완료되었습니다.");
  }

  @SneakyThrows(Exception.class)
  public void loadClasses(Plugin plugin, URLClassLoader loader, File folder) {
    for(String className : injectedClassNames) {
      File file = new File(folder, className.replace(".", "/") + ".class");
      Core.log("filePath: " + file.getAbsolutePath());

      Method addURL = KReflectionUtil.getMethod(URLClassLoader.class, "addURL");
      addURL.setAccessible(true);

      addURL.invoke(loader, file.toURI().toURL());

      Method setClass = KReflectionUtil.getMethod(JavaPluginLoader.class, "setClass");
      setClass.setAccessible(true);

      setClass.invoke(plugin.getPluginLoader(), className, loader.loadClass(className));
    }
  }

  @SneakyThrows(Exception.class)
  public void loadAbilities() {
    if(!pluginFolder.exists()) return;
    for(File pluginFile : pluginFolder.listFiles()) {
      if(!pluginFile.getName().endsWith(".jar")) continue;
      JavaPlugin plugin = (JavaPlugin) PluginUtil.loadPlugin(pluginFile);
      plugin.getConfig().set("설정 파일 버전", 1.8);
      if(useInject && injectPluginNames.contains(pluginFile.getName())) {
        File folder = new File(pluginFolder, "inject/" + pluginFile.getName().substring(0, pluginFile.getName().length() - 4));

        injectedClassNames.add("Xeon.VisualAbility.compat.v1_12_R1.NMSHandler");

        List<URL> newNames = new ArrayList<>();
        for(String str : injectedClassNames) {
          newNames.add(new URL("file:" + str + ".class"));
        }
        newNames.add(PluginUtil.getFile(plugin).toURI().toURL());

        URLClassLoader classLoader = new URLClassLoader((URL[]) newNames.toArray(new URL[newNames.size()]));
        // loadClasses(plugin, (URLClassLoader) plugin.getClass().getClassLoader(), folder);
        // loadClasses(plugin, classLoader, folder);

        for(URL url : classLoader.getURLs()) {
          Core.log("url: " + url.getPath());
        }
      }

      PluginUtil.enablePlugin(plugin);
      try {
        ZipInputStream zip = new ZipInputStream(new FileInputStream(pluginFile));
        ZipEntry entry = null;
        while((entry = zip.getNextEntry()) != null) {
          if(entry.isDirectory() || !entry.getName().endsWith(".class") || entry.getName().startsWith("Xeon/VisualAbility/compat")) continue;
          try {
            String name = entry.getName().replaceAll("/", ".").substring(0, entry.getName().length() - 6);
            Class<?> ac = Class.forName(name);
            if(plugin.getName().equals("PhysicalFighters")) {
              AbilityBase ab = (AbilityBase) ac.newInstance();
              if(ab.GetAbilityName() == null || api.getAbilityManager().isBlackListed(ab.GetAbilityName(), "PhysicalFighters")) continue;
              api.getAbilityManager().registerAbility(new PAbility(ab));
            } else if(plugin.getName().equals("BitAbility")) {
              Xeon.VisualAbility.MainModule.AbilityBase ab = (Xeon.VisualAbility.MainModule.AbilityBase) ac.newInstance();
              if(ab.GetAbilityName() == null || api.getAbilityManager().isBlackListed(ab.GetAbilityName(), "BitAbility")) continue;
              api.getAbilityManager().registerAbility(new BAbility(ab));
            }
          } catch (Exception ex) { }
        }
        zip.close();
        Bukkit.getScheduler().cancelTasks(plugin);
        PluginUtil.unRegisterListeners(plugin);
        PluginUtil.unRegisterCommands(plugin);
        Core.log(plugin.getName() + " 플러그인을 불러왔습니다.");
      } catch (Exception e) {
        e.printStackTrace();
        Core.wlog(plugin.getName() + "을 불러오는데 실패했습니다.");
      }
    }
  }

}