package org.dummycreator.dummyfactories;

import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomPrimitiveFactory<T> extends DummyFactory<T> {

	private final Class<T> clazz;

	public RandomPrimitiveFactory(Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * @return Depending on requested type, will call the associated <code>RandomCreator.getRandomT()</code> method.
	 * 
	 * @param knownInstances Not used.
	 * @param classBindings Not used.
	 * @param exceptions Not used.
	 * @see RandomCreator#getRandomInt()
	 * @see RandomCreator#getRandomLong()
	 * @see RandomCreator#getRandomFloat()
	 * @see RandomCreator#getRandomBoolean()
	 * @see RandomCreator#getRandomChar()
	 * @see RandomCreator#getRandomByte()
	 * @see RandomCreator#getRandomShort()
	 * @see RandomCreator#getRandomDouble()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T createDummy(Map<Class<?>, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
		if (clazz == (java.lang.Integer.TYPE)) {
			return (T) (Integer) RandomCreator.getRandomInt();
		} else if (clazz == (java.lang.Long.TYPE)) {
			return (T) (Long) RandomCreator.getRandomLong();
		} else if (clazz == (java.lang.Float.TYPE)) {
			return (T) (Float) RandomCreator.getRandomFloat();
		} else if (clazz == (java.lang.Boolean.TYPE)) {
			return (T) (Boolean) RandomCreator.getRandomBoolean();
		} else if (clazz == (java.lang.Character.TYPE)) {
			return (T) (Character) RandomCreator.getRandomChar();
		} else if (clazz == (java.lang.Byte.TYPE)) {
			return (T) (Byte) RandomCreator.getRandomByte();
		} else if (clazz == (java.lang.Short.TYPE)) {
			return (T) (Short) RandomCreator.getRandomShort();
		} else if (clazz == (java.lang.Double.TYPE)) {
			return (T) (Double) RandomCreator.getRandomDouble();
		}
		return null;
	}
}