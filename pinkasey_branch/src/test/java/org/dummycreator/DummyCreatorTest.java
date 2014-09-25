package org.dummycreator;

import java.math.BigDecimal;
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
import org.dummycreator.helperutils.NestedListClass.NestedDoubleListClass;
import org.dummycreator.helperutils.NestedListClass.NestedQuadrupleListClass;
import org.dummycreator.helperutils.NestedListClass.NestedSingleListClass;
import org.dummycreator.helperutils.NestedListClass.NestedTripleListClass;
import org.dummycreator.helperutils.NestedMapClass.NestedDoubleAssymetricMapClass;
import org.dummycreator.helperutils.NestedMapClass.NestedDoubleMapClass;
import org.dummycreator.helperutils.NestedMapClass.NestedEverythingClass;
import org.dummycreator.helperutils.NestedMapClass.NestedSingleMapClass;
import org.dummycreator.helperutils.NestedMapClass.NestedSingleSimpleMapClass;
import org.dummycreator.helperutils.NestedMapClass.NestedTripleMapClass;
import org.dummycreator.helperutils.NormalClass;
import org.dummycreator.helperutils.PrimitiveClass;
import org.dummycreator.helperutils.TestChainBinding.B;
import org.dummycreator.helperutils.TestChainBinding.C;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

/**
 * Tests for DummyCreator.
 */
public class DummyCreatorTest {

	private RandomCreator mock;

	private DummyCreator dummyCreator;

	/**
	 * Adds some default bindings useful for these tests, such as some {@link FixedInstanceFactory} instances and a
	 * {@link ConstructorBasedFactory}.
	 */
	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		final ClassBindings classBindings = ClassBindings.defaultBindings();
		classBindings.add(Integer.class, new ConstructorBasedFactory<Integer>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.add(Long.class, new FixedInstanceFactory<Long>(Long.MAX_VALUE));
		classBindings.add(Double.class, new FixedInstanceFactory<Double>(Double.MIN_VALUE));
		dummyCreator = new DummyCreator(classBindings);

		mock = EasyMock.createNiceMock(RandomCreator.class);
		RandomCreator.setInstance(mock);

		EasyMock.expect(mock.getRandomString()).andReturn("not so random test string").anyTimes();
		EasyMock.expect(mock.getRandomBoolean()).andReturn(true).anyTimes();
		EasyMock.expect(mock.getRandomInt()).andReturn(111).anyTimes();
		EasyMock.expect(mock.getRandomChar()).andReturn('[').anyTimes();
		EasyMock.expect(mock.getRandomByte()).andReturn((byte) 2).anyTimes();
		EasyMock.expect(mock.getRandomLong()).andReturn(33l).anyTimes();
		EasyMock.expect(mock.getRandomFloat()).andReturn(44.4f).anyTimes();
		EasyMock.expect(mock.getRandomDouble()).andReturn(55.5d).anyTimes();
		EasyMock.expect(mock.getRandomShort()).andReturn((short) 44).anyTimes();
		EasyMock.expect(mock.getRandomInt(EasyMock.anyInt())).andReturn(2).anyTimes();
		EasyMock.replay(mock);
	}

	@After
	public void cleanup() {
		RandomCreator.setInstance(new RandomCreator());
	};

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

		final ClassBindings classBindings = new ClassBindings();
		classBindings.add(List.class, new ClassBasedFactory<ArrayList>(ArrayList.class));
		classBindings.add(List.class, new ClassBasedFactory<LinkedList>(LinkedList.class));
		final DummyCreator dummyCreator = new DummyCreator(classBindings);
		assertEquals(LinkedList.class, dummyCreator.create(List.class).getClass());
	}

	/**
	 * Tests if array of <code>Integer</code> and <code>int</code> are created without problems.
	 */
	@Test
	public void testArrayCreation() {
		final Integer[] integers = dummyCreator.create(new Integer[] {}.getClass());
		assertNotNull(integers);
		for (final Integer i : integers) {
			assertSame(Integer.class, i.getClass());
		}
		final int[] ints = dummyCreator.create(new int[] {}.getClass());
		assertNotNull(ints);
	}

	/**
	 * Tests if a <code>String</code> is returned correctly.
	 */
	@Test
	public void testStringCreation() {
		final String dummy = dummyCreator.create(String.class);
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
		final ClassBindings classBindings = new ClassBindings();
		final LinkedList<Object> list = new LinkedList<Object>();
		classBindings.add(List.class, new FixedInstanceFactory<List>(list));
		final DummyCreator dummyCreator = new DummyCreator(classBindings);
		final List<?> dummy = dummyCreator.create(List.class);
		assertEquals(LinkedList.class, dummy.getClass());
		assertSame(list, dummy);
	}

	/**
	 * Tests if <code>B</code> sub class <code>C</code> constructor is invoked correctly to produce an object assignable to <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeConstructorBinding() throws SecurityException, NoSuchMethodException {
		final ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ConstructorBasedFactory<C>(C.class.getConstructor(int.class)));
		final DummyCreator dummyCreator = new DummyCreator(classBindings);
		final B dummy = dummyCreator.create(B.class);
		assertEquals(C.class, dummy.getClass());
	}

	/**
	 * Tests if <code>B</code> sub class <code>C</code> is created correctly (without providing explicit constructor) to produce an object
	 * assignable to <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeBinding() throws SecurityException, NoSuchMethodException {
		final ClassBindings classBindings = new ClassBindings();
		classBindings.add(B.class, new ClassBasedFactory<C>(C.class));
		final DummyCreator dummyCreator = new DummyCreator(classBindings);
		final B dummy = dummyCreator.create(B.class);
		assertEquals(C.class, dummy.getClass());
	}

	/**
	 * Tests if both <code>PrimitiveClass</code> (which contains some primitive fields and <code>InheritedPrimitiveClass</code> (which adds
	 * some inheritance) are produced correctly with the fields populated correctly.
	 */
	@Test
	public void testPrimitiveClassCreation() {
		final PrimitiveClass primitive = dummyCreator.create(PrimitiveClass.class);
		final InheritedPrimitiveClass inheritedPrimitive = dummyCreator.create(InheritedPrimitiveClass.class);
		assertEquals(PrimitiveClass.class, primitive.getClass());
		assertEquals(InheritedPrimitiveClass.class, inheritedPrimitive.getClass());

		testPrimitiveClass(primitive);
		testPrimitiveClass(inheritedPrimitive);
		assertEquals("not so random test string", inheritedPrimitive.getSecondString());

		EasyMock.verify(mock);
	}

	/**
	 * Tests if the <code>NormalClass</code> is created correctly. It has a nested <code>PrimitiveClass</code>, so we're testing inheritance
	 * by composition here instead of direct inheritance. And then ofcourse the primitive fields need to be populated correctly again.
	 */
	@Test
	public void testNormalClassCreation() {
		NormalClass dummy = dummyCreator.create(NormalClass.class);
		assertEquals(NormalClass.class, dummy.getClass());

		testPrimitiveClass(dummy.getPrimitiveClass());
		assertEquals(111, dummy.getId());
		assertEquals(true, dummy.getSomeBoolean());

		EasyMock.verify(mock);
	}

	/**
	 * Tests if an infinite recursive loop is handle correctly.
	 */
	@Test
	public void testLoopClassCreation() {
		LoopClass loopDummy = dummyCreator.create(LoopClass.class);
		assertEquals(LoopClass.class, loopDummy.getClass());
		assertSame(loopDummy, loopDummy.getLoopObject());
	}

	/**
	 * Tests whether complex constructors are invoked correctly.
	 */
	@Test
	public void testMultiConstructorClassCreation() {
		MultiConstructorClass dummy = dummyCreator.create(MultiConstructorClass.class);
		assertEquals(MultiConstructorClass.class, dummy.getClass());

		assertEquals(111, dummy.getA());
		assertEquals(111, dummy.getB());
		assertEquals(111, dummy.getC());
		assertEquals(111, dummy.getD());
		assertEquals(111, dummy.getE());
		assertEquals(111, dummy.getF());
		assertEquals(111, dummy.getG());
		assertEquals(111, dummy.getH());
		assertEquals(111, dummy.getI());
		testPrimitiveClass(dummy.getP1());
		testPrimitiveClass(dummy.getP2());
		testPrimitiveClass(dummy.getP3());
		testPrimitiveClass(dummy.getP4());
		testPrimitiveClass(dummy.getP5());
		testPrimitiveClass(dummy.getP6());
		testPrimitiveClass(dummy.getP7());

		EasyMock.verify(mock);
	}

	private void testPrimitiveClass(PrimitiveClass primitive) {
		assertEquals('[', primitive.get_char());
		assertEquals(true, primitive.is_boolean());
		assertEquals(33l, primitive.get_long());
		assertEquals(55.5d, primitive.get_double(), 0);
		assertEquals(55.5d, primitive.get_secondDouble(), 0);
		assertEquals("not so random test string", primitive.get_string());
		assertEquals(44, primitive.get_short());
		assertEquals(44.4f, primitive.get_float(), 0);
		assertEquals(2, primitive.get_byte());
		assertEquals(111, primitive.get_int());
	}

	/**
	 * Tests whether enumerations can be produced correctly.
	 */
	@Test
	public void testEnumClassCreation() {
		final EnumClass ec = dummyCreator.create(EnumClass.class);
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
		final Map<Integer, String> numberStringMap = new NumberStringMap();
		@SuppressWarnings("unchecked")
		final Map<Integer, String> ec = dummyCreator.create(numberStringMap.getClass());
		assertNotNull(ec);
		final Entry<Integer, String> firstItem = ec.entrySet().iterator().next();
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
		final List<Integer> numbers = new NumberStringList();
		final List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		assertSame(Integer.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>List</code> of <code>Integer</code> will be produced correctly. The Result should contain strings, because the
	 * generic meta data is not available in runtime.
	 */
	@Test
	public void testList() {
		final List<Integer> numbers = new ArrayList<Integer>();
		@SuppressWarnings("unchecked")
		final List<Integer> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		assertSame(String.class, ((Object) ec.get(0)).getClass());
	}

	/**
	 * Tests whether a nested <code>List</code> of <code>Double</code> will be produced correctly (generics should be preserved). Includes
	 * testing nested list of lists up to 4 deep.
	 */
	@Test
	public void testNestedGenericList() {
		// single nested list
		final NestedSingleListClass dummySingle = dummyCreator.create(NestedSingleListClass.class);
		assertSame(ArrayList.class, dummySingle.getNumbers().getClass());
		assertSame(Double.class, dummySingle.getNumbers().get(0).getClass());

		// double nested list
		final NestedDoubleListClass dummyDouble = dummyCreator.create(NestedDoubleListClass.class);
		assertSame(ArrayList.class, dummyDouble.getListsOfNumbers().get(0).getClass());
		assertSame(Double.class, dummyDouble.getListsOfNumbers().get(0).get(0).getClass());

		// triple nested list
		final NestedTripleListClass dummyTriple = dummyCreator.create(NestedTripleListClass.class);
		assertSame(ArrayList.class, dummyTriple.getListsOflistsOfNumbers().get(0).getClass());
		assertSame(ArrayList.class, dummyTriple.getListsOflistsOfNumbers().get(0).get(0).getClass());
		assertSame(Double.class, dummyTriple.getListsOflistsOfNumbers().get(0).get(0).get(0).getClass());

		// quadruple nested list
		final NestedQuadrupleListClass dummyQuadruple = dummyCreator.create(NestedQuadrupleListClass.class);
		assertSame(ArrayList.class, dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).getClass());
		assertSame(ArrayList.class, dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).get(0).getClass());
		assertSame(ArrayList.class, dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).get(0).get(0).getClass());
		assertSame(LoopClass.class, dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).get(0).get(0).get(0).getClass());
	}

	/**
	 * Tests whether a single nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testSingleSimpleNestedGenericMap() {
		// single nested map
		final NestedSingleSimpleMapClass dummySingle = dummyCreator.create(NestedSingleSimpleMapClass.class);
		assertSame(HashMap.class, dummySingle.getMap().getClass());
		final Entry<?, ?> firstEntrySingleMap = (Entry<?, ?>) dummySingle.getMap().entrySet().iterator().next();
		assertSame(String.class, firstEntrySingleMap.getKey().getClass());
		assertSame(String.class, firstEntrySingleMap.getValue().getClass());
	}

	/**
	 * Tests whether a single nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testSingleNestedGenericMap() {
		// single nested map
		final NestedSingleMapClass dummySingle = dummyCreator.create(NestedSingleMapClass.class);
		assertSame(HashMap.class, dummySingle.getNumbers().getClass());
		final Entry<Double, LoopClass> firstEntrySingleMap = dummySingle.getNumbers().entrySet().iterator().next();
		assertSame(Double.class, firstEntrySingleMap.getKey().getClass());
		assertSame(LoopClass.class, firstEntrySingleMap.getValue().getClass());
	}

	/**
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testDoubleNestedGenericMap() {
		// double nested map
		final NestedDoubleMapClass dummyDouble = dummyCreator.create(NestedDoubleMapClass.class);
		assertSame(HashMap.class, dummyDouble.getMapsOfNumbers().getClass());
		final Entry<Map<Integer, NestedDoubleMapClass>, Map<Double, LoopClass>> firstEntry = dummyDouble.getMapsOfNumbers().entrySet().iterator().next();

		assertSame(HashMap.class, firstEntry.getKey().getClass());
		assertSame(HashMap.class, firstEntry.getValue().getClass());

		final Entry<Integer, NestedDoubleMapClass> nestedKeyMap = firstEntry.getKey().entrySet().iterator().next();
		final Entry<Double, LoopClass> nestedValueMap = firstEntry.getValue().entrySet().iterator().next();

		assertSame(Integer.class, nestedKeyMap.getKey().getClass());
		assertSame(NestedDoubleMapClass.class, nestedKeyMap.getValue().getClass());
		assertSame(Double.class, nestedValueMap.getKey().getClass());
		assertSame(LoopClass.class, nestedValueMap.getValue().getClass());
	}

	/**
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved). Here the number of generic
	 * types on both sides (of the <code>Key</code> and <code>Value</code> side) are uneven.
	 */
	@Test
	public void testDoubleNestedAsymmetricGenericMap() {
		// double nested map
		final NestedDoubleAssymetricMapClass dummyDouble = dummyCreator.create(NestedDoubleAssymetricMapClass.class);
		assertSame(HashMap.class, dummyDouble.getMapsOfCharacters().getClass());
		final Entry<Map<Integer, NestedDoubleMapClass>, Character> firstEntry = dummyDouble.getMapsOfCharacters().entrySet().iterator().next();

		assertSame(HashMap.class, firstEntry.getKey().getClass());
		assertSame(Character.class, firstEntry.getValue().getClass());

		final Entry<Integer, NestedDoubleMapClass> nestedKeyMap = firstEntry.getKey().entrySet().iterator().next();

		assertSame(Integer.class, nestedKeyMap.getKey().getClass());
		assertSame(NestedDoubleMapClass.class, nestedKeyMap.getValue().getClass());
	}

	/**
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved), including nested generic
	 * lists.
	 */
	@Test
	public void testDoubleNestedGenericMapsAndLists() {
		// double nested map
		final NestedEverythingClass dummyDouble = dummyCreator.create(NestedEverythingClass.class);
		assertSame(HashMap.class, dummyDouble.getMapsOfLists().getClass());
		final Entry<Map<List<List<String>>, NestedDoubleMapClass>, List<Byte>> firstEntry = dummyDouble.getMapsOfLists().entrySet().iterator().next();

		assertSame(HashMap.class, firstEntry.getKey().getClass());
		assertSame(ArrayList.class, firstEntry.getValue().getClass());

		final Entry<List<List<String>>, NestedDoubleMapClass> nestedKeyMap = firstEntry.getKey().entrySet().iterator().next();

		assertSame(NestedDoubleMapClass.class, nestedKeyMap.getValue().getClass());
		assertSame(Byte.class, firstEntry.getValue().get(0).getClass());
		assertSame(ArrayList.class, nestedKeyMap.getKey().getClass());
		assertSame(ArrayList.class, nestedKeyMap.getKey().get(0).getClass());
		assertSame(String.class, nestedKeyMap.getKey().get(0).get(0).getClass());
	}

	/**
	 * Tests whether a triple nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testTripleNestedGenericMap() {
		// triple nested map
		final NestedTripleMapClass dummyTriple = dummyCreator.create(NestedTripleMapClass.class);
		assertSame(HashMap.class, dummyTriple.getMapsOfMapsOfNumbers().getClass());
		final Entry<Map<Integer, Map<Double, LoopClass>>, Map<Double, Map<Double, LoopClass>>> firstEntryTriple = dummyTriple.getMapsOfMapsOfNumbers().entrySet().iterator().next();

		assertSame(HashMap.class, firstEntryTriple.getKey().getClass());
		assertSame(HashMap.class, firstEntryTriple.getValue().getClass());

		final Entry<Integer, Map<Double, LoopClass>> nestedKeyMapTriple = firstEntryTriple.getKey().entrySet().iterator().next();
		final Entry<Double, Map<Double, LoopClass>> nestedValueMapTriple = firstEntryTriple.getValue().entrySet().iterator().next();

		assertSame(Integer.class, nestedKeyMapTriple.getKey().getClass());
		assertSame(HashMap.class, nestedKeyMapTriple.getValue().getClass());
		assertSame(Double.class, nestedValueMapTriple.getKey().getClass());
		assertSame(HashMap.class, nestedValueMapTriple.getValue().getClass());

		final Entry<Double, LoopClass> nestedNestedValueMapTriple = nestedKeyMapTriple.getValue().entrySet().iterator().next();
		final Entry<Double, LoopClass> nestedNestedKeyMapTriple = nestedValueMapTriple.getValue().entrySet().iterator().next();

		assertSame(Double.class, nestedNestedValueMapTriple.getKey().getClass());
		assertSame(LoopClass.class, nestedNestedValueMapTriple.getValue().getClass());
		assertSame(Double.class, nestedNestedKeyMapTriple.getKey().getClass());
		assertSame(LoopClass.class, nestedNestedKeyMapTriple.getValue().getClass());
	}

	/**
	 * Tests whether a <code>List</code> of <code>MyCustomTestClass</code> will be produced correctly.
	 */
	@Test
	public void testMyCustomTestClassList() {
		final List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		final List<?> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		assertSame(MyCustomTestClass.class, ec.get(0).getClass());
	}

	/**
	 * Tests whether a <code>Map<Integer, String></code> will be produced correctly. The Result should contain strings for keys and values,
	 * because the generic meta data is not available in runtime.
	 */
	@Test
	public void testMap() {
		final Map<Integer, Double> numbers = new HashMap<Integer, Double>();
		@SuppressWarnings("unchecked")
		final Map<Integer, Double> ec = dummyCreator.create(numbers.getClass());
		assertNotNull(ec);
		final Entry<Integer, Double> firstItem = ec.entrySet().iterator().next();
		assertSame(String.class, ((Object) firstItem.getKey()).getClass());
		assertSame(String.class, ((Object) firstItem.getValue()).getClass());
	}
    
    @Test
    public void testDummyCreatorBigDecimal() {
        BigDecimal bigDecimal = dummyCreator.create(BigDecimal.class);
        assertNotNull(bigDecimal);
        assertEquals(new BigDecimal(55.5), bigDecimal);
        System.out.println(bigDecimal);
        dummyCreator = new DummyCreator(ClassBindings.defaultBindings());
        bigDecimal = dummyCreator.create(BigDecimal.class);
        assertNotNull(bigDecimal);
        assertEquals(new BigDecimal(55.5), bigDecimal);
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
		} catch (final IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
		try {
			assertEquals(ArrayList.class, dummyCreator.create(AbstractList.class).getClass());
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (final IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
	}
}