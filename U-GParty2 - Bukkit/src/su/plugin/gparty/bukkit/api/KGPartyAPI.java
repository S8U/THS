package su.plugin.gparty.bukkit.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import su.plugin.core.bukkit.api.util.BungeeUtil;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.bukkit.KGPartyPlugin;
import su.plugin.gparty.bukkit.task.PartyParticleTask;
import su.plugin.gparty.common.api.manager.PlayerManager;

public class KGPartyAPI {

  @Setter
  @Getter
  private static String particleName;

  @Setter
  @Getter
  private static boolean useParticle, allowPartyPVP;

  @Setter
  @Getter
  private static int particleTaskId = -1;

  @Getter
  private static PlayerManager playerManager = new PlayerManager();

  public static void sendPartyDataRequest(PlayerKey playerKey) {
    BungeeUtil.sendMessageToBungeeCord(KGPartyPlugin.getInstance(), "ugparty:main",
        "RequestParty",
        playerKey.getId()
        );
  }

  public static void runPartyParticleTask() {
    if (particleTaskId != -1) return;

    particleTaskId = Bukkit.getScheduler().scheduleAsyncRepeatingTask(KGPartyPlugin.getInstance(), new PartyParticleTask(), 0, 20);
  }

  public static void stopPartyParticleTask() {
    if (getParticleTaskId() == -1) return;

    Bukkit.getScheduler().cancelTask(getParticleTaskId());

    particleTaskId = 0;
  }

}