package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RandomArrayFactory<T> extends LoremIpsumObjectFactory<T> {

	private final Class<T> clazz;

	public RandomArrayFactory(final Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @param knownInstances Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param classBindings  Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param exceptions     Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @return A new array with a random length of 2 or 3, populated with objects of the requested type.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public T createLoremIpsumObject(@Nullable final Type[] genericMetaData,
	                                final Map<String, ClassUsageInfo<?>> knownInstances,
	                                final ClassBindings classBindings,
	                                final List<Exception> exceptions) {
		final int length = LoremIpsumGenerator.getInstance().getRandomInt(2) + 2;
		final Object dummyArray = Array.newInstance(clazz.getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(dummyArray, i,
					new ClassBasedFactory(clazz.getComponentType()).createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions));
		}
		return (T) dummyArray;
	}
}