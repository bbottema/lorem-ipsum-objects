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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Stores a list of classes / interfaces and their associated deferred types. This list is used to tell {@link DummyCreator} which specific
 * implementation it should use to product new dummy instances for a certain type.
 * <p>
 * Classes can be used as deferred types, but the following deferred types are allowed:
 * <ul>
 * <li>Class references: {@link #add(Class, Class)}</li>
 * <li>methods with a return type: {@link #add(Class, Method)}</li>
 * <li>constructors: {@link #add(Class, Constructor)}</li>
 * <li>instance objects: {@link #add(Class, Object)}</li>
 * </ul>
 * 
 * Examples are:
 * <ul>
 * <li>List -> ArrayList</li>
 * <li>List -> LinkedList</li>
 * <li>Integer -> 4443 (which is autoboxed to an Integer)</li>
 * <li>Foo -> FooFactory.class.getMethod('createFoo')</li>
 * <li>Apple -> Apple.class.getConstructor(String.class)</li>
 * </ul>
 */
public class ClassBindings {

    /**
     * The list with bindings
     */
    private final HashMap<Class<?>, Object> bindings = new HashMap<Class<?>, Object>();

    public <T> void add(final Class<T> clazz, final Constructor<? extends T> constructor) {
	bindings.put(clazz, constructor);
    }

    public <T> void add(final Class<T> clazz, final Class<? extends T> deferredSubtype) {
	bindings.put(clazz, deferredSubtype);
    }

    public <T> void add(final Class<T> clazz, final Method method) {
	if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().equals(clazz)) {
	    bindings.put(clazz, method);
	} else {
	    throw new IllegalArgumentException("The method has to be static and return an object of the given class!");
	}
    }

    public <T> void add(final Class<T> clazz, final Object object) {
	if (clazz.isAssignableFrom(object.getClass())) {
	    bindings.put(clazz, object);
	} else {
	    throw new IllegalArgumentException("The object has to have a subclass of clazz");
	}
    }

    /**
     * This method returns a binding made for the given class. This binding might be of one of the following type: Constructor
     * Implementation of a Interface Method Object
     * 
     * @param _class
     * @return
     */
    public Object getBindingForClass(final Class<?> _class) {
	return bindings.get(_class);
    }

    /**
     * You can call this method to build some default bindings for common classes. This includes List.class, Map.class, Set.class
     */
    public static ClassBindings defaultBindings() {
	ClassBindings classBindings = new ClassBindings();
	classBindings.add(List.class, ArrayList.class);
	classBindings.add(Map.class, HashMap.class);
	classBindings.add(Set.class, HashSet.class);
	return classBindings;
    }
}