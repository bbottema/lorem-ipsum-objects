package org.bbottema.loremipsumobjects.typefactories;

import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.TimeLimitedCodeBlock;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static java.lang.String.format;

public class MethodBasedFactory<T> extends LoremIpsumObjectFactory<T> {

	private final Method method;

	public MethodBasedFactory(final Method method) {
		this.method = method;
	}

	@Override
	public boolean isValidForType(final Class<? super T> clazz) {
		if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().equals(clazz)) {
			return true;
		} else {
			throw new IllegalArgumentException("The method has to be static and return an object of the given class!");
		}
	}

	/**
	 * @param knownInstances Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Method</code>.
	 * @param classBindings  Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Method</code>.
	 * @param exceptions     Not used, but passed on to {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} when constructing the parameters for the <code>Method</code>.
	 * @return The result of a successful invocation of the given method or <code>null</code> in case of an error.
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	@Override
	public T createLoremIpsumObject(@Nullable final Type[] genericMetaData,
	                                final Map<String, ClassUsageInfo<?>> knownInstances,
	                                final ClassBindings classBindings,
	                                final List<Exception> exceptions) {
		final Method m = method;
		final Class<?>[] parameters = m.getParameterTypes();
		final Object[] params = new Object[parameters.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = new ClassBasedFactory<>((Class<Object>) parameters[i])
					.createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions);
		}
		try {
			return TimeLimitedCodeBlock.runWithTimeout(250, TimeUnit.MILLISECONDS, new Callable<T>() {
				@Override @Nullable
				public T call() throws Exception {
					return (T) m.invoke(null, params);
				}
			});
		} catch (final Exception e) {
			exceptions.add(new IllegalArgumentException(format("failed to invoke Method [%s] to product an object of type [%s]", m.getName(), method.getReturnType()), e));
		}
		return null;
	}
}