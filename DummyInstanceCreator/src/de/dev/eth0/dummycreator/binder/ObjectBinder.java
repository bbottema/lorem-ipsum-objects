/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.dev.eth0.dummycreator.binder;

import java.util.HashMap;

/**
 *
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
public class ObjectBinder {

    private static final HashMap<Class, Object> bindings = new HashMap<Class, Object>();

    private ObjectBinder() {
    }

    public static <T> void bind(final Class<T> clazz, final Object object) {
        //Check if the object is a subclass of clazz
        if (clazz.isAssignableFrom(object.getClass())) {
            bindings.put(clazz, object);
        } else {
            throw new IllegalArgumentException("The object has to have a subclass of clazz");
        }
    }

    public static <T> T getMethodForClassCreation(final Class<T> clazz) {
        return (T) bindings.get(clazz);
    }
}
