package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertArrayEquals;

import org.dummycreator.ClassBindings;
import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link RandomArrayFactory}
 */
public class RandomArrayFactoryTest {

	private RandomCreator mock;

	@Before
	public void setup() {
		mock = EasyMock.createStrictMock(RandomCreator.class);
		RandomCreator.setInstance(mock);
	};

	@After
	public void cleanup() {
		RandomCreator.setInstance(new RandomCreator());
	};

	/**
	 * Test for {@link RandomArrayFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}.
	 */
	@Test
	public void testCreateDummyInteger() {
		RandomArrayFactory<Integer[]> factory = new RandomArrayFactory<Integer[]>(Integer[].class);

		EasyMock.expect(mock.getRandomInt(2)).andReturn(1); // random array length
		EasyMock.expect(mock.getRandomString()).andReturn("abc"); // Integer(String), which should fail
		EasyMock.expect(mock.getRandomInt()).andReturn(12345); // Integer(int), which should succeed
		EasyMock.expect(mock.getRandomInt()).andReturn(54321); // Integer(int), which should succeed
		EasyMock.expect(mock.getRandomInt()).andReturn(678768); // Integer(int), which should succeed
		EasyMock.replay(mock);

		assertArrayEquals(new Integer[] { 12345, 54321, 678768 }, factory.createDummy(new ClassBindings()));

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link RandomArrayFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}. Tests if a array of
	 * primitive <code>int</code> can be created correctly.
	 */
	@Test
	public void testCreateDummyInt() {
		RandomArrayFactory<int[]> factory = new RandomArrayFactory<int[]>(int[].class);

		EasyMock.expect(mock.getRandomInt(2)).andReturn(1); // random array length
		EasyMock.expect(mock.getRandomInt()).andReturn(12345);
		EasyMock.expect(mock.getRandomInt()).andReturn(54321);
		EasyMock.expect(mock.getRandomInt()).andReturn(678768);
		EasyMock.replay(mock);

		assertArrayEquals(new int[] { 12345, 54321, 678768 }, factory.createDummy(new ClassBindings()));

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link RandomArrayFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}. Tests if a array of
	 * <code>String</code> can be created correctly.
	 */
	@Test
	public void testCreateDummyString() {
		RandomArrayFactory<String[]> factory = new RandomArrayFactory<String[]>(String[].class);

		EasyMock.expect(mock.getRandomInt(2)).andReturn(1); // random array length
		EasyMock.expect(mock.getRandomString()).andReturn("1234");
		EasyMock.expect(mock.getRandomString()).andReturn("aasdf");
		EasyMock.expect(mock.getRandomString()).andReturn("test");
		EasyMock.replay(mock);

		assertArrayEquals(new String[] { "1234", "aasdf", "test" }, factory.createDummy(new ClassBindings()));

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link RandomArrayFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}. Tests if a array of
	 * <code>TestClass</code> can be created correctly.
	 */
	@Test
	public void testCreateDummyTestClass() {
		RandomArrayFactory<TestClass[]> factory = new RandomArrayFactory<TestClass[]>(TestClass[].class);

		EasyMock.expect(mock.getRandomInt(2)).andReturn(0); // random array length
		EasyMock.expect(mock.getRandomBoolean()).andReturn(true);
		EasyMock.expect(mock.getRandomInt()).andReturn(9876);
		EasyMock.replay(mock);

		TestClass instance1 = new TestClass();
		instance1.setBooltje(true);
		instance1.setIntje(9876);
		instance1.setTestClass(instance1);
		TestClass instance2 = new TestClass();
		instance2.setBooltje(true);
		instance2.setIntje(9876);
		instance2.setTestClass(instance2);

		TestClass[] createDummy = factory.createDummy(new ClassBindings());
		assertArrayEquals(new TestClass[] { instance1, instance2 }, createDummy);

		EasyMock.verify(mock);
	}

	/**
	 * Test for {@link RandomArrayFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}. Tests if a array of
	 * <code>Integer[]</code> can be created correctly.
	 */
	@Test
	public void testCreateDummyIntegerArray() {
		RandomArrayFactory<Integer[][]> factory = new RandomArrayFactory<Integer[][]>(Integer[][].class);

		EasyMock.expect(mock.getRandomInt(2)).andReturn(0); // random master array length

		// first sub array
		EasyMock.expect(mock.getRandomInt(2)).andReturn(0); // random sub array 1 length
		EasyMock.expect(mock.getRandomString()).andReturn("abc").times(0, 1); // Integer(String) for sub array 1, which should fail
		EasyMock.expect(mock.getRandomInt()).andReturn(1234); // Integer(int) for sub array 1 index 0, which should succeed and become
																// preferred constructor
		EasyMock.expect(mock.getRandomInt()).andReturn(4321); // Integer(int) for sub array 1 index 1, which should succeed

		// second sub array
		EasyMock.expect(mock.getRandomInt(2)).andReturn(1); // random sub array 2 length
		EasyMock.expect(mock.getRandomInt()).andReturn(5678); // Integer(int) for sub array 2 index 0, which should succeed
		EasyMock.expect(mock.getRandomInt()).andReturn(8765); // Integer(int) for sub array 2 index 1, which should succeed
		EasyMock.expect(mock.getRandomInt()).andReturn(1029); // Integer(int) for sub array 2 index 2, which should succeed
		EasyMock.replay(mock);

		Integer[][] dummy = factory.createDummy(new ClassBindings());
		assertArrayEquals(new Integer[][] { new Integer[] { 1234, 4321 }, new Integer[] { 5678, 8765, 1029 } }, dummy);

		EasyMock.verify(mock);
	}

	/**
	 * Test class used to test if nested fields are populated correctly when the objects are created in the context of an array. This
	 * includes a simple test for recursive loops (the <code>testClass</code> field should be a reference to itself).
	 */
	public static class TestClass {
		boolean booltje;
		int intje;
		TestClass testClass;

		public boolean isBooltje() {
			return booltje;
		}

		public void setBooltje(boolean booltje) {
			this.booltje = booltje;
		}

		public int getIntje() {
			return intje;
		}

		public void setIntje(int intje) {
			this.intje = intje;
		}

		public TestClass getTestClass() {
			return testClass;
		}

		public void setTestClass(TestClass testClass) {
			this.testClass = testClass;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TestClass other = (TestClass) obj;
			if (booltje != other.booltje)
				return false;
			if (intje != other.intje)
				return false;
			if (testClass == null) {
				if (other.testClass != null)
					return false;
			}
			return true;
		}
	}
}