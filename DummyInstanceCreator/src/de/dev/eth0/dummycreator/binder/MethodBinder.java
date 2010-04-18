/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dev.eth0.dummycreator.binder;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;

/**
 *
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
public class MethodBinder {
// private static final InterfaceBinder _ifbinder = new InterfaceBinder();

    private static final HashMap<Class, Method> bindings = new HashMap<Class, Method>();

    private MethodBinder() {
    }

    public static void bind(final Class clazz, final Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            bindings.put(clazz, method);
        } else {
            throw new IllegalArgumentException("The method has to be static!");
        }
    }

    public static Method getMethodForClassCreation(final Class clazz) {
        return bindings.get(clazz);
    }
}
