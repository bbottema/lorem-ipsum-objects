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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

public class DummyCreatorTest {

    private static final DummyCreator DUMMY_CREATOR = new DummyCreator();

    @BeforeClass
    public static void setUp() {
	try {
	    ClassBinder.bind(List.class, ArrayList.class);
	    ClassBinder.bind(Integer.class, Integer.class.getConstructor(Integer.TYPE));
	    ClassBinder.bind(Long.class, Long.MAX_VALUE);
	    ClassBinder.bind(Double.class, Double.MIN_VALUE);
	} catch (NoSuchMethodException nsme) {
	}
    }

    @Test
    public void CheckObjectBindings() throws Exception {
	System.out.println("CheckInterfaceBindings");
	System.out.println("String");
	assertEquals(Long.MAX_VALUE, DUMMY_CREATOR.createDummyOfClass(Long.class), 0);

	System.out.println();
	System.out.println("Boolean");
	assertEquals(Double.MIN_VALUE, DUMMY_CREATOR.createDummyOfClass(Double.class), 0);
    }

    @Test
    public void CheckMethodBindings() throws Exception {
    }

    @Test
    public void CheckConstructorBindings() throws Exception {
	System.out.println("CheckConstructorBindings");
	System.out.println("Integer");
	assertEquals(Integer.class, DUMMY_CREATOR.createDummyOfClass(Integer.class).getClass());
    }

    @Test
    public void CheckInterfaceBindings() throws Exception {
	System.out.println("CheckInterfaceBindings");
	System.out.println("ArrayList");
	assertEquals(ArrayList.class, DUMMY_CREATOR.createDummyOfClass(List.class).getClass());
	System.out.println("Override Binding");
	ClassBinder.bind(List.class, LinkedList.class);
	assertEquals(LinkedList.class, DUMMY_CREATOR.createDummyOfClass(List.class).getClass());
    }

    @Test
    public void CheckStringCreation() {
	System.out.println("CheckPrimitiveCreation");
	System.out.println("String");
	String dummy = DUMMY_CREATOR.createDummyOfClass(String.class);
	System.out.println(dummy);
	assertEquals(String.class, dummy.getClass());
    }

    @Test
    public void CheckSimpleObjectCreation() {
	System.out.println("CheckSimpleObjectCreation");
	System.out.println("Byte");
	assertEquals(Byte.class, DUMMY_CREATOR.createDummyOfClass(Byte.class).getClass());
	System.out.println();
	System.out.println("Long");
	assertEquals(Long.class, DUMMY_CREATOR.createDummyOfClass(Long.class).getClass());
    }

    @Test
    public void CheckPrimitiveClassCreation() {
	System.out.println("CheckPrimitiveClassCreation");
	System.out.println("Primitive Class");
	assertEquals(PrimitiveClass.class, DUMMY_CREATOR.createDummyOfClass(PrimitiveClass.class).getClass());

	System.out.println();
	System.out.println("Inherited Primitive Object");
	assertEquals(InheritedPrimitiveClass.class, DUMMY_CREATOR.createDummyOfClass(InheritedPrimitiveClass.class).getClass());

	// TODO Check if all parameters have been set
    }

    @Test
    public void CheckNormalClassCreation() {
	System.out.println("CheckNormalClassCreation");
	System.out.println("Normal Class");
	assertEquals(NormalClass.class, DUMMY_CREATOR.createDummyOfClass(NormalClass.class).getClass());
	// TODO Check if all parameters have been set
    }

    @Test
    public void CheckLoopClassCreation() {
	System.out.println("CheckLoopClassCreation");
	System.out.println("Loop Class");
	assertEquals(LoopClass.class, DUMMY_CREATOR.createDummyOfClass(LoopClass.class).getClass());
    }

    @Test
    public void CheckMultiConstructorClassCreation() {
	System.out.println("CheckMultiConstructorClassCreation");
	System.out.println("MultiConstructor Class");
	assertEquals(MultiConstructorClass.class, DUMMY_CREATOR.createDummyOfClass(MultiConstructorClass.class).getClass());
    }

    @Test
    public void CheckEnumClassCreation() {
	System.out.println("CheckEnumClassCreation");
	System.out.println("Enum Class");
	EnumClass ec = DUMMY_CREATOR.createDummyOfClass(EnumClass.class);
	assertNotNull(ec.getEnumTester());
	assertNotNull(ec.getInternalEnum());
    }

    @SuppressWarnings("serial")
    public static class NumberStringMap extends HashMap<Integer, String> {
    };

    @Test
    public void checkGenericMap() {
	Map<Integer, String> numberStringMap = new NumberStringMap();
	Map<?, ?> ec = DUMMY_CREATOR.createDummyOfClass(numberStringMap.getClass());
	assertNotNull(ec);
    }

    @SuppressWarnings("serial")
    public static class NumberStringList extends ArrayList<Integer> {
    };

    @Test
    public void checkGenericList() {
	List<Integer> numbers = new NumberStringList();
	List<?> ec = DUMMY_CREATOR.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void checkList() {
	List<Integer> numbers = new ArrayList<Integer>();
	List<?> ec = DUMMY_CREATOR.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void checkMooooList() {
	List<MyCustomTestClass> numbers = new MyCustomTestClassList();
	List<?> ec = DUMMY_CREATOR.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void checkMap() {
	Map<Integer, String> numbers = new HashMap<Integer, String>();
	Map<?, ?> ec = DUMMY_CREATOR.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void CheckSomethingElse() throws Exception {
	System.out.println();
	System.out.println("Array Object");
	testDummyCreation(ArrayClass.class);

	System.out.println();
	System.out.println("Array of PrimitiveClass Object");
	testDummyCreation(ArrayWithObjectsClass.class);

	System.out.println();
	System.out.println("Array List Object");
	// testDummyCreation(ArrayListClass.class);
	ArrayListClass alc = DUMMY_CREATOR.createDummyOfClass(ArrayListClass.class);
	System.out.println(alc.getMyList());
	for (String s : alc.getMyList()) {
	    System.out.println(s);
	}

	System.out.println();
	System.out.println("List Object");
	// testDummyCreation(ListClass.class);

	ListClass lc = DUMMY_CREATOR.createDummyOfClass(ListClass.class);
	System.out.println(lc.getMyList());
    }

    // @Test
    public void Benchmark() throws Exception {
	System.out.println();
	System.out.println("Benchmarking");
	System.out.println("Primitive Class");
	long t = System.currentTimeMillis();
	for (int i = 0; i < 1000000; i++) {
	    DUMMY_CREATOR.createDummyOfClass(PrimitiveClass.class);
	}
	System.out.println("Time: " + (System.currentTimeMillis() - t));
	System.out.println("Loop Class ");
	t = System.currentTimeMillis();
	for (int i = 0; i < 1000000; i++) {
	    DUMMY_CREATOR.createDummyOfClass(LoopClass.class);
	}
	System.out.println("Time: " + (System.currentTimeMillis() - t));
	System.out.println("MultiConstructor Class ");
	t = System.currentTimeMillis();
	for (int i = 0; i < 1000000; i++) {
	    DUMMY_CREATOR.createDummyOfClass(MultiConstructorClass.class);
	}
	System.out.println("Time: " + (System.currentTimeMillis() - t));
    }

    private static void testDummyCreation(Class<?> clazz) throws Exception {
	Object t = DUMMY_CREATOR.createDummyOfClass(clazz);
	printInfo(t, clazz);
    }

    private static <T> void printInfo(T object, Class<?> clazz) throws Exception {
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
			} else { // Have we already populated the class we need as parameter?
			    System.out.println("Sub-Object : " + m.getReturnType());
			    printInfo(o, m.getReturnType());
			}
		    }
		}
	    }
	}
    }
}
