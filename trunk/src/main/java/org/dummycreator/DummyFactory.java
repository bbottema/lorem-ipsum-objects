package org.dummycreator;

import java.util.List;

/**
 * Defines the interface for creating a dummy instance. A default implementation is provided for validation purposed (always returns true by
 * default).
 * 
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public abstract class DummyFactory<T> {
	/**
	 * Default implementation returns true.
	 */
	public boolean isValidForType(Class<? super T> clazz) {
		return true;
	}

	/**
	 * @param constructorExceptions A list in which to store constructor exceptions so they can be logged at some later point. This is done
	 *            so, because not all constructor exceptions are bad (in case a factory is trying to find a useful constructor, other are
	 *            allowed to fail). In case all constructors fail, only then the exceptions are logged.
	 * @param classBindings A list of bindings to which a factory may defer dummy creation to.
	 * @return A new instance of a given type.
	 */
	public abstract T createDummy(List<Exception> constructorExceptions, ClassBindings classBindings);
}