package org.dummycreator.dummyfactories;

import java.util.List;

import org.dummycreator.ClassBindings;
import org.dummycreator.DummyFactory;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomEnumFactory<T extends Enum<?>> extends DummyFactory<T> {

	private final Class<T> clazz;

	public RandomEnumFactory(Class<T> clazz) {
		this.clazz = clazz;
	}

	@Override
	public T createDummy(List<Exception> constructorExceptions, ClassBindings classBindings) {
		T[] enums = clazz.getEnumConstants();
		return enums[RandomCreator.getRandomInt(enums.length - 1)];
	}
}