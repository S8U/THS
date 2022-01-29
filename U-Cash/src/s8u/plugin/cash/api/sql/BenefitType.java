package s8u.plugin.cash.api.sql;

import lombok.RequiredArgsConstructor;
import su.plugin.core.common.api.ChatColor;

@RequiredArgsConstructor
public enum BenefitType {
  MONEY("Money"),
  MONEY_BOOST("MoneyBoost"),
  VIP_PREFIX("Vip+Prefix"),
  VIP_PLUS_PREFIX("VipPrefix"),
  VVIP_PREFIX("VVipPrefix"),
  VVIP_PLUS_PREFIX("VVip+Prefix"),
  LEAF_PREFIX("LeafPrefix"),
  COLOR_DISPLAY_NAME("ColorDisplayName"),
  DISPLAY_NAME("DisplayName"),
  DONATION("Donation");

  private final String text;
  private String displayName;
  private ChatColor color;

  public String getText() {
    if (color != null) {
      return text + "_" + color.getChar();
    } else if (displayName != null){
      return text + "_" + displayName;
    }

    return text;
  }

  public BenefitType displayName(String displayName) {
    this.displayName = displayName;

    return this;
  }

  public BenefitType color(ChatColor color) {
    this.color = color;

    return this;
  }
}