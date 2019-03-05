package su.plugin.core.common.api.util;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import su.plugin.core.common.api.ChatColor;

@UtilityClass
public class StringUtil {
	
	public static String connectString(List<String> args, String connectChar) {
		return connectString(args.toArray(new String[args.size()]), connectChar);
	}
	
	public static String connectString(String[] args, String connectChar) {
		return connectString(args, 0, connectChar);
	}
	
	public static String connectString(String[] args, int start, String connectChar) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = start; i < args.length; i++) {
			if(args[i] == null) continue;
			sb.append((sb.length() < 1 ? "" : connectChar) + args[i]);
		}
		
		return sb.toString();
	}
	
	public static String connectString(Object[] args, String connectChar) {
		return connectString(args, 0, connectChar);
	}
	
	public static String connectString(Object[] args, int start, String connectChar) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = start; i < args.length; i++) {
			if(args[i] == null) continue;
			sb.append((sb.length() < 1 ? "" : connectChar) + args[i]);
		}
		
		return sb.toString();
	}
	
	public static String connectString(Object[] args, String connectChar, int end) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < end; i++) {
			if(args[i] == null) continue;
			sb.append((sb.length() < 1 ? "" : connectChar) + args[i]);
		}
		
		return sb.toString();
	}
	
	public static String repeatString(String repeatChar, int number) {
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < number; i++) {
			sb.append(repeatChar);
		}
		
		return sb.toString();
	}
	
	public static String buildDateString(long time, String pattern) {
		Date date = new Date(time);
		return new SimpleDateFormat(pattern).format(date);
	}
	
	public static String buildTimeString(long time) {
		String[] times = new String[4];
		times[0] = (int) (time / 86400000) < 1 ? null : (int) (time / 86400000) + "일";
		times[1] = (int) (time / 3600000 % 24) < 1 ? null : (int) (time / 3600000 % 24) + "시";
		times[2] = (int) (time / 60000 % 60) < 1 ? null : (int) (time / 60000 % 60) + "분";
		times[3] = (int) (time / 1000 % 60) < 1 ? null : (int) (time / 1000 % 60) + "초";
		
		return connectString(times, " ");
	}

	public static List<String> getValue(String key, String text) {
		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("<" + key + ":.*>");
		while(pattern.matcher(text).find()) {
			int startIndex = text.indexOf("<" + key + ":");
			int endIndex = text.indexOf(">", startIndex);

			String value = text.substring(startIndex + (2 + key.length()), endIndex);
			text = text.replace(text.subSequence(startIndex, endIndex), "");

			values.add(value);
		}

		return values;
	}

	// Method: String methodName(String value)
	@SneakyThrows(Exception.class)
	public static String replaceValue(String key, String text, Method method) {
		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("<" + key + ":.*>");
		for(int i = 0; pattern.matcher(text).find(); i++) {
			int startIndex = text.indexOf("<" + key + ":");
			int endIndex = text.indexOf(">", startIndex);

			String value = text.substring(startIndex + (2 + key.length()), endIndex);
			text = text.replace(text.subSequence(startIndex, endIndex + 1), method.invoke(method, value).toString());
		}

		return text;
	}

	public static String replaceValue(String key, String text, Object replace) {
		List<String> values = new ArrayList<>();

		Pattern pattern = Pattern.compile("<" + key + ":.*>");

		while(pattern.matcher(text).find()) {
			int startIndex = text.indexOf("<" + key + ":");
			int endIndex = text.indexOf(">", startIndex);

			String value = text.substring(startIndex + (2 + key.length()), endIndex);
			text = text.replace(text.subSequence(startIndex, endIndex + 1), replace.toString().replace("$$value", value));
		}

		return text;
	}
	
	public static boolean hasValue(String key, String text) {
		Pattern pattern = Pattern.compile("<" + key + ":.*>");
		
		return pattern.matcher(text).find();
	}
	
	public static List<String> translateAlternateColorCodes(List<String> texts) {
		List<String> list = new ArrayList<>();
		
		texts.forEach(text -> list.add(ChatColor.translateAlternateColorCodes('&', text)));
		
		return list;
	}
	
	public static int countMatches(String text, String str) {
		int count = 0;
		int index = -1;
		
		while((index = text.indexOf(str, index + 1)) >= 0) {
			count++;
		}
		
		return count;
	}

	public String getDecimalFormat(double d, String pattern) {
		return new DecimalFormat(pattern).format(d);
	}
	
}