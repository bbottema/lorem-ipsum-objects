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
import static org.junit.Assert.assertSame;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dummycreator.TestChainBinding.B;
import org.dummycreator.TestChainBinding.C;
import org.junit.Before;
import org.junit.Test;

public class DummyCreatorTest {

    private DummyCreator dummyCreator;

    @Before
    public void setUp() throws SecurityException, NoSuchMethodException {
	ClassBindings classBinder = ClassBindings.defaultBindings();
	classBinder.add(List.class, ArrayList.class);
	classBinder.add(Integer.class, Integer.class.getConstructor(Integer.TYPE));
	classBinder.add(Long.class, Long.MAX_VALUE);
	classBinder.add(Double.class, Double.MIN_VALUE);
	dummyCreator = new DummyCreator(classBinder);
    }

    @Test
    public void CheckObjectBindings() throws Exception {
	System.out.println("CheckInterfaceBindings");
	System.out.println("String");
	assertEquals(Long.MAX_VALUE, dummyCreator.createDummyOfClass(Long.class), 0);

	System.out.println();
	System.out.println("Boolean");
	assertEquals(Double.MIN_VALUE, dummyCreator.createDummyOfClass(Double.class), 0);
    }

    @Test
    public void CheckMethodBindings() throws Exception {
    }

    @Test
    public void CheckConstructorBindings() throws Exception {
	System.out.println("CheckConstructorBindings");
	System.out.println("Integer");
	assertEquals(Integer.class, dummyCreator.createDummyOfClass(Integer.class).getClass());
    }

    @Test
    public void CheckInterfaceBindings() throws Exception {
	System.out.println("CheckInterfaceBindings");
	System.out.println("ArrayList");
	assertEquals(ArrayList.class, dummyCreator.createDummyOfClass(List.class).getClass());
	System.out.println("Override Binding");

	ClassBindings classBinder = new ClassBindings();
	classBinder.add(List.class, ArrayList.class);
	classBinder.add(List.class, LinkedList.class);
	DummyCreator dummyCreator = new DummyCreator(classBinder);
	assertEquals(LinkedList.class, dummyCreator.createDummyOfClass(List.class).getClass());
    }

    @Test
    public void CheckStringCreation() {
	System.out.println("CheckPrimitiveCreation");
	System.out.println("String");
	String dummy = dummyCreator.createDummyOfClass(String.class);
	System.out.println(dummy);
	assertEquals(String.class, dummy.getClass());
    }

    @Test
    public void CheckSimpleObjectCreation() {
	System.out.println("CheckSimpleObjectCreation");
	System.out.println("Byte");
	assertEquals(Byte.class, dummyCreator.createDummyOfClass(Byte.class).getClass());
	System.out.println();
	System.out.println("Long");
	assertEquals(Long.class, dummyCreator.createDummyOfClass(Long.class).getClass());
    }

    @Test
    public void CheckObjectBinding() {
	ClassBindings classBinder = new ClassBindings();
	LinkedList<Object> list = new LinkedList<Object>();
	classBinder.add(List.class, list);
	DummyCreator dummyCreator = new DummyCreator(classBinder);
	List<?> dummy = dummyCreator.createDummyOfClass(List.class);
	assertEquals(LinkedList.class, dummy.getClass());
	assertSame(list, dummy);
    }

    @Test
    public void CheckDeferredSubTypeConstructorBinding() throws SecurityException, NoSuchMethodException {
	ClassBindings classBinder = new ClassBindings();
	classBinder.add(B.class, C.class.getConstructor(int.class));
	DummyCreator dummyCreator = new DummyCreator(classBinder);
	B dummy = dummyCreator.createDummyOfClass(B.class);
	assertEquals(C.class, dummy.getClass());
    }

    @Test
    public void CheckDeferredSubTypeBinding() throws SecurityException, NoSuchMethodException {
	ClassBindings classBinder = new ClassBindings();
	classBinder.add(B.class, C.class);
	DummyCreator dummyCreator = new DummyCreator(classBinder);
	B dummy = dummyCreator.createDummyOfClass(B.class);
	assertEquals(C.class, dummy.getClass());
    }

    @Test
    public void CheckPrimitiveClassCreation() {
	System.out.println("CheckPrimitiveClassCreation");
	System.out.println("Primitive Class");
	assertEquals(PrimitiveClass.class, dummyCreator.createDummyOfClass(PrimitiveClass.class).getClass());

	System.out.println();
	System.out.println("Inherited Primitive Object");
	assertEquals(InheritedPrimitiveClass.class, dummyCreator.createDummyOfClass(InheritedPrimitiveClass.class).getClass());

	// TODO Check if all parameters have been set
    }

    @Test
    public void CheckNormalClassCreation() {
	System.out.println("CheckNormalClassCreation");
	System.out.println("Normal Class");
	assertEquals(NormalClass.class, dummyCreator.createDummyOfClass(NormalClass.class).getClass());
	// TODO Check if all parameters have been set
    }

    @Test
    public void CheckLoopClassCreation() {
	System.out.println("CheckLoopClassCreation");
	System.out.println("Loop Class");
	assertEquals(LoopClass.class, dummyCreator.createDummyOfClass(LoopClass.class).getClass());
    }

    @Test
    public void CheckMultiConstructorClassCreation() {
	System.out.println("CheckMultiConstructorClassCreation");
	System.out.println("MultiConstructor Class");
	assertEquals(MultiConstructorClass.class, dummyCreator.createDummyOfClass(MultiConstructorClass.class).getClass());
    }

    @Test
    public void CheckEnumClassCreation() {
	System.out.println("CheckEnumClassCreation");
	System.out.println("Enum Class");
	EnumClass ec = dummyCreator.createDummyOfClass(EnumClass.class);
	assertNotNull(ec.getEnumTester());
	assertNotNull(ec.getInternalEnum());
    }

    @SuppressWarnings("serial")
    public static class NumberStringMap extends HashMap<Integer, String> {
    };

    @Test
    public void checkGenericMap() {
	Map<Integer, String> numberStringMap = new NumberStringMap();
	Map<?, ?> ec = dummyCreator.createDummyOfClass(numberStringMap.getClass());
	assertNotNull(ec);
    }

    @SuppressWarnings("serial")
    public static class NumberStringList extends ArrayList<Integer> {
    };

    @Test
    public void checkGenericList() {
	List<Integer> numbers = new NumberStringList();
	List<?> ec = dummyCreator.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void checkList() {
	List<Integer> numbers = new ArrayList<Integer>();
	List<?> ec = dummyCreator.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void checkMooooList() {
	List<MyCustomTestClass> numbers = new MyCustomTestClassList();
	List<?> ec = dummyCreator.createDummyOfClass(numbers.getClass());
	assertNotNull(ec);
    }

    @Test
    public void checkMap() {
	Map<Integer, String> numbers = new HashMap<Integer, String>();
	Map<?, ?> ec = dummyCreator.createDummyOfClass(numbers.getClass());
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
	ArrayListClass alc = dummyCreator.createDummyOfClass(ArrayListClass.class);
	System.out.println(alc.getMyList());
	for (String s : alc.getMyList()) {
	    System.out.println(s);
	}

	System.out.println();
	System.out.println("List Object");
	// testDummyCreation(ListClass.class);

	ListClass lc = dummyCreator.createDummyOfClass(ListClass.class);
	System.out.println(lc.getMyList());
    }

    // @Test
    public void Benchmark() throws Exception {
	System.out.println();
	System.out.println("Benchmarking");
	System.out.println("Primitive Class");
	long t = System.currentTimeMillis();
	for (int i = 0; i < 1000000; i++) {
	    dummyCreator.createDummyOfClass(PrimitiveClass.class);
	}
	System.out.println("Time: " + (System.currentTimeMillis() - t));
	System.out.println("Loop Class ");
	t = System.currentTimeMillis();
	for (int i = 0; i < 1000000; i++) {
	    dummyCreator.createDummyOfClass(LoopClass.class);
	}
	System.out.println("Time: " + (System.currentTimeMillis() - t));
	System.out.println("MultiConstructor Class ");
	t = System.currentTimeMillis();
	for (int i = 0; i < 1000000; i++) {
	    dummyCreator.createDummyOfClass(MultiConstructorClass.class);
	}
	System.out.println("Time: " + (System.currentTimeMillis() - t));
    }

    private void testDummyCreation(Class<?> clazz) throws Exception {
	Object t = dummyCreator.createDummyOfClass(clazz);
	printInfo(t, clazz);
    }

    private <T> void printInfo(T object, Class<?> clazz) throws Exception {
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
