package org.bbottema.loremipsumobjects.typefactories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bbottema.javareflection.MethodUtils;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static java.util.EnumSet.of;
import static java.util.Objects.requireNonNull;
import static org.bbottema.javareflection.ClassUtils.findFirstMethodByName;
import static org.bbottema.javareflection.ClassUtils.locateClass;
import static org.bbottema.javareflection.model.MethodModifier.PUBLIC;

public class RandomOptionalFactory extends LoremIpsumObjectFactory<Object> {

	private static final Class<?> CLASS_OPTIONAL = locateClass("Optional", "java.util", null);

	@Override
	@SuppressWarnings("ConstantConditions")
	@SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE", justification = "false positive")
	public Object _createLoremIpsumObject(
			@Nullable final Type[] genericMetaData,
			@Nullable final Map<String, ClassUsageInfo<?>> knownInstances,
			@Nullable final ClassBindings classBindings,
			@Nullable final List<Exception> exceptions) {
		// static method Optional.of(value)
		Method optionalOf = requireNonNull(findFirstMethodByName(CLASS_OPTIONAL, Object.class, of(PUBLIC), "of"));
		return MethodUtils.invokeMethodSimple(optionalOf, null,
				createLoremIpsumObjectForOptional(genericMetaData, knownInstances, classBindings, exceptions));
	}

	private Object createLoremIpsumObjectForOptional(@Nullable Type[] genericMetaData, @Nullable Map<String, ClassUsageInfo<?>> knownInstances, @Nullable ClassBindings classBindings, @Nullable List<Exception> exceptions) {
		final Class clazz;
		final Type[] nextGenericsMetaData;

		if (genericMetaData != null) {
			Type genericParameterType = genericMetaData[0];
			final boolean isRawClassItself = genericParameterType instanceof Class;
			nextGenericsMetaData = isRawClassItself ? null : ((ParameterizedType) genericParameterType).getActualTypeArguments();
			clazz = extractConcreteType(genericParameterType);
		} else {
			nextGenericsMetaData = null;
			clazz = String.class;
		}

		//noinspection unchecked
		return new ClassBasedFactory<Object>(clazz)
				.createLoremIpsumObject(nextGenericsMetaData, knownInstances, classBindings, exceptions);
	}

	private Class extractConcreteType(@Nullable Type genericMetaData) {
		return (genericMetaData instanceof ParameterizedType)
				? (Class) ((ParameterizedType) genericMetaData).getRawType()
				: (Class) genericMetaData;
	}
}