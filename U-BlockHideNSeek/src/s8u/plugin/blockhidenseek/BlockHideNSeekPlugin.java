package s8u.plugin.blockhidenseek;

import lombok.Getter;
import s8u.plugin.blockhidenseek.api.BlockHideNSeekAPI;
import su.plugin.core.bukkit.api.plugin.UKPlugin;
import su.plugin.core.common.api.command.UCommandSender;

public class BlockHideNSeekPlugin extends UKPlugin {

  @Getter
  private static BlockHideNSeekPlugin instance;

  @Getter
  private static BlockHideNSeekAPI api = new BlockHideNSeekAPI();

  @Override
  public void onUEnable() {
    instance = this;

  }

  @Override
  public void onUDisable() {

  }

  @Override
  public void onConfigLoad(UCommandSender sender) {

  }

}