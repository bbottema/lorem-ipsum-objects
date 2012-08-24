package org.dummycreator;

import java.util.List;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public interface DummyFactory<T> {
    T createDummy(List<Exception> constructorExceptions, ClassBindings classBindings);
}