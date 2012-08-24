package org.dummycreator.dummyfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.dummycreator.ClassBindings;
import org.dummycreator.DummyFactory;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class ConstructorBasedFactory<T> extends DummyFactory<T> {

	private final Constructor<T> constructor;

	public ConstructorBasedFactory(Constructor<T> constructor) {
		this.constructor = constructor;
	}

	@Override
	public T createDummy(List<Exception> constructorExceptions, ClassBindings classbindings) {
		@SuppressWarnings("unchecked")
		Class<T>[] parameters = (Class<T>[]) constructor.getParameterTypes();
		try {
			if (parameters.length > 0) {
				final Object[] params = new Object[parameters.length];
				for (int i = 0; i < params.length; i++) {
					params[i] = new ClassBasedFactory<T>(parameters[i]).createDummy(constructorExceptions, classbindings);
				}
				return constructor.newInstance(params);
			} else {
				return constructor.newInstance();
			}
		} catch (InvocationTargetException e) {
			constructorExceptions.add(e);
		} catch (InstantiationException e) {
			constructorExceptions.add(e);
		} catch (IllegalAccessException e) {
			constructorExceptions.add(e);
		}
		return null;
	}

}