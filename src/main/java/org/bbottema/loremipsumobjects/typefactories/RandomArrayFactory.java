package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.LoremIpsumConfig;
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
	 * @return A new array with a random length of 2 or 3, populated with objects of the requested type.
	 */
	@Override
	@SuppressWarnings({"unchecked"})
	public T _createLoremIpsumObject(@Nullable final Type[] genericMetaData,
	                                 final Map<String, ClassUsageInfo<?>> knownInstances,
	                                 final LoremIpsumConfig loremIpsumConfig,
	                                 final List<Exception> exceptions) {
		final int length = LoremIpsumGenerator.getInstance().getRandomInt(2) + 2;
		final Object dummyArray = Array.newInstance(clazz.getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(dummyArray, i,
					new ClassBasedFactory(clazz.getComponentType()).createLoremIpsumObject(genericMetaData, knownInstances, loremIpsumConfig, exceptions));
		}
		return (T) dummyArray;
	}
}