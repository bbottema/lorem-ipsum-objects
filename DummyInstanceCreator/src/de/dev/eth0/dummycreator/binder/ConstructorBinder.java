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

import java.lang.reflect.Constructor;
import java.util.HashMap;

/**
 *
 * @author Alexander Muthmann <amuthmann@dev-eth0.de>
 * @version 04/2010
 */
public class ConstructorBinder {

    //private static final ConstructorBinder _cbinder = new ConstructorBinder();
    private static final HashMap<Class, Constructor> bindings = new HashMap<Class, Constructor>();

    private ConstructorBinder() {
    }

    public static <T> void bind(final Class<T> _class, final Constructor<T> _constructor) {
        bindings.put(_class, _constructor);
    }

    public static <T> Constructor<T> getConstructorOfClass(final Class<T> _class) {
        return bindings.get(_class);
    }
}
