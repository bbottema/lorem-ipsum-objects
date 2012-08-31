package org.dummycreator.dummyfactories;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Method;

import org.dummycreator.ClassBindings;
import org.dummycreator.RandomCreator;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MethodBasedFactoryTest {
	
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
	public void testCreateDummyInteger() throws SecurityException, NoSuchMethodException {
		Method method = Integer.class.getMethod("valueOf", int.class);
		MethodBasedFactory<Integer> factory = new MethodBasedFactory<Integer>(method);
		
		EasyMock.expect(mock.getRandomInt()).andReturn(12345);
		EasyMock.replay(mock);
		
		Integer dummy = factory.createDummy(new ClassBindings());
		
		assertNotNull(dummy.getClass());
		assertEquals(Integer.class, dummy.getClass());
		assertEquals(Integer.valueOf(12345), dummy);
		
		EasyMock.verify(mock);
	}

	@Test
	public void testCreateDummyString() throws SecurityException, NoSuchMethodException {
		Method method = Integer.class.getMethod("valueOf", String.class);
		MethodBasedFactory<Integer> factory = new MethodBasedFactory<Integer>(method);
		
		EasyMock.expect(mock.getRandomString()).andReturn("98765");
		EasyMock.replay(mock);
		
		Integer dummy = factory.createDummy(new ClassBindings());
		
		assertNotNull(dummy.getClass());
		assertEquals(Integer.class, dummy.getClass());
		assertEquals(Integer.valueOf(98765), dummy);
		
		EasyMock.verify(mock);
	}
}