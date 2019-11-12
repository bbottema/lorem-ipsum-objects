package org.bbottema.loremipsumobjects;

import org.bbottema.loremipsumobjects.typefactories.ClassBasedFactory;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Tool to create populated dummy objects of a given class. This tool will recursively run through its setters and try to come up with newly
 * populated objects for those fields.
 * <p>
 * To avoid recursive infinite loops, this tool keeps track of previously populated instances of a certain type and reuse that instead.
 * <p>
 * For numbers being generated a random number is being using, for strings a lorem ipsum generated is being used.
 *
 * @see #createLoremIpsumObject(Class)
 */
public class LoremIpsumObjectCreator {

	private final LoremIpsumConfig loremIpsumConfig;

	/**
	 * Default constructor: configures the Dummy Creator with vanilla new bindings and caches.
	 */
	public LoremIpsumObjectCreator() {
		this(LoremIpsumConfig.builder().build());
	}

	/**
	 * Constructor: configures the Dummy Creator with a given {@link ClassBindings} instance and new caches.
	 */
	public LoremIpsumObjectCreator(final ClassBindings classBindings) {
		this(LoremIpsumConfig.builder().classBindings(classBindings).build());
	}

	/**
	 * Default constructor: configures the Dummy Creator with vanilla new bindings and caches.
	 */
	public LoremIpsumObjectCreator(LoremIpsumConfig loremIpsumConfig) {
		this.loremIpsumConfig = loremIpsumConfig;
	}

	/**
	 * Main method, creates a dummy object of a given type (using {@link ClassBasedFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}).
	 * <p>
	 * Provide your own {@link ClassBindings} in {@link #LoremIpsumObjectCreator(ClassBindings)} to control how objects are created for specific types
	 * (such as the abstract List class). This is the main-method used to create a dummy of a certain class. It's called with the needed
	 * class. e.g. Integer i = createLoremIpsumObjectOfClass(Integer.class)
	 *
	 * @param <T>   The type to be created and returned (returned type can be a sub type of <code>T</code>).
	 * @param clazz The type that should be created
	 * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured!).
	 * @throws IllegalArgumentException Thrown if an abstract type or interface was given for which no binding could be found in the
	 *                                  provided {@link ClassBindings}.
	 */
	public <T> T createLoremIpsumObject(final Class<T> clazz) {
		return new ClassBasedFactory<>(clazz).createLoremIpsumObject(loremIpsumConfig);
	}
}