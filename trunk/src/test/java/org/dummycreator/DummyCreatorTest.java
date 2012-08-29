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

public class DummyCreatorTest {

	private DummyCreator dummyCreator;

	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = ClassBindings.defaultBindings();
		classBindings.add(Integer.class, new ConstructorBasedFactory<Integer>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.add(Long.class, new FixedInstanceFactory<Long>(Long.MAX_VALUE));
		classBindings.add(Double.class, new FixedInstanceFactory<Double>(Double.MIN_VALUE));
		dummyCreator = new DummyCreator(classBindings);
	}

	@Test
	public void CheckObjectBindings() throws Exception {
		assertEquals(Long.MAX_VALUE, dummyCreator.create(Long.class), 0);
		assertEquals(Double.MIN_VALUE, dummyCreator.create(Double.class), 0);
	}

	@Test
	public void CheckConstructorBindings() throws Exception {
		assertEquals(Integer.class, dummyCreator.create(Integer.class).getClass());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void CheckInterfaceBindings() throws Exception {
		assertEquals(ArrayList.class, dummyCreator.create(List.class).getClass());

		ClassBindings classBindings = new ClassBindings();
		classBindings.add(List.class, new ClassBasedFactory<ArrayList>(ArrayList.class));
		classBindings.add(List.class, new ClassBasedFactory<LinkedList>(LinkedList.class));
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		assertEquals(LinkedList.class, dummyCreator.create(List.class).getClass());
	}

	@Test
	public void CheckArrayCreation() {
		Integer[] integers = dummyCreator.create(new Integer[]{}.getClass());
		assertNotNull(integers);
		for (Integer i : integers) {
			assertSame(Integer.class, i.getClass());
		}
		int[] ints = dummyCreator.create(new int[]{}.getClass());
		assertNotNull(ints);
	}

	@Test
	public void CheckStringCreation() {
		String dummy = dummyCreator.create(String.class);
		assertEquals(String.class, dummy.getClass());
	}

	@Test
	public void CheckSimpleObjectCreation() {
		assertEquals(Byte.class, dummyCreator.create(Byte.class).getClass());
		assertEquals(Long.class, dummyCreator.create(Long.class).getClass());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void CheckObjectBinding() {
		ClassBindings classBindings = new ClassBindings();
		LinkedList<Object> list = new LinkedList<Object>();
		classBindings.add(List.class, new FixedInstanceFactory<List>(list));
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		List<?> dummy = dummyCreator.create(List.class);
		assertEquals(LinkedList.class, dummy.getClass());
		assertSame(list, dummy);
	}

	@Test
	public void CheckDeferredSubTypeConstructorBinding() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ConstructorBasedFactory<C>(C.class.getConstructor(int.class)));
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		B dummy = dummyCreator.create(B.class);
		assertEquals(C.class, dummy.getClass());
	}

	@Test
	public void CheckDeferredSubTypeBinding() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ClassBasedFactory<C>(C.class));
		DummyCreator dummyCreator = new DummyCreator(classBindings);
		B dummy = dummyCreator.create(B.class);
		assertEquals(C.class, dummy.getClass());
	}

	@Test
	public void CheckPrimitiveClassCreation() {
		assertEquals(PrimitiveClass.class, dummyCreator.create(PrimitiveClass.class).getClass());
		assertEquals(InheritedPrimitiveClass.class, dummyCreator.create(InheritedPrimitiveClass.class).getClass());
		// TODO Check if all parameters have been set
	}

	@Test
	public void CheckNormalClassCreation() {
		assertEquals(NormalClass.class, dummyCreator.create(NormalClass.class).getClass());
		// TODO Check if all parameters have been set
	}

	@Test
	public void CheckLoopClassCreation() {
		assertEquals(LoopClass.class, dummyCreator.create(LoopClass.class).getClass());
	}

	@Test
	public void CheckMultiConstructorClassCreation() {
		assertEquals(MultiConstructorClass.class, dummyCreator.create(MultiConstructorClass.class).getClass());
	}

	@Test
	public void CheckEnumClassCreation() {
		EnumClass ec = dummyCreator.create(EnumClass.class);
		assertNotNull(ec.getEnumTester());
		assertNotNull(ec.getInternalEnum());
	}

	@SuppressWarnings("serial")
	public static class NumberStringMap extends HashMap<Integer, String> {
	};

	@Test
	public void checkGenericMap() {
		Map<Integer, String> numberStringMap = new NumberStringMap();
		Map<?, ?> ec = dummyCreator.create(numberStringMap.getClass());
		assertNotNull(ec);
	}

	@SuppressWarnings("serial")
	public static class NumberStringList extends ArrayList<Integer> {
	};

	@Test
	public void checkGenericList() {
		List<Integer> numbers = new NumberStringList();
		List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
	}

	@Test
	public void checkList() {
		List<Integer> numbers = new ArrayList<Integer>();
		List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
	}

	@Test
	public void checkMooooList() {
		List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
	}

	@Test
	public void checkMap() {
		Map<Integer, String> numbers = new HashMap<Integer, String>();
		Map<?, ?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
	}

	@Test
	public void CheckInterfaceBindingErrors() throws Exception {
		dummyCreator = new DummyCreator();
		try {
			assertEquals(ArrayList.class, dummyCreator.create(List.class).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			// ok
		}
		try {
			assertEquals(ArrayList.class, dummyCreator.create(AbstractList.class).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			// ok
		}
	}
}