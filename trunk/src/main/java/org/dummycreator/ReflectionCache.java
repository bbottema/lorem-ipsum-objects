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
 */
package org.dummycreator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains previously found class methods and constructors and contains references to preferred constructors (constructors called
 * successfully previously).
 * 
 * @author Alexander Muthmann <amuthmann@dev-eth0.de> (original author)
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class ReflectionCache {

    private final Map<Class<?>, List<Method>> methodCache = new HashMap<Class<?>, List<Method>>();
    private final Map<Class<?>, List<Constructor<?>>> constructorCache = new HashMap<Class<?>, List<Constructor<?>>>();
    private final Map<Class<?>, Constructor<?>> preferedConstructors = new HashMap<Class<?>, Constructor<?>>();

    public List<Constructor<?>> getConstructorCache(final Class<?> clazz) {
	return constructorCache.containsKey(clazz) ? constructorCache.get(clazz) : null;
    }

    public List<Method> getMethodCache(final Class<?> clazz) {
	return methodCache.containsKey(clazz) ? methodCache.get(clazz) : null;
    }

    public void add(final Class<?> clazz, final Method... setter) {
	List<Method> setters = methodCache.get(clazz);
	if (setters == null) {
	    setters = new ArrayList<Method>();
	}
	methodCache.put(clazz, setters);
	setters.addAll(Arrays.asList(setter));
    }

    public void add(final Class<?> clazz, final Constructor<?>... cons) {
	List<Constructor<?>> cs = constructorCache.get(clazz);
	if (cs == null) {
	    cs = new ArrayList<Constructor<?>>();
	}
	constructorCache.put(clazz, cs);
	cs.addAll(Arrays.asList(cons));
    }

    public Constructor<?> getPreferedConstructor(final Class<?> clazz) {
	return preferedConstructors.get(clazz);
    }

    public void setPreferedConstructor(final Class<?> clazz, final Constructor<?> cons) {
	preferedConstructors.put(clazz, cons);
    }
}