package org.dummycreator.dummyfactories;

import java.util.List;
import java.util.Map;

import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;

/**
 * This factory prevents the default {@link ClassBasedFactory} approach for creating a <code>Boolean</code>, because that will invoke
 * {@link Boolean#Boolean(String)} with a randomly generated <code>String</code>, which will always result in a value of <code>false</code>.
 * 
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class RandomBooleanFactory extends DummyFactory<Boolean> {

	/**
	 * @return The result of {@link RandomCreator#getRandomString()}.
	 * 
	 * @param knownInstances Not used.
	 * @param classBindings Not used.
	 * @param exceptions Not used.
	 */
	@Override
	public Boolean createDummy(Map<Class<?>, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
		return RandomCreator.getInstance().getRandomBoolean();
	}
}