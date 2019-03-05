package su.plugin.core.common.api.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;

@ToString
@Getter
public class JsonConfig {
	
	protected File file;
	
	protected HashMap<String, Object> defaults = new LinkedHashMap<>();
	protected List<String> hiddenDefaults = new ArrayList<>();
	
	protected HashMap<String, Object> values = new LinkedHashMap<>(); // Key, Value
	
	public JsonConfig(File file) {
		if(file == null) {
			throw new NullPointerException("File cannot be null");
		}
		
		this.file = file;
	}
	
	@SneakyThrows(IOException.class)
	public void createFile() {
		if(file.exists()) return;
		
		new File(file.getPath().substring(0, file.getPath().lastIndexOf("\\"))).mkdirs();
		
		file.createNewFile();
	}

	public void clearDefaults() {
		defaults.clear();
		hiddenDefaults.clear();
	}

	public void clearValues() {
		values.clear();
	}
	
	protected void valuesToDot(String parentPath, Map<String, Object> map, Map<String, Object> originalValues) {
		originalValues.forEach((name, value) -> {
			String pname = parentPath + "." + name;
			
			if(value instanceof Map<?, ?>) {
				valuesToDot(pname, map, (Map<String, Object>) value);
			} else {
				map.put(pname, value);
			}
		});
	}
	
	@SneakyThrows(IOException.class)
	public JsonConfig load() {
		if(!file.exists()) return this;
		
		defaults.clear();
		hiddenDefaults.clear();
		
		values.clear();
		
		HashMap<String, Object> tempValues = new Gson().fromJson(new FileReader(file), LinkedHashMap.class);
		
		tempValues.forEach((path, value) -> {
			if(value instanceof Map<?, ?>) {
				valuesToDot(path, values, (Map<String, Object>) value);
			} else {
				values.put(path, value);
			}
		});
		
		return this;
	}
	
	private HashMap<String, Object> createValueHashMap() {
		HashMap<String, Object> newValues = new LinkedHashMap<>();
		
		defaults.forEach((path, value) -> {
			if(!isHiddenDefault(path)) {
				newValues.put(path,  value);
			}
		});
		values.forEach((path, value) -> newValues.put(path, value));
		
		return newValues;
	}
	
	private HashMap<String, Object> createOrganizedHashMap(HashMap<String, Object> originalValues) {
		HashMap<String, Object> organizedValues = new LinkedHashMap<>();
		
		originalValues.forEach((path, value) -> {
			if(path.contains(".")) {
				String[] pathSplit = path.split("\\.");
				
				String firstName = pathSplit[0];
				Object firstChildObject = organizedValues.get(firstName);
				if(firstChildObject instanceof HashMap<?, ?> || firstChildObject == null) {
					HashMap<String, Object> parent = firstChildObject == null ? new LinkedHashMap<>() : (HashMap<String, Object>) firstChildObject;
					HashMap<String, Object> child = parent;
					
					for (int i = 1; i < pathSplit.length; i++) {
						String childName = pathSplit[i];
						Object childObject = child.get(childName);
						
						if(i == pathSplit.length - 1) {
							child.put(childName, value);
							organizedValues.put(firstName, parent);
						} else {
							HashMap<String, Object> tempChild = childObject == null ? new LinkedHashMap<>() : (LinkedHashMap<String, Object>) childObject;
							if(tempChild instanceof HashMap<?, ?>) {
								child.put(childName, tempChild);
								child = tempChild;
							}
						}
					}
				}
			} else {
				organizedValues.put(path,  value);
			}
		});
		
		return organizedValues;
	}
	
	@SneakyThrows(IOException.class)
	public void save() {
		String json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(createOrganizedHashMap(createValueHashMap()));
		
		createFile();
		
		FileWriter writer = new FileWriter(file);
		
		writer.write(json);
		writer.flush();
		writer.close();
	}
	
	public void saveDefaults() {
		if(!values.isEmpty()) {
			boolean needSave = false;

			for(String defaultKey : defaults.keySet()) {
				if(!isHiddenDefault(defaultKey) && !values.containsKey(defaultKey)) {
					needSave = true;
					break;
				}
			}

			if(!needSave) return;
		}

		save();
	}
	
	public String saveToString() {
		return new Gson().toJson(createOrganizedHashMap(createValueHashMap()));
	}
	
	public void addDefault(String path, Object val) {
		addDefault(path, val, false);
	}
	
	public void addDefault(String path, Object val, boolean hidden) {
		defaults.put(path, val);
		
		if(!hidden || isHiddenDefault(path)) return;
		hiddenDefaults.add(path);
	}
	
	public Object getDefault(String path) {
		return defaults.get(path);
	}
	
	public boolean isHiddenDefault(String path) {
		return hiddenDefaults.contains(path);
	}
	
	public void set(String path, Object value) {
		if(path == null) return;
		else if(value == null) {
			String dpath = path + ".";

			Iterator<String> it = values.keySet().iterator();
			while(it.hasNext()) {
				String k = it.next();

				if(k.length() > 1 && k.startsWith(dpath)) {
					it.remove();
				}
			}
			
			values.remove(path);
			
			return;
		}
		
		values.put(path, value);
	}
	
	public Object get(String path) {
		if(path == null || path.length() < 1) {
			throw new NullPointerException("Path cannot be null");
		}
		
		return values.get(path);
	}
	
	/**
	 * path의 한 단계 아래 키를 반환
	 * 
	 * path.first = O
	 * path.first.second = X
	 * 
	 * @return path의 한 단계 아래 키
	 */
	public List<String> getKeys(String path) {
		List<String> list = new ArrayList<>();
		
		values.keySet().forEach(key -> {
			if(key.startsWith(path + ".")) {
				String t = key.substring(path.length() + 1);
				t = t.contains(".") ? t.substring(0, t.indexOf(".")) : t;
				
				if(!list.contains(t)) {
					list.add(t);
				}
			}
		});
		
		return list;
	}
	
	public boolean isSet(String path) {
		return values.containsKey(path);
	}
	
	public boolean isString(String path) {
		Object val = get(path);
		return val != null && val instanceof String;
	}
	
	public String getString(String path) {
		Object def = getDefault(path);
		return getString(path, def == null ? null : def.toString());
	}
	
	public String getString(String path, String def) {
		Object val = get(path);
		return val == null ? def : val.toString();
	}
	
	public boolean isBoolean(String path) {
		Object val = get(path);
		return val != null && val instanceof Boolean;
	}
	
	public boolean getBoolean(String path) {
		Object def = getDefault(path);
		return getBoolean(path, def instanceof Boolean ? (Boolean) def : false);
	}
	
	public boolean getBoolean(String path, boolean def) {
		Object val = get(path);
		return val instanceof Boolean ? (Boolean) val : def;
	}
	
	public boolean isInt(String path) {
		Object val = get(path);
		return val != null && val instanceof Integer;
	}
	
	public int getInt(String path) {
		Object def = getDefault(path);
		return getInt(path, def instanceof Integer ? Integer.valueOf(def.toString()) : 0);
	}
	
	public int getInt(String path, int def) {
		return (int) getDouble(path, def);
	}
	
	public boolean isLong(String path) {
		Object val = get(path);
		return val != null && val instanceof Long;
	}
	
	public long getLong(String path) {
		Object def = getDefault(path);
		return getLong(path, def instanceof Long ? Long.valueOf(def.toString()) : 0);
	}
	
	public long getLong(String path, long def) {
		Object val = get(path);
		return val instanceof Long ? Long.valueOf(val.toString()) : def;
	}
	
	public boolean isDouble(String path) {
		Object val = get(path);
		return val != null && val instanceof Double;
	}
	
	public double getDouble(String path) {
		Object def = getDefault(path);
		return getDouble(path, def instanceof Double ? Double.valueOf(def.toString()) : 0);
	}
	
	public double getDouble(String path, double def) {
		Object val = get(path);
		return val instanceof Double ? Double.valueOf(val.toString()) : def;
	}
	
	//
	
	public boolean isList(String path) {
		Object val = get(path);
		return val != null && val instanceof List;
	}
	
	public List<?> getList(String path) {
		Object def = getDefault(path);
		return getList(path, def instanceof List ? (List<?>) def : null);
	}
	
	public List<?> getList(String path, List<?> def) {
		Object val = get(path);
		return val instanceof List ? (List<?>) val : def;
	}
	
	public List<String> getStringList(String path) {
		List<?> list = getList(path);
		
		List<String> result = new ArrayList<>();
		if(list != null) {
			for(Object obj : list) {
				if(!(obj instanceof String)) continue;
				
				result.add(obj.toString());
			}
		}
		
		return result;
	}
	
}