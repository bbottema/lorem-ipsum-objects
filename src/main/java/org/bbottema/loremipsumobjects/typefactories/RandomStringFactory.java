package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class RandomStringFactory extends LoremIpsumObjectFactory<String> {

	/**
	 * @param knownInstances   Not used.
	 * @param loremIpsumConfig Not used.
	 * @param exceptions       Not used.
	 * @return The result of {@link LoremIpsumGenerator#getRandomString()}.
	 */
	@Override
	public String _createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			LoremIpsumConfig loremIpsumConfig,
			@Nullable final List<Exception> exceptions) {
		return LoremIpsumGenerator.getInstance().getRandomString();
	}
}