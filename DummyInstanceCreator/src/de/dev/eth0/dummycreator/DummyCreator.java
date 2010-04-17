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
package de.dev.eth0.dummycreator;

import de.dev.eth0.dummycreator.binder.ConstructorBinder;
import de.dev.eth0.dummycreator.binder.InterfaceBinder;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
//TODO: Generische Objekte erstellen
//TODO: Es wäre cool, wenn man eine Methode übergeben kann, mit der man bestimmte Klassen erstellen lassen möchte
//TODO: Es gibt noch einige unchecked conversion oder cast warnings

/**
 * This is the main-class of the dummycreator, which contains only static methods
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
public class DummyCreator {

    /**
     * Hide the constructor
     */
    private DummyCreator() {
    }

    /**
     * This is the main-method, we use to create a dummy-class. It's called with the needed class.
     * e.g. Integer i = createDummyOfClass(Integer.class)
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> T createDummyOfClass(final Class<T> clazz) {
        List<Class> used_classes = new ArrayList<Class>();
        return createDummyOfClass(clazz, used_classes);
    }

    /**
     * A helper for the main-method to avoid loops
     * @param <T>
     * @param clazz
     * @param used_classes
     * @return
     */
    private static <T> T createDummyOfClass(final Class<T> clazz, final List<Class> used_classes) {
        T ret = checkSpecials(clazz);
        //If it was a special, we got ret != null
        if (ret != null) {
            return ret;
        }
        //Is it an interface?
        if (clazz.isInterface()) {
            //Load class to use
            Class<? extends T> c = InterfaceBinder.getImplementationOfInterface(clazz);
            //Did we get an implementation of the interface?
            if (c != null) {
                return createDummyOfClass(c, used_classes);
            } //Else we have a problem... TODO: What to do??
            else {
                return null;
            }
        } //Check if we have a given constructor
        Constructor<T> c = ConstructorBinder.getConstructorOfClass(clazz);
        if (c != null) {
            ret = tryConstructor(c, used_classes);
        } else {
            //Sort the constructors by there parameter-count
            final Constructor[] consts = clazz.getConstructors();
            java.util.Arrays.sort(consts, new ConstructorComparator());
            //Try every constructor, begin with the one, with the least parameters
            for (Constructor<T> co : consts) {
                ret = tryConstructor(co, used_classes);
                if (ret != null) {
                    //Worked
                    break;
                }
            }
            populateObject(ret, clazz, used_classes);
        }
        return ret;
    }

    /**
     * A method, which tries to instanciate an object with the given constructor
     * @param <T>
     * @param c
     * @param used_classes
     * @return
     */
    private static <T> T tryConstructor(final Constructor<T> c, final List<Class> used_classes) {
        Class<T>[] parameters = (Class<T>[]) c.getParameterTypes();
        final Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            params[i] = createDummyOfClass(parameters[i], used_classes);
        }
        try {
            T ret = (T) c.newInstance(params);
            //If it worked, we can break
            return ret;

        } //TODO: what to do, if we get an exception?
        catch (InvocationTargetException ite) {
            // ite.printStackTrace();
        } catch (InstantiationException ie) {
            // ie.printStackTrace();
        } catch (IllegalAccessException iae) {
            // iae.printStackTrace();
        }
        //If this didn't work, we return null
        return null;
    }

    /**
     * When we created an object, we need to populate it's attributes. this is done here
     * @param ret
     * @param clazz
     * @param used_classes
     */
    private static void populateObject(final Object ret, final Class clazz, final List<Class> used_classes) {
        //List of Classes, we already used for population. By remembering, we can avoid looping
        used_classes.add(clazz);
        for (Method m : clazz.getMethods()) {
            if (isSetter(m)) {
                final Class c = m.getParameterTypes()[0];
                //The parameter we will pass later to the method
                Object parameter = null;
                //We didn't use this class already
                if (!used_classes.contains(c)) {
                    parameter = createDummyOfClass(m.getParameterTypes()[0]);
                }
                try {
                    m.invoke(ret, parameter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method checks, if the class, we want to create is a special one (primitives and String)
     * @param <T>
     * @param clazz
     * @return
     */
    private static <T> T checkSpecials(final Class<T> clazz) {
        //Check  if we have a primitive or string
        if (clazz.isPrimitive()) {
            return (T) buildPrimitive(clazz);
        }
        if (clazz.equals(String.class)) {
            return (T) RandomCreator.getRandomString();
        }
        //Do we have an array?
        if (clazz.isArray()) {
            int length = getRandomArrayLength();
            Object parameter = Array.newInstance(clazz.getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(parameter, i, createDummyOfClass(clazz.getComponentType()));
            }
            return (T) parameter;
        }
        return null;
    }

    /**
     * Method to check, if the given method is a Setter.
     * A setter is defined by the name setXXXX and by having only one parameter
     * @param method
     * @return
     */
    private static boolean isSetter(Method method) {
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
     * @param c
     * @return
     */
    private static Object buildPrimitive(Class c) {
        Object parameter = null;
        if (c.equals(java.lang.Boolean.TYPE)) {
            parameter = RandomCreator.getRandomBoolean();
        } else if (c.equals(java.lang.Character.TYPE)) {
            parameter = RandomCreator.getRandomChar();
        } else if (c.equals(java.lang.Byte.TYPE)) {
            parameter = RandomCreator.getRandomByte();
        } else if (c.equals(java.lang.Short.TYPE)) {
            parameter = RandomCreator.getRandomShort();
        } else if (c.equals(java.lang.Integer.TYPE)) {
            parameter = RandomCreator.getRandomInt();
        } else if (c.equals(java.lang.Long.TYPE)) {
            parameter = RandomCreator.getRandomLong();
        } else if (c.equals(java.lang.Float.TYPE)) {
            parameter = RandomCreator.getRandomFloat();
        } else if (c.equals(java.lang.Double.TYPE)) {
            parameter = RandomCreator.getRandomDouble();
        }
        return parameter;
    }

    /**
     * Method to generate a random length for an array
     * @return
     */
    private static int getRandomArrayLength() {
        Random rand = new Random();
        return rand.nextInt(20) + 1;
    }

    /**
     * Comparator to sort constructors by their number of parameters
     */
    private static class ConstructorComparator implements Comparator<Constructor> {

        public int compare(Constructor o1, Constructor o2) {
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
    //<editor-fold desc="Old Code">
    /**
    if (c.isArray()) {
    int length = getRandomArrayLength();
    parameter = Array.newInstance(c.getComponentType(), length);
    for (int i = 0; i < length; i++) {
    Array.set(parameter, i, createDummyOfClass(c.getComponentType()));
    }
    } else {
    //Check if we want to create a list
    boolean list = false;
    for (Class _c : c.getInterfaces()) {
    if (_c.equals(List.class)) {
    list = true;
    break;
    }
    }
    if (list) {
    //Build a list
    List tmp = createDummyOfClass(ArrayList.class);
    //Check if we have a generic list
    Type genericParameterType = m.getGenericParameterTypes()[0];
    if (genericParameterType instanceof ParameterizedType) {
    //If the list is generic, we can create objects of its type
    ParameterizedType type = (ParameterizedType) genericParameterType;
    Class parameterArgClass = (Class) type.getActualTypeArguments()[0];
    for (int i = 0; i < getRandomArrayLength(); i++) {
    tmp.add(createDummyOfClass(parameterArgClass));
    }
    }
    parameter = tmp;
    } //Have we already populated the class we need as parameter?
    else if (!usedClasses.contains(m.getParameterTypes()[0])) {
    parameter = createDummyOfClass(m.getParameterTypes()[0]);
    usedClasses.add(m.getParameterTypes()[0]);
    } else {
    parameter = null;
    }
    }

    }
    }
    }
     **/
    //</editor-fold>
}
