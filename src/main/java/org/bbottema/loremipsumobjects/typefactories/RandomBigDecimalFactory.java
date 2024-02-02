package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.LoremIpsumConfig;
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

	@Override
	public BigDecimal _createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			final LoremIpsumConfig loremIpsumConfig,
			@Nullable final List<Exception> exceptions) {
		LoremIpsumGenerator instance = LoremIpsumGenerator.getInstance();
		int fixedBigdecimalScaleConfig = loremIpsumConfig.getFixedBigdecimalScale();
		int scale = fixedBigdecimalScaleConfig < 0 ? instance.getRandomInt(6) : fixedBigdecimalScaleConfig;
		return BigDecimal.valueOf(instance.getRandomInt(), scale);
	}
}