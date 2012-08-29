package org.dummycreator.dummyfactories;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomArrayFactory<T> extends DummyFactory<Array> {

	private Class<T> clazz;

	public RandomArrayFactory(Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return A new array with a random length of 2 or 3, populated with objects of the requested type.
	 * @param knownInstances Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)}.
	 * @param classBindings Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)}.
	 * @param exceptions Not used, but passed on to {@link ClassBasedFactory#createDummy(Map, ClassBindings, List)}.
	 */
	@Override
	public Array createDummy(Map<Class<?>, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
		int length = RandomCreator.getRandomInt(2) + 2;
		Array dummyArray = (Array) Array.newInstance(clazz.getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(dummyArray, i, new ClassBasedFactory<T>(clazz).createDummy(knownInstances, classBindings, exceptions));
		}
		return dummyArray;
	}
}