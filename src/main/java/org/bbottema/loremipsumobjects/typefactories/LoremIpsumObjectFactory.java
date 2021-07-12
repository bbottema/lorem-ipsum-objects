package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Defines the interface for creating a dummy instance. A default implementation is provided for validation purposes (always returns true by
 * default).
 */
public abstract class LoremIpsumObjectFactory<T> {

	/**
	 * Default implementation returns true. Should be used when a factory knows beforehand when it will be unable to produce an instance of
	 * the requested type.
	 */
	public boolean isValidForType(final Class<? super T> clazz) {
		return true;
	}

	/**
	 * Delegates to {@link #createLoremIpsumObject(LoremIpsumConfig)} with the default bindings.
	 */
	@Nullable
	public final T createLoremIpsumObject() {
		return createLoremIpsumObject(LoremIpsumConfig.builder().build());
	}

	/**
	 * Starts a new chain for the creation of a dummy with an empty list of known class instances and empty list of exceptions.
	 *
	 * @param loremIpsumConfig A list of bindings to which a factory may defer dummy creation to.
	 * @return See {@link #createLoremIpsumObject(Type[], Map, LoremIpsumConfig, List)}.
	 */
	@Nullable
	public final T createLoremIpsumObject(@Nullable final LoremIpsumConfig loremIpsumConfig) {
		return createLoremIpsumObject(null, new HashMap<String, ClassUsageInfo<?>>(), loremIpsumConfig, new ArrayList<Exception>());
	}

	/**
	 * Creates a new instance of one of the following: the requested type, a sub type of the requested type or, if the requested type is an
	 * interface definition, a suitable implementation. Should return <code>null</code> if nothing can be found. In case of method/constructor
	 * errors due to generated test data falling out of a range or causing a timeout, the process is retried a few times for each step (for example
	 * when a method really wants a small numbers, but a large number is generated the first time).
	 * <p>
	 * If there are known exceptions (reflection exceptions when invoking a {@link Constructor} for example), don't throw them, but record
	 * them in the given <code>exceptions</code> list. Not all failures are counted towards complete failures.
	 *
	 * @param genericMetaData Can be <code>null</code>. Should be non-null when requested type is a {@link List} or {@link Map}.
	 * @param knownInstances  A list of previously created and populated objects for a specific type.
	 * @param loremIpsumConfig   A list of bindings to which a factory may defer dummy creation to.
	 * @param exceptions      A list in which to store exceptions so they can be logged at some later point. This is done so, because not all
	 *                        exceptions are bad (in case a factory is trying to find a useful constructor, invocations are allowed to fail if a
	 *                        suitable constructor can be found otherwise).
	 * @return A new instance of the given type.
	 */
	@Nullable
	public T createLoremIpsumObject(
			@Nullable Type[] genericMetaData,
			@Nullable Map<String, ClassUsageInfo<?>> knownInstances,
			LoremIpsumConfig loremIpsumConfig,
			@Nullable List<Exception> exceptions) {
		T result;
		int tries = loremIpsumConfig.getRetries();
		do {
			result = _createLoremIpsumObject(genericMetaData, knownInstances, loremIpsumConfig, exceptions);
		} while(result == null && --tries > 0);
		return result;
	}

	@Nullable
	public abstract T _createLoremIpsumObject(
			@Nullable Type[] genericMetaData,
			@Nullable Map<String, ClassUsageInfo<?>> knownInstances,
			LoremIpsumConfig loremIpsumConfig,
			@Nullable List<Exception> exceptions);
}