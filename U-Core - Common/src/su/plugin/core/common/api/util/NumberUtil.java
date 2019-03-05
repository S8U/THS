package su.plugin.core.common.api.util;

import java.util.ArrayList;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NumberUtil {

	public long getDayTick() {
		return 86400000;
	}

	public long getHourTick() {
		return 3600000;
	}

	public long getMinuteTick() {
		return 60000;
	}

	//

	public boolean isInteger(Object o) {
		try {
			Integer.parseInt(o.toString());
			return true;
		} catch(NumberFormatException e) {
			return false;
		}
	}

	public boolean isLong(Object o) {
		try {
			Long.parseLong(o.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean isFloat(Object o) {
		try {
			Float.parseFloat(o.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean isDouble(Object o) {
		try {
			Double.parseDouble(o.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public boolean isShort(Object o) {
		try {
			Short.parseShort(o.toString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	//

	public Integer getInteger(Object o) {
		return isInteger(o) ? Integer.parseInt(o.toString()) : null;
	}

	public Long getLong(Object o) {
		return isLong(o) ? Long.parseLong(o.toString()) : null;
	}

	public Float getFloat(Object o) {
		return isFloat(o) ? Float.parseFloat(o.toString()) : null;
	}

	public Double getDouble(Object o) {
		return isDouble(o) ? Double.parseDouble(o.toString()) : null;
	}
	
	public Short getShort(Object o) {
		return isShort(o) ? Short.parseShort(o.toString()) : null;
	}
	
	/*
	 * Form: number1~number2
	 */
	public List<Integer> getIntegers(String str) {
		List<Integer> list = new ArrayList<>();
		
		if(str.contains("~")) {
			String[] s = str.split("~");
			
			int min = Integer.parseInt(s[0]);
			int max = Integer.parseInt(s[1]);
			
			for(; min <= max; min++) {
				list.add(min);
			}
		} else {
			list.add(Integer.parseInt(str));
		}
		
		return list;
	}

	public List<Float> getFloats(String str) {
		List<Float> list = new ArrayList<>();

		if (str.contains("~")) {
			String[] l = str.split("~");

			if (isFloat(l[0]) && isFloat(l[1])) {
				float min = getFloat(l[0]);
				float max = getFloat(l[1]);

				for (; min <= max; min++) {
					list.add(min);
				}
			}
		} else if (isFloat(str)) {
			list.add(getFloat(str));
		}

		return list;
	}

	public List<Double> getDoubles(String str) {
		List<Double> list = new ArrayList<>();
		if (str.contains("~")) {
			String[] l = str.split("~");

			if (isDouble(l[0]) && isDouble(l[1])) {
				double min = getDouble(l[0]);
				double max = getDouble(l[1]);

				for (; min <= max; min++) {
					list.add(min);
				}
			}
		} else if (isDouble(str)) {
			list.add(getDouble(str));
		}

		return list;
	}

	public List<Long> getLongs(String str) {
		List<Long> list = new ArrayList<>();
		if (str.contains("~")) {
			String[] l = str.split("~");

			if (isLong(l[0]) && isLong(l[1])) {
				long min = getLong(l[0]);
				long max = getLong(l[1]);
				for (; min <= max; min++) {
					list.add(min);
				}
			}
		} else if (isLong(str)) {
			list.add(getLong(str));
		}

		return list;
	}

	//

	public double random() {
		return Math.random();
	}

	public int random(int i) {
		return (int) (random() * i);
	}

	public int random(int small, int big) {
		return (int) (Math.random() * (big - small + 1)) + small;
	}
	
}