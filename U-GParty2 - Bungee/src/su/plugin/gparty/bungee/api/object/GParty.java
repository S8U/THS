package su.plugin.gparty.bungee.api.object;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.bungee.api.task.PluginMessageTask;
import su.plugin.gparty.bungee.GPartyPlugin;
import su.plugin.gparty.bungee.api.GPartyAPI;
import su.plugin.gparty.common.api.object.Party;
import su.plugin.gparty.common.api.object.PartyPlayer;

public class GParty extends Party {

  public void sendCreate() {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF("PartyCreate");
    out.writeInt(getLeader().getId()); // Leader PlayerId

    new PluginMessageTask(GPartyPlugin.getInstance(), ((ProxiedPlayer) getLeader().getPlatformPlayer()).getServer().getInfo(), "ugparty:main", out.toByteArray()).runAsync();
  }

  public void sendDelete() {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF("PartyDelete");
    out.writeInt(getLeader().getId()); // Leader PlayerId

    new PluginMessageTask(GPartyPlugin.getInstance(), ((ProxiedPlayer) getLeader().getPlatformPlayer()).getServer().getInfo(), "ugparty:main", out.toByteArray()).runAsync();
  }

  public void sendInfo() {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    out.writeUTF("PartyInfo");
    out.writeInt(getLeader().getId()); // Leader PlayerId
    out.writeInt(getPlayers().size()); // Player Count
    getPlayers().forEach(pp -> out.writeInt(pp.getPlayerKey().getId())); // PlayerId

    new PluginMessageTask(GPartyPlugin.getInstance(), ((ProxiedPlayer) getLeader().getPlatformPlayer()).getServer().getInfo(), "ugparty:main", out.toByteArray()).runAsync();
  }

  @Override
  public void msg(PartyPlayer partyPlayer, String msg) {
    ProxiedPlayer p = ((ProxiedPlayer) partyPlayer.getPlayerKey().getUPlayer().getPlatformSender());

    String pf = "";
    String pm = "";
    String channel = p.getServer().getInfo().getName();

    if(GPartyAPI.isUseGEssentials()) {
      su.plugin.gessentials.bungee.api.object.EPlayer ep = su.plugin.gessentials.bungee.api.GGEssentialsAPI.getPlayerManager().getEPlayer(p);

      pf = ep.hasPrefixerPrefix() ? ep.getPrefixerPrefix() : "";
      pm = ep.hasPermissionPrefix() ? ep.getPermissionPrefix() : "";
    }

    if(GPartyAPI.isUseChannel()) {
      channel = su.plugin.channel.common.api.ChannelAPI.getChannelManager().getChannel(p.getServer().getInfo().getName()).getDisplayName();
    }

    for (PartyPlayer ptp : getPlayers()) {
      ptp.getPlayerKey().getUPlayer().nmsg("§a[파티 채팅] " + pf + pm + partyPlayer.getPlayerKey().getDisplayName() + "§a: " + msg);
    }

    for (PartyPlayer cp : GPartyAPI.getPlayerManager().getChatSpys()) {
      cp.getPlayerKey().getUPlayer().nmsg("§a<PChatSpy> [" + channel + "] " + pf + pm + partyPlayer.getPlayerKey().getDisplayName() + "§a: " + msg);
    }
  }

}