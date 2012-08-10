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
 *
 * The Original Software is dummyCreator. The Initial Developer of the Original
 * Software is Alexander Muthmann <amuthmann@dev-eth0.de>.
 */
package org.dummycreator;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codemonkey.javareflection.FieldUtils;
import org.codemonkey.javareflection.FieldUtils.BeanRestriction;
import org.codemonkey.javareflection.FieldUtils.Visibility;
import org.codemonkey.javareflection.FieldWrapper;

/**
 * Tool to create populated dummy objects of a given class. This tool will recursively run through its setters and try to come up with newly
 * populated objects for those fields.
 * <p>
 * To avoid recursive infinite loops, this tool keeps track of previously populated instances of a certain type and reuse that instead.
 * <p>
 * For numbers being generated a random number is being using, for strings a lorem ipsum generated is being used.
 * 
 * @see #create(Class)
 * 
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @author Benny Bottema <b.bottema@projectnibble.org>
 */
public class DummyCreator {

    private final Logger logger = Logger.getLogger(getClass());

    /**
     * A cache for previously found {@link Constructor} instances and preferred constructor for previous successfully invoked constructors.
     */
    private final ConstructorCache constructorCache;

    /**
     * A cache for previously found {@link Method} instances and preferred constructor for previous successfully invoked constructors.
     */
    private final MethodCache methodCache;

    /**
     * A map that contains deferred class types for a given type. With this you can defer the creation of a dummy instance to another type.
     * This is useful if you need to instance dummy objects for an interface or abstract class.
     * 
     * @see ClassBindings
     */
    private final ClassBindings classBindings;

    /**
     * Default constructor: configures the Dummy Creator with vanilla new bindings and caches.
     */
    public DummyCreator() {
	this(new ClassBindings());
    }

    /**
     * Constructor: configures the Dummy Creator with a given {@link ClassBindings} instance and new caches.
     */
    public DummyCreator(ClassBindings classBindings) {
	this.classBindings = classBindings;
	this.constructorCache = new ConstructorCache();
	this.methodCache = new MethodCache();
    }

    /**
     * Main method, creates a dummy object of a given type.
     * <p>
     * Provide your own {@link ClassBindings} in {@link #DummyCreator(ClassBindings)} to control how objects are created for specific types
     * (such as the abstract List class). This is the main-method used to create a dummy of a certain class. It's called with the needed
     * class. e.g. Integer i = createDummyOfClass(Integer.class)
     * 
     * @param <T> The type to be created and returned (returned type can be a sub type of <code>T</code>).
     * @param clazz The type that should be created
     * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured!).
     * @throws IllegalArgumentException Thrown if an abstract type or interface was given for which no binding could be found in the
     *             provided {@link ClassBindings}.
     */
    public <T> T create(final Class<T> clazz) {
	Map<Class<?>, ClassUsageInfo<?>> used_classes = new HashMap<Class<?>, ClassUsageInfo<?>>();
	if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
	    if (classBindings.find(clazz) == null) {
		throw new IllegalArgumentException(String.format("Unable to instantiate object of type [%s] and no binding found for this type", clazz));
	    }
	}
	return create(clazz, used_classes);
    }

    /**
     * Will try to create a new object for the given type, while maintaining a track record of already created - and - populated objects to
     * avoid recursive loops.
     * 
     * @param <T> The type to be created and returned (returned type can be a sub type of <code>T</code>).
     * @param clazz The type that should be created
     * @param knownInstances A list of previously created and populated objects for a specific type.
     * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured).
     * @throws IllegalArgumentException Thrown if class could not be instantiated. Possible constructor invocation exceptions are logged separately.
     */
    @SuppressWarnings("unchecked")
    private <T> T create(final Class<T> clazz, final Map<Class<?>, ClassUsageInfo<?>> knownInstances) {
	// List of Classes, we already used for population. By remembering, we can avoid looping
	if (knownInstances.get(clazz) == null || !knownInstances.get(clazz).isPopulated()) {
	    Object bind = classBindings.find(clazz);
	    if (bind != null && bind.getClass() == clazz) {
		return (T) bind;
	    }

	    T ret = checkPrimitivesAndArray(clazz);
	    // If it was a special, we got ret != null
	    if (ret != null) {
		return ret;
	    }

	    ClassUsageInfo<T> usedInfo = new ClassUsageInfo<T>();
	    usedInfo.setInstance(ret);
	    knownInstances.put(clazz, usedInfo);

	    List<Exception> constructorExceptions = new ArrayList<Exception>();

	    // Try to defer instantiation to a binding, if available
	    ret = findClassBindings(clazz, knownInstances, constructorExceptions);
	    if (ret != null) {
		return ret;
	    }

	    // Do we need to create a string?
	    if (clazz == String.class) {
		return (T) RandomCreator.getRandomString();
	    }

	    // Is the class an enum?
	    if (clazz.isEnum()) {
		T[] enums = clazz.getEnumConstants();
		return enums[RandomCreator.getRandomInt(enums.length - 1)];
	    }
	    // Load the constructors
	    List<Constructor<?>> consts = constructorCache.getCachedConstructors(clazz);
	    if (consts == null) {
		consts = new ArrayList<Constructor<?>>();
		Constructor<?>[] _con = clazz.getConstructors();
		// Sort the constructors by their parameter-count
		java.util.Arrays.sort(_con, new ConstructorComparator());
		consts.addAll(Arrays.asList(_con));
		// Add to cache
		constructorCache.addConstructors(clazz, consts);
	    }

	    // Check if we have a prefered Constructor and try it
	    Constructor<T> preferedConstructor = (Constructor<T>) constructorCache.getPreferedConstructor(clazz);
	    if (preferedConstructor != null) {
		ret = tryConstructor(preferedConstructor, knownInstances, constructorExceptions);
	    }
	    
	    if (ret == null) {
		for (Constructor<?> co : consts) {
		    ret = (T) tryConstructor(co, knownInstances, constructorExceptions);
		    if (ret != null) {
			constructorCache.setPreferedConstructor(clazz, co);
			// Worked
			break;
		    }

		}
	    }
	    if (ret == null) {
		for (Exception e : constructorExceptions) {
		    logger.error("tried but failed to use constructor: ", e);
		}
		throw new IllegalArgumentException(String.format("Could not instantiate object for type [%s]", clazz));
	    }

	    usedInfo.setInstance(ret);
	    usedInfo.setPopulated(true);
	    populateObject(ret, knownInstances);

	    return ret;
	} else {
	    return (T) knownInstances.get(clazz).getInstance();
	}
    }

    /**
     * Tries to instantiate an object with the given constructor.
     */
    private <T> T tryConstructor(final Constructor<T> c, final Map<Class<?>, ClassUsageInfo<?>> knownInstances, List<Exception> constructorExceptions) {
	@SuppressWarnings("unchecked")
	Class<T>[] parameters = (Class<T>[]) c.getParameterTypes();
	try {
	    if (parameters.length > 0) {
		final Object[] params = new Object[parameters.length];
		for (int i = 0; i < params.length; i++) {
		    params[i] = create(parameters[i], knownInstances);
		}
		return c.newInstance(params);
	    } else {
		return c.newInstance();
	    }
	} catch (InvocationTargetException e) {
	    constructorExceptions.add(e);
	} catch (InstantiationException e) {
	    constructorExceptions.add(e);
	} catch (IllegalAccessException e) {
	    constructorExceptions.add(e);
	}
	return null;
    }

    /**
     * Populates given object with dummy value. The behavior can vary depending on the type of object, as follows:
     * <ul>
     * <li><strong>Collection</strong>: if the type is a subtype of {@link Collection}, a random number (1 or 2) of items will be added</li>
     * <li><strong>Map</strong>: if the type is a subtype of {@link Map}, a random number (1 or 2) of key/value entries will be added</li>
     * <li><strong>Other types</strong>: The <em>setter</em> methods will be retrieved from the object and will be invoked with a new dummy
     * value.</li>
     * </ul>
     * <p>
     * <strong>note: </strong> In case of a <code>Collection</code> or <code>Map</code>, the objects created are <code>String</code>
     * instances, unless a generic type can be derived with java reflection.
     * <p>
     * For example, <code>List<Foo></code> or <code>HashMap<Number, String></code> will both result in an instance containing only strings,
     * but a type declared as <code>class FooList extends ArrayList<Foo></code> will result in an instance containing <code>Foo</code>
     * elements. The behavior of the first case is a result of runtime <a
     * href="http://en.wikipedia.org/wiki/Generics_in_Java#Problems_with_type_erasure">type erasure</a>. Only declared generic types in the
     * class or interface signature, as in the latter case, are discoverable in runtime (the latter case).
     * 
     * @param subject The object to populate with dummy values.
     * @param knownInstances A list of known instances to keep track of already processed classes (to avoid infinite loop)
     * @see #create(Class, Map)
     */
    @SuppressWarnings({ "unchecked" })
    private <T> void populateObject(final T subject, final Map<Class<?>, ClassUsageInfo<?>> knownInstances) {
	final Class<?> clazz = subject.getClass();

	if (subject instanceof Collection) {
	    for (int i = 0; i < RandomCreator.getRandomInt(2) + 1; i++) {
		// detect generic declarations
		Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > 0 && (genericTypes[0] instanceof Class)) {
		    // uses generic type if available, Integer for '<T extends List<Integer>>'
		    ((Collection<Object>) subject).add(create((Class<?>) genericTypes[0], knownInstances));
		} else {
		    // use default String value for raw type Collection
		    ((Collection<Object>) subject).add(create(String.class, knownInstances));
		}
	    }
	} else if (subject instanceof Map) {
	    for (int i = 0; i < RandomCreator.getRandomInt(2) + 1; i++) {
		// detect generic declarations
		Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > 0 && (genericTypes[0] instanceof Class)) {
		    // uses generic type if available, String and Integer for '<T extends Map<String, Double>>'
		    ((Map<Object, Object>) subject).put(create((Class<?>) genericTypes[0], knownInstances), create((Class<?>) genericTypes[1], knownInstances));
		} else {
		    // use default String and String value for raw type Collection
		    ((Map<Object, Object>) subject).put(create(String.class, knownInstances), create(String.class, knownInstances));
		}
	    }
	} else {
	    List<Method> setter = discoverSetters(clazz);

	    Object parameter = null;
	    for (Method m : setter) {
		// Load the parameter to pass to this method
		parameter = create(m.getParameterTypes()[0], knownInstances);
		try {
		    m.invoke(subject, parameter);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private List<Method> discoverSetters(final Class<?> clazz) {
	List<Method> setter = methodCache.getSetterForClass(clazz);
	if (setter == null) {
	    setter = new ArrayList<Method>();
	    Map<Class<?>, List<FieldWrapper>> fields = FieldUtils.collectFields(clazz, Object.class, EnumSet.allOf(Visibility.class), EnumSet.of(BeanRestriction.YES_SETTER));
	    for (List<FieldWrapper> fieldWrappers : fields.values()) {
		for (FieldWrapper fieldWrapper : fieldWrappers) {
		    setter.add(fieldWrapper.getSetter());
		}
	    }
	    methodCache.addSetterForClass(clazz, setter);
	}
	return setter;
    }

    @SuppressWarnings("unchecked")
    private <T> T findClassBindings(final Class<T> clazz, final Map<Class<?>, ClassUsageInfo<?>> used_classes, List<Exception> constructorExceptions) {
	Object bind = classBindings.find(clazz);
	if (bind != null) {
	    // Do we have a constructor binding?
	    if (bind instanceof Constructor) {
		return tryConstructor((Constructor<T>) bind, used_classes, constructorExceptions);
	    } else if (bind instanceof Method) {
		Method m = (Method) bind;
		Class<?>[] parameters = m.getParameterTypes();
		final Object[] params = new Object[parameters.length];
		for (int i = 0; i < params.length; i++) {
		    params[i] = create(parameters[i], used_classes);
		}
		try {
		    return (T) m.invoke(null, params);
		} catch (InvocationTargetException e) {
		    logger.debug(String.format("failed to invoke Method [%s] to product an object of type [%s]", m.getName(), clazz), e);
		} catch (IllegalAccessException e) {
		    logger.debug(String.format("failed to invoke Method [%s] to product an object of type [%s]", m.getName(), clazz), e);
		}
		return null;
	    } else if (bind.getClass() == Class.class) {
		return create((Class<? extends T>) bind, used_classes);
	    } else {
		return (T) bind;
	    }
	} else {
	    return null;
	}
    }

    /**
     * This method checks, if the class, we want to create is a special one (primitives and String)
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T checkPrimitivesAndArray(final Class<T> clazz) {
	// Check if we have a primitive or string
	if (clazz.isPrimitive()) {
	    return buildPrimitive(clazz);
	}
	// Do we have an array?
	if (clazz.isArray()) {
	    int length = RandomCreator.getRandomInt(2) + 1;
	    Object parameter = Array.newInstance(clazz.getComponentType(), length);
	    for (int i = 0; i < length; i++) {
		Array.set(parameter, i, create(clazz.getComponentType()));
	    }
	    return (T) parameter;
	}
	return null;
    }

    /**
     * Method to build a primitive dummy.
     * 
     * @see RandomCreator
     * 
     * @param c The primitive type to be created.
     * @return A primitive dummy value.
     */
    @SuppressWarnings("unchecked")
    private static <T> T buildPrimitive(Class<T> c) {
	if (c == (java.lang.Integer.TYPE)) {
	    return (T) (Integer) RandomCreator.getRandomInt();
	} else if (c == (java.lang.Long.TYPE)) {
	    return (T) (Long) RandomCreator.getRandomLong();
	} else if (c == (java.lang.Float.TYPE)) {
	    return (T) (Float) RandomCreator.getRandomFloat();
	} else if (c == (java.lang.Boolean.TYPE)) {
	    return (T) (Boolean) RandomCreator.getRandomBoolean();
	} else if (c == (java.lang.Character.TYPE)) {
	    return (T) (Character) RandomCreator.getRandomChar();
	} else if (c == (java.lang.Byte.TYPE)) {
	    return (T) (Byte) RandomCreator.getRandomByte();
	} else if (c == (java.lang.Short.TYPE)) {
	    return (T) (Short) RandomCreator.getRandomShort();
	} else if (c == (java.lang.Double.TYPE)) {
	    return (T) (Double) RandomCreator.getRandomDouble();
	}
	return null;
    }

    /**
     * Comparator to sort constructors by their number of parameters
     */
    private static class ConstructorComparator implements Comparator<Constructor<?>> {

	public int compare(Constructor<?> o1, Constructor<?> o2) {
	    int num_o1 = o1.getParameterTypes().length;
	    int num_o2 = o2.getParameterTypes().length;
	    if (num_o1 < num_o2) {
		return -1;
	    }
	    if (num_o1 == num_o2) {
		return 0;
	    }
	    return 1;
	}
    }
}
