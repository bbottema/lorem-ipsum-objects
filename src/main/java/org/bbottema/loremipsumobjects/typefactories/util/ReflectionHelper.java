package org.bbottema.loremipsumobjects.typefactories.util;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@UtilityClass
public final class ReflectionHelper {

	@Nullable
	public static Type[] determineGenericsMetaData(@Nullable Type[] parentGenericsMetaData, Type genericParameterType) {
		final Type[] nextGenericsMetaData;
		if (genericParameterType instanceof Class) {
			nextGenericsMetaData = null;
		} else if (genericParameterType instanceof ParameterizedType) {
			nextGenericsMetaData = ((ParameterizedType) genericParameterType).getActualTypeArguments();
		} else {
			// parameter is of type T, which we will assume is T from the class declaration SomeClass<T>
			nextGenericsMetaData = parentGenericsMetaData;
		}
		return nextGenericsMetaData;
	}

	public static Class extractConcreteType(@Nullable Type[] parentGenericsMetaData, @Nullable Type genericMetaData) {
		if (genericMetaData instanceof Class) {
			return (Class) genericMetaData;
		} else if (genericMetaData instanceof ParameterizedType) {
			return (Class) ((ParameterizedType) genericMetaData).getRawType();
		} else {
			return extractConcreteType(null, parentGenericsMetaData[0]);
		}
	}
}