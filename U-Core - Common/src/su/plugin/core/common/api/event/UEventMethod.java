package su.plugin.core.common.api.event;

import java.lang.reflect.Method;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import su.plugin.core.common.api.plugin.UPlugin;

@Getter
@RequiredArgsConstructor
public class UEventMethod implements Comparable<UEventMethod>{
	
	private final UPlugin plugin;
	
	private final UEventListener listener;
	
	private final byte priority;
	
	private final Method method;
	
	@SneakyThrows(Exception.class)
	public void invoke(UEvent event) {
		method.invoke(listener, event);
	}

	@Override
	public int compareTo(UEventMethod o) {
		return priority == o.getPriority() ? 0 : (priority > o.getPriority() ? 1 : -1);
	}
	
}