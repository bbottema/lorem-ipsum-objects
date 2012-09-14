package org.dummycreator.dummyfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;

/**
 * Generates a dummy of a primitive type which is returned in its wrapped (autoboxed) form.
 * <p>
 * Used by the {@link ClassBasedFactory} so that it doesn't have to manually create the wrapper types by trying to manually invoke a
 * {@link Constructor}.
 * <p>
 * See {@link #SUPPORTED_PRIMITIVE_CLASSES} for a list of supported primitive types.
 * 
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomPrimitiveFactory<T> extends DummyFactory<T> {

	/**
	 * Primitive classes that can be created by this factory are: <code>int</code>, <code>long</code>, <code>float</code>,
	 * <code>boolean</code>, <code>char</code>, <code>byte</code>, <code>short</code>, <code>double</code>.
	 */
	private static final List<Class<?>> SUPPORTED_PRIMITIVE_CLASSES = new ArrayList<Class<?>>();

	static {
		SUPPORTED_PRIMITIVE_CLASSES.add(int.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(long.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(float.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(boolean.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(char.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(byte.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(short.class);
		SUPPORTED_PRIMITIVE_CLASSES.add(double.class);
	}

	/**
	 * The primitive type that should be created.
	 */
	private final Class<T> clazz;

	public RandomPrimitiveFactory(final Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Indicates whether the given type can be produced by creating one of the primitive types defined by
	 * {@link #SUPPORTED_PRIMITIVE_CLASSES}.
	 */
	@Override
	public boolean isValidForType(final Class<? super T> clazz) {
		return SUPPORTED_PRIMITIVE_CLASSES.contains(clazz);
	}

	/**
	 * @return Depending on requested type, will call the associated <code>RandomCreator.getRandomT()</code> method.
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
	public T createDummy(final Type[] genericMetaData, final Map<String, ClassUsageInfo<?>> knownInstances,
			final ClassBindings classBindings, final List<Exception> exceptions) {
		if (clazz == int.class) {
			return (T) (Integer) RandomCreator.getInstance().getRandomInt();
		} else if (clazz == long.class) {
			return (T) (Long) RandomCreator.getInstance().getRandomLong();
		} else if (clazz == float.class) {
			return (T) (Float) RandomCreator.getInstance().getRandomFloat();
		} else if (clazz == boolean.class) {
			return (T) (Boolean) RandomCreator.getInstance().getRandomBoolean();
		} else if (clazz == char.class) {
			return (T) (Character) RandomCreator.getInstance().getRandomChar();
		} else if (clazz == byte.class) {
			return (T) (Byte) RandomCreator.getInstance().getRandomByte();
		} else if (clazz == short.class) {
			return (T) (Short) RandomCreator.getInstance().getRandomShort();
		} else if (clazz == double.class) {
			return (T) (Double) RandomCreator.getInstance().getRandomDouble();
		}
		return null;
	}
}