package su.plugin.gparty.bungee.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.event.UEventHandler;
import su.plugin.core.common.api.event.UEventListener;
import su.plugin.core.common.api.event.c.player.UPlayerJoinEvent;
import su.plugin.core.common.api.event.c.player.UPlayerQuitEvent;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.gparty.bungee.GPartyPlugin;
import su.plugin.gparty.bungee.api.GPartyAPI;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class PlayerListener implements UEventListener, Listener {

  private GPartyAPI api = GPartyPlugin.getApi();

  @UEventHandler
  public void onJoin(UPlayerJoinEvent e) {
    api.getPlayerManager().getPartyPlayers().put(e.getPlayer().getPlayerKey(), new GPartyPlayer(e.getPlayer().getPlayerKey()));
  }

  @UEventHandler
  public void onQuit(UPlayerQuitEvent e) {
    PartyPlayer pp = api.getPlayerManager().getPartyPlayers().get(e.getPlayer().getPlayerKey());
    if (pp == null) return;

    api.leaveParty((GPartyPlayer) pp);

    api.getPlayerManager().getPartyPlayers().remove(e.getPlayer().getPlayerKey());
  }

  @EventHandler (priority = EventPriority.HIGH)
  public void onServerConnect(ServerConnectEvent e) {
    ProxiedPlayer p = e.getPlayer();
    if (p.getServer() == null) return;

    PlayerKey pk = PlayerKey.getPlayerKeyByPlatformPlayer(p);
    GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(pk);
    if (pp == null) return;

    GParty party = (GParty) pp.getParty();
    if (party == null || party.getPlayers().size() < 2) return;

    String targetName = e.getTarget().getName();
    if(api.isUseChannel()) {
      targetName = su.plugin.channel.common.api.ChannelAPI.getChannelManager().getChannel(e.getTarget().getName()).getDisplayName();
    }

    if (party.getLeader().equals(pk)) {
      for (PartyPlayer ptp : party.getPlayers()) {
        if (ptp.equals(pp)) continue;

        ProxiedPlayer prp = (ProxiedPlayer) ptp.getPlayerKey().getPlatformPlayer();
        if (prp.getServer().getInfo().equals(e.getTarget())) continue;

        ((GPartyPlayer) ptp).setMoving(true);
        prp.connect(e.getTarget());

        Core.msg(prp, "§a파티장을 따라 §f" + targetName + "§a(으)로 이동했습니다.");
      }

      Core.msg(p, "§a파티원들과 함께 §f" + targetName + "§a(으)로 이동했습니다.");
    } else if (pp.isMoving()) {
      pp.setMoving(false);

       return;
    } else {
      ProxiedPlayer lp = (ProxiedPlayer) party.getLeader().getPlatformPlayer();

      if (e.getTarget().equals(lp.getServer().getInfo())) return;

      e.setCancelled(true);

      Core.wmsg(p, "파티장만 채널을 이동할 수 있습니다.");
      Core.wmsg(p, "파티를 탈퇴하려면 '/파티 탈퇴' 명령어를 사용하세요.");
    }
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onChat(ChatEvent e) {
    if(e.isCommand()) return;

    ProxiedPlayer p = (ProxiedPlayer) e.getSender();
    PlayerKey playerKey = PlayerKey.getPlayerKeyByPlatformPlayer(p);

    if(api.isUseGLogin() && (!su.plugin.glogin.bungee.api.GGLoginAPI.getAccountManager().hasAccount(playerKey) || !su.plugin.glogin.bungee.api.GGLoginAPI.getAccountManager().getAccount(playerKey).isLogin())) return;

    GPartyPlayer pp = (GPartyPlayer) api.getPlayerManager().getPartyPlayers().get(playerKey);
    if(!pp.hasParty() || !pp.isPartyChat()) return;

    e.setCancelled(true);

    pp.getParty().msg(pp, e.getMessage());
  }

}
