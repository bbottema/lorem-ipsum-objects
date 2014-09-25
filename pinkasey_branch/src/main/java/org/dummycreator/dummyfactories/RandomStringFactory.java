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
public class RandomStringFactory extends DummyFactory<String> {

	/**
	 * @return The result of {@link RandomCreator#getRandomString()}.
	 * @param knownInstances Not used.
	 * @param classBindings Not used.
	 * @param exceptions Not used.
	 */
	@Override
	public String createDummy(final Type[] genericMetaData, final Map<String, ClassUsageInfo<?>> knownInstances,
			final ClassBindings classBindings, final List<Exception> exceptions) {
		return RandomCreator.getInstance().getRandomString();
	}
}