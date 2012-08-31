package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertSame;

import org.codemonkey.javareflection.FieldUtils.Visibility;
import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RandomEnumFactoryTest {
	
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

	@Test
	public void testCreateDummy() {
		EasyMock.expect(mock.getRandomInt(Visibility.values().length - 1)).andReturn(Visibility.DEFAULT.ordinal());
		EasyMock.expect(mock.getRandomInt(Visibility.values().length - 1)).andReturn(Visibility.PRIVATE.ordinal());
		EasyMock.expect(mock.getRandomInt(Visibility.values().length - 1)).andReturn(Visibility.PROTECTED.ordinal());
		EasyMock.expect(mock.getRandomInt(Visibility.values().length - 1)).andReturn(Visibility.PUBLIC.ordinal());
		EasyMock.replay(mock);
		
		assertSame(Visibility.DEFAULT, new RandomEnumFactory<Visibility>(Visibility.class).createDummy(null));
		assertSame(Visibility.PRIVATE, new RandomEnumFactory<Visibility>(Visibility.class).createDummy(null));
		assertSame(Visibility.PROTECTED, new RandomEnumFactory<Visibility>(Visibility.class).createDummy(null));
		assertSame(Visibility.PUBLIC, new RandomEnumFactory<Visibility>(Visibility.class).createDummy(null));
		
		EasyMock.verify(mock);
	}
}