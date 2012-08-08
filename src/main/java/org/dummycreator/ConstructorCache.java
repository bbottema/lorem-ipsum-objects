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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Alexander Muthmann <amuthmann at dev-eth0.de>
 * @version 05/2010
 */
public class ConstructorCache {

    private final Map<Class<?>, List<Constructor<?>>> cache = new HashMap<Class<?>, List<Constructor<?>>>();
    private final Map<Class<?>, Constructor<?>> preferedConstructors = new HashMap<Class<?>, Constructor<?>>();

    public List<Constructor<?>> getCachedConstructors(final Class<?> clazz) {
	List<Constructor<?>> cs = cache.get(clazz);
	return cs == null ? null : Collections.unmodifiableList(cs);
    }

    public void addConstructor(final Class<?> clazz, final Constructor<?> cons) {
	List<Constructor<?>> cs = cache.get(clazz);
	if (cs == null) {
	    cs = new ArrayList<Constructor<?>>();
	    cache.put(clazz, cs);
	}
	cs.add(cons);
    }

    public void addConstructors(final Class<?> clazz, final List<Constructor<?>> cons) {
	List<Constructor<?>> cs = cache.get(clazz);
	if (cs == null) {
	    cs = new ArrayList<Constructor<?>>();
	    cache.put(clazz, cs);
	}
	cs.addAll(cons);
    }

    public Constructor<?> getPreferedConstructor(final Class<?> clazz) {
	return preferedConstructors.get(clazz);
    }

    public void setPreferedConstructor(final Class<?> clazz, final Constructor<?> cons) {
	preferedConstructors.put(clazz, cons);
    }
}