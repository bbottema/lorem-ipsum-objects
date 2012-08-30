package org.dummycreator.dummyfactories;

import static org.junit.Assert.*;

import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.Test;

public class RandomStringFactoryTest {

	@Test
	public void testCreateDummy() {
		RandomStringFactory factory = new RandomStringFactory();
		
		RandomCreator mock = EasyMock.createMock(RandomCreator.class);
		RandomCreator.setInstance(mock);
		EasyMock.expect(mock.getRandomString()).andReturn("1234");
		EasyMock.replay(mock);
		
		assertEquals("1234", factory.createDummy(null, null, null));
		
		EasyMock.verify(mock);
		RandomCreator.setInstance(new RandomCreator());
	}
}