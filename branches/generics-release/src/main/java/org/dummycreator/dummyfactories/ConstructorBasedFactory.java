package org.dummycreator.dummyfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class ConstructorBasedFactory<T> extends DummyFactory<T> {

	private final Constructor<T> constructor;

	public ConstructorBasedFactory(Constructor<T> constructor) {
		this.constructor = constructor;
	}

	/**
	 * @return The result of a successful invocation of the given constructor or <code>null</code> in case of an error.
	 * @param knownInstances Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)} when constructing
	 *            the parameters for the <code>Constructor</code>.
	 * @param classBindings Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)} when constructing the
	 *            parameters for the <code>Constructor</code>.
	 * @param exceptions Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)} when constructing the
	 *            parameters for the <code>Constructor</code>.
	 */
	@Override
	public T createDummy(Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classbindings, List<Exception> exceptions) {
		@SuppressWarnings("unchecked")
		Class<T>[] parameters = (Class<T>[]) constructor.getParameterTypes();
		try {
			if (parameters.length > 0) {
				final Object[] params = new Object[parameters.length];
				for (int i = 0; i < params.length; i++) {
					params[i] = new ClassBasedFactory<T>(parameters[i]).createDummy(genericMetaData, knownInstances, classbindings, exceptions);
				}
				return constructor.newInstance(params);
			} else {
				return constructor.newInstance();
			}
		} catch (InvocationTargetException e) {
			exceptions.add(e);
		} catch (InstantiationException e) {
			exceptions.add(e);
		} catch (IllegalAccessException e) {
			exceptions.add(e);
		}
		return null;
	}
}