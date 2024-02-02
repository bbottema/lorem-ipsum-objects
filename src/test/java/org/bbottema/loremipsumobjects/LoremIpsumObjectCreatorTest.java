package org.bbottema.loremipsumobjects;

import org.bbottema.javareflection.MethodUtils;
import org.bbottema.loremipsumobjects.helperutils.EnumClass;
import org.bbottema.loremipsumobjects.helperutils.InheritedPrimitiveClass;
import org.bbottema.loremipsumobjects.helperutils.LoopClass;
import org.bbottema.loremipsumobjects.helperutils.MultiConstructorClass;
import org.bbottema.loremipsumobjects.helperutils.MyCustomTestClass;
import org.bbottema.loremipsumobjects.helperutils.MyCustomTestClassList;
import org.bbottema.loremipsumobjects.helperutils.NestedListClass.NestedDoubleListClass;
import org.bbottema.loremipsumobjects.helperutils.NestedListClass.NestedQuadrupleListClass;
import org.bbottema.loremipsumobjects.helperutils.NestedListClass.NestedSingleListClass;
import org.bbottema.loremipsumobjects.helperutils.NestedListClass.NestedTripleListClass;
import org.bbottema.loremipsumobjects.helperutils.NestedMapClass.NestedDoubleAssymetricMapClass;
import org.bbottema.loremipsumobjects.helperutils.NestedMapClass.NestedDoubleMapClass;
import org.bbottema.loremipsumobjects.helperutils.NestedMapClass.NestedEverythingClass;
import org.bbottema.loremipsumobjects.helperutils.NestedMapClass.NestedSingleMapClass;
import org.bbottema.loremipsumobjects.helperutils.NestedMapClass.NestedSingleSimpleMapClass;
import org.bbottema.loremipsumobjects.helperutils.NestedMapClass.NestedTripleMapClass;
import org.bbottema.loremipsumobjects.helperutils.NormalClass;
import org.bbottema.loremipsumobjects.helperutils.PrimitiveClass;
import org.bbottema.loremipsumobjects.helperutils.ProcessStatus;
import org.bbottema.loremipsumobjects.helperutils.TestChainBinding.B;
import org.bbottema.loremipsumobjects.helperutils.TestChainBinding.C;
import org.bbottema.loremipsumobjects.typefactories.ClassBasedFactory;
import org.bbottema.loremipsumobjects.typefactories.ConstructorBasedFactory;
import org.bbottema.loremipsumobjects.typefactories.FixedInstanceFactory;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import static java.util.EnumSet.of;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.data.Offset.offset;
import static org.bbottema.javareflection.ClassUtils.findFirstMethodByName;
import static org.bbottema.javareflection.ClassUtils.locateClass;
import static org.bbottema.javareflection.model.MethodModifier.PUBLIC;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoremIpsumObjectCreatorTest {

	private LoremIpsumObjectCreator loremIpsumObjectCreator;

	/**
	 * Adds some default bindings useful for these tests, such as some {@link FixedInstanceFactory} instances and a {@link ConstructorBasedFactory}.
	 */
	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		final ClassBindings classBindings = new ClassBindings();
		classBindings.bind(Integer.class, new ConstructorBasedFactory<>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.bind(Long.class, new FixedInstanceFactory<>(Long.MAX_VALUE));
		loremIpsumObjectCreator = new LoremIpsumObjectCreator(classBindings);

		LoremIpsumGenerator mock = mock(LoremIpsumGenerator.class);
		LoremIpsumGenerator.setInstance(mock);

		when(mock.getRandomString()).thenReturn("not so random test string");
		when(mock.getRandomBoolean()).thenReturn(true);
		when(mock.getRandomInt()).thenReturn(111);
		when(mock.getRandomChar()).thenReturn('[');
		when(mock.getRandomByte()).thenReturn((byte) 2);
		when(mock.getRandomLong()).thenReturn(33L);
		when(mock.getRandomFloat()).thenReturn(44.4f);
		when(mock.getRandomDouble()).thenReturn(55.5d);
		when(mock.getRandomShort()).thenReturn((short) 44);
		when(mock.getRandomInt(anyInt())).thenReturn(2);
	}

	@After
	public void cleanup() {
		LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
	}

	/**
	 * Tests if the {@link FixedInstanceFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Long</code> and
	 * <code>Double</code>.
	 */
	@Test
	public void testObjectBindings() {
		assertThat(loremIpsumObjectCreator.createLoremIpsumObject(Long.class)).isCloseTo(Long.MAX_VALUE, offset(0L));
		assertThat(loremIpsumObjectCreator.createLoremIpsumObject(Double.class)).isCloseTo(55.5D, offset(0D));
	}

	/**
	 * Tests if the {@link ConstructorBasedFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Integer</code>.
	 */
	@Test
	public void testConstructorBindings() {
		assertThat(loremIpsumObjectCreator.createLoremIpsumObject(Integer.class).getClass()).isEqualTo(Integer.class);
	}

	/**
	 * Tests if the default binding for <code>List</code> (which is <code>ArrayList</code>) is returned and after providing some custom bindings if a
	 * <code>LinkedList</code> is now returned instead.
	 */
	@Test
	public void testInterfaceBindings() {
		assertThat(this.loremIpsumObjectCreator.createLoremIpsumObject(List.class).getClass()).isEqualTo(ArrayList.class);

		final ClassBindings classBindings = new ClassBindings();
		classBindings.bind(List.class, new ClassBasedFactory<>(ArrayList.class));
		classBindings.bind(List.class, new ClassBasedFactory<>(LinkedList.class));
		final LoremIpsumObjectCreator loremIpsumObjectCreator = new LoremIpsumObjectCreator(classBindings);
		assertThat(loremIpsumObjectCreator.createLoremIpsumObject(List.class).getClass()).isEqualTo(LinkedList.class);
	}

	/**
	 * Tests if array of <code>Integer</code> and <code>int</code> are created without problems.
	 */
	@SuppressWarnings("InstantiatingObjectToGetClassObject")
	@Test
	public void testArrayCreation() {
		final Integer[] integers = loremIpsumObjectCreator.createLoremIpsumObject(new Integer[]{}.getClass());
		assertThat(integers).isNotNull();
		for (final Integer i : integers) {
			assertThat(i.getClass()).isSameAs(Integer.class);
		}
		final int[] ints = loremIpsumObjectCreator.createLoremIpsumObject(new int[]{}.getClass());
		assertThat(ints).isNotNull();
	}

	/**
	 * Tests if a <code>String</code> is returned correctly.
	 */
	@Test
	public void testStringCreation() {
		final String dummy = loremIpsumObjectCreator.createLoremIpsumObject(String.class);
		assertThat(dummy.getClass()).isEqualTo(String.class);
	}

	/**
	 * Tests if a <code>Byte</code> and <code>Long</code> is created correctly without using custom factories.
	 */
	@Test
	public void testSimpleObjectCreation() {
		assertThat(loremIpsumObjectCreator.createLoremIpsumObject(Byte.class).getClass()).isEqualTo(Byte.class);
		assertThat(loremIpsumObjectCreator.createLoremIpsumObject(Long.class).getClass()).isEqualTo(Long.class);
	}

	/**
	 * Extra test to make sure {@link FixedInstanceFactory} returns the exact same instance.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testObjectBinding() {
		final ClassBindings classBindings = new ClassBindings();
		final LinkedList<Object> list = new LinkedList<>();
		classBindings.bind(List.class, new FixedInstanceFactory<List>(list));
		final LoremIpsumObjectCreator loremIpsumObjectCreator = new LoremIpsumObjectCreator(classBindings);
		final List<?> dummy = loremIpsumObjectCreator.createLoremIpsumObject(List.class);
		assertThat(dummy.getClass()).isEqualTo(LinkedList.class);
		assertThat(dummy).isSameAs(list);
	}

	/**
	 * Tests if <code>B</code> sub class <code>C</code> constructor is invoked correctly to produce an object assignable to <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeConstructorBinding() throws SecurityException, NoSuchMethodException {
		final ClassBindings classBindings = new ClassBindings();
		classBindings.bind(B.class, new ConstructorBasedFactory<>(C.class.getConstructor(int.class)));
		final LoremIpsumObjectCreator loremIpsumObjectCreator = new LoremIpsumObjectCreator(classBindings);
		final B dummy = loremIpsumObjectCreator.createLoremIpsumObject(B.class);
		assertThat(dummy.getClass()).isEqualTo(C.class);
	}

	/**
	 * Tests if <code>B</code> sub class <code>C</code> is created correctly (without providing explicit constructor) to produce an object assignable to
	 * <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeBinding() throws SecurityException {
		final ClassBindings classBindings = new ClassBindings();
		classBindings.bind(B.class, new ClassBasedFactory<>(C.class));
		final LoremIpsumObjectCreator loremIpsumObjectCreator = new LoremIpsumObjectCreator(classBindings);
		final B dummy = loremIpsumObjectCreator.createLoremIpsumObject(B.class);
		assertThat(dummy.getClass()).isEqualTo(C.class);
	}

	/**
	 * Tests if both <code>PrimitiveClass</code> (which contains some primitive fields and <code>InheritedPrimitiveClass</code> (which adds some inheritance) are
	 * produced correctly with the fields populated correctly.
	 */
	@Test
	public void testPrimitiveClassCreation() {
		final PrimitiveClass primitive = loremIpsumObjectCreator.createLoremIpsumObject(PrimitiveClass.class);
		final InheritedPrimitiveClass inheritedPrimitive = loremIpsumObjectCreator.createLoremIpsumObject(InheritedPrimitiveClass.class);
		assertThat(primitive.getClass()).isEqualTo(PrimitiveClass.class);
		assertThat(inheritedPrimitive.getClass()).isEqualTo(InheritedPrimitiveClass.class);

		testPrimitiveClass(primitive);
		testPrimitiveClass(inheritedPrimitive);
		assertThat(inheritedPrimitive.getSecondString()).isEqualTo("not so random test string");
	}

	/**
	 * Tests if the <code>NormalClass</code> is created correctly. It has a nested <code>PrimitiveClass</code>, so we're testing inheritance by composition here
	 * instead of direct inheritance. And then ofcourse the primitive fields need to be populated correctly again.
	 */
	@Test
	public void testNormalClassCreation() {
		NormalClass dummy = loremIpsumObjectCreator.createLoremIpsumObject(NormalClass.class);
		assertThat(dummy.getClass()).isEqualTo(NormalClass.class);

		testPrimitiveClass(dummy.getPrimitiveClass());
		assertThat(dummy.getId()).isEqualTo(111);
		assertThat(dummy.getSomeBoolean()).isEqualTo(true);
	}

	/**
	 * Tests if an infinite recursive loop is handle correctly.
	 */
	@Test
	public void testLoopClassCreation() {
		LoopClass loopDummy = loremIpsumObjectCreator.createLoremIpsumObject(LoopClass.class);
		assertThat(loopDummy.getClass()).isEqualTo(LoopClass.class);
		assertThat(loopDummy.getLoopObject()).isSameAs(loopDummy);
	}

	/**
	 * Tests whether complex constructors are invoked correctly.
	 */
	@Test
	public void testMultiConstructorClassCreation() {
		MultiConstructorClass dummy = loremIpsumObjectCreator.createLoremIpsumObject(MultiConstructorClass.class);
		assertThat(dummy.getClass()).isEqualTo(MultiConstructorClass.class);

		assertThat(dummy.getA()).isEqualTo(111);
		assertThat(dummy.getB()).isEqualTo(111);
		assertThat(dummy.getC()).isEqualTo(111);
		assertThat(dummy.getD()).isEqualTo(111);
		assertThat(dummy.getE()).isEqualTo(111);
		assertThat(dummy.getF()).isEqualTo(111);
		assertThat(dummy.getG()).isEqualTo(111);
		assertThat(dummy.getH()).isEqualTo(111);
		assertThat(dummy.getI()).isEqualTo(111);
		testPrimitiveClass(dummy.getP1());
		testPrimitiveClass(dummy.getP2());
		testPrimitiveClass(dummy.getP3());
		testPrimitiveClass(dummy.getP4());
		testPrimitiveClass(dummy.getP5());
		testPrimitiveClass(dummy.getP6());
		testPrimitiveClass(dummy.getP7());
	}

	private void testPrimitiveClass(PrimitiveClass primitive) {
		assertThat(primitive.get_char()).isEqualTo('[');
		assertThat(primitive.is_boolean()).isTrue();
		assertThat(primitive.get_long()).isEqualTo(33L);
		assertThat(primitive.get_double()).isCloseTo(55.5d, offset(0D));
		assertThat(primitive.get_secondDouble()).isCloseTo(55.5d, offset(0D));
		assertThat(primitive.get_string()).isEqualTo("not so random test string");
		assertThat(primitive.get_short()).isEqualTo((short) 44);
		assertThat(primitive.get_float()).isCloseTo(44.4f, offset(0F));
		assertThat(primitive.get_byte()).isEqualTo((byte) 2);
		assertThat(primitive.get_int()).isEqualTo(111);
	}

	/**
	 * Tests whether enumerations can be produced correctly.
	 */
	@Test
	public void testEnumClassCreation() {
		final EnumClass ec = loremIpsumObjectCreator.createLoremIpsumObject(EnumClass.class);
		assertThat(ec.getEnumTester()).isNotNull();
		assertThat(ec.getInternalEnum()).isNotNull();
	}

	@SuppressWarnings({"serial", "WeakerAccess"})
	public static class NumberStringMap extends HashMap<Integer, String> {
	}

	/**
	 * Tests if a <code>Map<Integer, String></code> will be created with the right type of objects inside.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testGenericMap() {
		final Map<Integer, String> numberStringMap = new NumberStringMap();
		@SuppressWarnings("unchecked") final Map<Integer, String> ec = loremIpsumObjectCreator.createLoremIpsumObject(numberStringMap.getClass());
		assertThat(ec).isNotNull();
		final Entry<Integer, String> firstItem = ec.entrySet().iterator().next();
		assertThat(firstItem.getKey().getClass()).isSameAs(Integer.class);
		assertThat(firstItem.getValue().getClass()).isSameAs(String.class);
	}

	@SuppressWarnings({"serial", "WeakerAccess"})
	public static class NumberStringList extends ArrayList<Integer> {
	}

	/**
	 * Tests if a <code>List<Integer></code> will be created with the right type of objects inside.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testGenericList() {
		final List<Integer> numbers = new NumberStringList();
		final List<?> ec = loremIpsumObjectCreator.createLoremIpsumObject(numbers.getClass());
		assertThat(ec).isNotNull();
		assertThat(ec.get(0).getClass()).isSameAs(Integer.class);
	}

	/**
	 * Tests whether a <code>List</code> of <code>Integer</code> will be produced correctly. The Result should contain strings, because the generic meta data is
	 * not available in runtime.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testList() {
		final List<Integer> numbers = new ArrayList<>();
		@SuppressWarnings("unchecked") final List<Integer> ec = loremIpsumObjectCreator.createLoremIpsumObject(numbers.getClass());
		assertThat(ec).isNotNull();
		assertThat(((Object) ec.get(0)).getClass()).isSameAs(String.class);
	}

	/**
	 * Tests whether a nested <code>List</code> of <code>Double</code> will be produced correctly (generics should be preserved). Includes testing nested list of
	 * lists up to 4 deep.
	 */
	@Test
	public void testNestedGenericList() {
		// single nested list
		final NestedSingleListClass dummySingle = loremIpsumObjectCreator.createLoremIpsumObject(NestedSingleListClass.class);
		assertThat(dummySingle.getNumbers().getClass()).isSameAs(ArrayList.class);
		assertThat(dummySingle.getNumbers().get(0).getClass()).isSameAs(Double.class);

		// double nested list
		final NestedDoubleListClass dummyDouble = loremIpsumObjectCreator.createLoremIpsumObject(NestedDoubleListClass.class);
		assertThat(dummyDouble.getListsOfNumbers().get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyDouble.getListsOfNumbers().get(0).get(0).getClass()).isSameAs(Double.class);

		// triple nested list
		final NestedTripleListClass dummyTriple = loremIpsumObjectCreator.createLoremIpsumObject(NestedTripleListClass.class);
		assertThat(dummyTriple.getListsOflistsOfNumbers().get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyTriple.getListsOflistsOfNumbers().get(0).get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyTriple.getListsOflistsOfNumbers().get(0).get(0).get(0).getClass()).isSameAs(Double.class);

		// quadruple nested list
		final NestedQuadrupleListClass dummyQuadruple = loremIpsumObjectCreator.createLoremIpsumObject(NestedQuadrupleListClass.class);
		assertThat(dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).get(0).get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyQuadruple.getListsOfListsOflistsOfNumbers().get(0).get(0).get(0).get(0).getClass()).isSameAs(LoopClass.class);
	}

	/**
	 * Tests whether a single nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testSingleSimpleNestedGenericMap() {
		// single nested map
		final NestedSingleSimpleMapClass dummySingle = loremIpsumObjectCreator.createLoremIpsumObject(NestedSingleSimpleMapClass.class);
		assertThat(dummySingle.getMap().getClass()).isSameAs(HashMap.class);
		final Entry<?, ?> firstEntrySingleMap = (Entry<?, ?>) dummySingle.getMap().entrySet().iterator().next();
		assertThat(firstEntrySingleMap.getKey().getClass()).isSameAs(String.class);
		assertThat(firstEntrySingleMap.getValue().getClass()).isSameAs(String.class);
	}

	/**
	 * Tests whether a single nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testSingleNestedGenericMap() {
		// single nested map
		final NestedSingleMapClass dummySingle = loremIpsumObjectCreator.createLoremIpsumObject(NestedSingleMapClass.class);
		assertThat(dummySingle.getNumbers().getClass()).isSameAs(HashMap.class);
		final Entry<Double, LoopClass> firstEntrySingleMap = dummySingle.getNumbers().entrySet().iterator().next();
		assertThat(firstEntrySingleMap.getKey().getClass()).isSameAs(Double.class);
		assertThat(firstEntrySingleMap.getValue().getClass()).isSameAs(LoopClass.class);
	}

	/**
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testDoubleNestedGenericMap() {
		// double nested map
		final NestedDoubleMapClass dummyDouble = loremIpsumObjectCreator.createLoremIpsumObject(NestedDoubleMapClass.class);
		assertThat(dummyDouble.getMapsOfNumbers().getClass()).isSameAs(HashMap.class);
		final Entry<Map<Integer, NestedDoubleMapClass>, Map<Double, LoopClass>> firstEntry = dummyDouble.getMapsOfNumbers().entrySet().iterator().next();

		assertThat(firstEntry.getKey().getClass()).isSameAs(HashMap.class);
		assertThat(firstEntry.getValue().getClass()).isSameAs(HashMap.class);

		final Entry<Integer, NestedDoubleMapClass> nestedKeyMap = firstEntry.getKey().entrySet().iterator().next();
		final Entry<Double, LoopClass> nestedValueMap = firstEntry.getValue().entrySet().iterator().next();

		assertThat(nestedKeyMap.getKey().getClass()).isSameAs(Integer.class);
		assertThat(nestedKeyMap.getValue().getClass()).isSameAs(NestedDoubleMapClass.class);
		assertThat(nestedValueMap.getKey().getClass()).isSameAs(Double.class);
		assertThat(nestedValueMap.getValue().getClass()).isSameAs(LoopClass.class);
	}

	/**
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved). Here the number of generic types on both sides
	 * (of the <code>Key</code> and <code>Value</code> side) are uneven.
	 */
	@Test
	public void testDoubleNestedAsymmetricGenericMap() {
		// double nested map
		final NestedDoubleAssymetricMapClass dummyDouble = loremIpsumObjectCreator.createLoremIpsumObject(NestedDoubleAssymetricMapClass.class);
		assertThat(dummyDouble.getMapsOfCharacters().getClass()).isSameAs(HashMap.class);
		final Entry<Map<Integer, NestedDoubleMapClass>, Character> firstEntry = dummyDouble.getMapsOfCharacters().entrySet().iterator().next();

		assertThat(firstEntry.getKey().getClass()).isSameAs(HashMap.class);
		assertThat(firstEntry.getValue().getClass()).isSameAs(Character.class);

		final Entry<Integer, NestedDoubleMapClass> nestedKeyMap = firstEntry.getKey().entrySet().iterator().next();

		assertThat(nestedKeyMap.getKey().getClass()).isSameAs(Integer.class);
		assertThat(nestedKeyMap.getValue().getClass()).isSameAs(NestedDoubleMapClass.class);
	}

	/**
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved), including nested generic lists.
	 */
	@Test
	public void testDoubleNestedGenericMapsAndLists() {
		// double nested map
		final NestedEverythingClass dummyDouble = loremIpsumObjectCreator.createLoremIpsumObject(NestedEverythingClass.class);
		assertThat(dummyDouble.getMapsOfLists().getClass()).isSameAs(HashMap.class);
		final Entry<Map<List<List<String>>, NestedDoubleMapClass>, List<Byte>> firstEntry = dummyDouble.getMapsOfLists().entrySet().iterator().next();

		assertThat(firstEntry.getKey().getClass()).isSameAs(HashMap.class);
		assertThat(firstEntry.getValue().getClass()).isSameAs(ArrayList.class);

		final Entry<List<List<String>>, NestedDoubleMapClass> nestedKeyMap = firstEntry.getKey().entrySet().iterator().next();

		assertThat(nestedKeyMap.getValue().getClass()).isSameAs(NestedDoubleMapClass.class);
		assertThat(firstEntry.getValue().get(0).getClass()).isSameAs(Byte.class);
		assertThat(nestedKeyMap.getKey().getClass()).isSameAs(ArrayList.class);
		assertThat(nestedKeyMap.getKey().get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(nestedKeyMap.getKey().get(0).get(0).getClass()).isSameAs(String.class);
	}

	/**
	 * Tests whether a triple nested <code>Map</code> will be produced correctly (generics should be preserved).
	 */
	@Test
	public void testTripleNestedGenericMap() {
		// triple nested map
		final NestedTripleMapClass dummyTriple = loremIpsumObjectCreator.createLoremIpsumObject(NestedTripleMapClass.class);
		assertThat(dummyTriple.getMapsOfMapsOfNumbers().getClass()).isSameAs(HashMap.class);
		final Entry<Map<Integer, Map<Double, LoopClass>>, Map<Double, Map<Double, LoopClass>>> firstEntryTriple = dummyTriple.getMapsOfMapsOfNumbers().entrySet().iterator().next();

		assertThat(firstEntryTriple.getKey().getClass()).isSameAs(HashMap.class);
		assertThat(firstEntryTriple.getValue().getClass()).isSameAs(HashMap.class);

		final Entry<Integer, Map<Double, LoopClass>> nestedKeyMapTriple = firstEntryTriple.getKey().entrySet().iterator().next();
		final Entry<Double, Map<Double, LoopClass>> nestedValueMapTriple = firstEntryTriple.getValue().entrySet().iterator().next();

		assertThat(nestedKeyMapTriple.getKey().getClass()).isSameAs(Integer.class);
		assertThat(nestedKeyMapTriple.getValue().getClass()).isSameAs(HashMap.class);
		assertThat(nestedValueMapTriple.getKey().getClass()).isSameAs(Double.class);
		assertThat(nestedValueMapTriple.getValue().getClass()).isSameAs(HashMap.class);

		final Entry<Double, LoopClass> nestedNestedValueMapTriple = nestedKeyMapTriple.getValue().entrySet().iterator().next();
		final Entry<Double, LoopClass> nestedNestedKeyMapTriple = nestedValueMapTriple.getValue().entrySet().iterator().next();

		assertThat(nestedNestedValueMapTriple.getKey().getClass()).isSameAs(Double.class);
		assertThat(nestedNestedValueMapTriple.getValue().getClass()).isSameAs(LoopClass.class);
		assertThat(nestedNestedKeyMapTriple.getKey().getClass()).isSameAs(Double.class);
		assertThat(nestedNestedKeyMapTriple.getValue().getClass()).isSameAs(LoopClass.class);
	}

	/**
	 * Tests whether a <code>List</code> of <code>MyCustomTestClass</code> will be produced correctly.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testMyCustomTestClassList() {
		final List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		final List<?> ec = loremIpsumObjectCreator.createLoremIpsumObject(numbers.getClass());
		assertThat(ec).isNotNull();
		assertThat(ec.get(0).getClass()).isSameAs(MyCustomTestClass.class);
	}

	/**
	 * Tests whether a <code>Map<Integer, String></code> will be produced correctly. The Result should contain strings for keys and values, because the generic
	 * meta data is not available in runtime.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testMap() {
		final Map<Integer, Double> numbers = new HashMap<>();
		@SuppressWarnings("unchecked") final Map<Integer, Double> ec = loremIpsumObjectCreator.createLoremIpsumObject(numbers.getClass());
		assertThat(ec).isNotNull();
		final Entry<Integer, Double> firstItem = ec.entrySet().iterator().next();
		assertThat(((Object) firstItem.getKey()).getClass()).isSameAs(String.class);
		assertThat(((Object) firstItem.getValue()).getClass()).isSameAs(String.class);
	}

	@Test
	public void testLorumIpsumBigDecimal() {
		BigDecimal bigDecimal = loremIpsumObjectCreator.createLoremIpsumObject(BigDecimal.class);
		assertThat(bigDecimal).isNotNull();
		assertThat(bigDecimal).isEqualTo(new BigDecimal("1.11"));
		System.out.println(bigDecimal);
		loremIpsumObjectCreator = new LoremIpsumObjectCreator();
		bigDecimal = loremIpsumObjectCreator.createLoremIpsumObject(BigDecimal.class);
		assertThat(bigDecimal).isNotNull();
		assertThat(bigDecimal).isEqualTo(new BigDecimal("1.11"));
	}

	@Test
	public void testLorumIpsumBigDecimalFixedScale() {
		LoremIpsumObjectCreator loremIpsumObjectCreator = new LoremIpsumObjectCreator(LoremIpsumConfig.builder().fixedBigdecimalScale(0).build());

		BigDecimal bigDecimal = loremIpsumObjectCreator.createLoremIpsumObject(BigDecimal.class);
		assertThat(bigDecimal).isNotNull();
		assertThat(bigDecimal).isEqualTo(new BigDecimal("111"));
		System.out.println(bigDecimal);

		loremIpsumObjectCreator = new LoremIpsumObjectCreator(LoremIpsumConfig.builder().fixedBigdecimalScale(1).build());
		bigDecimal = loremIpsumObjectCreator.createLoremIpsumObject(BigDecimal.class);
		assertThat(bigDecimal).isNotNull();
		assertThat(bigDecimal).isEqualTo(new BigDecimal("11.1"));

		loremIpsumObjectCreator = new LoremIpsumObjectCreator(LoremIpsumConfig.builder().fixedBigdecimalScale(2).build());
		bigDecimal = loremIpsumObjectCreator.createLoremIpsumObject(BigDecimal.class);
		assertThat(bigDecimal).isNotNull();
		assertThat(bigDecimal).isEqualTo(new BigDecimal("1.11"));
	}

	@Test
	public void testLombokHeavyProcessStatus() {
		ProcessStatus processStatus = loremIpsumObjectCreator.createLoremIpsumObject(ProcessStatus.class);
		assertThat(processStatus).isNotNull();
		assertThat(processStatus).isEqualTo(ProcessStatus.builder()
				.success(true)
				.code("not so random test string")
				.message("not so random test string")
				.build());
		System.out.println(processStatus);
	}

	/**
	 * Tests whether the right error will be produced in case an abstract or interface type should be created where no binding is available to indicate the
	 * correct implementation.
	 */
	@SuppressWarnings("ThrowablePrintedToSystemOut")
	@Test
	public void testInterfaceBindingErrors() {
		loremIpsumObjectCreator = new LoremIpsumObjectCreator();
		try {
			assertThat(loremIpsumObjectCreator.createLoremIpsumObject(Queue.class).getClass()).isEqualTo(ArrayList.class);
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (final IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
		try {
			assertThat(loremIpsumObjectCreator.createLoremIpsumObject(AbstractQueue.class).getClass()).isEqualTo(ArrayList.class);
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (final IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
	}

	@Test
	public void testOptionalDirect() {
		final boolean JAVA_8_OR_LATER = System.getProperty("java.specification.version").compareTo("1.8") >= 0;
		Assume.assumeTrue(JAVA_8_OR_LATER);

		final Class<?> classOptional = requireNonNull(locateClass("Optional", false, null));

		Object optional = loremIpsumObjectCreator.createLoremIpsumObject(classOptional);
		assertThat(optional).isNotNull();
		assertThat(optional).isInstanceOf(classOptional);

		Method optionalGet = requireNonNull(findFirstMethodByName(classOptional, Object.class, of(PUBLIC), "get"));
		Object value = MethodUtils.invokeMethodSimple(optionalGet, optional);

		assertThat(value).isEqualTo("not so random test string");
	}

	// uncomment all below code when testing with JDK8+

//	@Test
//	public void testHiddenOptionalWithTypeT() {
//		HiddenClassWithOptional value = loremIpsumObjectCreator.createLoremIpsumObject(HiddenClassWithOptional.class);
//		assertThat(value).isNotNull();
//		assertThat(value.getOCharByConstructor().get()).isInstanceOf(AtomicReferenceOfTypeT.class);
//		assertThat(value.getOCharByConstructor().get().get()).isInstanceOf(Character.class);
//		assertThat(value.getOIntByConstructor().get()).isInstanceOf(AtomicReferenceOfTypeT.class);
//		assertThat(value.getOIntByConstructor().get().get()).isInstanceOf(Integer.class);
//		assertThat(value.getOStringRefBySetter().get()).isInstanceOf(AtomicReferenceOfTypeT.class);
//		assertThat(value.getOStringRefBySetter().get().get()).isInstanceOf(String.class);
//		assertThat(value.getODoubleRefBySetter().get()).isInstanceOf(AtomicReferenceOfTypeT.class);
//		assertThat(value.getODoubleRefBySetter().get().get()).isInstanceOf(Double.class);
//	}
//
//	@Test
//	public void testOptionalIndirect() {
//		OptionalTestClass value = loremIpsumObjectCreator.createLoremIpsumObject(OptionalTestClass.class);
//
//		assertThat(value).isNotNull();
//		assertThat(value.getNumbersFromConstructor()).isNotNull();
//		assertThat(value.getNumbersFromConstructor().isPresent()).isTrue();
//		assertThat(value.getSimpleOptionalFromMethod()).isNotNull();
//		assertThat(value.getSimpleOptionalFromMethod().isPresent()).isTrue();
//		assertThat(value.getComplexOptionalFromMethod()).isNotNull();
//		assertThat(value.getComplexOptionalFromMethod().isPresent()).isTrue();
//
//		assertThat(value.getNumbersFromConstructor().get()).isInstanceOf(List.class);
//		assertThat(value.getNumbersFromConstructor().get()).isNotEmpty();
//		assertThat(value.getNumbersFromConstructor().get().get(0)).isInstanceOf(Integer.class);
//		assertThat(value.getNumbersFromConstructor().get().get(0)).isEqualTo(111);
//
//		assertThat(value.getSimpleOptionalFromMethod().get()).isInstanceOf(Double.class);
//		assertThat(value.getSimpleOptionalFromMethod().get()).isEqualTo(55.5d);
//
//		assertThat(value.getComplexOptionalFromMethod().get()).isInstanceOf(List.class);
//		assertThat(value.getComplexOptionalFromMethod().get()).isNotEmpty();
//		assertThat(value.getComplexOptionalFromMethod().get().get(0)).isInstanceOf(Number.class);
//	}
//
//	@Data
//	static class OptionalTestClass {
//		private final Optional<List<Integer>> numbersFromConstructor;
//		private Optional<Double> simpleOptionalFromMethod;
//		private Optional<List<Number>> complexOptionalFromMethod;
//	}
//
//	@Data
//	@RequiredArgsConstructor
//	private static class HiddenClassWithOptional {
//		private final Optional<AtomicReferenceOfTypeT<Character>> oCharByConstructor;
//		private final Optional<AtomicReferenceOfTypeT<Integer>> oIntByConstructor;
//		private Optional<AtomicReferenceOfTypeT<String>> oStringRefBySetter;
//		private Optional<AtomicReferenceOfTypeT<Double>> oDoubleRefBySetter;
//	}
//
//	private static class AtomicReferenceOfTypeT<T> extends AtomicReference<T> {
//		AtomicReferenceOfTypeT(T value) {
//			super(value);
//		}
//	}
}