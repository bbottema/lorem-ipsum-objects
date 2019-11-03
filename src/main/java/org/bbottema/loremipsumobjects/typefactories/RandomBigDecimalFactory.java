package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Generates big decimal's while making sure scale is not set too high.
 */
public class RandomBigDecimalFactory extends LoremIpsumObjectFactory<BigDecimal> {

	/**
	 * @param knownInstances Not used.
	 * @param classBindings  Not used.
	 * @param exceptions     Not used.
	 * @return The result of {@link LoremIpsumGenerator#getRandomBoolean()}.
	 */
	@Override
	public BigDecimal _createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			@Nullable final ClassBindings classBindings,
			@Nullable final List<Exception> exceptions) {
		LoremIpsumGenerator instance = LoremIpsumGenerator.getInstance();
		return BigDecimal.valueOf(instance.getRandomInt(), instance.getRandomInt(6));
	}
}