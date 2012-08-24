package org.dummycreator.dummyfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codemonkey.javareflection.FieldUtils;
import org.codemonkey.javareflection.FieldUtils.BeanRestriction;
import org.codemonkey.javareflection.FieldUtils.Visibility;
import org.codemonkey.javareflection.FieldWrapper;
import org.dummycreator.ReflectionCache;
import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.DummyFactory;
import org.dummycreator.RandomCreator;

/**
 * Creates a populated dummy object of a given class <code>T</code>. First tries to defer creation using {@link #classBindings}, if no class
 * bindings can be found for the given type, it will try to determine if a specific factory is needed (for primitives, strings, arrays and
 * other common types). If no specific factory is applicable, this factory will try to find constructors to create an instance with. Then
 * any fields are being created the same way.
 * <p>
 * This class relies on all the other factories to complete creating and fully populating a dummy object of the given type.
 * 
 * @param <T> The type of object that should be created by the dummy factory.
 * @author Benny Bottema <b.bottema@projectnibble.org> (further developed project)
 */
public class ClassBasedFactory<T> extends DummyFactory<T> {

    private static final Logger logger = Logger.getLogger(ClassBasedFactory.class);

    /**
     * The class to create (and populate).
     */
    private final Class<T> clazz;

    /**
     * A cache for previously found {@link Constructor} and {@link Method} instances and preferred constructor for previous successfully
     * invoked constructors.
     */
    private static final ReflectionCache constructorCache = new ReflectionCache();

    /**
     * A list of previously created and populated objects for a specific type.
     */
    private static Map<Class<?>, ClassUsageInfo<?>> knownInstances = new HashMap<Class<?>, ClassUsageInfo<?>>();

    private ClassBindings classBindings;

    public ClassBasedFactory(Class<T> clazz) {
	this.clazz = clazz;
    }

    @Override
    public T createDummy(List<Exception> constructorExceptions, ClassBindings classBindings) {
	this.classBindings = classBindings;
	return create(clazz);
    }

    /**
     * Will try to create a new object for the given type, while maintaining a track record of already created - and - populated objects to
     * avoid recursive loops.
     * 
     * @param clazz The type that should be created
     * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured).
     * @throws IllegalArgumentException Thrown if class could not be instantiated. Possible constructor invocation exceptions are logged
     *             separately.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private T create(final Class<T> clazz) {
	// List of Classes, we already used for population. by remembering, we can avoid recursive looping
	if (knownInstances.get(clazz) == null || !knownInstances.get(clazz).isPopulated()) {
	    // try to defer instantiation to a binding, if available
	    List<Exception> constructorExceptions = new ArrayList<Exception>();
	    T ret = deferCreationToClassBinding(clazz, constructorExceptions);

	    if (ret == null) {
		ClassUsageInfo<T> usedInfo = new ClassUsageInfo<T>();
		usedInfo.setInstance(ret);
		knownInstances.put(clazz, usedInfo);

		// Is the class an enum?
		if (clazz.isEnum()) {
		    return (T) new RandomEnumFactory<Enum>((Class<Enum>) clazz).createDummy(constructorExceptions, classBindings);
		}

		// Load the constructors
		List<Constructor<?>> consts = constructorCache.getConstructorCache(clazz);
		if (consts == null) {
		    consts = new ArrayList<Constructor<?>>();
		    Constructor<?>[] _con = clazz.getConstructors();
		    java.util.Arrays.sort(_con, new Comparator<Constructor<?>>() {
			/**
			 * Comparator to sort constructors by their number of parameters
			 */
			@Override
			public int compare(Constructor<?> o1, Constructor<?> o2) {
			    int num_o1 = o1.getParameterTypes().length;
			    int num_o2 = o2.getParameterTypes().length;
			    return (num_o1 < num_o2) ? -1 : (num_o1 == num_o2) ? 0 : 1;
			}
		    });
		    consts.addAll(Arrays.asList(_con));
		    constructorCache.add(clazz, consts.toArray(new Constructor<?>[] {}));
		}

		// Check if we have a prefered Constructor and try it
		Constructor<T> preferedConstructor = (Constructor<T>) constructorCache.getPreferedConstructor(clazz);
		if (preferedConstructor != null) {
		    ret = new ConstructorBasedFactory<T>(preferedConstructor).createDummy(constructorExceptions, classBindings);
		}

		if (ret == null) {
		    for (Constructor<?> co : consts) {
			ret = new ConstructorBasedFactory<T>((Constructor<T>) co).createDummy(constructorExceptions, classBindings);
			if (ret != null) {
			    constructorCache.setPreferedConstructor(clazz, co);
			    // Worked
			    break;
			}

		    }
		}

		if (ret == null) {
		    for (Exception e : constructorExceptions) {
			logger.error("tried but failed to use constructor: ", e);
		    }
		    throw new IllegalArgumentException(String.format("Could not instantiate object for type [%s], is it abstract and missing a binding?", clazz));
		}

		usedInfo.setInstance(ret);
		usedInfo.setPopulated(true);
		populateObject(ret);
	    }

	    return ret;
	} else {
	    return (T) knownInstances.get(clazz).getInstance();
	}
    }

    @SuppressWarnings({ "unchecked" })
    private T deferCreationToClassBinding(final Class<T> clazz, List<Exception> constructorExceptions) {
	Object bind = classBindings.find(clazz);
	if (bind != null) {
	    if (bind instanceof DummyFactory<?>) {
		return (T) ((DummyFactory<?>) bind).createDummy(constructorExceptions, classBindings);
	    } else if (bind.getClass() == Class.class) {
		return create((Class<T>) bind);
	    } else {
		return (T) bind;
	    }
	} else {
	    return null;
	}
    }

    /**
     * Populates given object with dummy value. The behavior can vary depending on the type of object, as follows:
     * <ul>
     * <li><strong>Collection</strong>: if the type is a subtype of {@link Collection}, a random number (2 or 3) of items will be added</li>
     * <li><strong>Map</strong>: if the type is a subtype of {@link Map}, a random number (2 or 3) of key/value entries will be added</li>
     * <li><strong>Other types</strong>: The <em>setter</em> methods will be retrieved from the object and will be invoked with a new dummy
     * value.</li>
     * </ul>
     * <p>
     * <strong>note: </strong> In case of a <code>Collection</code> or <code>Map</code>, the objects created are <code>String</code>
     * instances, unless a generic type can be derived with java reflection.
     * <p>
     * For example, <code>List<Foo></code> or <code>HashMap<Number, String></code> will both result in an instance containing only strings,
     * but a type declared as <code>class FooList extends ArrayList<Foo></code> will result in an instance containing <code>Foo</code>
     * elements. The behavior of the first case is a result of runtime <a
     * href="http://en.wikipedia.org/wiki/Generics_in_Java#Problems_with_type_erasure">type erasure</a>. Only declared generic types in the
     * class or interface signature, as in the latter case, are discoverable in runtime (the latter case).
     * 
     * @param subject The object to populate with dummy values.
     * @param knownInstances A list of known instances to keep track of already processed classes (to avoid infinite loop)
     * @see #create(Class, Map)
     */
    @SuppressWarnings({ "unchecked" })
    private void populateObject(final T subject) {
	final Class<?> clazz = subject.getClass();

	if (subject instanceof Collection) {
	    for (int i = 0; i < RandomCreator.getRandomInt(2) + 2; i++) {
		// detect generic declarations
		Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > 0 && (genericTypes[0] instanceof Class)) {
		    // uses generic type if available, Integer for '<T extends List<Integer>>'
		    ((Collection<Object>) subject).add(create((Class<T>) genericTypes[0]));
		} else {
		    // use default String value for raw type Collection
		    ((Collection<Object>) subject).add(create((Class<T>) String.class));
		}
	    }
	} else if (subject instanceof Map) {
	    for (int i = 0; i < RandomCreator.getRandomInt(2) + 2; i++) {
		// detect generic declarations
		Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > 0 && (genericTypes[0] instanceof Class) && (genericTypes[1] instanceof Class)) {
		    // uses generic type if available, String and Integer for '<T extends Map<String, Double>>'
		    ((Map<Object, Object>) subject).put(create((Class<T>) genericTypes[0]), create((Class<T>) genericTypes[1]));
		} else {
		    // use default String and String value for raw type Collection
		    ((Map<Object, Object>) subject).put(create((Class<T>) String.class), create((Class<T>) String.class));
		}
	    }
	} else {
	    List<Method> setter = discoverSetters(clazz);

	    Object parameter = null;
	    for (Method m : setter) {
		// Load the parameter to pass to this method
		parameter = create((Class<T>) m.getParameterTypes()[0]);
		try {
		    m.invoke(subject, parameter);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }
	}
    }

    private List<Method> discoverSetters(final Class<?> clazz) {
	List<Method> setters = constructorCache.getMethodCache(clazz);
	if (setters == null) {
	    setters = new ArrayList<Method>();
	    Map<Class<?>, List<FieldWrapper>> fields = FieldUtils.collectFields(clazz, Object.class, EnumSet.allOf(Visibility.class), EnumSet.of(BeanRestriction.YES_SETTER));
	    for (List<FieldWrapper> fieldWrappers : fields.values()) {
		for (FieldWrapper fieldWrapper : fieldWrappers) {
		    setters.add(fieldWrapper.getSetter());
		}
	    }

	    constructorCache.add(clazz, setters.toArray(new Method[] {}));
	}
	return setters;
    }
}