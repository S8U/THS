package su.plugin.ability.command;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import lombok.SneakyThrows;
import su.plugin.ability.AbilityPlugin;
import su.plugin.ability.PermissionList;
import su.plugin.ability.api.AbilityAPI;
import su.plugin.ability.api.category.GameState;
import su.plugin.ability.api.object.Ability;
import su.plugin.ability.api.object.GamePlayer;
import su.plugin.core.common.api.ChatColor;
import su.plugin.core.common.api.Core;
import su.plugin.core.common.api.command.CommandHandler;
import su.plugin.core.common.api.command.SubCommandHandler;
import su.plugin.core.common.api.command.UCommandListener;
import su.plugin.core.common.api.command.UCommandSender;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.core.common.api.util.NumberUtil;

public class AdminCommand implements UCommandListener {

  private AbilityAPI api = AbilityPlugin.getApi();

  @CommandHandler(
      name = "시작",
      aliases = {"start"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "게임을 시작합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "시작",
      aliases = {"start"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "게임을 시작합니다."
  )
  public void ability_start(UCommandSender sender, String[] args) {
    if(!api.getGameManager().startGame(false)) {
      sender.wmsg("이미 게임이 시작되었습니다.");
      return;
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 게임을 시작시켰습니다.");
  }

  @CommandHandler(
      name = "모드시작",
      aliases = {"autoModeStart"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "게임을 자동 모드로 시작합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "자동모드시작",
      aliases = {"autoModeStart"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "게임을 자동 모드로 시작합니다."
  )
  public void ability_autoModeStart(UCommandSender sender, String[] args) {
    if(!api.getGameManager().startGame(true)) {
      sender.wmsg("이미 게임이 시작되었습니다.");
      return;
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 게임을 시작시켰습니다.");
  }

  @SubCommandHandler(
      parent = "능력자",
      name = "중단",
      aliases = {"강제중단", "stop"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "게임을 중단시킵니다."
  )
  public void ability_stop(UCommandSender sender, String[] args) {
			if(!api.getGameManager().stopGame()) {
      sender.wmsg("아직 게임 중이 아닙니다.");
      return;
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " 님께서 게임을 중단시켰습니다.");
  }

  @CommandHandler(
      name = "무적해제",
      aliases = {"go"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "무적을 해제시킵니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "무적해제",
      aliases = {"go"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "무적을 해제시킵니다."
  )
  public void ability_go(UCommandSender sender, String[] args) {
    if(!api.getTaskManager().stopInvincbilityTask()) {
    sender.wmsg("무적 시간이 아닙니다.");
      return;
    } else if(api.getGameManager().isAutoMode() && api.isUseAutoTeleport()) {
      api.getTaskManager().runTeleportAllTask(20 * 3, api.getAutoTeleportCount());
    }

    api.setInvincibilityTime(false);

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 무적을 해제했습니다.");
  }

  @CommandHandler(
      name = "강제확정",
      aliases = {"skip"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "능력을 강제로 확정시킵니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "강제확정",
      aliases = {"skip"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "능력을 강제로 확정시킵니다."
  )
  public void ability_skip(UCommandSender sender, String[] args) {
    if(!api.getGameManager().getGameState().equals(GameState.DRAWING)) {
      sender.wmsg("능력 추첨 중이 아닙니다.");
      return;
    }

    for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
      gp.setRedrawCount(0);
    }

    api.getTaskManager().runGameStartCountTask(20);
    api.getTaskManager().stopDrawSkipTask();

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 능력을 강제로 확정시켰습니다.");
  }

  @CommandHandler(
      name = "할당",
      aliases = {"assign", "give"},
      additional = "<플레이어> <능력 번호>",
      minArgs = 2,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "능력을 할당합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "할당",
      aliases = {"assign", "give"},
      additional = "<플레이어> <능력 번호>",
      minArgs = 2,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "능력을 할당합니다."
  )
  public void ability_assgin(UCommandSender sender, String[] args) {
    GamePlayer tp = api.getPlayerManager().getGamePlayer(PlayerKey.getPlayerKeyByDisplayName(args[0]));
    if(tp == null) {
      sender.wmsg(sender, "존재하지 않거나 접속 중이 아닌 플레이어입니다.");
      return;
    }

    int abilityCount = api.getAbilityManager().getAbilities().size();

    if(!NumberUtil.isInteger(args[1])) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
    }

    int num = Integer.parseInt(args[1]);
    if(num > abilityCount) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
      return;
    }

    Ability ab = api.getAbilityManager().getAbilities().get(num);
    if(tp.hasAbility(ab)) {
      sender.wmsg("이미 해당 능력을 가지고있습니다.");
      return;
    }
    tp.addAbility(ab);
    tp.getAbility(ab.getAbilityId()).setPlayer(tp.getPlayer());

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 §f" + tp.getDisplayName() + " §b님께 능력을 할당했습니다.");
  }

  @CommandHandler(
      name = "삭제",
      aliases = {"resign", "remove"},
      additional = "<플레이어> <능력 번호>",
      minArgs = 2,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "플레이어의 능력을 삭제합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "삭제",
      aliases = {"resign", "remove"},
      additional = "<플레이어> <능력 번호>",
      minArgs = 2,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "플레이어의 능력을 삭제합니다."
  )
  public void ability_resign(UCommandSender sender, String[] args) {
    GamePlayer tp = api.getPlayerManager().getGamePlayer(PlayerKey.getPlayerKeyByDisplayName(args[0]));
    if(tp == null) {
      sender.wmsg(sender, "존재하지 않거나 접속 중이 아닌 플레이어입니다.");
      return;
    }

    int abilityCount = api.getAbilityManager().getAbilities().size();

    if(!NumberUtil.isInteger(args[1])) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
    }

    int num = Integer.parseInt(args[1]);
    if(num > abilityCount) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
      return;
    }

    Ability ab = api.getAbilityManager().getAbilities().get(num);
    if(!tp.hasAbility(ab)) {
      sender.wmsg("해당 능력을 가지고 있지 않습니다.");
      return;
    }
    tp.removeAbility(ab);

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 §f" + tp.getDisplayName() + " §b님의 능력을 삭제했습니다.");
  }

  @CommandHandler(
      name = "초기화",
      aliases = {"clear"},
      additional = "<플레이어>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "플레이어의 능력을 초기화합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "초기화",
      aliases = {"clear"},
      additional = "<플레이어>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "플레이어의 능력을 초기화합니다."
  )
  public void ability_clear(UCommandSender sender, String[] args) {
    GamePlayer tp = api.getPlayerManager().getGamePlayer(PlayerKey.getPlayerKeyByDisplayName(args[0]));
    if(tp == null) {
      sender.wmsg(sender, "존재하지 않거나 접속 중이 아닌 플레이어입니다.");
      return;
    }

    tp.clearAbility();
    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " 님께서 " + tp.getDisplayName() + " 님의 능력을 초기화시켰습니다.");
  }

  @CommandHandler(
      name = "모두할당",
      aliases = {"assignAll", "giveAll"},
      additional = "<능력 번호>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어에게 능력을 할당합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "모두할당",
      aliases = {"assignAll", "giveAll"},
      additional = "<능력 번호>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어에게 능력을 할당합니다."
  )
  public void ability_assignAll(UCommandSender sender, String[] args) {
    int abilityCount = api.getAbilityManager().getAbilities().size();

    if(!NumberUtil.isInteger(args[0])) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
    }

    int num = Integer.parseInt(args[0]);
    if(num > abilityCount) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
      return;
    }

    Ability ab = api.getAbilityManager().getAbilities().get(num);
    for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
      if(gp.hasAbility(ab)) continue;
      gp.addAbility(ab);
      gp.getAbility(ab.getAbilityId()).setPlayer(gp.getPlayer());
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 모든 플레이어에게 §f" + ab.getName() + " §b능력을 할당했습니다.");
  }

  @CommandHandler(
      name = "모두삭제",
      aliases = {"resignAll", "removeAll"},
      additional = "<능력 번호>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어의 능력을 삭제합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "모두삭제",
      aliases = {"resignAll", "removeAll"},
      additional = "<능력 번호>",
      minArgs = 1,
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어의 능력을 삭제합니다."
  )
  public void ability_resignAll(UCommandSender sender, String[] args) {
    int abilityCount = api.getAbilityManager().getAbilities().size();

    if(!NumberUtil.isInteger(args[0])) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
    }

    int num = Integer.parseInt(args[0]);
    if(num > abilityCount) {
      sender.wmsg("능력 번호는 1~" + abilityCount + "의 숫자만 입력할 수 있습니다.");
      return;
    }

    Ability ab = api.getAbilityManager().getAbilities().get(num);
    for(GamePlayer gp : api.getPlayerManager().getOnlineJoinedPlayers()) {
      if(gp.hasAbility(ab)) continue;
      gp.removeAbility(ab);
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 §f" + ab.getName() + " §b능력을 삭제했습니다.");
  }

  @CommandHandler(
      name = "모두초기화",
      aliases = {"clearAll"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어의 능력을 초기화합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "모두초기화",
      aliases = {"clearAll"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어의 능력을 초기화합니다."
  )
  public void ability_clearAll(UCommandSender sender, String[] args) {
    for(GamePlayer gp : api.getPlayerManager().getJoinedPlayers()) {
      gp.clearAbility();
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 모든 플레이어의 능력을 초기화시켰습니다.");
  }

  @CommandHandler(
      name = "능력목록확인",
      aliases = {"showAbilityList"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어의 능력을 확인합니다."
  )
  @SubCommandHandler(
      parent = "능력자",
      name = "능력목록확인",
      aliases = {"showAbilityList"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "모든 플레이어의 능력을 확인합니다."
  )
  public void ability_showAbilityList(UCommandSender sender, String[] args) {
    for(GamePlayer gp : api.getPlayerManager().getJoinedPlayers()) {
      StringBuilder sb = new StringBuilder();
      for(Ability ab : gp.getAbilities()) {
        sb.append(sb.length() < 1 ? ab.getName() : ", " + ab.getName());
      }

      sender.cmsg(ChatColor.BLUE, gp.getDisplayName() + " §b님의 능력: §f" + sb.toString());
    }

    Core.cbc(ChatColor.BLUE, sender.getDisplayName() + " §b님께서 모든 플레이어의 능력을 확인했습니다.");
  }

  @SubCommandHandler(
      parent = "능력자",
      name = "능력목록파일생성",
      aliases = {"makeAbilityListFile"},
      permission = PermissionList.ABILITY_ADMIN,
      usage = "능력 목록 파일을 생성합니다."
  )
  @SneakyThrows(IOException.class)
  public void ability_makeAbilityListFile(UCommandSender sender, String[] args) {
    File file = new File(AbilityPlugin.getInstance().getDataFolder(), "ability_list.txt");
    file.createNewFile();

    FileWriter fw = new FileWriter(file, false);

    for(Ability ab : AbilityAPI.getAbilityManager().getAbilities().values()) {
      fw.write("이름: " + ab.getName() + "\n");
      fw.write("플러그인: " + ChatColor.stripColor(ab.getPluginName()) + "\n");
      fw.write("타입: " + ab.getType().getText() + "\n");
      fw.write("랭크: " + ChatColor.stripColor(ab.getRank().getText()) + "\n");
      if(ab.getCoolTime() > 0) {
        fw.write("쿨타임: " + ab.getCoolTime() + "\n");
      }
      if(ab.getDurationTime() > 0) {
        fw.write("지속시간: " + ab.getDurationTime() + "\n");
      }
      fw.write("설명: " + "\n");
      for(String str : ab.getManual()) {
        fw.write(str + "\n");
      }

      fw.write( "\n");
    }

    fw.close();

    sender.cmsg(ChatColor.BLUE, "능력 목록 파일이 생성되었습니다.");
  }

}