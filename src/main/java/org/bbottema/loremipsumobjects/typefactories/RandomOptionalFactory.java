package org.bbottema.loremipsumobjects.typefactories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.bbottema.javareflection.MethodUtils;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.ReflectionHelper;
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
import static org.bbottema.loremipsumobjects.typefactories.util.ReflectionHelper.*;

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
		Class clazz = String.class;
		Type[] nextGenericsMetaData = null;

		if (genericMetaData != null) {
			nextGenericsMetaData = determineGenericsMetaData(genericMetaData, genericMetaData[0]);
			clazz = extractConcreteType(null, genericMetaData[0]);
		}

		//noinspection unchecked
		return new ClassBasedFactory<Object>(clazz)
				.createLoremIpsumObject(nextGenericsMetaData, knownInstances, classBindings, exceptions);
	}
}