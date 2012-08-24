package org.dummycreator.dummyfactories;

import java.util.List;

import org.dummycreator.ClassBindings;
import org.dummycreator.DummyFactory;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomStringFactory extends DummyFactory<String> {

	@Override
	public String createDummy(List<Exception> constructorExceptions, ClassBindings classBindings) {
		return RandomCreator.getRandomString();
	}
}