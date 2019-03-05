package su.plugin.core.bukkit.api.enumeration;

import lombok.Getter;

public enum NMSVersion {
	
	UNKNOWN(-1, "UNKNOWN"), 
	v1_5_R3(10503, "v1_5_R3"),
	v1_6_R1(10601, "v1_6_R1"),
	v1_6_R2(10602, "v1_6_R2"),
	v_1_6_R3(10603, "v_1_6_R3"),
	v1_7_R1(10701, "v1_7_R1"),
	v1_7_R2(10702, "v1_7_R2"),
	v1_7_R3(10703, "v1_7_R3"),
	v1_7_R4(10704, "v1_7_R4"),
	v1_8_R1(10801, "v1_8_R1"),
	v1_8_R2(10802, "v1_8_R2"),
	v1_8_R3(10803, "v1_8_R3"),
	v1_8_R4(10804, "v1_8_R4"),
	v1_9_R1(10901, "v1_9_R1"),
	v1_9_R2(10902, "v1_9_R2"),
	v1_10_R1(11001, "v1_10_R1"),
	v1_11_R1(11101, "v1_11_R1"),
	v1_12_R1(11101, "v1_12_R1");
	
	@Getter
	private int number;
	@Getter
	private String string;
	
	NMSVersion(int number, String NMSVersion) {
		this.number = number;
		this.string = NMSVersion;
	}
	
	public boolean isBefore(NMSVersion target) {
		return number < target.getNumber();
	}
	
	public boolean isAfter(NMSVersion target) {
		return number > target.getNumber();
	}
	
	public boolean isInRange(NMSVersion oldVersion, NMSVersion newVersion) {
		return oldVersion.getNumber() <= number && number <= newVersion.getNumber();
	}
	
	public static NMSVersion getByName(String NMSVersion) {
		for(NMSVersion ver : values()) {
			if(ver.getString().equals(NMSVersion)) return ver;
		}
		return null;
	}
	
}