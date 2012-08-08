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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;


/**
 * 
 * 
 * This is the main-class of the dummycreator, which contains only static methods
 * 
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>, Benny Bottema <b.bottema@projectnibble.org>
 */
public class DummyCreator {

    private final ConstructorCache constructorCache;
    
    private final MethodCache methodCache;

    public DummyCreator() {
	this(new ConstructorCache(), new MethodCache());
    }

    public DummyCreator(ConstructorCache constructorCache, MethodCache methodCache) {
	this.constructorCache = constructorCache;
	this.methodCache = methodCache;
    }

    /**
     * This is the main-method used to create a dummy of a certain class. It's called with the needed class. e.g. Integer i =
     * createDummyOfClass(Integer.class)
     * 
     * @param <T>
     * @param clazz
     * @return
     */
    public <T> T createDummyOfClass(final Class<T> clazz) {
	Map<Class<?>, ClassUsageInfo<?>> used_classes = new HashMap<Class<?>, ClassUsageInfo<?>>();
	if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
	    if (ClassBinder.getBindingForClass(clazz) == null) {
		throw new IllegalArgumentException("Cant instantiate an abstract class or an interface. Please bind it into ClassBinder");
	    }
	}
	return createDummyOfClass(clazz, used_classes);
    }

    /**
     * A helper for the main-method to avoid loops
     * 
     * @param <T>
     * @param clazz
     * @param used_classes
     * @return
     */
    @SuppressWarnings("unchecked")
    private <T> T createDummyOfClass(final Class<T> clazz, final Map<Class<?>, ClassUsageInfo<?>> used_classes) {
	// List of Classes, we already used for population. By remembering, we can avoid looping
	if (used_classes.get(clazz) == null || !used_classes.get(clazz).isPopulated()) {
	    // Check, if there is an objectbinding for this class
	    Object bind = ClassBinder.getBindingForClass(clazz);
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
	    used_classes.put(clazz, usedInfo);

	    // Has this class be bind?
	    ret = checkClassBinder(clazz, used_classes);
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
		ret = tryConstructor(preferedConstructor, used_classes);
	    }
	    if (ret == null) {
		for (Constructor<?> co : consts) {
		    ret = (T) tryConstructor(co, used_classes);
		    if (ret != null) {
			constructorCache.setPreferedConstructor(clazz, co);
			// Worked
			break;
		    }

		}
	    }
	    if (ret == null) {
		throw new IllegalArgumentException("The given class couldn't be instantiated");
	    }

	    usedInfo.setInstance(ret);
	    usedInfo.setPopulated(true);
	    populateObject(ret, used_classes);

	    return ret;
	} else {
	    return (T) used_classes.get(clazz).getInstance();
	}
    }

    /**
     * A method, which tries to instanciate an object with the given constructor
     * 
     * @param <T>
     * @param c
     * @param used_classes
     * @return
     */
    private <T> T tryConstructor(final Constructor<T> c, final Map<Class<?>, ClassUsageInfo<?>> used_classes) {
	@SuppressWarnings("unchecked")
	Class<T>[] parameters = (Class<T>[]) c.getParameterTypes();
	try {
	    if (parameters.length > 0) {
		final Object[] params = new Object[parameters.length];
		for (int i = 0; i < params.length; i++) {
		    params[i] = createDummyOfClass(parameters[i], used_classes);
		}
		return c.newInstance(params);
	    } else {
		return c.newInstance();
	    }
	} catch (InvocationTargetException ite) {
	    // ite.printStackTrace();
	} catch (InstantiationException ie) {
	    // ie.printStackTrace();
	} catch (IllegalAccessException iae) {
	    // iae.printStackTrace();
	}
	// If this didn't work, we return null
	return null;
    }

    /**
     * Populates given object with dummy value. The behavior can vary depending on the type of object, as follows:
     * <ul>
     * <li><strong>Collection</strong>: if the type is a subtype of {@link Collection}, a random number of items, 1 or 2, will be added</li>
     * <li><strong>Map</strong>: if the type is a subtype of {@link Map}, a random number of key/value entries, 1 or 2, will be added</li>
     * <li><strong>Other types</strong>: The <em>setter</em> methods will be retrieved from the object and will be invoked with a new dummy
     * value.</li>
     * </ul>
     * 
     * @param subject The object to populate with dummy values.
     * @param knownInstances A list of known instances to keep track of already processed classes (to avoid infinite loop)
     * @see #createDummyOfClass(Class, Map)
     */
    @SuppressWarnings({ "unchecked" })
    private <T> void populateObject(final T subject, final Map<Class<?>, ClassUsageInfo<?>> knownInstances) {
	final Class<?> clazz = subject.getClass();

	if (subject instanceof Collection) {
	    for (int i = 0; i < getRandomArrayLength(2); i++) {
		// detect generic declarations
		Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > 0 && (genericTypes[0] instanceof Class)) {
		    // uses generic type if available, Integer for '<T extends List<Integer>>'
		    ((Collection<Object>) subject).add(createDummyOfClass((Class<?>) genericTypes[0], knownInstances));
		} else {
		    // use default String value for raw type Collection
		    ((Collection<Object>) subject).add(createDummyOfClass(String.class, knownInstances));
		}
	    }
	} else if (subject instanceof Map) {
	    for (int i = 0; i < getRandomArrayLength(2); i++) {
		// detect generic declarations
		Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > 0 && (genericTypes[0] instanceof Class)) {
		    // uses generic type if available, String and Integer for '<T extends Map<String, Double>>'
		    ((Map<Object, Object>) subject).put(createDummyOfClass((Class<?>) genericTypes[0], knownInstances), createDummyOfClass((Class<?>) genericTypes[1], knownInstances));
		} else {
		    // use default String and String value for raw type Collection
		    ((Map<Object, Object>) subject).put(createDummyOfClass(String.class, knownInstances), createDummyOfClass(String.class, knownInstances));
		}
	    }
	} else {
	    List<Method> setter = methodCache.getSetterForClass(clazz);
	    if (setter == null) {
		setter = new ArrayList<Method>();
		for (Method m : clazz.getMethods()) {
		    if (isSetter(m)) {
			setter.add(m);
		    }
		}
		methodCache.addSetterForClass(clazz, setter);
	    }

	    Object parameter = null;
	    for (Method m : setter) {
		// Load the parameter to pass to this method
		parameter = createDummyOfClass(m.getParameterTypes()[0], knownInstances);
		try {
		    m.invoke(subject, parameter);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    @SuppressWarnings("unchecked")
    private <T> T checkClassBinder(final Class<T> clazz, final Map<Class<?>, ClassUsageInfo<?>> used_classes) {
	Object bind = ClassBinder.getBindingForClass(clazz);
	T ret = null;
	if (bind != null) {
	    // Do we have a constructor binding?
	    if (bind instanceof Constructor) {
		ret = tryConstructor((Constructor<T>) bind, used_classes);
		return ret;
	    } else // Was this class bind to a method?
	    if (bind instanceof Method) {
		Method m = (Method) bind;
		Class<?>[] parameters = m.getParameterTypes();
		final Object[] params = new Object[parameters.length];
		for (int i = 0; i < params.length; i++) {
		    params[i] = createDummyOfClass(parameters[i], used_classes);
		}
		try {
		    return (T) m.invoke(null, params);
		} catch (InvocationTargetException ite) {
		    // ite.printStackTrace();
		} catch (IllegalAccessException iae) {
		    // iae.printStackTrace();
		}
	    } else // Was this an interface?
	    if (clazz.isInterface()) {
		// Load class to use
		Class<? extends T> c = (Class<? extends T>) bind;
		return createDummyOfClass(c, used_classes);
	    }
	}
	return ret;
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
	    int length = getRandomArrayLength(20);
	    Object parameter = Array.newInstance(clazz.getComponentType(), length);
	    for (int i = 0; i < length; i++) {
		Array.set(parameter, i, createDummyOfClass(clazz.getComponentType()));
	    }
	    return (T) parameter;
	}
	return null;
    }

    /**
     * Method to check, if the given method is a Setter. A setter is defined by the name setXXXX and by having only one parameter
     * 
     * @param method
     * @return
     */
    private boolean isSetter(Method method) {
	if (!method.getName().startsWith("set")) {
	    return false;
	}
	if (method.getParameterTypes().length != 1) {
	    return false;
	}
	return true;
    }

    /**
     * Method to build a primitive
     * 
     * @param c
     * @return
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
     * Method to generate a random length for an array
     * 
     * @param max TODO
     * @return
     */
    private static int getRandomArrayLength(int max) {
	return new Random().nextInt(max) + 1;
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
