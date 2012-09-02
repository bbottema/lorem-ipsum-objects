package org.dummycreator.dummyfactories;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomEnumFactory<T extends Enum<?>> extends DummyFactory<T> {

	private final Class<T> clazz;

	public RandomEnumFactory(final Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return A random enum from the list acquired by invoking {@link Class#getEnumConstants()} on the requested type.
	 * @param knownInstances Not used.
	 * @param classBindings Not used.
	 * @param exceptions Not used.
	 */
	@Override
	public T createDummy(final Type[] genericMetaData, final Map<String, ClassUsageInfo<?>> knownInstances,
			final ClassBindings classBindings, final List<Exception> exceptions) {
		final T[] enums = clazz.getEnumConstants();
		return enums[RandomCreator.getInstance().getRandomInt(enums.length - 1)];
	}
}