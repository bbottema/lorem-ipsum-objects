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

public class ClassBasedFactoryTest {


	private ClassBindings classBindings;

	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		classBindings = ClassBindings.defaultBindings();
		classBindings.add(Integer.class, new ConstructorBasedFactory<Integer>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.add(Long.class, new FixedInstanceFactory<Long>(Long.MAX_VALUE));
		classBindings.add(Double.class, new FixedInstanceFactory<Double>(Double.MIN_VALUE));
	}

	@Test
	public void CheckObjectBindings() throws Exception {
		assertEquals(Long.MAX_VALUE, new ClassBasedFactory<Long>(Long.class).createDummy(classBindings), 0);
		assertEquals(Double.MIN_VALUE, new ClassBasedFactory<Double>(Double.class).createDummy(classBindings), 0);
	}

	@Test
	public void CheckConstructorBindings() throws Exception {
		assertEquals(Integer.class, new ClassBasedFactory<Integer>(Integer.class).createDummy(classBindings).getClass());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void CheckInterfaceBindings() throws Exception {
		assertEquals(ArrayList.class, new ClassBasedFactory<List>(List.class).createDummy(classBindings).getClass());

		ClassBindings classBindings = new ClassBindings();
		classBindings.add(List.class, new ClassBasedFactory<ArrayList>(ArrayList.class));
		classBindings.add(List.class, new ClassBasedFactory<LinkedList>(LinkedList.class));
		assertEquals(LinkedList.class, new ClassBasedFactory<List>(List.class).createDummy(classBindings).getClass());
	}

	@Test
	public void CheckArrayCreation() {
		@SuppressWarnings("unchecked")
		Integer[] integers = new ClassBasedFactory<Integer[]>((Class<Integer[]>) new Integer[]{}.getClass()).createDummy(classBindings);
		assertNotNull(integers);
		for (Integer i : integers) {
			assertSame(Integer.class, i.getClass());
		}
		@SuppressWarnings("unchecked")
		int[] ints = new ClassBasedFactory<int[]>((Class<int[]>) new int[]{}.getClass()).createDummy(classBindings);
		assertNotNull(ints);
	}

	@Test
	public void CheckStringCreation() {
		String dummy = new ClassBasedFactory<String>(String.class).createDummy(classBindings);
		assertEquals(String.class, dummy.getClass());
	}

	@Test
	public void CheckSimpleObjectCreation() {
		assertEquals(Byte.class, new ClassBasedFactory<Byte>(Byte.class).createDummy(classBindings).getClass());
		assertEquals(Long.class, new ClassBasedFactory<Long>(Long.class).createDummy(classBindings).getClass());
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void CheckObjectBinding() {
		ClassBindings classBindings = new ClassBindings();
		LinkedList<Object> list = new LinkedList<Object>();
		classBindings.add(List.class, new FixedInstanceFactory<List>(list));
		List<?> dummy = new ClassBasedFactory<List>(List.class).createDummy(classBindings);
		assertEquals(LinkedList.class, dummy.getClass());
		assertSame(list, dummy);
	}

	@Test
	public void CheckDeferredSubTypeConstructorBinding() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ConstructorBasedFactory<C>(C.class.getConstructor(int.class)));
		B dummy = new ClassBasedFactory<B>(B.class).createDummy(classBindings);
		assertEquals(C.class, dummy.getClass());
	}

	@Test
	public void CheckDeferredSubTypeBinding() throws SecurityException, NoSuchMethodException {
		ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ClassBasedFactory<C>(C.class));
		B dummy = new ClassBasedFactory<B>(B.class).createDummy(classBindings);
		assertEquals(C.class, dummy.getClass());
	}

	@Test
	public void CheckPrimitiveClassCreation() {
		PrimitiveClass primitive = new ClassBasedFactory<PrimitiveClass>(PrimitiveClass.class).createDummy(classBindings);
		InheritedPrimitiveClass inheritedPrimitive = new ClassBasedFactory<InheritedPrimitiveClass>(InheritedPrimitiveClass.class).createDummy(classBindings);
		assertEquals(PrimitiveClass.class, primitive.getClass());
		assertEquals(InheritedPrimitiveClass.class, inheritedPrimitive.getClass());
		// TODO Check if all parameters have been set
	}

	@Test
	public void CheckNormalClassCreation() {
		assertEquals(NormalClass.class, new ClassBasedFactory<NormalClass>(NormalClass.class).createDummy(classBindings).getClass());
		// TODO Check if all parameters have been set
	}

	@Test
	public void CheckLoopClassCreation() {
		assertEquals(LoopClass.class, new ClassBasedFactory<LoopClass>(LoopClass.class).createDummy(classBindings).getClass());
	}

	@Test
	public void CheckMultiConstructorClassCreation() {
		MultiConstructorClass dummy = new ClassBasedFactory<MultiConstructorClass>(MultiConstructorClass.class).createDummy(classBindings);
		assertEquals(MultiConstructorClass.class, dummy.getClass());
	}

	@Test
	public void CheckEnumClassCreation() {
		EnumClass ec = new ClassBasedFactory<EnumClass>(EnumClass.class).createDummy(classBindings);
		assertNotNull(ec.getEnumTester());
		assertNotNull(ec.getInternalEnum());
	}

	@SuppressWarnings("serial")
	public static class NumberStringMap extends HashMap<Integer, String> {
	};

	@Test
	public void checkGenericMap() {
		Map<Integer, String> numberStringMap = new NumberStringMap();
		@SuppressWarnings("unchecked")
		Map<Integer, String> ec = new ClassBasedFactory<Map<Integer, String>>((Class<Map<Integer, String>>) numberStringMap.getClass()).createDummy(classBindings);
		assertNotNull(ec);
	}

	@SuppressWarnings("serial")
	public static class NumberStringList extends ArrayList<Integer> {
	};

	@Test
	public void checkGenericList() {
		List<Integer> numbers = new NumberStringList();
		@SuppressWarnings("unchecked")
		List<Integer> ec = new ClassBasedFactory<List<Integer>>((Class<List<Integer>>) numbers.getClass()).createDummy(classBindings);
		assertNotNull(ec);
	}

	@Test
	public void checkList() {
		List<Integer> numbers = new ArrayList<Integer>();
		@SuppressWarnings("unchecked")
		List<Integer> ec = new ClassBasedFactory<List<Integer>>((Class<List<Integer>>) numbers.getClass()).createDummy(classBindings);
		assertNotNull(ec);
	}

	@Test
	public void checkMooooList() {
		List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		@SuppressWarnings("unchecked")
		List<MyCustomTestClass> ec = new ClassBasedFactory<List<MyCustomTestClass>>((Class<List<MyCustomTestClass>>) numbers.getClass()).createDummy(classBindings);
		assertNotNull(ec);
	}

	@Test
	public void checkMap() {
		Map<Integer, String> numbers = new HashMap<Integer, String>();
		@SuppressWarnings("unchecked")
		Map<Integer, String> ec = new ClassBasedFactory<Map<Integer, String>>((Class<Map<Integer, String>>) numbers.getClass()).createDummy(classBindings);
		assertNotNull(ec);
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void CheckInterfaceBindingErrors() throws Exception {
		try {
			assertEquals(ArrayList.class, new ClassBasedFactory<List>(List.class).createDummy(new ClassBindings()).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			// ok
		}
		try {
			assertEquals(ArrayList.class, new ClassBasedFactory<AbstractList>(AbstractList.class).createDummy(new ClassBindings()).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (IllegalArgumentException e) {
			// ok
		}
	}
}