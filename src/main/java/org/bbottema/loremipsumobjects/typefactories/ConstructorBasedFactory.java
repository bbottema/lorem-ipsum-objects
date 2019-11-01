package org.bbottema.loremipsumobjects.typefactories;

import lombok.extern.slf4j.Slf4j;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.TimeLimitedCodeBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ConstructorBasedFactory<T> extends LoremIpsumObjectFactory<T> {

	private final Constructor<T> constructor;

	public ConstructorBasedFactory(final Constructor<T> constructor) {
		this.constructor = constructor;
	}

	/**
	 * @param genericMetaData Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Constructor</code>.
	 * @param knownInstances  Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Constructor</code>.
	 * @param classbindings   Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Constructor</code>.
	 * @param exceptions      Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Constructor</code>.
	 * @return The result of a successful invocation of the given constructor or <code>null</code> in case of an error.
	 */
	@Nullable
	@Override
	public T createLoremIpsumObject(@Nullable final Type[] genericMetaData,
	                                final Map<String, ClassUsageInfo<?>> knownInstances,
	                                final ClassBindings classbindings,
	                                final List<Exception> exceptions) {
		@SuppressWarnings("unchecked") final Class<T>[] parameters = (Class<T>[]) constructor.getParameterTypes();
		try {
			try {
				constructor.setAccessible(true); // might fail due to security policy
			} catch (NumberFormatException e) {
				// ignore, try without making it explicitly accessibly
			}
			
			return TimeLimitedCodeBlock.runWithTimeout(250, TimeUnit.MILLISECONDS, new Callable<T>() {
				@Override @NotNull
				public T call() throws Exception {
					if (parameters.length > 0) {
						return constructor.newInstance(determineArguments(parameters, genericMetaData, knownInstances, classbindings, exceptions));
					} else {
						return constructor.newInstance();
					}
				}
			});
		} catch (final Exception e) {
			exceptions.add(e);
		}
		return null;
	}
	
	@NotNull
	private Object[] determineArguments(Class<T>[] parameters, Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classbindings, List<Exception> exceptions) {
		final Object[] args = new Object[parameters.length];
		for (int i = 0; i < args.length; i++) {
			args[i] = new ClassBasedFactory<>(parameters[i]).createLoremIpsumObject(genericMetaData, knownInstances, classbindings,
					exceptions);
		}
		return args;
	}
}