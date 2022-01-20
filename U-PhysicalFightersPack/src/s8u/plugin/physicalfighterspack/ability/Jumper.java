package s8u.plugin.physicalfighterspack.ability;

public class Jumper {
  // public class Jumper extends PFPAbility implements Listener {

  /*public Jumper() {
    super();

    initAbility("점퍼",
        AbilityType.ACTIVE,
        AbilityRank.B,
        "철괴 클릭 시 능력을 사용합니다.",
        "능력 사용 시 보고 있는 방향으로 40칸 순간이동합니다.",
        "단, 블럭은 통과할 수 없습니다.");
    setCoolTime(30);

    registerLeftClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
    registerRightClick(new ItemStack[] { new ItemStack(Material.IRON_INGOT) });
  }

  @Override
  public void onUseCastingItem(PlayerInteractEvent e, ItemStack castingItem, ClickType clickType) {
    Block targetBlock = e.getPlayer().getTargetBlock((Set<Material>) null, 40);
    Vector target = targetBlock == null ?
        e.getPlayer().getVelocity().normalize().multiply(40) :
        e.getPlayer().getVelocity().normalize().multiply(e.getPlayer().getLocation().distance(targetBlock.getLocation()) - 1);

    KCore.teleport(e.getPlayer(), new Location(e.getPlayer().getLocation().getWorld(), target.getX(), target.getY(), target.getZ()));
  }*/

}