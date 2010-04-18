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
package de.dev.eth0.dummycreator.binder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 *
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
public class ClassBinder {

    private static final HashMap<Class, Object> bindings = new HashMap<Class, Object>();

    private ClassBinder() {
    }

    public static <T> void bind(final Class<T> _class, final Constructor<T> _constructor) {
        bindings.put(_class, _constructor);
    }

    public static <T> void bind(final Class<T> _interface, final Class<? extends T> _implementation) {
        bindings.put(_interface, _implementation);
    }

    public static <T> void bind(final Class<T> clazz, final Method method) {
        if (Modifier.isStatic(method.getModifiers()) && method.getReturnType().equals(clazz)) {
            bindings.put(clazz, method);
        } else {
            throw new IllegalArgumentException("The method has to be static and return an object of the given class!");
        }
    }

    public static <T> void bind(final Class<T> clazz, final Object object) {
        //Check if the object is a subclass of clazz
        if (clazz.isAssignableFrom(object.getClass())) {
            bindings.put(clazz, object);
        } else {
            throw new IllegalArgumentException("The object has to have a subclass of clazz");
        }
    }

    /**
     * This method returns a binding made for the given class. This binding might be of one of the following type:
     * Constructor
     * Implementation of a Interface
     * Method
     * Object
     * 
     * @param _class
     * @return
     */
    public static Object getBindingForClass(final Class _class) {
        return bindings.get(_class);
    }
}
