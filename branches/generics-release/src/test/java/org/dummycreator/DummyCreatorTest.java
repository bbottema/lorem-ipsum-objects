package org.dummycreator;

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

import org.dummycreator.dummyfactories.ClassBasedFactory;
import org.dummycreator.dummyfactories.ConstructorBasedFactory;
import org.dummycreator.dummyfactories.FixedInstanceFactory;
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
 * Tests for DummyCreator.
 */
public class DummyCreatorTest {

	private DummyCreator dummyCreator;

	/**
	 * Adds some default bindings useful for these tests, such as some {@link FixedInstanceFactory} instances and a
	 * {@link ConstructorBasedFactory}.
	 */
	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = ClassBindings.defaultBindings();
		classBindings.add(Integer.class, new ConstructorBasedFactory<Integer>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.add(Long.class, new FixedInstanceFactory<Long>(Long.MAX_VALUE));
		classBindings.add(Double.class, new FixedInstanceFactory<Double>(Double.MIN_VALUE));
		dummyCreator = new DummyCreator(classBindings);
	}

	/**
	 * Tests if the {@link FixedInstanceFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Long</code> and
	 * <code>Double</code>.
	 */
	@Test
	public void testObjectBindings() throws Exception {
		assertEquals(Long.MAX_VALUE, dummyCreator.create(Long.class), 0);
		assertEquals(Double.MIN_VALUE, dummyCreator.create(Double.class), 0);
	}

	/**
	 * Tests if the {@link ConstructorBasedFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Integer</code>.
	 */
	@Test
	public void testConstructorBindings() throws Exception {
		assertEquals(Integer.class, dummyCreator.create(Integer.class).getClass());
	}

	/**
	 * Tests if the default binding for <code>List</code> (which is <code>ArrayList</code>) is returned and after providing some custom
	 * bindings if a <code>LinkedList</code> is now returned instead.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testInterfaceBindings() throws Exception {
		assertEquals(ArrayList.class, dummyCreator.create(List.class).getClass());

		ClassBindings classBindings = new ClassBindings();
		classBindings.add(List.class, new ClassBasedFactory<ArrayList>(ArrayList.class));
		classBindings.add(List.class, new ClassBasedFactory<LinkedList>(LinkedList.class));
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		assertEquals(LinkedList.class, dummyCreator.create(List.class).getClass());
	}

	/**
	 * Tests if array of <code>Integer</code> and <code>int</code> are created without problems.
	 */
	@Test
	public void testArrayCreation() {
		Integer[] integers = dummyCreator.create(new Integer[] {}.getClass());
		assertNotNull(integers);
		for (Integer i : integers) {
			assertSame(Integer.class, i.getClass());
		}
		int[] ints = dummyCreator.create(new int[] {}.getClass());
		assertNotNull(ints);
	}

	/**
	 * Tests if a <code>String</code> is returned correctly.
	 */
	@Test
	public void testStringCreation() {
		String dummy = dummyCreator.create(String.class);
		assertEquals(String.class, dummy.getClass());
	}

	/**
	 * Tests if a <code>Byte</code> and <code>Long</code> is created correctly without using custom factories.
	 */
	@Test
	public void testSimpleObjectCreation() {
		assertEquals(Byte.class, dummyCreator.create(Byte.class).getClass());
		assertEquals(Long.class, dummyCreator.create(Long.class).getClass());
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
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		List<?> dummy = dummyCreator.create(List.class);
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
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		B dummy = dummyCreator.create(B.class);
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
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		B dummy = dummyCreator.create(B.class);
		assertEquals(C.class, dummy.getClass());
	}

	/**
	 * Tests if both <code>PrimitiveClass</code> (which contains some primitive fields and <code>InheritedPrimitiveClass</code> (which adds
	 * some inheritance) are produced correctly with the fields populated correctly.
	 */
	@Test
	public void testPrimitiveClassCreation() {
		assertEquals(PrimitiveClass.class, dummyCreator.create(PrimitiveClass.class).getClass());
		assertEquals(InheritedPrimitiveClass.class, dummyCreator.create(InheritedPrimitiveClass.class).getClass());
		// TODO Check if all parameters have been set
	}

	/**
	 * Tests if the <code>NormalClass</code> is created correctly. It has a nested <code>PrimitiveClass</code>, so we're testing inheritance
	 * by composition here instead of direct inheritance. And then ofcourse the primitive fields need to be populated correctly again.
	 */
	@Test
	public void testNormalClassCreation() {
		assertEquals(NormalClass.class, dummyCreator.create(NormalClass.class).getClass());
		// TODO Check if all parameters have been set
	}

	/**
	 * Tests if an infinite recursive loop is handle correctly.
	 */
	@Test
	public void testLoopClassCreation() {
		assertEquals(LoopClass.class, dummyCreator.create(LoopClass.class).getClass());
	}

	/**
	 * Tests whether complex constructors are invoked correctly.
	 */
	@Test
	public void testMultiConstructorClassCreation() {
		assertEquals(MultiConstructorClass.class, dummyCreator.create(MultiConstructorClass.class).getClass());
	}

	/**
	 * Tests whether enumerations can be produced correctly.
	 */
	@Test
	public void testEnumClassCreation() {
		EnumClass ec = dummyCreator.create(EnumClass.class);
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
		Map<Integer, String> ec = dummyCreator.create(numberStringMap.getClass());
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
		List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		assertSame(Integer.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>List</code> of <code>Integer</code> will be produced correctly. The Result should contain strings, because the
	 * generic meta data is not available in runtime.
	 */
	@Test
	public void testList() {
		List<Integer> numbers = new ArrayList<Integer>();
		@SuppressWarnings("unchecked")
		List<Integer> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		assertSame(String.class, ((Object) ec.get(0)).getClass());
	}

	/**
	 * Tests whether a <code>List</code> of <code>MyCustomTestClass</code> will be produced correctly.
	 */
	@Test
	public void testMyCustomTestClassList() {
		List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		assertSame(MyCustomTestClass.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>Map<Integer, String></code> will be produced correctly. The Result should contain strings for keys and values,
	 * because the generic meta data is not available in runtime.
	 */
	@Test
	public void testMap() {
		Map<Integer, Double> numbers = new HashMap<Integer, Double>();
		@SuppressWarnings("unchecked")
		Map<Integer, Double> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		Entry<Integer, Double> firstItem = ec.entrySet().iterator().next();
		assertSame(String.class, ((Object) firstItem.getKey()).getClass());
		assertSame(String.class, ((Object) firstItem.getValue()).getClass());
	}

	/**
	 * Tests whether the right error will be produced in case an abstract or interface type should be created where no binding is available
	 * to indicate the correct implementation.
	 */
	@Test
	public void testInterfaceBindingErrors() throws Exception {
		dummyCreator = new DummyCreator();
		try {
			assertEquals(ArrayList.class, dummyCreator.create(List.class).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
		try {
			assertEquals(ArrayList.class, dummyCreator.create(AbstractList.class).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
	}
}