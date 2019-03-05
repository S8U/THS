package su.plugin.core.common.api.event;

import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import su.plugin.core.common.api.plugin.UPlugin;

public class UEventManager {
	
	private HashMap<UPlugin, List<UEventListener>> registeredListeners = new HashMap<>();
	private HashMap<String, List<UEventMethod>> eventMethods = new HashMap<>();
	
	public boolean registerListener(UPlugin plugin, UEventListener listener) {
		List<UEventListener> listeners = registeredListeners.containsKey(plugin) ? registeredListeners.get(plugin) : new ArrayList<>();
		if(listeners.contains(listener)) return false;
		
		listeners.add(listener);
		
		List<UEventMethod> methods = new ArrayList<>();
		
		for(Method method : listener.getClass().getMethods()) {
			UEventHandler anno = method.getAnnotation(UEventHandler.class);
			if(anno == null) continue;
			
			UEventMethod emethod = new UEventMethod(plugin, listener, anno.priority(), method);
			
			methods.add(emethod);
		}
		
		eventMethods.put(listener.getClass().getCanonicalName(), methods);
		
		return true;
	}
	
	public int registerListeners(UPlugin plugin) {
		return registerListeners(plugin, null);
	}
	
	public int registerListeners(UPlugin plugin, String pack) {
		int i = 0;
		
		try {
			ZipInputStream jarStream = new ZipInputStream(new FileInputStream(plugin.getFile()));
			ZipEntry item = null;
			
			while((item = jarStream.getNextEntry()) != null) {
				if(item.isDirectory() || !item.getName().endsWith(".class")) continue;
				
				String className = item.getName().replaceAll("/", ".").substring(0, item.getName().length() - 6);
				if(pack != null && !className.substring(0, className.lastIndexOf(".")).equals(pack)) continue;
				
				try {
					Class<?> c = Class.forName(className);
					
					try {
						UEventListener listener = (UEventListener) c.newInstance();
						if(listener instanceof UnregisterableListener) continue;
						registerListener(plugin, listener);
						i++;
					} catch (Exception ex) { }
				} catch(ClassNotFoundException cnfe) { }
			}
			
			jarStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return i;
	}
	
	public void callEvent(UEvent event) {
		for(UEventMethod emethod : getEventMethods(event)) {
			emethod.invoke(event);
		}
	}
	
	private List<UEventMethod> getEventMethods(UEvent event) {
		List<UEventMethod> list = new ArrayList<>();
		
		for(List<UEventMethod> mlist : eventMethods.values()) {
			for(UEventMethod emethod : mlist) {
				if(!emethod.getPlugin().isEnabled()) break;
				else if(emethod.getMethod().getParameterCount() != 1 || !event.getClass().isAssignableFrom(emethod.getMethod().getParameterTypes()[0])) continue;
				
				list.add(emethod);
			}
		}
		
		Collections.sort(list);
		
		return list;
	}
	
}