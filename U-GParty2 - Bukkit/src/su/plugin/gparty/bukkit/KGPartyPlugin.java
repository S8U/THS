package su.plugin.gparty.bukkit;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.bukkit.api.KGPartyAPI;
import su.plugin.gparty.bukkit.listener.ControlListener;
import su.plugin.gparty.bukkit.listener.PluginListener;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class KGPartyPlugin extends UKPlugin {

  @Getter
  private static KGPartyPlugin instance;
  @Getter
  private static KGPartyAPI api = new KGPartyAPI();

  @Override
  public void onUEnable() {
    instance = this;
    setPrefix("§a[ U-Party ]");
    setPluginPackage(getClass().getPackage().getName().substring(0,getClass().getPackage().getName().lastIndexOf(".")));
    setColor(ChatColor.GREEN);

    loadConfig();

    registerListeners(ControlListener.class.getPackage().getName());
    registerUEventListeners(ControlListener.class.getPackage().getName());

    Bukkit.getMessenger().registerIncomingPluginChannel(this, "ugparty:main", new PluginListener());
    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "ugparty:main");

    if(api.isUseParticle()) {
      api.runPartyParticleTask();
    }

    initPlayers();
  }

  @Override
  public void onConfigLoad(UCommandSender sender) {
    getJsonConfig().addDefault("파티끼리 PVP 허용", false);
    getJsonConfig().addDefault("파티클.사용", true);
    getJsonConfig().addDefault("파티클.이름", "flame");

    getJsonConfig().saveDefaults();

    api.setAllowPartyPVP(getJsonConfig().getBoolean("파티끼리 PVP 허용"));
    api.setUseParticle(getJsonConfig().getBoolean("파티클.사용"));
    api.setParticleName(getJsonConfig().getString("파티클.이름"));
  }

  private void initPlayers() {
    for (Player ap : KCore.getOnlinePlayers()) {
      PartyPlayer pp = new PartyPlayer(PlayerKey.getPlayerKeyByPlatformPlayer(ap));
      api.getPlayerManager().getPartyPlayers().put(pp.getPlayerKey(), pp);

      api.sendPartyDataRequest(pp.getPlayerKey());
    }
  }

}