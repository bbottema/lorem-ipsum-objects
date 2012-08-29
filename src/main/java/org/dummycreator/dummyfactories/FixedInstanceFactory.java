package org.dummycreator.dummyfactories;

import java.util.List;

import org.dummycreator.ClassBindings;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class FixedInstanceFactory<T> extends DummyFactory<T> {

	private final T instance;

	public FixedInstanceFactory(T instance) {
		this.instance = instance;
	}

	@Override
	public boolean isValidForType(Class<? super T> clazz) {
		if (clazz.isAssignableFrom(instance.getClass())) {
			return true;
		} else {
			throw new IllegalArgumentException("The object has to have a subclass of clazz");
		}
	}

	@Override
	public T createDummy(List<Exception> exceptions, ClassBindings classBindings) {
		return instance;
	}
}