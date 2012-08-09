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
package org.dummycreator;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is a cache for use with dummycreator. It caches all found setter for a certain class
 * 
 * @author Alexander Muthmann <amuthmann at dev-eth0.de>
 * @version 05/2010
 */
class MethodCache {

    private final Map<Class<?>, List<Method>> cache = new HashMap<Class<?>, List<Method>>();

    /**
     * This method returns a list of setter-methods for the given class. If no setter is cached, it returns null
     * 
     * @param clazz
     * @return
     */
    public List<Method> getSetterForClass(final Class<?> clazz) {
	List<Method> m = cache.get(clazz);
	return m == null ? null : Collections.unmodifiableList(m);
    }

    /**
     * Adds a Method as setter for the given class.
     * 
     * @param clazz
     * @param setter
     */
    public void addSetterForClass(final Class<?> clazz, final Method setter) {
	List<Method> setters = cache.get(clazz);
	if (setters == null) {
	    setters = new ArrayList<Method>();
	    cache.put(clazz, setters);
	}
	setters.add(setter);
    }

    /**
     * Adds the list of Methods as setters for the given class
     * 
     * @param clazz
     * @param setter
     */
    public void addSetterForClass(final Class<?> clazz, final List<Method> setter) {
	List<Method> setters = cache.get(clazz);
	if (setters == null) {
	    setters = new ArrayList<Method>();
	    cache.put(clazz, setters);
	}
	setters.addAll(setter);
    }
}