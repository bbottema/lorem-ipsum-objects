package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RandomStringFactory extends LoremIpsumObjectFactory<String> {

	/**
	 * @param knownInstances Not used.
	 * @param classBindings  Not used.
	 * @param exceptions     Not used.
	 * @return The result of {@link LoremIpsumGenerator#getRandomString()}.
	 */
	@Override
	public String createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			@Nullable final ClassBindings classBindings,
			@Nullable final List<Exception> exceptions) {
		return LoremIpsumGenerator.getInstance().getRandomString();
	}
}