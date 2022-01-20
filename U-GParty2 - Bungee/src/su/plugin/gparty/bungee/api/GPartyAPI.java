package su.plugin.gparty.bungee.api;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import su.plugin.core.common.api.Core;
import su.plugin.gparty.bungee.api.manager.GPlayerManager;
import su.plugin.gparty.bungee.api.object.GParty;
import su.plugin.gparty.bungee.api.object.GPartyPlayer;
import su.plugin.gparty.common.api.object.Party;

public class GPartyAPI {

  @Setter
  @Getter
  private static int maxPartyMember;

  @Setter
  @Getter
  private static boolean useGLogin, useGEssentials, useGFriend, useChannel;

  @Getter
  private static GPlayerManager playerManager = new GPlayerManager();

  public boolean createParty(GPartyPlayer pp) {
    if (pp.hasParty()) return false;

    GParty party = new GParty();
    party.getPlayers().add(pp);

    party.setLeader(pp.getPlayerKey());

    party.sendCreate();

    pp.setParty(party);

    return true;
  }

  public boolean inviteParty(GPartyPlayer pp, GPartyPlayer target) {
    if (pp.equals(target)) {
      pp.getPlayerKey().getUPlayer().wmsg("자신에게는 초대를 보낼 수 없습니다.");

      return false;
    } else if (useGLogin && (!su.plugin.glogin.bungee.api.GGLoginAPI.getAccountManager().hasAccount(target.getPlayerKey()) || !su.plugin.glogin.bungee.api.GGLoginAPI
        .getAccountManager().getAccount(target.getPlayerKey()).isLogin())) {
      pp.getPlayerKey().getUPlayer().wmsg("아직 로그인하지 않은 플레이어입니다.");

      return false;
    }

    Object option = Core.getOptionManager().getPlayerOption(target.getPlayerKey(), "gparty_allow_invite");
    if(option != null && (option.equals("block") || (option.equals("friend") && useGFriend && !su.plugin.gfriend.api.GFriendAPI.getSQLManager().isFriend(pp.getPlayerKey(), target.getPlayerKey())))) {
      pp.getPlayerKey().getUPlayer().wmsg("상대가 파티 초대를 허용하지 않았습니다.");

      return false;
    }

    if (target.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("이미 파티에 가입된 플레이어입니다.");

      return false;
    }

    if (!pp.hasParty()) {
      createParty(pp);
    }

    Party party = pp.getParty();

    if (party.getPlayers().size() >= maxPartyMember) {
      pp.getPlayerKey().getUPlayer().wmsg("파티가 가득 찼습니다.");

      return false;
    }

    target.setInvitedParty(party);

    target.getPlayerKey().getUPlayer().msg(pp.getPlayerKey().getDisplayName() + " §a님께서 파티에 초대했습니다.");

    party.bc(pp.getPlayerKey().getDisplayName() + " §a님께서 §f" + target.getPlayerKey().getDisplayName() + " §a님을 파티에 초대했습니다.");
    target.getPlayerKey().getUPlayer().msg(
        new ComponentBuilder("§a파티 수락")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 수락"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 파티를 수락합니다.").create()))
            .create(),
        " §f/ ",
        new ComponentBuilder("§c파티 거절")
            .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/파티 거절"))
            .event(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("클릭 시 파티를 거절합니다.").create()))
            .create());

    return true;
  }

  public boolean acceptParty(GPartyPlayer pp) {
    if (pp.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("이미 파티에 가입되어 있습니다.");

      return false;
    } else if (!pp.hasInvitedParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("아직 파티에 초대 받지 못했습니다.");

      return false;
    }

    GParty party = (GParty) pp.getInvitedParty();
    if (party.getPlayers().size() >= maxPartyMember) {
      pp.getPlayerKey().getUPlayer().wmsg("파티가 가득 찼습니다.");

      return false;
    }

    pp.setParty(party);
    party.getPlayers().add(pp);

    party.sendInfo();

    party.bc(pp.getPlayerKey().getDisplayName() + " §a님께서 파티에 가입했습니다.");

    ProxiedPlayer p = (ProxiedPlayer) pp.getPlayerKey().getPlatformPlayer();
    ProxiedPlayer lp = (ProxiedPlayer) party.getLeader().getPlatformPlayer();
    if (!p.getServer().getInfo().equals(lp.getServer().getInfo())) {
      pp.setMoving(true);
      p.connect(lp.getServer().getInfo());

      String targetChannelName = lp.getServer().getInfo().getName();
      if(useChannel) {
        targetChannelName = su.plugin.channel.common.api.ChannelAPI.getChannelManager().getChannel(targetChannelName).getDisplayName();
      }

      Core.msg(p, "§a파티장을 따라 §f" + targetChannelName + "§a(으)로 이동했습니다.");
    }

    return true;
  }

  public boolean denyParty(GPartyPlayer pp) {
    if (pp.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("이미 파티에 가입되어있습니다.");

      return false;
    } else if (!pp.hasInvitedParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("아직 파티에 초대받지 못했습니다.");

      return false;
    }

    pp.getInvitedParty().bc(pp.getPlayerKey().getDisplayName() + " §c님께서 파티 초대를 거절했습니다.");

    pp.setInvitedParty(null);

    pp.getPlayerKey().getUPlayer().msg("§c파티 초대를 거절했습니다.");

    return true;
  }

  public boolean leaveParty(GPartyPlayer pp) {
    if (!pp.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("파티에 가입되어있지 않습니다.");

      return false;
    }

    GParty party = (GParty) pp.getParty();

    party.getPlayers().remove(pp);
    pp.setParty(null);

    if (party.getPlayers().size() > 0) {
      party.bc(pp.getPlayerKey().getDisplayName() + " §c님께서 파티를 탈퇴했습니다.");

      party.setLeader(party.getPlayers().get(0).getPlayerKey());

      if (party.getPlayers().size() > 1) {
        party.bc(party.getLeader().getDisplayName() + " §a님께서 파티장으로 임명되었습니다.");

        party.sendInfo();
      } else {
        party.bc("§c파티가 해체되었습니다.");

        party.getPlayers().forEach(ptp -> ptp.setParty(null));

        party.sendDelete();
      }
    } else {
      party.sendDelete();
    }

    pp.setParty(null);
    pp.setPartyChat(false);

    pp.getPlayerKey().getUPlayer().msg("§c파티를 탈퇴했습니다.");

    return true;
  }

  public boolean kickParty(GPartyPlayer pp, GPartyPlayer target) {
    if (!pp.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("참여 중인 파티가 없습니다.");

      return false;
    } else if (!target.hasParty() || !pp.getParty().equals(target.getParty())) {
      pp.getPlayerKey().getUPlayer().wmsg("파티에 소속되어 있지 않은 플레이어입니다.");

      return false;
    } else if (!pp.getParty().getLeader().equals(pp.getPlayerKey())) {
      pp.getPlayerKey().getUPlayer().wmsg("파티장만 사용 가능합니다.");

      return false;
    } else if (pp.equals(target)) {
      pp.getPlayerKey().getUPlayer().wmsg("자신은 추방할 수 없습니다.");

      return false;
    }

    GParty party = (GParty) pp.getParty();

    party.getPlayers().remove(target);
    target.setParty(null);

    party.bc(pp.getPlayerKey().getDisplayName() + " §c님께서 §f" + target.getPlayerKey().getDisplayName() + " §c님을 파티에서 추방했습니다.");

    if (party.getPlayers().size() > 1) {
      party.sendInfo();
    } else {
      party.bc("§c파티가 해체되었습니다.");

      party.getPlayers().forEach(ptp -> ptp.setParty(null));

      party.sendDelete();
    }

    target.getPlayerKey().getUPlayer().msg("§c파티에서 추방당했습니다.");

    return true;
  }

  public boolean giveLeader(GPartyPlayer pp, GPartyPlayer target) {
    if (!pp.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("참여 중인 파티가 없습니다.");

      return false;
    } else if (!target.hasParty() || !pp.getParty().equals(target.getParty())) {
      pp.getPlayerKey().getUPlayer().wmsg("파티에 소속되어 있지 않은 플레이어입니다.");

      return false;
    } else if (!pp.getParty().getLeader().equals(pp.getPlayerKey())) {
      pp.getPlayerKey().getUPlayer().wmsg("파티장만 파티원을 추방할 수 있습니다.");

      return false;
    } else if (pp.equals(target)) {
      pp.getPlayerKey().getUPlayer().wmsg("자신에겐 파티장을 위임할 수 없습니다.");

      return false;
    }

    GParty party = (GParty) pp.getParty();

    party.setLeader(target.getPlayerKey());
    party.sendInfo();

    party.bc(pp.getPlayerKey().getDisplayName() + " §a님께서 §f" + target.getPlayerKey().getDisplayName() + " §a님께 파티장을 위임했습니다.");

    return true;
  }

  public boolean togglePartyChat(GPartyPlayer pp) {
    if (!pp.hasParty()) {
      pp.getPlayerKey().getUPlayer().wmsg("파티가 없습니다.");

      return false;
    }

    pp.setPartyChat(!pp.isPartyChat());

    pp.getPlayerKey().getUPlayer().msg((pp.isPartyChat() ? "§a" : "§c") + "파티 채팅 모드" + (pp.isPartyChat() ? "로 전환" : "를 해제") + "했습니다.");

    return true;
  }

}