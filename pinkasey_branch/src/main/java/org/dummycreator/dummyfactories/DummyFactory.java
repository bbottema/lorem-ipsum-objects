package org.dummycreator.dummyfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;

/**
 * Defines the interface for creating a dummy instance. A default implementation is provided for validation purposes (always returns true by
 * default).
 * 
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public abstract class DummyFactory<T> {

	/**
	 * Default implementation returns true. Should be used when a factory knows beforehand when it will be unable to produce an instance of
	 * the requested type.
	 * 
	 * @throws IllegalArgumentException
	 */
	public boolean isValidForType(final Class<? super T> clazz) {
		return true;
	}

	/**
	 * Starts a new chain for the creation of a dummy with an empty list of known class instances and empty list of exceptions.
	 * 
	 * @param classBindings A list of bindings to which a factory may defer dummy creation to.
	 * @return See {@link #createDummy(Map, ClassBindings, List)}.
	 */
	public final T createDummy(final ClassBindings classBindings) {
		return createDummy(null, new HashMap<String, ClassUsageInfo<?>>(), classBindings, new ArrayList<Exception>());
	}

	/**
	 * Creates a new instance of one of the following: the requested type, a sub type of the requested type or, if the requested type is an
	 * interface definition, a suitable implementation. Should return <code>null</code> if nothing can be found.
	 * <p>
	 * If there are known exceptions (reflection exceptions when invoking a {@link Constructor} for example), don't throw them, but record
	 * them in the given <code>exceptions</code> list. Not all failures are counted towards complete failures.
	 * 
	 * @param Can be <code>null</code>. Should be non-null when requested type is a {@link List} or {@link Map}.
	 * @param knownInstances A list of previously created and populated objects for a specific type.
	 * @param classBindings A list of bindings to which a factory may defer dummy creation to.
	 * @param exceptions A list in which to store exceptions so they can be logged at some later point. This is done so, because not all
	 *            exceptions are bad (in case a factory is trying to find a useful constructor, invocations are allowed to fail if a
	 *            suitable constructor can be found otherwise).
	 * @return A new instance of the given type.
	 */
	public abstract T createDummy(Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings,
			List<Exception> exceptions);
}