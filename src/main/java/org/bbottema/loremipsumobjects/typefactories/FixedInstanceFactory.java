package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class FixedInstanceFactory<T> extends LoremIpsumObjectFactory<T> {

	@Nullable
	private final T instance;

	public FixedInstanceFactory(@Nullable final T instance) {
		this.instance = instance;
	}

	@Override
	public boolean isValidForType(final Class<? super T> clazz) {
		if (instance == null || clazz.isAssignableFrom(instance.getClass())) {
			return true;
		} else {
			throw new IllegalArgumentException("The object has to have a subclass of class " + clazz);
		}
	}

	/**
	 * @param knownInstances Not used.
	 * @param classBindings  Not used.
	 * @param exceptions     Not used.
	 * @return The instance that was passed into {@link #FixedInstanceFactory(Object)}.
	 */
	@Override
	@Nullable
	public T _createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			@Nullable final ClassBindings classBindings,
			@Nullable final List<Exception> exceptions) {
		return instance;
	}
}