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

package de.dev.eth0.dummycreator.test;

import de.dev.eth0.dummycreator.DummyCreator;
import de.dev.eth0.dummycreator.binder.InterfaceBinder;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
public class Main {

    public static void main(String[] args) {
        try {
            System.out.println();
            System.out.println("Integer");
            System.out.println(DummyCreator.createDummyOfClass(Integer.class));

            System.out.println();
            System.out.println("String");
            System.out.println(DummyCreator.createDummyOfClass(String.class));

            System.out.println();
            System.out.println("Byte");
            System.out.println(DummyCreator.createDummyOfClass(Byte.class));

            System.out.println();
            System.out.println("Boolean");
            System.out.println(DummyCreator.createDummyOfClass(Boolean.class));

            System.out.println();
            System.out.println("Long");
            System.out.println(DummyCreator.createDummyOfClass(Long.class));

            System.out.println();
            System.out.println("Short");
            System.out.println(DummyCreator.createDummyOfClass(Short.class));

            System.out.println();
            System.out.println("Double");
            System.out.println(DummyCreator.createDummyOfClass(Double.class));

            System.out.println();
            System.out.println("Float");
            System.out.println(DummyCreator.createDummyOfClass(Float.class));

            System.out.println();
            System.out.println("Character");
            System.out.println(DummyCreator.createDummyOfClass(Character.class));

            System.out.println();
            System.out.println("Simple Object");
            testDummyCreation(PrimitiveClass.class);

            System.out.println();
            System.out.println("Inherited Simple Object");
            testDummyCreation(InheritedPrimitiveClass.class);

            System.out.println();
            System.out.println("Normal Object");
            testDummyCreation(NormalClass.class);

            System.out.println();
            System.out.println("Loop Object");
            testDummyCreation(LoopClass.class);

            System.out.println();
            System.out.println("Array Object");
            testDummyCreation(ArrayClass.class);

            System.out.println();
            System.out.println("Array of PrimitiveClass Object");
            testDummyCreation(ArrayWithObjectsClass.class);

            System.out.println();
            System.out.println("Array List Object");
            //testDummyCreation(ArrayListClass.class);
            ArrayListClass alc = DummyCreator.createDummyOfClass(ArrayListClass.class);
            System.out.println(alc.getMyList());
            for (String s : alc.getMyList()) {
                System.out.println(s);
            }

            InterfaceBinder.bind(List.class, LinkedList.class);

            System.out.println();
            System.out.println("List Object");
            //testDummyCreation(ListClass.class);

            ListClass lc = DummyCreator.createDummyOfClass(ListClass.class);
            System.out.println(lc.getMyList());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testDummyCreation(Class clazz) throws Exception {
        Object t = DummyCreator.createDummyOfClass(clazz);
        printInfo(t, clazz);
    }

    private static <T> void printInfo(T object, Class clazz) throws Exception {
        if (object != null) {
            for (Method m : clazz.getMethods()) {
                if (m.getName().startsWith("get") && !m.getName().startsWith("getClass")) {
                    if (m.getParameterTypes().length == 0) {
                        Object o = m.invoke(object);
                        if (m.getReturnType().isPrimitive() || m.getReturnType().equals(String.class)) {
                            System.out.println(m.getName() + " : " + m.invoke(object));
                        } else if (m.getReturnType().isArray()) {
                            for (int i = 0; i < Array.getLength(o); i++) {
                                System.out.println(i + ": " + Array.get(o, i));
                                printInfo(Array.get(o, i), m.getReturnType().getComponentType());
                            }
                        } else { //Have we already populated the class we need as parameter?
                            System.out.println("Sub-Object : " + m.getReturnType());
                            printInfo(o, m.getReturnType());
                        }
                    }
                }
            }
        }
    }
}
