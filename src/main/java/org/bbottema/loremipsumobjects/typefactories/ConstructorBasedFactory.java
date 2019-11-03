package org.bbottema.loremipsumobjects.typefactories;

import lombok.extern.slf4j.Slf4j;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.TimeLimitedCodeBlock;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
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
	 * @param genericMetaData Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the
	 *                        <code>Constructor</code>.
	 * @param knownInstances  Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the
	 *                        <code>Constructor</code>.
	 * @param classbindings   Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the
	 *                        <code>Constructor</code>.
	 * @param exceptions      Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the
	 *                        <code>Constructor</code>.
	 *
	 * @return The result of a successful invocation of the given constructor or <code>null</code> in case of an error.
	 */
	@Nullable
	@Override
	public T createLoremIpsumObject(@Nullable final Type[] genericMetaData,
									final Map<String, ClassUsageInfo<?>> knownInstances,
									final ClassBindings classbindings,
									final List<Exception> exceptions) {
		try {
			try {
				constructor.setAccessible(true); // might fail due to security policy
			} catch (Exception e) {
				// ignore, try without making it explicitly accessibly
			}
			
			return TimeLimitedCodeBlock.runWithTimeout(250, TimeUnit.MILLISECONDS, new Callable<T>() {
				public T call() throws Exception {
					return (constructor.getParameterTypes().length > 0)
						   ? constructor.newInstance(determineArguments(constructor, knownInstances, classbindings, exceptions))
						   : constructor.newInstance();
				}
			});
		} catch (final Exception e) {
			exceptions.add(new IllegalArgumentException("failed to invoke constructor", e));
		}
		return null;
	}
	
	@NotNull
	private Object[] determineArguments(Constructor<T> constructor, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classbindings, List<Exception> exceptions) {
		@SuppressWarnings("unchecked") final Class<T>[] parameters = (Class<T>[]) constructor.getParameterTypes();
		
		final Object[] args = new Object[parameters.length];
		for (int i = 0; i < args.length; i++) {
			final Type genericParameterType = constructor.getGenericParameterTypes()[i];
			final boolean isRawClassItself = genericParameterType instanceof Class;
			final Type[] nextGenericsMetaData = isRawClassItself ? null : ((ParameterizedType) genericParameterType).getActualTypeArguments();
			
			args[i] = new ClassBasedFactory<>(parameters[i]).createLoremIpsumObject(nextGenericsMetaData, knownInstances, classbindings,
					exceptions);
		}
		return args;
	}
}