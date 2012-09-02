package org.dummycreator.dummyfactories;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class MethodBasedFactory<T> extends DummyFactory<T> {

	private static final Logger logger = Logger.getLogger(MethodBasedFactory.class);

	private final Method method;

	public MethodBasedFactory(Method method) {
		this.method = method;
	}

	@Override
	public boolean isValidForType(Class<? super T> clazz) {
		if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().equals(clazz)) {
			return true;
		} else {
			throw new IllegalArgumentException("The method has to be static and return an object of the given class!");
		}
	}

	/**
	 * @return The result of a successful invocation of the given method or <code>null</code> in case of an error.
	 * @param knownInstances Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)} when constructing
	 *            the parameters for the <code>Method</code>.
	 * @param classBindings Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)} when constructing the
	 *            parameters for the <code>Method</code>.
	 * @param exceptions Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)} when constructing the
	 *            parameters for the <code>Method</code>.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public T createDummy(Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
		Method m = method;
		Class<?>[] parameters = m.getParameterTypes();
		final Object[] params = new Object[parameters.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = new ClassBasedFactory<Object>((Class<Object>) parameters[i]).createDummy(genericMetaData, knownInstances, classBindings, exceptions);
		}
		try {
			return (T) m.invoke(null, params);
		} catch (InvocationTargetException e) {
			logger.debug(String.format("failed to invoke Method [%s] to product an object of type [%s]", m.getName(), method.getReturnType()), e);
		} catch (IllegalAccessException e) {
			logger.debug(String.format("failed to invoke Method [%s] to product an object of type [%s]", m.getName(), method.getReturnType()), e);
		}
		return null;
	}
}