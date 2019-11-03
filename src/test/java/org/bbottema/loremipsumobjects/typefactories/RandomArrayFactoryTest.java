package org.bbottema.loremipsumobjects.typefactories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.assertj.core.api.AbstractObjectArrayAssert;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RandomArrayFactoryTest {

	private LoremIpsumGenerator mock;

	@Before
	public void setup() {
		mock = mock(LoremIpsumGenerator.class);
		LoremIpsumGenerator.setInstance(mock);
	}

	@After
	public void cleanup() {
		LoremIpsumGenerator.setInstance(new LoremIpsumGenerator());
	}

	@Test
	public void testCreateLoremIpsumInteger() {
		final RandomArrayFactory<Integer[]> factory = new RandomArrayFactory<>(Integer[].class);

		when(mock.getRandomInt(2)).thenReturn(1); // random array length
		// the following might not happen depending on how the list of constructors are sorted
		when(mock.getRandomString()).thenReturn("abc").thenReturn("abc"); // Integer(String), which should fail
		when(mock.getRandomInt()).thenReturn(12345).thenReturn(54321).thenReturn(678768); // Integer(int), which should succeed

		AbstractObjectArrayAssert<?, Integer> integerAbstractObjectArrayAssert = assertThat(factory.createLoremIpsumObject());
		integerAbstractObjectArrayAssert.containsExactlyElementsOf(newArrayList(12345, 54321, 678768));
	}

	/**
	 * Tests if a array of primitive <code>int</code> can be created correctly.
	 */
	@Test
	public void testCreateLoremIpsumInt() {
		final RandomArrayFactory<int[]> factory = new RandomArrayFactory<>(int[].class);

		when(mock.getRandomInt(2)).thenReturn(1); // random array length
		when(mock.getRandomInt()).thenReturn(12345).thenReturn(54321).thenReturn(678768);

		assertThat(factory.createLoremIpsumObject()).containsExactly(12345, 54321, 678768);
	}

	/**
	 * Tests if a array of <code>String</code> can be created correctly.
	 */
	@Test
	public void testCreateLoremIpsumString() {
		final RandomArrayFactory<String[]> factory = new RandomArrayFactory<>(String[].class);

		when(mock.getRandomInt(2)).thenReturn(1); // random array length
		when(mock.getRandomString()).thenReturn("1234").thenReturn("aasdf").thenReturn("test");

		assertThat(factory.createLoremIpsumObject()).containsExactlyElementsOf(newArrayList("1234", "aasdf", "test"));
	}

	/**
	 * Tests if a array of <code>TestClass</code> can be created correctly.
	 */
	@Test
	public void testCreateLoremIpsumTestClass() {
		final RandomArrayFactory<TestClass[]> factory = new RandomArrayFactory<>(TestClass[].class);

		when(mock.getRandomInt(2)).thenReturn(0); // random array length
		when(mock.getRandomBoolean()).thenReturn(true);
		when(mock.getRandomInt()).thenReturn(9876);

		final TestClass instance1 = new TestClass();
		instance1.setBooltje(true);
		instance1.setIntje(9876);
		instance1.setTestClass(instance1);
		final TestClass instance2 = new TestClass();
		instance2.setBooltje(true);
		instance2.setIntje(9876);
		instance2.setTestClass(instance2);

		assertThat(factory.createLoremIpsumObject()).containsExactlyElementsOf(newArrayList(instance1, instance2));
	}

	/**
	 * Tests if a array of <code>Integer[]</code> can be created correctly.
	 */
	@Test
	public void testCreateLoremIpsumIntegerArray() {
		final RandomArrayFactory<Integer[][]> factory = new RandomArrayFactory<>(Integer[][].class);

		when(mock.getRandomInt(2))
				.thenReturn(0) // random master array length
				.thenReturn(0) // random sub array 1 length
				.thenReturn(1); // random sub array 2 length

		when(mock.getRandomString()).thenReturn("abc").thenReturn("abc"); // Integer(String) for sub array 1, which should fail
		when(mock.getRandomInt())
				.thenReturn(1234) // 1st array: Integer(int) for sub array 1 index 0, which should succeed and become preferred constructor
				.thenReturn(4321) // 1st array: Integer(int) for sub array 1 index 1, which should succeed
				.thenReturn(5678) // 2nd array: Integer(int) for sub array 2 index 0, which should succeed
				.thenReturn(8765) // 2nd array: Integer(int) for sub array 2 index 1, which should succeed
				.thenReturn(1029); // 2nd array: Integer(int) for sub array 2 index 2, which should succeed

		final Integer[][] dummy = factory.createLoremIpsumObject();

		assertThat(dummy).containsExactlyElementsOf(newArrayList(new Integer[]{1234, 4321}, new Integer[]{5678, 8765, 1029}));
	}

	/**
	 * Test class used to test if nested fields are populated correctly when the objects are created in the context of an array. This
	 * includes a simple test for recursive loops (the <code>testClass</code> field should be a reference to itself).
	 */
	@Data
	@SuppressFBWarnings(justification = "Generated code")
	public static class TestClass {
		boolean booltje;
		int intje;
		@ToString.Exclude
		@EqualsAndHashCode.Exclude
		TestClass testClass;
	}
}