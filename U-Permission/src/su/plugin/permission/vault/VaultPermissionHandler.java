package su.plugin.permission.vault;

import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import su.plugin.core.bukkit.api.KCore;
import su.plugin.core.common.api.player.PlayerKey;
import su.plugin.permission.PermissionPlugin;
import su.plugin.permission.api.PermissionAPI;
import su.plugin.permission.api.object.PermissionGroup;
import su.plugin.permission.api.object.PermissionPlayer;

public class VaultPermissionHandler extends Permission {

  private PermissionAPI api = PermissionPlugin.getApi();

  public static void register() {
    Bukkit.getServicesManager().register(Permission.class, new VaultPermissionHandler(), PermissionPlugin.getInstance(), ServicePriority.High);
  }

  @Override
  public String getName() {
    return "U-Permission";
  }

  @Override
  public boolean isEnabled() {
    return PermissionPlugin.getInstance().isEnabled();
  }

  @Override
  public boolean hasSuperPermsCompat() {
    return true;
  }

  //

  @Override
  public boolean playerHas(String world, String player, String permission) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null || !api.getSQLManager().loadPermissionPlayer(pk)) return false;

    PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);

    if (api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
      api.getSQLManager().loadGroup(pp.getGroupName());
    }

    return pp.hasNodeIncludeGroup(permission);
  }

  @Override
  public boolean playerAdd(String world, String player, String permission) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return false;
    else if ((Bukkit.getPlayer(player) != null && Bukkit.getPlayer(player).hasPermission(permission)) || api.getSQLManager().hasPlayerNode(pk, permission)) return false;

    if (api.getPlayerManager().existsPermissionPlayer(pk)) {
      PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);

      pp.addNode(permission);
      pp.addPermission(permission);
    }

    api.getSQLManager().addPlayerNode(pk, permission);

    api.getPlayerManager().sendPlayerChange(pk);

    return true;
  }

  @Override
  public boolean playerRemove(String world, String player, String permission) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return false;
    else if ((Bukkit.getPlayer(player) != null && !Bukkit.getPlayer(player).hasPermission(permission)) || !api.getSQLManager().hasPlayerNode(pk, permission)) return false;

    if (api.getPlayerManager().existsPermissionPlayer(pk)) {
      PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);

      pp.removeNode(permission);
      pp.removePermission(permission);
    }

    api.getSQLManager().removePlayerNode(pk, permission);

    api.getPlayerManager().sendPlayerChange(pk);

    return true;
  }

  @Override
  public boolean playerInGroup(String world, String player, String group) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return false;

    if (api.getPlayerManager().existsPermissionPlayer(pk) || api.getSQLManager().loadPermissionPlayer(pk)) {
      PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);

      if (pp.getGroup() == null) return false;
      else if (pp.getGroup().getName().equals(group)) return true;

      for (PermissionGroup pg : pp.getGroup().getParentGroups()) {
        if (pg.getName().equals(group)) return true;
      }
    }

    return false;
  }

  @Override
  public boolean playerAddGroup(String world, String player, String groupName) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return false;

    else if (api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
      api.getSQLManager().loadGroup(groupName);
    }

    PermissionGroup group = api.getGroupManager().getGroup(groupName);
    if (group == null) return false;

    if (api.getPlayerManager().existsPermissionPlayer(pk)) {
      PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);

      pp.setGroupName(group.getName());
      pp.updatePermissionAttachment();
    }

    api.getSQLManager().setPlayerGroup(pk, group.getName());

    api.getPlayerManager().sendPlayerChange(pk);

    return true;
  }

  @Override
  public boolean playerRemoveGroup(String world, String player, String group) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return false;

    if (api.getPlayerManager().existsPermissionPlayer(pk)) {
      PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);

      pp.setGroupName(null);
      pp.updatePermissionAttachment();
    }

    api.getSQLManager().setPlayerGroup(pk, null);

    api.getPlayerManager().sendPlayerChange(pk);

    return true;
  }

  @Override
  public String[] getPlayerGroups(String world, String player) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return null;

    List<String> gl = new ArrayList<>();
    if (api.getPlayerManager().existsPermissionPlayer(pk) || api.getSQLManager().loadPermissionPlayer(pk)) {
      PermissionPlayer pp = api.getPlayerManager().getPermissionPlayer(pk);
      if (pp.getGroup() == null) return null;

      gl.add(pp.getGroup().getName());

      for (PermissionGroup pg : pp.getGroup().getParentGroups()) {
        gl.add(pg.getName());
      }
    }

    return gl.toArray(new String[gl.size()]);
  }

  @Override
  public String getPrimaryGroup(String world, String player) {
    PlayerKey pk = PlayerKey.getPlayerKey(player);
    if (pk == null) return null;

    return api.getPlayerManager().existsPermissionPlayer(pk) ? api.getPlayerManager().getPermissionPlayer(pk).getGroupName() : api.getSQLManager().getPlayerGroup(pk);
  }

  //

  @Override
  public boolean hasGroupSupport() {
    return true;
  }

  @Override
  public String[] getGroups() {
    return api.getGroupManager().getPermissionGroupList().toArray(new String[api.getGroupManager().getPermissionGroups().size()]);
  }

  @Override
  public boolean groupHas(String world, String groupName, String permission) {
    if (api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
      api.getSQLManager().loadGroup(groupName);
    }

    PermissionGroup group = api.getGroupManager().getGroup(groupName);
    if (group == null) return false;

    return group.hasNodeIncludeParent(permission);
  }

  @Override
  public boolean groupAdd(String world, String groupName, String permission) {
    if (api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
      api.getSQLManager().loadGroup(groupName);
    }

    PermissionGroup group = api.getGroupManager().getGroup(groupName);
    if (group == null) return false;

    group.addNode(permission);

    api.getSQLManager().addGroupNode(group.getName(), permission);

    api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());

    group.updatePlayerPermissionAttachments();

    return true;
  }

  @Override
  public boolean groupRemove(String world, String groupName, String permission) {
    if (api.isUseBungeecord() && KCore.getOnlinePlayers().size() < 1) {
      api.getSQLManager().loadGroup(groupName);
    }

    PermissionGroup group = api.getGroupManager().getGroup(groupName);
    if (group == null) return false;

    group.removeNode(groupName);

    api.getSQLManager().removeGroupNode(group.getName(), groupName);

    api.getGroupManager().sendGroupUpdateToAllChannel(group.getName());

    group.updatePlayerPermissionAttachments();

    return true;
  }
}
