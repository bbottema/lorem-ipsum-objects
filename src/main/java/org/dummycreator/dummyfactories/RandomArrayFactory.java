package org.dummycreator.dummyfactories;

import java.lang.reflect.Array;
import java.util.List;

import org.dummycreator.ClassBindings;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomArrayFactory<T> extends DummyFactory<Array> {

	private Class<T> clazz;

	public RandomArrayFactory(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public Array createDummy(List<Exception> exceptions, ClassBindings classBindings) {
		int length = RandomCreator.getRandomInt(2) + 2;
		Array parameter = (Array) Array.newInstance(clazz.getComponentType(), length);
		for (int i = 0; i < length; i++) {
			Array.set(parameter, i, new ClassBasedFactory<T>(clazz).createDummy(exceptions, classBindings));
		}
		return parameter;
	}
}