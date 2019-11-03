package org.bbottema.loremipsumobjects.typefactories;

import org.assertj.core.api.Condition;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.LoremIpsumObjectCreator;
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
import org.bbottema.loremipsumobjects.helperutils.TestChainBinding.B;
import org.bbottema.loremipsumobjects.helperutils.TestChainBinding.C;
import org.bbottema.loremipsumobjects.helperutils.TimeoutSimulator;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.jetbrains.annotations.Nullable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.data.Offset.offset;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.ArrayListIntegerHoldingClass;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.ListIntegerHoldingClass;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.ListMyDataClassHoldingClass;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.ListMyValueClassHoldingClass;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.ListMyValueClassesHoldingClass;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.MyDataClass;
import static org.bbottema.loremipsumobjects.helperutils.ListGenericsClasses.MyValueClass;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ClassBasedFactoryTest {
	
	private ClassBindings classBindings;
	
	/**
	 * Adds some default bindings useful for these tests, such as some {@link FixedInstanceFactory} instances and a {@link ConstructorBasedFactory}.
	 */
	@Before
	public void setUp() throws SecurityException, NoSuchMethodException {
		classBindings = new ClassBindings();
		classBindings.bind(Integer.class, new ConstructorBasedFactory<>(Integer.class.getConstructor(Integer.TYPE)));
		classBindings.bind(Long.class, new FixedInstanceFactory<>(Long.MAX_VALUE));
		classBindings.bind(Double.class, new FixedInstanceFactory<>(Double.MIN_VALUE));
		
		LoremIpsumGenerator mock = mock(LoremIpsumGenerator.class);
		LoremIpsumGenerator.setInstance(mock);
		
		when(mock.getRandomString()).thenReturn("not so random test string");
		when(mock.getRandomBoolean()).thenReturn(true);
		when(mock.getRandomInt()).thenReturn(111);
		when(mock.getRandomChar()).thenReturn('[');
		when(mock.getRandomByte()).thenReturn((byte) 2);
		when(mock.getRandomLong()).thenReturn(33L);
		when(mock.getRandomFloat()).thenReturn(44f);
		when(mock.getRandomDouble()).thenReturn(55.5d);
		when(mock.getRandomShort()).thenReturn((short) 44);
		when(mock.getRandomInt(anyInt())).thenReturn(2);
	}
	
	@After
	public void cleanup() {
		LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
	}
	
	@Test
	public void testPrimitives() {
		LoremIpsumObjectCreator creator = new LoremIpsumObjectCreator();
		
		assertThat(creator.createLoremIpsumObject(int.class)).isEqualTo(111);
		assertThat(creator.createLoremIpsumObject(String.class)).isEqualTo("not so random test string");
	}
	
	/**
	 * Tests if the {@link FixedInstanceFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Long</code> and
	 * <code>Double</code>.
	 */
	@Test
	public void testObjectBindings() {
		assertThat(new ClassBasedFactory<>(Long.class).createLoremIpsumObject(classBindings)).isCloseTo(Long.MAX_VALUE, offset(0L));
		assertThat(new ClassBasedFactory<>(Double.class).createLoremIpsumObject(classBindings)).isCloseTo(Double.MIN_VALUE, offset(0D));
	}
	
	/**
	 * Tests if the {@link ConstructorBasedFactory} is invoked correctly by the {@link ClassBasedFactory} for <code>Integer</code>.
	 */
	@Test
	public void testConstructorBindings() {
		assertThat(new ClassBasedFactory<>(Integer.class).createLoremIpsumObject(classBindings).getClass()).isEqualTo(Integer.class);
	}
	
	/**
	 * Tests if the default binding for <code>List</code> (which is <code>ArrayList</code>) is returned and after providing some custom bindings if a <code>LinkedList</code> is now returned instead.
	 */
	@Test
	@SuppressWarnings("rawtypes")
	public void testInterfaceBindings() {
		assertThat(new ClassBasedFactory<>(List.class).createLoremIpsumObject(classBindings).getClass()).isEqualTo(ArrayList.class);
		final ClassBindings classBindings = new ClassBindings();
		classBindings.bind(List.class, new ClassBasedFactory<>(ArrayList.class));
		classBindings.bind(List.class, new ClassBasedFactory<>(LinkedList.class));
		assertThat(new ClassBasedFactory<>(List.class).createLoremIpsumObject(classBindings).getClass()).isEqualTo(LinkedList.class);
	}
	
	/**
	 * Tests if array of <code>Integer</code> and <code>int</code> are created without problems.
	 */
	@SuppressWarnings("InstantiatingObjectToGetClassObject")
	@Test
	public void testArrayCreation() {
		@SuppressWarnings("unchecked") final Integer[] integers = new ClassBasedFactory<>((Class<Integer[]>) new Integer[]{}.getClass()).createLoremIpsumObject(classBindings);
		assertThat(integers).isNotNull();
		for (final Integer i : integers) {
			assertThat(i.getClass()).isSameAs(Integer.class);
		}
		@SuppressWarnings("unchecked") final int[] ints = new ClassBasedFactory<>((Class<int[]>) new int[]{}.getClass()).createLoremIpsumObject(classBindings);
		assertThat(ints).isNotNull();
	}
	
	/**
	 * Tests if a <code>String</code> is returned correctly.
	 */
	@Test
	public void testStringCreation() {
		final String dummy = new ClassBasedFactory<>(String.class).createLoremIpsumObject(classBindings);
		assertThat(dummy.getClass()).isEqualTo(String.class);
	}
	
	/**
	 * Tests if a <code>Byte</code> and <code>Long</code> is created correctly without using custom factories.
	 */
	@Test
	public void testSimpleObjectCreation() {
		assertThat(new ClassBasedFactory<>(Byte.class).createLoremIpsumObject(classBindings).getClass()).isEqualTo(Byte.class);
		assertThat(new ClassBasedFactory<>(Long.class).createLoremIpsumObject(classBindings).getClass()).isEqualTo(Long.class);
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
		final List<?> dummy = new ClassBasedFactory<>(List.class).createLoremIpsumObject(classBindings);
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
		final B dummy = new ClassBasedFactory<>(B.class).createLoremIpsumObject(classBindings);
		assertThat(dummy.getClass()).isEqualTo(C.class);
	}
	
	/**
	 * Tests if <code>B</code> sub class <code>C</code> is created correctly (without providing explicit constructor) to produce an object assignable to <code>B</code>.
	 */
	@Test
	public void testDeferredSubTypeBinding() throws SecurityException {
		final ClassBindings classBindings = new ClassBindings();
		classBindings.bind(B.class, new ClassBasedFactory<>(C.class));
		final B dummy = new ClassBasedFactory<>(B.class).createLoremIpsumObject(classBindings);
		assertThat(dummy.getClass()).isEqualTo(C.class);
	}
	
	/**
	 * Tests if both <code>PrimitiveClass</code> (which contains some primitive fields and <code>InheritedPrimitiveClass</code> (which adds some inheritance) are produced correctly with the fields
	 * populated correctly.
	 */
	@Test
	public void testPrimitiveClassCreation() {
		final PrimitiveClass primitive = new ClassBasedFactory<>(PrimitiveClass.class).createLoremIpsumObject(classBindings);
		final InheritedPrimitiveClass inheritedPrimitive = new ClassBasedFactory<>(InheritedPrimitiveClass.class).createLoremIpsumObject(classBindings);
		assertThat(primitive.getClass()).isEqualTo(PrimitiveClass.class);
		assertThat(inheritedPrimitive.getClass()).isEqualTo(InheritedPrimitiveClass.class);
		
		testPrimitiveClass(primitive);
		testPrimitiveClass(inheritedPrimitive);
		assertThat(inheritedPrimitive.getSecondString()).isEqualTo("not so random test string");
	}
	
	/**
	 * Tests if the <code>NormalClass</code> is created correctly. It has a nested <code>PrimitiveClass</code>, so we're testing inheritance by composition here instead of direct inheritance. And then
	 * ofcourse the primitive fields need to be populated correctly again.
	 */
	@Test
	public void testNormalClassCreation() {
		NormalClass dummy = new ClassBasedFactory<>(NormalClass.class).createLoremIpsumObject(classBindings);
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
		final LoopClass loopDummy = new ClassBasedFactory<>(LoopClass.class).createLoremIpsumObject(classBindings);
		assertThat(loopDummy.getClass()).isEqualTo(LoopClass.class);
		assertThat(loopDummy.getLoopObject()).isSameAs(loopDummy);
	}
	
	/**
	 * Tests whether complex constructors are invoked correctly.
	 */
	@Test
	public void testMultiConstructorClassCreation() {
		final MultiConstructorClass dummy = new ClassBasedFactory<>(MultiConstructorClass.class).createLoremIpsumObject(classBindings);
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
		assertThat(primitive.get_float()).isCloseTo(44f, offset(0F));
		assertThat(primitive.get_byte()).isEqualTo((byte) 2);
		assertThat(primitive.get_int()).isEqualTo(111);
	}
	
	/**
	 * Tests whether enumerations can be produced correctly.
	 */
	@Test
	public void testEnumClassCreation() {
		final EnumClass ec = new ClassBasedFactory<>(EnumClass.class).createLoremIpsumObject(classBindings);
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
		@SuppressWarnings("unchecked") final ClassBasedFactory<Map<Integer, String>> factory = new ClassBasedFactory<>((Class<Map<Integer, String>>) numberStringMap.getClass());
		final Map<Integer, String> ec = factory.createLoremIpsumObject(classBindings);
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
		@SuppressWarnings("unchecked")
		List<Integer> ec = new ClassBasedFactory<>((Class<List<Integer>>) numbers.getClass()).createLoremIpsumObject(classBindings);
		assertThat(ec).isNotNull();
		assertThat(ec.get(0).getClass()).isSameAs(Integer.class);
	}
	
	/**
	 * Tests whether a <code>List</code> of <code>Integer</code> will be produced correctly. The Result should contain strings, because the generic meta data is not available in runtime.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testList() {
		final List<Integer> numbers = new ArrayList<>();
		@SuppressWarnings("unchecked") final ClassBasedFactory<List<Integer>> factory = new ClassBasedFactory<>((Class<List<Integer>>) numbers.getClass());
		final List<Integer> ec = factory.createLoremIpsumObject(classBindings);
		assertThat(ec).isNotNull();
		assertThat(((Object) ec.get(0)).getClass()).isSameAs(String.class);
	}
	
	/**
	 * Tests whether a nested <code>List</code> of <code>Double</code> will be produced correctly (generics should be preserved). Includes testing nested list of lists up to 4 deep.
	 */
	@Test
	public void testNestedGenericList() {
		// single nested list
		final ClassBasedFactory<NestedSingleListClass> factorySingle = new ClassBasedFactory<>(NestedSingleListClass.class);
		final NestedSingleListClass dummySingle = factorySingle.createLoremIpsumObject(classBindings);
		assertThat(dummySingle.getNumbers().getClass()).isSameAs(ArrayList.class);
		assertThat(dummySingle.getNumbers().get(0).getClass()).isSameAs(Double.class);
		
		// double nested list
		final ClassBasedFactory<NestedDoubleListClass> factoryDouble = new ClassBasedFactory<>(NestedDoubleListClass.class);
		final NestedDoubleListClass dummyDouble = factoryDouble.createLoremIpsumObject(classBindings);
		assertThat(dummyDouble.getListsOfNumbers().get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyDouble.getListsOfNumbers().get(0).get(0).getClass()).isSameAs(Double.class);
		
		// triple nested list
		final ClassBasedFactory<NestedTripleListClass> factoryTriple = new ClassBasedFactory<>(NestedTripleListClass.class);
		final NestedTripleListClass dummyTriple = factoryTriple.createLoremIpsumObject(classBindings);
		assertThat(dummyTriple.getListsOflistsOfNumbers().get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyTriple.getListsOflistsOfNumbers().get(0).get(0).getClass()).isSameAs(ArrayList.class);
		assertThat(dummyTriple.getListsOflistsOfNumbers().get(0).get(0).get(0).getClass()).isSameAs(Double.class);
		
		// quadruple nested list
		final ClassBasedFactory<NestedQuadrupleListClass> factoryQuadruple = new ClassBasedFactory<>(NestedQuadrupleListClass.class);
		final NestedQuadrupleListClass dummyQuadruple = factoryQuadruple.createLoremIpsumObject(classBindings);
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
		final ClassBasedFactory<NestedSingleSimpleMapClass> factorySingle = new ClassBasedFactory<>(NestedSingleSimpleMapClass.class);
		final NestedSingleSimpleMapClass dummySingle = factorySingle.createLoremIpsumObject(classBindings);
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
		final ClassBasedFactory<NestedSingleMapClass> factorySingle = new ClassBasedFactory<>(NestedSingleMapClass.class);
		final NestedSingleMapClass dummySingle = factorySingle.createLoremIpsumObject(classBindings);
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
		final ClassBasedFactory<NestedDoubleMapClass> factoryDouble = new ClassBasedFactory<>(NestedDoubleMapClass.class);
		final NestedDoubleMapClass dummyDouble = factoryDouble.createLoremIpsumObject(classBindings);
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
	 * Tests whether a double nested <code>Map</code> will be produced correctly (generics should be preserved). Here the number of generic types on both sides (of the <code>Key</code> and
	 * <code>Value</code> side) are uneven.
	 */
	@Test
	public void testDoubleNestedAsymmetricGenericMap() {
		// double nested map
		final ClassBasedFactory<NestedDoubleAssymetricMapClass> factoryDouble = new ClassBasedFactory<>(NestedDoubleAssymetricMapClass.class);
		final NestedDoubleAssymetricMapClass dummyDouble = factoryDouble.createLoremIpsumObject(classBindings);
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
		final ClassBasedFactory<NestedEverythingClass> factoryDouble = new ClassBasedFactory<>(NestedEverythingClass.class);
		final NestedEverythingClass dummyDouble = factoryDouble.createLoremIpsumObject(classBindings);
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
		final ClassBasedFactory<NestedTripleMapClass> factoryTriple = new ClassBasedFactory<>(NestedTripleMapClass.class);
		final NestedTripleMapClass dummyTriple = factoryTriple.createLoremIpsumObject(classBindings);
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
	
	@Test
	public void testCreateTypeMarkerLists() throws SecurityException, NoSuchFieldException {
		final Field fieldSingle = NestedSingleListClass.class.getField("numbers");
		final Field fieldDouble = NestedDoubleListClass.class.getField("listsOfNumbers");
		final Field fieldTriple = NestedTripleListClass.class.getField("listsOflistsOfNumbers");
		final String markerSingle = ClassBasedFactory.createTypeMarker(fieldSingle.getType(), new Type[]{fieldSingle.getGenericType()});
		final String markerDouble = ClassBasedFactory.createTypeMarker(fieldDouble.getType(), new Type[]{fieldDouble.getGenericType()});
		final String markerTriple = ClassBasedFactory.createTypeMarker(fieldTriple.getType(), new Type[]{fieldTriple.getGenericType()});
		assertThat(markerSingle).isEqualTo("|java.util.List|java.util.List|java.lang.Double|");
		assertThat(markerDouble).isEqualTo("|java.util.List|java.util.List|java.util.List|java.lang.Double|");
		assertThat(markerTriple).isEqualTo("|java.util.List|java.util.List|java.util.List|java.util.List|java.lang.Double|");
	}
	
	@Test
	public void testCreateTypeMarkerMaps() throws SecurityException, NoSuchFieldException {
		final Field fieldSingle = NestedSingleMapClass.class.getField("numbers");
		final Field fieldDouble = NestedDoubleMapClass.class.getField("MapsOfNumbers");
		final Field fieldTriple = NestedTripleMapClass.class.getField("mapsOfMapsOfNumbers");
		final String markerSingle = ClassBasedFactory.createTypeMarker(fieldSingle.getType(), new Type[]{fieldSingle.getGenericType()});
		final String markerDouble = ClassBasedFactory.createTypeMarker(fieldDouble.getType(), new Type[]{fieldDouble.getGenericType()});
		final String markerTriple = ClassBasedFactory.createTypeMarker(fieldTriple.getType(), new Type[]{fieldTriple.getGenericType()});
		assertThat(markerSingle).isEqualTo("|java.util.Map" +
				"|java.util.Map" +
				"|java.lang.Double" +
				"|org.bbottema.loremipsumobjects.helperutils.LoopClass|");
		assertThat(markerDouble).isEqualTo("|java.util.Map" +
				"|java.util.Map" +
				"|java.util.Map" +
				"|java.lang.Integer" +
				"|org.bbottema.loremipsumobjects.helperutils.NestedMapClass$NestedDoubleMapClass" +
				"|java.util.Map" +
				"|java.lang.Double" +
				"|org.bbottema.loremipsumobjects.helperutils.LoopClass|");
		assertThat(markerTriple).isEqualTo("|java.util.Map" +
				"|java.util.Map" +
				"|java.util.Map" +
				"|java.lang.Integer" +
				"|java.util.Map" +
				"|java.lang.Double" +
				"|org.bbottema.loremipsumobjects.helperutils.LoopClass" +
				"|java.util.Map|java.lang.Double" +
				"|java.util.Map|java.lang.Double" +
				"|org.bbottema.loremipsumobjects.helperutils.LoopClass|");
	}
	
	/**
	 * Tests whether a <code>List</code> of <code>MyCustomTestClass</code> will be produced correctly.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testMyCustomTestClassList() {
		final List<MyCustomTestClass> numbers = new MyCustomTestClassList();
		@SuppressWarnings("unchecked") final ClassBasedFactory<List<MyCustomTestClass>> factory = new ClassBasedFactory<>((Class<List<MyCustomTestClass>>) numbers.getClass());
		final List<MyCustomTestClass> ec = factory.createLoremIpsumObject(classBindings);
		assertThat(ec).isNotNull();
		assertThat(ec.get(0).getClass()).isSameAs(MyCustomTestClass.class);
	}
	
	/**
	 * Tests whether a <code>Map<Integer, String></code> will be produced correctly. The Result should contain strings for keys and values, because the generic meta data is not available in runtime.
	 */
	@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
	@Test
	public void testMap() {
		final Map<Integer, Double> numbers = new HashMap<>();
		@SuppressWarnings("unchecked") final ClassBasedFactory<Map<Integer, Double>> factory = new ClassBasedFactory<>((Class<Map<Integer, Double>>) numbers.getClass());
		final Map<Integer, Double> ec = factory.createLoremIpsumObject(classBindings);
		assertThat(ec).isNotNull();
		final Entry<Integer, Double> firstItem = ec.entrySet().iterator().next();
		assertThat(((Object) firstItem.getKey()).getClass()).isSameAs(String.class);
		assertThat(((Object) firstItem.getValue()).getClass()).isSameAs(String.class);
	}
	
	/**
	 * Tests whether the right error will be produced in case an abstract or interface type should be created where no binding is available to indicate the correct implementation.
	 */
	@Test
	@SuppressWarnings({"ThrowablePrintedToSystemOut", "ConstantConditions"})
	public void testInterfaceBindingErrors() {
		try {
			//noinspection ResultOfMethodCallIgnored
			new ClassBasedFactory<>(Queue.class).createLoremIpsumObject().getClass();
			fail("illegal argument exception expected (can't instantiate abstract type or interface)");
		} catch (final IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
		try {
			//noinspection ResultOfMethodCallIgnored
			new ClassBasedFactory<>(AbstractQueue.class).createLoremIpsumObject().getClass();
			fail("illegal argument exception expected (can't instantiate abstract type or interface");
		} catch (final IllegalArgumentException e) {
			System.out.println(e);
			// ok
		}
	}
	
	@Test
	public void testTimeouts() {
		final TimeoutSimulator result = new ClassBasedFactory<>(TimeoutSimulator.class).createLoremIpsumObject();
		
		assertThat(result).isNotNull();
		assertThat(TimeoutSimulator.contructorTimeoutTriggered).isTrue();
		assertThat(TimeoutSimulator.methodTimeoutTriggered).isTrue();
		assertThat(result.getConstructorValue()).isEqualTo(111);
		assertThat(result.getMethod1Value()).is(nullOrEqual(111));
		assertThat(result.getMethod2Value()).is(nullOrEqual(55.5f));
		assertThat(result.getMethod1Value()).isNotEqualTo(result.getMethod2Value());
	}
	
	private <T extends Number> Condition<T> nullOrEqual(final T number) {
		return new Condition<T>() {
			@Override
			public boolean matches(@Nullable T value) {
				return value == null || value.equals(number);
			}
		};
	}
	
	@Test
	public void testSimpleGenericsArrayList() {
		ArrayListIntegerHoldingClass result = new ClassBasedFactory<>(ArrayListIntegerHoldingClass.class).createLoremIpsumObject();
		
		assertThat(result).isNotNull();
		assertThat(result.getIntegers()).isNotEmpty();
		assertThat(result.getIntegers().get(0)).isInstanceOf(Integer.class);
	}
	
	@Test
	public void testSimpleGenericsList() {
		ListIntegerHoldingClass result = new ClassBasedFactory<>(ListIntegerHoldingClass.class).createLoremIpsumObject();
		
		assertThat(result).isNotNull();
		assertThat(result.getIntegers()).isNotEmpty();
		assertThat(result.getIntegers().get(0)).isInstanceOf(Integer.class);
	}
	
	@Test
	public void testSimpleGenericsListMyDataClass() {
		@Nullable ListMyDataClassHoldingClass result = new ClassBasedFactory<>(ListMyDataClassHoldingClass.class).createLoremIpsumObject();
		
		assertThat(result).isNotNull();
		assertThat(result.getMyClasses()).isNotEmpty();
		assertThat(result.getMyClasses().get(0)).isInstanceOf(MyDataClass.class);
		assertThat(result.getMyClasses().get(0).getIntegers()).isNotEmpty();
		assertThat(result.getMyClasses().get(0).getIntegers().iterator().next()).isInstanceOf(Double.class);
	}
	
	@Test
	public void testSimpleGenericsListMyValueClass() {
		@Nullable ListMyValueClassHoldingClass result = new ClassBasedFactory<>(ListMyValueClassHoldingClass.class).createLoremIpsumObject();
		
		assertThat(result).isNotNull();
		assertThat(result.getMyClasses()).isNotEmpty();
		assertThat(result.getMyClasses().get(0)).isInstanceOf(MyValueClass.class);
		assertThat(result.getMyClasses().get(0).getValue()).isInstanceOf(Double.class);
	}
	
	@Test
	public void testSimpleGenericsListMyValueClasses() {
		@Nullable ListMyValueClassesHoldingClass result = new ClassBasedFactory<>(ListMyValueClassesHoldingClass.class).createLoremIpsumObject();
		
		assertThat(result).isNotNull();
		assertThat(result.getMyClasses()).isNotEmpty();
		assertThat(result.getMyClasses().get(0)).isInstanceOf(MyValueClass.class);
		assertThat(result.getMyClasses().get(0).getValue()).isInstanceOf(Double.class);
		assertThat(result.getInts().get(0)).isInstanceOf(Integer.class);
		assertThat(result.getStrings().get(0)).isInstanceOf(String.class);
	}
}