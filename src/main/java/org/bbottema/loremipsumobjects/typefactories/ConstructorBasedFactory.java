package org.bbottema.loremipsumobjects.typefactories;

import lombok.extern.slf4j.Slf4j;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.LoremIpsumConfig;
import org.bbottema.loremipsumobjects.typefactories.util.TimeLimitedCodeBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static org.bbottema.loremipsumobjects.typefactories.util.ReflectionHelper.determineGenericsMetaData;
import static org.bbottema.loremipsumobjects.typefactories.util.ReflectionHelper.extractConcreteType;

@Slf4j
public class ConstructorBasedFactory<T> extends LoremIpsumObjectFactory<T> {
	
	private final Constructor<T> constructor;
	
	public ConstructorBasedFactory(final Constructor<T> constructor) {
		this.constructor = constructor;
	}
	
	/**
	 * @return The result of a successful invocation of the given constructor or <code>null</code> in case of an error.
	 */
	@Nullable
	@Override
	public T _createLoremIpsumObject(@Nullable final Type[] genericMetaData,
	                                 final Map<String, ClassUsageInfo<?>> knownInstances,
	                                 final LoremIpsumConfig loremIpsumConfig,
	                                 final List<Exception> exceptions) {
		try {
			try {
				constructor.setAccessible(true); // might fail due to security policy
			} catch (Exception e) {
				// ignore, try without making it explicitly accessibly
			}
			
			return TimeLimitedCodeBlock.runWithTimeout(loremIpsumConfig.getTimeoutMillis(), TimeUnit.MILLISECONDS, new Callable<T>() {
				public T call() throws Exception {
					return (constructor.getParameterTypes().length > 0)
						   ? constructor.newInstance(determineArguments(constructor, genericMetaData, knownInstances, loremIpsumConfig, exceptions))
						   : constructor.newInstance();
				}
			});
		} catch (final Exception e) {
			exceptions.add(new IllegalArgumentException("failed to invoke constructor", e));
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@NotNull
	private Object[] determineArguments(Constructor<T> constructor, @Nullable Type[] currentGenericsMetaData, Map<String, ClassUsageInfo<?>> knownInstances, LoremIpsumConfig loremIpsumConfig, List<Exception> exceptions) {
		@SuppressWarnings("unchecked") final Class<T>[] parameters = (Class<T>[]) constructor.getParameterTypes();
		
		final Object[] args = new Object[parameters.length];
		for (int i = 0; i < args.length; i++) {
			Class clazz = parameters[i];
			// this is a hack that works if the constructor has one argument of type T and that is the first type declared by the class (the most common use case though)
			final Type[] nextGenericsMetaData = determineGenericsMetaData(currentGenericsMetaData, constructor.getGenericParameterTypes()[i]);
			if (clazz == Object.class && constructor.getGenericParameterTypes()[i] instanceof TypeVariable) {
				clazz = extractConcreteType(null, nextGenericsMetaData[0]);
			}
			args[i] = new ClassBasedFactory<>(clazz).createLoremIpsumObject(nextGenericsMetaData, knownInstances, loremIpsumConfig, exceptions);
		}
		return args;
	}
}