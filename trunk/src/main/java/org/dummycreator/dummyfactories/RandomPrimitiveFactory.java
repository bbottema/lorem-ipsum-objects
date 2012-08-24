package org.dummycreator.dummyfactories;

import java.util.List;

import org.dummycreator.ClassBindings;
import org.dummycreator.DummyFactory;
import org.dummycreator.RandomCreator;


/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomPrimitiveFactory<T> extends DummyFactory<T> {

    private final Class<T> clazz;

    public RandomPrimitiveFactory(Class<T> clazz) {
	this.clazz = clazz;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T createDummy(List<Exception> constructorExceptions, ClassBindings classBindings) {
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