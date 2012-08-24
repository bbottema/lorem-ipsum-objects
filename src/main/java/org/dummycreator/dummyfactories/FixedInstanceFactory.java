package org.dummycreator.dummyfactories;

import java.util.List;

import org.dummycreator.ClassBindings;
import org.dummycreator.DummyFactory;


/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class FixedInstanceFactory<T> implements DummyFactory<T> {

    private final T instance;

    public FixedInstanceFactory(T instance) {
	this.instance = instance;
    }

    @Override
    public T createDummy(List<Exception> constructorExceptions, ClassBindings classBindings) {
	return instance;
    }
}