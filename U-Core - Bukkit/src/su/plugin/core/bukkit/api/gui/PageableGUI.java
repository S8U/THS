package su.plugin.core.bukkit.api.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import su.plugin.core.bukkit.KCorePlugin;


public abstract class PageableGUI {

  @Getter
  private final String title, key;

  @Getter
  private final int row;

  @Getter
  private int maxPage;

  @Getter
  private boolean dynamic, canPickUp;
  private boolean iconChanged;

  @Setter
  @Getter
  private int iconRangeLeft, iconRangeRight, iconRangeTop, iconRangeBottom;

  @Getter
  private List<Icon> icons = new ArrayList<>();
  private HashMap<Integer, Icon> commonIcons = new HashMap<>();

  private List<GUI> GUIs = new ArrayList<>();

  @Getter
  private HashMap<String, Object> objects = new HashMap<>();

  // Constructor
  public PageableGUI(String key, String title, int row, int maxPage) {
    this.key = key;
    this.title = title;
    this.row = row;
    this.maxPage = maxPage;

    iconRangeLeft = 1;
    iconRangeRight = 9;
    iconRangeTop = 1;
    iconRangeBottom = row;
  }

  public PageableGUI(String key, String title, int maxPage, int row, int iconRangeLeft, int iconRangeRight, int iconRangeTop, int iconRangeBottom) {
    this.key = key;
    this.title = title;
    this.row = row;
    this.maxPage = maxPage;

    this.iconRangeLeft = iconRangeLeft;
    this.iconRangeRight = iconRangeRight;
    this.iconRangeTop = iconRangeTop;
    this.iconRangeBottom = iconRangeBottom;
  }

  public PageableGUI(String key, String title, int row) { // Dynamic
    this.key = key;
    this.title = title;
    this.row = row;
    dynamic = true;

    iconRangeLeft = 1;
    iconRangeRight = 9;
    iconRangeTop = 1;
    iconRangeBottom = row;
  }

  public PageableGUI(String key, String title, int row, int iconRangeLeft, int iconRangeRight, int iconRangeTop, int iconRangeBottom) { // Dynamic
    this.key = key;
    this.title = title;
    this.row = row;
    dynamic = true;

    this.iconRangeLeft = iconRangeLeft;
    this.iconRangeRight = iconRangeRight;
    this.iconRangeTop = iconRangeTop;
    this.iconRangeBottom = iconRangeBottom;
  }

  // 페이지 당 아이콘 개수
  public int getPageIconNumber() {
    return (iconRangeRight - iconRangeLeft + 1) * (iconRangeBottom - iconRangeTop + 1);
  }

  public void setCanPickUp(boolean can) {
    canPickUp = can;

    GUIs.forEach(g -> g.setCanPickUp(can));
  }

  // Icon
  public void setIcon(int index, Icon icon) {
    icons.set(index, icon);

    iconChanged = true;
  }

  public void addIcon(Icon icon) {
    icons.add(icon);

    iconChanged = true;
  }

  public void removeIcon(int index) {
    icons.remove(index);

    iconChanged = true;
  }

  public void removeIcon(Icon icon) {
    icons.remove(icon);

    iconChanged = true;
  }

  public Icon getIcon(int index) {
    return icons.get(index);
  }

  public void clearIcons() {
    icons.clear();

    iconChanged = true;
  }

  // Common Icon
  public void setCommonIcon(int x, int y, Icon icon) {
    setCommonIcon(x - 1 + (y - 1) * 9, icon);
  }

  public void setCommonIcon(int pos, Icon icon) {
    commonIcons.put(pos, icon);
  }

  public Icon getCommonIcon(int x, int y) {
    return getCommonIcon(x - 1 + (y - 1) * 9);
  }

  public Icon getCommonIcon(int pos) {
    return commonIcons.get(pos);
  }

  public void setPreviousIcon(int x, int y) {
    setPreviousIcon(x - 1 + (y - 1) * 9);
  }

  public void setPreviousIcon(int pos) {
    Icon icon = getCommonIcon(pos);
    if (icon == null) return;

    icon.setObject("pageableEvent", "previous");
  }

  public void setNextIcon(int x, int y) {
    setNextIcon(x - 1 + (y - 1) * 9);
  }

  public void setNextIcon(int pos) {
    Icon icon = getCommonIcon(pos);
    if (icon == null) return;

    icon.setObject("pageableEvent", "next");
  }

  // GUI
  public final GUI getPageGUI(int index) {
    return GUIs.get(index);
  }

  // Update
  protected void onUpdate() { }

  public void update() {
    onUpdate();

    if (iconChanged) {
      if (dynamic) {
        maxPage = (int) Math.ceil((float) icons.size() / getPageIconNumber());

        for (int i = maxPage; i < GUIs.size(); i++) {
          GUIs.remove(i);
        }
      }

      for (int i = 0; i < maxPage; i++) {
        updateGUI(i);
      }
    } else {
      GUIs.forEach(g -> g.update());
    }
  }

  public void updateGUI(int index) {
    int pageIconNumber = getPageIconNumber(); // 페이지 당 아이콘 개수

    GUI gui = GUIs.get(index);
    if (gui == null) {
      gui = new GUI(key + "/" + index, title + " (" + (index + 1) + " / " + maxPage + ")", row);

      for (int pos : commonIcons.keySet()) {
        Icon icon = getCommonIcon(pos);
        if (icon.existsObject("pageableEvent")
            && (index == 0 && icon.getObject("pageableEvent").equals("left")
            || index == maxPage - 1 && icon.getObject("pageableEvent").equals("right"))) continue;

        gui.setIcon(pos, icon);
      }

      gui.setCanPickUp(canPickUp);
      gui.setObject("pageableGUIParent", this);
      gui.setObject("pageableGUIIndex", index);

      GUIs.set(index, gui);
    } else {
      gui.getInventory().clear();
      gui.getIcons().clear();
    }

    for (int j = 0; j < pageIconNumber; j++) {
      gui.setIcon(j, icons.get(index * pageIconNumber + j));
    }

    gui.update();
  }

  public void updateAsynchronously() {
    Bukkit.getScheduler().runTaskAsynchronously(KCorePlugin.getInstance(), () -> update());
  }

  //

  public void open(Player player, int page) {
    GUI gui = getPageGUI(page - 1);
    if (gui == null) return;

    gui.open(player);
  }

}