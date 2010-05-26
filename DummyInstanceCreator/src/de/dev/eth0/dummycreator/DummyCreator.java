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

import de.dev.eth0.dummycreator.binder.ClassBinder;
import de.dev.eth0.dummycreator.cache.ConstructorCache;
import de.dev.eth0.dummycreator.cache.MethodCache;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

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
     * This is the main-method used to create a dummy of a certain class. It's called with the needed class.
     * e.g. Integer i = createDummyOfClass(Integer.class)
     * @param <T>
     * @param clazz
     * @return
     */
    public static <T> T createDummyOfClass(final Class<T> clazz) {
        Set<Class> used_classes = new HashSet<Class>();
        if (Modifier.isAbstract(clazz.getModifiers()) || Modifier.isInterface(clazz.getModifiers())) {
            if (ClassBinder.getBindingForClass(clazz) == null) {
                throw new IllegalArgumentException("Cant instantiate an abstract class or an interface. Please bind it into ClassBinder");
            }
        }
        return createDummyOfClass(clazz, used_classes);
    }

    /**
     * A helper for the main-method to avoid loops
     * @param <T>
     * @param clazz
     * @param used_classes
     * @return
     */
    private static <T> T createDummyOfClass(final Class<T> clazz, final Set<Class> used_classes) {
        //List of Classes, we already used for population. By remembering, we can avoid looping
        if (!used_classes.contains(clazz)) {
            //Check, if there is an objectbinding for this class
            Object bind = ClassBinder.getBindingForClass(clazz);
            if (bind != null && bind.getClass() == clazz) {
                return (T) bind;
            }

            T ret = checkPrimitivesAndArray(clazz);
            //If it was a special, we got ret != null
            if (ret != null) {
                return ret;
            }
            used_classes.add(clazz);

            //Has this class be bind?
            ret = checkClassBinder(clazz, used_classes);
            if (ret != null) {
                return ret;
            }

            //Do we need to create a string?
            if (clazz == String.class) {
                return (T) RandomCreator.getRandomString();
            }

            //Is the class an enum?
            if (clazz.isEnum()) {
                T[] enums = clazz.getEnumConstants();
                return enums[RandomCreator.getRandomInt(enums.length - 1)];
            }
            //Load the constructors
            List<Constructor> consts = ConstructorCache.getCachedConstructors(clazz);
            if (consts == null) {
                consts = new ArrayList<Constructor>();
                Constructor[] _con = clazz.getConstructors();
                //Sort the constructors by their parameter-count
                java.util.Arrays.sort(_con, new ConstructorComparator());
                consts.addAll(Arrays.asList(_con));
                //Add to cache
                ConstructorCache.addConstructors(clazz, consts);
            }
            //Check if we have a prefered Constructor and try it
            Constructor<T> preferedConstructor = ConstructorCache.getPreferedConstructor(clazz);
            if (preferedConstructor != null) {

                ret = tryConstructor(preferedConstructor, used_classes);
            }
            if (ret == null) {
                for (Constructor<T> co : consts) {
                    ret = tryConstructor(co, used_classes);
                    if (ret != null) {
                        ConstructorCache.setPreferedConstructor(clazz, co);
                        //Worked
                        break;
                    }

                }
            }
            if (ret == null) {
                throw new IllegalArgumentException("The given class couldn't be instantiated");
            }

            populateObject(ret, clazz, used_classes);

            return ret;
        } //If this class has already be used for creation
        return null;

    }

    /**
     * A method, which tries to instanciate an object with the given constructor
     * @param <T>
     * @param c
     * @param used_classes
     * @return
     */
    private static <T> T tryConstructor(final Constructor<T> c, final Set<Class> used_classes) {
        Class<T>[] parameters = (Class<T>[]) c.getParameterTypes();
        try {
            if (parameters.length > 0) {
                final Object[] params = new Object[parameters.length];
                for (int i = 0; i < params.length; i++) {
                    params[i] = createDummyOfClass(parameters[i], used_classes);
                }
                return (T) c.newInstance(params);
            } else {
                return (T) c.newInstance();
            }
        } catch (InvocationTargetException ite) {
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
     * After the creation of an object, we need to populate it's attributes. This is done here
     * @param ret
     * @param clazz
     * @param used_classes
     */
    private static void populateObject(final Object ret, final Class clazz, final Set<Class> used_classes) {
        List<Method> setter = MethodCache.getSetterForClass(clazz);
        if (setter == null) {
            setter = new ArrayList<Method>();
            for (Method m : clazz.getMethods()) {
                if (isSetter(m)) {
                    setter.add(m);
                }
            }
            MethodCache.addSetterForClass(clazz, setter);
        }

        Object parameter = null;
        for (Method m : setter) {
            //Load the parameter to pass to this method
            parameter = createDummyOfClass(m.getParameterTypes()[0], used_classes);
            try {
                m.invoke(ret, parameter);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static <T> T checkClassBinder(final Class<T> clazz, final Set<Class> used_classes) {
        Object bind = ClassBinder.getBindingForClass(clazz);
        T ret = null;
        if (bind != null) {
            //Do we have a constructor binding?
            if (bind instanceof Constructor) {
                ret = tryConstructor((Constructor<T>) bind, used_classes);
                return ret;
            } else //Was this class bind to a method?
            if (bind instanceof Method) {
                Method m = (Method) bind;
                Class[] parameters = m.getParameterTypes();
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
            } else //Was this an interface?
            if (clazz.isInterface()) {
                //Load class to use
                Class<? extends T> c = (Class<? extends T>) bind;
                return createDummyOfClass(c, used_classes);
            }
        }
        return ret;
    }

    /**
     * This method checks, if the class, we want to create is a special one (primitives and String)
     * @param <T>
     * @param clazz
     * @return
     */
    private static <T> T checkPrimitivesAndArray(final Class<T> clazz) {
        //Check  if we have a primitive or string
        if (clazz.isPrimitive()) {
            return (T) buildPrimitive(clazz);
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
        if (c ==(java.lang.Integer.TYPE)) {
            return RandomCreator.getRandomInt();
        } else if (c ==(java.lang.Long.TYPE)) {
            return RandomCreator.getRandomLong();
        } else if (c ==(java.lang.Float.TYPE)) {
            return RandomCreator.getRandomFloat();
        } else if (c ==(java.lang.Boolean.TYPE)) {
            return RandomCreator.getRandomBoolean();
        } else if (c ==(java.lang.Character.TYPE)) {
            return RandomCreator.getRandomChar();
        } else if (c ==(java.lang.Byte.TYPE)) {
            return RandomCreator.getRandomByte();
        } else if (c ==(java.lang.Short.TYPE)) {
            return RandomCreator.getRandomShort();
        } else if (c ==(java.lang.Double.TYPE)) {
            return RandomCreator.getRandomDouble();
        }
        return null;
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
}
