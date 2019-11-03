package org.bbottema.loremipsumobjects;

import org.bbottema.loremipsumobjects.typefactories.ClassBasedFactory;
import org.bbottema.loremipsumobjects.typefactories.ConstructorBasedFactory;
import org.bbottema.loremipsumobjects.typefactories.LoremIpsumObjectFactory;
import org.bbottema.loremipsumobjects.typefactories.MethodBasedFactory;
import org.bbottema.loremipsumobjects.typefactories.RandomBigDecimalFactory;
import org.bbottema.loremipsumobjects.typefactories.RandomBooleanFactory;
import org.bbottema.loremipsumobjects.typefactories.RandomPrimitiveFactory;
import org.bbottema.loremipsumobjects.typefactories.RandomStringFactory;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores a list of classes / interfaces and their associated deferred types. This list is used to tell {@link LoremIpsumObjectCreator} which specific
 * implementation it should use to produce new dummy instances for a certain type. This is most useful to make sure Dummy Creator can create
 * dummy objects for interface / abstract types it encounters.
 * <p>
 * Deferred types are produced by {@link LoremIpsumObjectFactory} implementations. Default factories are in place for strings, primitives, enums. In
 * addition,there are factories that can be configured to invoke a specific {@link Method} ({@link MethodBasedFactory}) or a specific
 * {@link Constructor} ({@link ConstructorBasedFactory}). Finally, there is a factory to return a fixed instance. The
 * {@link ClassBasedFactory} is used automatically internally if no class binding can be found for a particular type. Examples are:
 * <ul>
 * <li>List -> ArrayList (deferred to first succesfully invoked class constructor)</li>
 * <li>List -> LinkedList (idem)</li>
 * <li>Integer -> 4443 (which is autoboxed to an Integer and then acts as deferred instance)</li>
 * <li>Foo -> FooFactory.class.getMethod('createFoo') (deferred to method call)</li>
 * <li>Apple -> Apple.class.getConstructor(String.class) (deferred to constructor call)</li>
 * <li>Apple -> new AppleFactory() (deferred to object factory call)</li>
 * </ul>
 */
public class ClassBindings {

	private final HashMap<Class<?>, LoremIpsumObjectFactory<?>> bindings = new HashMap<>();

	/**
	 * Initializes with basic bindings for primitives, arrays and strings.
	 */
	public ClassBindings() {
		bind(Long.TYPE, new RandomPrimitiveFactory<>(Long.TYPE));
		bind(Integer.TYPE, new RandomPrimitiveFactory<>(Integer.TYPE));
		bind(Float.TYPE, new RandomPrimitiveFactory<>(Float.TYPE));
		bind(Boolean.TYPE, new RandomPrimitiveFactory<>(Boolean.TYPE));
		bind(Character.TYPE, new RandomPrimitiveFactory<>(Character.TYPE));
		bind(Byte.TYPE, new RandomPrimitiveFactory<>(Byte.TYPE));
		bind(Short.TYPE, new RandomPrimitiveFactory<>(Short.TYPE));
		bind(Double.TYPE, new RandomPrimitiveFactory<>(Double.TYPE));
		bind(String.class, new RandomStringFactory());
		bind(Boolean.class, new RandomBooleanFactory());
		bind(List.class, new ClassBasedFactory<>(ArrayList.class));
		bind(Map.class, new ClassBasedFactory<>(HashMap.class));
		bind(Set.class, new ClassBasedFactory<>(HashSet.class));
		bind(BigDecimal.class, new RandomBigDecimalFactory());
	}

	/**
	 * Binds a {@link LoremIpsumObjectFactory} to a specific <code>Class</code> instance.
	 *
	 * @param clazz   The class to bind the dummy factory to.
	 * @param factory The factory to bind the the given class.
	 * @throws IllegalArgumentException Thrown if {@link LoremIpsumObjectFactory#isValidForType(Class)} returns <code>false</code> or throws an
	 *                                  <code>IllegalArgumentException</code> itself.
	 * @see LoremIpsumObjectFactory#isValidForType(Class)
	 */
	public <T> void bind(final Class<T> clazz, final LoremIpsumObjectFactory<? extends T> factory) {
		try {
			if (factory.isValidForType(clazz)) {
				bindings.put(clazz, factory);
			} else {
				// factory didn't throw an exception, so we'll do it ourself
				throw new IllegalArgumentException("given factory not valid for given type");
			}
		} catch (final IllegalArgumentException e) {
			// note: exception is also thrown by LoremIpsumObjectFactory.isValidForType
			throw new IllegalArgumentException(String.format("dummy factory [%s] is not valid for class type [%s]", factory, clazz), e);
		}
	}

	/**
	 * This method returns a binding made for the given class. This binding might be of one of the following type: Constructor
	 * Implementation of a Interface Method Object
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	public <T> LoremIpsumObjectFactory<T> find(final Class<T> _class) {
		return (LoremIpsumObjectFactory<T>) bindings.get(_class);
	}
}