package org.dummycreator.dummyfactories;

import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;

/**
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomStringFactory extends DummyFactory<String> {

	/**
	 * @return The result of {@link RandomCreator#getRandomString()}.
	 * 
	 * @param knownInstances Not used.
	 * @param classBindings Not used.
	 * @param exceptions Not used.
	 */
	@Override
	public String createDummy(Map<Class<?>, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
		return RandomCreator.getInstance().getRandomString();
	}
}