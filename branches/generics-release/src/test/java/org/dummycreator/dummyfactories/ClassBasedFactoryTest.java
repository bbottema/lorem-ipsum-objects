package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.dummycreator.ClassBindings;
import org.dummycreator.helperutils.EnumClass;
import org.dummycreator.helperutils.InheritedPrimitiveClass;
import org.dummycreator.helperutils.LoopClass;
import org.dummycreator.helperutils.MultiConstructorClass;
import org.dummycreator.helperutils.MyCustomTestClass;
import org.dummycreator.helperutils.MyCustomTestClassList;
import org.dummycreator.helperutils.NormalClass;
import org.dummycreator.helperutils.PrimitiveClass;
import org.dummycreator.helperutils.TestChainBinding.B;
import org.dummycreator.helperutils.TestChainBinding.C;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for ConstructorBasedFactory.
 */
public class ClassBasedFactoryTest {

	private ClassBindings classBindings;

	/**
	 * Adds some default bindings useful for these tests, such as some {@link FixedInstanceFactory} instances and a
	 * {@link ConstructorBasedFactory}.
	 */
	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		classBindings = ClassBindings.defaultBindings();
		classBindings.add(Integer.class, new ConstructorBasedFactory<Integer>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.add(Long.class, new FixedInstanceFactory<Long>(Long.MAX_VALUE));
		classBindings.add(Double.class, new FixedInstanceFactory<Double>(Double.MIN_VALUE));
	}

	/**
	 * Tests if the {@link FixedInstanceFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Long</code> and
	 * <code>Double</code>.
	 */
	@Test
	public void testObjectBindings() throws Exception {
		assertEquals(Long.MAX_VALUE, new ClassBasedFactory<Long>(Long.class).createDummy(classBindings), 0);
		assertEquals(Double.MIN_VALUE, new ClassBasedFactory<Double>(Double.class).createDummy(classBindings), 0);
	}

	/**
	 * Tests if the {@link ConstructorBasedFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Integer</code>.
	 */
	@Test
	public void testConstructorBindings() throws Exception {
		assertEquals(Integer.class, new ClassBasedFactory<Integer>(Integer.class).createDummy(classBindings).getClass());
	}

	/**
	 * Tests if the default binding for <code>List</code> (which is <code>ArrayList</code>) is returned and after providing some custom
	 * bindings if a <code>LinkedList</code> is now returned instead.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testInterfaceBindings() throws Exception {
		assertEquals(ArrayList.class, new ClassBasedFactory<List>(List.class).createDummy(classBindings).getClass());
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(List.class, new ClassBasedFactory<ArrayList>(ArrayList.class));
		classBindings.add(List.class, new ClassBasedFactory<LinkedList>(LinkedList.class));
		assertEquals(LinkedList.class, new ClassBasedFactory<List>(List.class).createDummy(classBindings).getClass());
	}

	/**
	 * Tests if array of <code>Integer</code> and <code>int</code> are created without problems.
	 */
	@Test
	public void testArrayCreation() {
		@SuppressWarnings("unchecked")
		Integer[] integers = new ClassBasedFactory<Integer[]>((Class<Integer[]>) new Integer[] {}.getClass()).createDummy(classBindings);
		assertNotNull(integers);
		for (Integer i : integers) {
			assertSame(Integer.class, i.getClass());
		}
		@SuppressWarnings("unchecked")
		int[] ints = new ClassBasedFactory<int[]>((Class<int[]>) new int[] {}.getClass()).createDummy(classBindings);
		assertNotNull(ints);
	}

	/**
	 * Tests if a <code>String</code> is returned correctly.
	 */
	@Test
	public void testStringCreation() {
		String dummy = new ClassBasedFactory<String>(String.class).createDummy(classBindings);
		assertEquals(String.class, dummy.getClass());
	}

	/**
	 * Tests if a <code>Byte</code> and <code>Long</code> is created correctly without using custom factories.
	 */
	@Test
	public void testSimpleObjectCreation() {
		assertEquals(Byte.class, new ClassBasedFactory<Byte>(Byte.class).createDummy(classBindings).getClass());
		assertEquals(Long.class, new ClassBasedFactory<Long>(Long.class).createDummy(classBindings).getClass());
	}

	/**
	 * Extra test to make sure {@link FixedInstanceFactory} returns the exact same instance.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testObjectBinding() {
		ClassBindings classBindings = new ClassBindings();
		LinkedList<Object> list = new LinkedList<Object>();
		classBindings.add(List.class, new FixedInstanceFactory<List>(list));
		List<?> dummy = new ClassBasedFactory<List>(List.class).createDummy(classBindings);
		assertEquals(LinkedList.class, dummy.getClass());
		assertSame(list, dummy);
	}

	/**
	 * Tests if <code>B</code> sub class <code>C</code> constructor is invoked correctly to produce an object assignable to <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeConstructorBinding() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ConstructorBasedFactory<C>(C.class.getConstructor(int.class)));
		B dummy = new ClassBasedFactory<B>(B.class).createDummy(classBindings);
		assertEquals(C.class, dummy.getClass());
	}

	/**
	 * Tests if <code>B</code> sub class <code>C</code> is created correctly (without providing explicit constructor) to produce an object
	 * assignable to <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeBinding() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ClassBasedFactory<C>(C.class));
		B dummy = new ClassBasedFactory<B>(B.class).createDummy(classBindings);
		assertEquals(C.class, dummy.getClass());
	}

	/**
	 * Tests if both <code>PrimitiveClass</code> (which contains some primitive fields and <code>InheritedPrimitiveClass</code> (which adds
	 * some inheritance) are produced correctly with the fields populated correctly.
	 */
	@Test
	public void testPrimitiveClassCreation() {
		PrimitiveClass primitive = new ClassBasedFactory<PrimitiveClass>(PrimitiveClass.class).createDummy(classBindings);
		InheritedPrimitiveClass inheritedPrimitive = new ClassBasedFactory<InheritedPrimitiveClass>(InheritedPrimitiveClass.class).createDummy(classBindings);
		assertEquals(PrimitiveClass.class, primitive.getClass());
		assertEquals(InheritedPrimitiveClass.class, inheritedPrimitive.getClass());
		// TODO Check if all parameters have been set
	}

	/**
	 * Tests if the <code>NormalClass</code> is created correctly. It has a nested <code>PrimitiveClass</code>, so we're testing inheritance
	 * by composition here instead of direct inheritance. And then ofcourse the primitive fields need to be populated correctly again.
	 */
	@Test
	public void testNormalClassCreation() {
		assertEquals(NormalClass.class, new ClassBasedFactory<NormalClass>(NormalClass.class).createDummy(classBindings).getClass());
		// TODO Check if all parameters have been set
	}

	/**
	 * Tests if an infinite recursive loop is handle correctly.
	 */
	@Test
	public void testLoopClassCreation() {
		LoopClass loopDummy = new ClassBasedFactory<LoopClass>(LoopClass.class).createDummy(classBindings);
		assertEquals(LoopClass.class, loopDummy.getClass());
		assertSame(loopDummy, loopDummy.getLoopObject());
	}

	/**
	 * Tests whether complex constructors are invoked correctly.
	 */
	@Test
	public void testMultiConstructorClassCreation() {
		MultiConstructorClass dummy = new ClassBasedFactory<MultiConstructorClass>(MultiConstructorClass.class).createDummy(classBindings);
		assertEquals(MultiConstructorClass.class, dummy.getClass());
		// TODO Check if all parameters have been set
	}

	/**
	 * Tests whether enumerations can be produced correctly.
	 */
	@Test
	public void testEnumClassCreation() {
		EnumClass ec = new ClassBasedFactory<EnumClass>(EnumClass.class).createDummy(classBindings);
		assertNotNull(ec.getEnumTester());
		assertNotNull(ec.getInternalEnum());
	}

	@SuppressWarnings("serial")
	public static class NumberStringMap extends HashMap<Integer, String> {
	};

	/**
	 * Tests if a <code>Map<Integer, String></code> will be created with the right type of objects inside.
	 */
	@Test
	public void testGenericMap() {
		Map<Integer, String> numberStringMap = new NumberStringMap();
		@SuppressWarnings("unchecked")
		ClassBasedFactory<Map<Integer, String>> factory = new ClassBasedFactory<Map<Integer, String>>((Class<Map<Integer, String>>) numberStringMap.getClass());
		Map<Integer, String> ec = factory.createDummy(classBindings);
		assertNotNull(ec);
		Entry<Integer, String> firstItem = ec.entrySet().iterator().next();
		assertSame(Integer.class, firstItem.getKey().getClass());
		assertSame(String.class, firstItem.getValue().getClass());
	}

	@SuppressWarnings("serial")
	public static class NumberStringList extends ArrayList<Integer> {
	};

	/**
	 * Tests if a <code>List<Integer></code> will be created with the right type of objects inside.
	 */
	@Test
	public void testGenericList() {
		List<Integer> numbers = new NumberStringList();
		@SuppressWarnings("unchecked")
		List<Integer> ec = new ClassBasedFactory<List<Integer>>((Class<List<Integer>>) numbers.getClass()).createDummy(classBindings);
		assertNotNull(ec);
		assertSame(Integer.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>List</code> of <code>Integer</code> will be produced correctly.
	 */
	@Test
	public void testList() {
		List<Integer> numbers = new ArrayList<Integer>();
		@SuppressWarnings("unchecked")
		List<Integer> ec = new ClassBasedFactory<List<Integer>>((Class<List<Integer>>) numbers.getClass()).createDummy(classBindings);
		assertNotNull(ec);
		//assertSame(Integer.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>List</code> of <code>MyCustomTestClass</code> will be produced correctly.
	 */
	@Test
	public void testMyCustomTestClassList() {
		List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		@SuppressWarnings("unchecked")
		ClassBasedFactory<List<MyCustomTestClass>> factory = new ClassBasedFactory<List<MyCustomTestClass>>((Class<List<MyCustomTestClass>>) numbers.getClass());
		List<MyCustomTestClass> ec = factory.createDummy(classBindings);
		assertNotNull(ec);
		assertSame(MyCustomTestClass.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>Map<Integer, String></code> will be produced correctly.
	 */
	@Test
	public void testMap() {
		Map<Integer, Double> numbers = new HashMap<Integer, Double>();
		@SuppressWarnings("unchecked")
		ClassBasedFactory<Map<Integer, Double>> factory = new ClassBasedFactory<Map<Integer, Double>>((Class<Map<Integer, Double>>) numbers.getClass());
		Map<Integer, Double> ec = factory.createDummy(classBindings);
		assertNotNull(ec);
		//Entry<Integer, Double> firstItem = ec.entrySet().iterator().next();
		//assertSame(Integer.class, firstItem.getKey().getClass());
		//assertSame(Double.class, firstItem.getValue().getClass());
	}

	/**
	 * Tests whether the right error will be produced in case an abstract or interface type should be created where no binding is available
	 * to indicate the correct implementation.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testInterfaceBindingErrors() throws Exception {
		try {
			new ClassBasedFactory<List>(List.class).createDummy(new ClassBindings()).getClass();
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
		try {
			new ClassBasedFactory<AbstractList>(AbstractList.class).createDummy(new ClassBindings()).getClass();
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
	}
}