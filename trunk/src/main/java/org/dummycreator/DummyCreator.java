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

import org.dummycreator.dummyfactories.ClassBasedFactory;

/**
 * Tool to create populated dummy objects of a given class. This tool will recursively run through its setters and try to come up with newly
 * populated objects for those fields.
 * <p>
 * To avoid recursive infinite loops, this tool keeps track of previously populated instances of a certain type and reuse that instead.
 * <p>
 * For numbers being generated a random number is being using, for strings a lorem ipsum generated is being used.
 * 
 * @see #create(Class)
 * @author Alexander Muthmann <amuthmann@dev-eth0.de> (original author)
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class DummyCreator {

	/**
	 * A map that contains deferred class types for a given type. With this you can defer the creation of a dummy instance to another type.
	 * This is useful if you need to instance dummy objects for an interface or abstract class.
	 * 
	 * @see ClassBindings
	 */
	private final ClassBindings classBindings;

	/**
	 * Default constructor: configures the Dummy Creator with vanilla new bindings and caches.
	 */
	public DummyCreator() {
		this(new ClassBindings());
	}

	/**
	 * Constructor: configures the Dummy Creator with a given {@link ClassBindings} instance and new caches.
	 */
	public DummyCreator(ClassBindings classBindings) {
		this.classBindings = classBindings;
	}

	/**
	 * Main method, creates a dummy object of a given type (using {@link ClassBasedFactory#createDummy(java.util.List, ClassBindings)}).
	 * <p>
	 * Provide your own {@link ClassBindings} in {@link #DummyCreator(ClassBindings)} to control how objects are created for specific types
	 * (such as the abstract List class). This is the main-method used to create a dummy of a certain class. It's called with the needed
	 * class. e.g. Integer i = createDummyOfClass(Integer.class)
	 * 
	 * @param <T> The type to be created and returned (returned type can be a sub type of <code>T</code>).
	 * @param clazz The type that should be created
	 * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured!).
	 * @throws IllegalArgumentException Thrown if an abstract type or interface was given for which no binding could be found in the
	 *             provided {@link ClassBindings}.
	 */
	public <T> T create(final Class<T> clazz) {
		return new ClassBasedFactory<T>(clazz).createDummy(null, classBindings);
	}
}