package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;

import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for {@link RandomBooleanFactory}
 */
public class RandomBooleanFactoryTest {

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
	 * Test for {@link RandomBooleanFactory#createDummy(java.util.Map, org.dummycreator.ClassBindings, java.util.List)}.
	 */
	@Test
	public void test() {
		EasyMock.expect(mock.getRandomBoolean()).andReturn(true);
		EasyMock.expect(mock.getRandomBoolean()).andReturn(true);
		EasyMock.expect(mock.getRandomBoolean()).andReturn(false);
		EasyMock.expect(mock.getRandomBoolean()).andReturn(false);
		EasyMock.replay(mock);

		assertEquals(true, new RandomBooleanFactory().createDummy(null));
		assertEquals(true, new RandomBooleanFactory().createDummy(null));
		assertEquals(false, new RandomBooleanFactory().createDummy(null));
		assertEquals(false, new RandomBooleanFactory().createDummy(null));

		EasyMock.verify(mock);
	}
}