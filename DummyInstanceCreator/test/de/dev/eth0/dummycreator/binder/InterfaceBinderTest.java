/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.opensource.org/licenses/cddl1.php
 * or http://www.opensource.org/licenses/cddl1.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.opensource.org/licenses/cddl1.php.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is dummyCreator. The Initial Developer of the Original
 * Software is Alexander Muthmann <amuthmann@dev-eth0.de>.
 */
package de.dev.eth0.dummycreator.binder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 */
public class InterfaceBinderTest {

    public InterfaceBinderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of bind method, of class InterfaceBinder.
     */
    @Test
    public void testBindAndBind() {
        System.out.println("bind and get");
        InterfaceBinder.bind(List.class, ArrayList.class);

        assertEquals(ArrayList.class, InterfaceBinder.getImplementationOfInterface(List.class));

        //Test to override binding
        InterfaceBinder.bind(List.class, LinkedList.class);
        assertEquals(LinkedList.class, InterfaceBinder.getImplementationOfInterface(List.class));
    }
}
