/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.opensource.org/licenses/cddl1.php
 * or http://www.opensource.org/licenses/cddl1.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.opensource.org/licenses/cddl1.php.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */
package org.dummycreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dummycreator.dummyfactories.ClassBasedFactory;
import org.dummycreator.dummyfactories.ConstructorBasedFactory;
import org.dummycreator.dummyfactories.DummyFactory;
import org.dummycreator.dummyfactories.MethodBasedFactory;
import org.dummycreator.dummyfactories.RandomBooleanFactory;
import org.dummycreator.dummyfactories.RandomPrimitiveFactory;
import org.dummycreator.dummyfactories.RandomStringFactory;

/**
 * Stores a list of classes / interfaces and their associated deferred types. This list is used to tell {@link DummyCreator} which specific
 * implementation it should use to produce new dummy instances for a certain type. This is most useful to make sure Dummy Creator can create
 * dummy objects for interface / abstract types it encounters.
 * <p>
 * Deferred types are produced by {@link DummyFactory} implementations. Default factories are in place for strings, primitives, enums. In
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
 * 
 * @author Alexander Muthmann <amuthmann@dev-eth0.de> (original author)
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class ClassBindings {

	private final HashMap<Class<?>, DummyFactory<?>> bindings = new HashMap<Class<?>, DummyFactory<?>>();

	/**
	 * Initializes with basic bindings for primitives, arrays and strings.
	 */
	public ClassBindings() {
		add(Long.TYPE, new RandomPrimitiveFactory<Long>(Long.TYPE));
		add(Integer.TYPE, new RandomPrimitiveFactory<Integer>(Integer.TYPE));
		add(Float.TYPE, new RandomPrimitiveFactory<Float>(Float.TYPE));
		add(Boolean.TYPE, new RandomPrimitiveFactory<Boolean>(Boolean.TYPE));
		add(Character.TYPE, new RandomPrimitiveFactory<Character>(Character.TYPE));
		add(Byte.TYPE, new RandomPrimitiveFactory<Byte>(Byte.TYPE));
		add(Short.TYPE, new RandomPrimitiveFactory<Short>(Short.TYPE));
		add(Double.TYPE, new RandomPrimitiveFactory<Double>(Double.TYPE));
		add(String.class, new RandomStringFactory());
		add(Boolean.class, new RandomBooleanFactory());
	}

	/**
	 * Binds a {@link DummyFactory} to a specific <code>Class</code> instance.
	 * 
	 * @param clazz The class to bind the dummy factory to.
	 * @param factory The factory to bind the the given class.
	 * @throws IllegalArgumentException Thrown if {@link DummyFactory#isValidForType(Class)} returns <code>false</code> or throws an
	 *             <code>IllegalArgumentException</code> itself.
	 * @see DummyFactory#isValidForType(Class)
	 */
	public <T> void add(final Class<T> clazz, final DummyFactory<? extends T> factory) {
		try {
			if (factory.isValidForType(clazz)) {
				bindings.put(clazz, factory);
			} else {
				// factory didn't throw an exception, so we'll do it ourself
				throw new IllegalArgumentException();
			}
		} catch (IllegalArgumentException e) {
			// note: exception is also thrown by DummyFactory.isValidForType
			throw new IllegalArgumentException(String.format("dummy factory [%s] is not valid for class type [%s]", factory, clazz), e);
		}
	}

	/**
	 * This method returns a binding made for the given class. This binding might be of one of the following type: Constructor
	 * Implementation of a Interface Method Object
	 */
	@SuppressWarnings("unchecked")
	public <T> DummyFactory<T> find(final Class<T> _class) {
		return (DummyFactory<T>) bindings.get(_class);
	}

	/**
	 * You can call this method to build some default bindings for common classes. This includes List.class, Map.class, Set.class.
	 * <p>
	 * These are in addition to the basic bindings added in {@link #ClassBindings()}.
	 */
	@SuppressWarnings("rawtypes")
	public static ClassBindings defaultBindings() {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(List.class, new ClassBasedFactory<ArrayList>(ArrayList.class));
		classBindings.add(Map.class, new ClassBasedFactory<HashMap>(HashMap.class));
		classBindings.add(Set.class, new ClassBasedFactory<HashSet>(HashSet.class));
		return classBindings;
	}
}