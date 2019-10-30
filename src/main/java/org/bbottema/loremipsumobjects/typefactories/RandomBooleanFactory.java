package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This factory prevents the default {@link ClassBasedFactory} approach for creating a <code>Boolean</code>, because that will invoke
 * {@link Boolean#Boolean(String)} with a randomly generated <code>String</code>, which will always result in a value of <code>false</code>.
 */
public class RandomBooleanFactory extends LoremIpsumObjectFactory<Boolean> {

	/**
	 * @param knownInstances Not used.
	 * @param classBindings  Not used.
	 * @param exceptions     Not used.
	 * @return The result of {@link LoremIpsumGenerator#getRandomBoolean()}.
	 */
	@Override
	public Boolean createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			@Nullable final ClassBindings classBindings,
			@Nullable final List<Exception> exceptions) {
		return LoremIpsumGenerator.getInstance().getRandomBoolean();
	}
}