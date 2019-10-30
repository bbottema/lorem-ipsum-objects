package org.bbottema.loremipsumobjects.typefactories.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains previously found class methods and constructors and contains references to preferred constructors (constructors called
 * successfully previously).
 */
public class ReflectionCache {

	private final Map<Class<?>, List<Method>> methodCache = new HashMap<>();
	private final Map<Class<?>, List<Constructor<?>>> constructorCache = new HashMap<>();
	private final Map<Class<?>, Constructor<?>> preferedConstructors = new HashMap<>();

	@Nullable
	public List<Constructor<?>> getConstructorCache(final Class<?> clazz) {
		return constructorCache.containsKey(clazz) ? constructorCache.get(clazz) : null;
	}

	@Nullable
	public List<Method> getMethodCache(final Class<?> clazz) {
		return methodCache.containsKey(clazz) ? methodCache.get(clazz) : null;
	}

	public void add(final Class<?> clazz, final Method... setter) {
		List<Method> setters = methodCache.get(clazz);
		if (setters == null) {
			setters = new ArrayList<>();
		}
		methodCache.put(clazz, setters);
		setters.addAll(Arrays.asList(setter));
	}

	public void add(final Class<?> clazz, final Constructor<?>... cons) {
		List<Constructor<?>> cs = constructorCache.get(clazz);
		if (cs == null) {
			cs = new ArrayList<>();
		}
		constructorCache.put(clazz, cs);
		cs.addAll(Arrays.asList(cons));
	}

	@Nullable
	public Constructor<?> getPreferedConstructor(final Class<?> clazz) {
		return preferedConstructors.get(clazz);
	}

	public void setPreferedConstructor(final Class<?> clazz, final Constructor<?> cons) {
		preferedConstructors.put(clazz, cons);
	}
}