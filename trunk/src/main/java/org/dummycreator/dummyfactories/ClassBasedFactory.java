package org.dummycreator.dummyfactories;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codemonkey.javareflection.FieldUtils;
import org.codemonkey.javareflection.FieldUtils.BeanRestriction;
import org.codemonkey.javareflection.FieldUtils.Visibility;
import org.codemonkey.javareflection.FieldWrapper;
import org.dummycreator.ClassBindings;
import org.dummycreator.ClassUsageInfo;
import org.dummycreator.RandomCreator;
import org.dummycreator.ReflectionCache;

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

	public ClassBasedFactory(Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Will try to create a new object for the given type, while maintaining a track record of already created - and - populated objects to
	 * avoid recursive loops.
	 * <p>
	 * Will first try to defer dummy object creation to a bound {@link DummyFactory} and if found will defer dummy object creation to
	 * {@link DummyFactory#createDummy(List, ClassBindings)}.
	 * 
	 * @param knownInstances See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param classBindings See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param exceptions See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param clazz See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured).
	 * @throws IllegalArgumentException Thrown if class could not be instantiated. Constructor invocation exceptions are logged separately.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public T createDummy(Type[] nextGenericsMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings,
			List<Exception> exceptions) {
		// list of classes, we already used for population. by remembering, we can avoid recursive looping
		final String typeMarker = createTypeMarker(clazz, nextGenericsMetaData);
		if (knownInstances.get(typeMarker) == null || !knownInstances.get(typeMarker).isPopulated()) {

			final T ret = create(nextGenericsMetaData, knownInstances, classBindings, exceptions);

			if (ret != null) {
				return ret;
			} else {
				logger.error("tried but failed to produce dummy object...");
				if (!exceptions.isEmpty()) {
					logger.error("errors logged:");
					for (Exception e : exceptions) {
						logger.error(e.getMessage(), e);
					}
				}
				throw new IllegalArgumentException(String.format(
						"Could not instantiate object for type [%s], is it abstract and missing a binding?", clazz));
			}
		} else {
			return (T) knownInstances.get(typeMarker).getInstance();
		}
	}

/**
	 * Will try to create a new object for the given type, while maintaining a track record of already created - and - populated objects to
	 * avoid recursive loops.
	 * <p>
	 * Will first try to defer dummy object creation to a bound {@link DummyFactory} and if found will defer dummy object creation to
	 * {@link DummyFactory#createDummy(Map, ClassBindings, List).
	 * 
	 * @param knownInstances See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param classBindings See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param exceptions See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured).
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private T create(Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings,
			List<Exception> exceptions) {
		T ret = null;

		// first try possibly bound dummy factory
		DummyFactory<T> factory = classBindings.find(clazz);
		if (factory != null) {
			ret = factory.createDummy(genericMetaData, knownInstances, classBindings, exceptions);
		}

		if (ret == null && clazz.isArray()) {
			ret = new RandomArrayFactory<T>((Class<T>) clazz).createDummy(genericMetaData, knownInstances, classBindings, exceptions);
		}

		// if null, we need to create it ourself
		if (ret == null) {
			ClassUsageInfo<T> usedInfo = new ClassUsageInfo<T>();
			usedInfo.setInstance(ret);

			if (!knownLoopSafeType(clazz)) {
				knownInstances.put(createTypeMarker(clazz, genericMetaData), usedInfo);
			}

			// is the class an enum?
			if (clazz.isEnum()) {
				return (T) new RandomEnumFactory<Enum>((Class<Enum>) clazz).createDummy(genericMetaData, knownInstances, classBindings,
						exceptions);
			}

			// load the constructors
			List<Constructor<?>> cachedConstructors = constructorCache.getConstructorCache(clazz);
			if (cachedConstructors == null) {
				cachedConstructors = new ArrayList<Constructor<?>>();
				Constructor<?>[] foundConstructors = clazz.getConstructors();
				java.util.Arrays.sort(foundConstructors, new Comparator<Constructor<?>>() {
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
				cachedConstructors.addAll(Arrays.asList(foundConstructors));
				constructorCache.add(clazz, cachedConstructors.toArray(new Constructor<?>[] {}));
			}

			// check if we have a prefered Constructor and try it
			Constructor<T> preferedConstructor = (Constructor<T>) constructorCache.getPreferedConstructor(clazz);
			if (preferedConstructor != null) {
				ret = new ConstructorBasedFactory<T>(preferedConstructor).createDummy(genericMetaData, knownInstances, classBindings,
						exceptions);
			}

			if (ret == null) {
				for (Constructor<?> co : cachedConstructors) {
					ret = new ConstructorBasedFactory<T>((Constructor<T>) co).createDummy(genericMetaData, knownInstances, classBindings,
							exceptions);
					if (ret != null) {
						constructorCache.setPreferedConstructor(clazz, co);
						break;
					}

				}
			}

			if (ret != null) {
				usedInfo.setInstance(ret);
				usedInfo.setPopulated(true);
				populateObject(ret, genericMetaData, knownInstances, classBindings, exceptions);
			}
		}

		return ret;
	}

	/**
	 * Creates a unique key for a class, but distinguishes generalized versions of the same type. This way, a <code>List<String></code> and
	 * a <code>List<Integer></code> won't clash and are recognized correctly. In other words: a known instance of <code>List<Integer></code>
	 * won't be used to populate a field of type <code>List<String></code>.
	 * 
	 * @param clazz The raw class, used if no generic meta data is available.
	 * @param genericMetaData The generics meta data, if not null, used to create a unique string including all nested types.
	 * @return A unique key based on a given <code>Class</code> or its generic meta data if available.
	 * @see #createTypeMarker(Type[])
	 */
	protected static String createTypeMarker(Class<?> clazz, Type[] genericMetaData) {
		return "|" + clazz.getName() + "|" + createTypeMarker(genericMetaData);
	}
	
	/**
	 * Creates a unique key for a generics type, including nested generic types.
	 * 
	 * @param genericMetaData The generics meta data, if not null, used to create a unique string including all nested types.
	 * @return A unique key based on a given <code>Class</code> or its generic meta data if available.
	 */
	private static String createTypeMarker(Type[] genericMetaData) {
		String typeMarker = "";
		if (genericMetaData != null) {
			// if only one type to process
			if (genericMetaData.length == 1) {
				Type nextGenericMetaData = genericMetaData[0];
				if (nextGenericMetaData instanceof Class) {
					// use the classname
					typeMarker += ((Class<?>) nextGenericMetaData).getName() + "|";
				} else {
					// go deeper using the next generics declarations
					typeMarker += ((Class<?>) ((ParameterizedType) nextGenericMetaData).getRawType()).getName() + "|";
					typeMarker += createTypeMarker(((ParameterizedType) nextGenericMetaData).getActualTypeArguments());
				}
			} else {
				// split up multiple types
				for (Type type : genericMetaData) {
					typeMarker += createTypeMarker(new Type[] { type });
				}
			}
		}
		return typeMarker;
	}

	/**
	 * Determines whether the given <code>Class</code> represents a known loop-safe type. A loop-safe type is a type that does not refer to
	 * itself or contains a method or constructor (that DummyCreator would invoke) that refers to itself.
	 * <p>
	 * This mechanism ensures that <code>Number</code> instances for example don't get locked down to a single known instance. Without this
	 * mechanism, an <code>Integer</code> would be cached and reused for all fields Dummy Creator would encounter. By marking
	 * <code>Integer.class</code> as loop-safe, a new Integer will be created each time a dummy should be created.
	 * 
	 * @param The type to check for loop-safety.
	 * @return Whether the given type is known not to cause an infinite recursive loop.
	 */
	private boolean knownLoopSafeType(Class<T> clazz) {
		final List<Class<?>> safeClasses = new ArrayList<Class<?>>();
		safeClasses.add(Integer.class);
		safeClasses.add(Long.class);
		safeClasses.add(Float.class);
		safeClasses.add(Boolean.class);
		safeClasses.add(Character.class);
		safeClasses.add(Byte.class);
		safeClasses.add(Short.class);
		safeClasses.add(Double.class);
		safeClasses.add(String.class);
		return safeClasses.contains(clazz);
	}

	/**
	 * Populates given object with dummy value. The behavior can vary depending on the type of object, as follows:
	 * <ul>
	 * <li><strong>Collection</strong>: if the type is a subtype of {@link Collection}, a random number (2 or 3) of items will be added.
	 * Will be deferred {@link #populateCollection(Collection, Type, Map, ClassBindings, List)}.</li>
	 * <li><strong>Map</strong>: if the type is a subtype of {@link Map}, a random number (2 or 3) of key/value entries will be added. Will
	 * be deferred {@link #populateMap(Map, Type, Map, ClassBindings, List)}.</li>
	 * <li><strong>Other types</strong>: The bean-style <em>setter</em> methods will be retrieved from the object and will be invoked with a
	 * new dummy value.</li>
	 * </ul>
	 * <p>
	 * <strong>note: </strong> In case of a <code>Collection</code> or <code>Map</code>, the objects created are <code>String</code>
	 * instances, unless a generic type can be derived with java reflection.
	 * <p>
	 * For example, <code>List<Foo></code> or <code>HashMap<Number, String></code> -<em>when not declared as a field on another object</em>-
	 * will both result in an instance containing only strings, but a type declared as <code>class FooList extends ArrayList<Foo></code>
	 * will result in an instance containing <code>Foo</code> elements.
	 * <p>
	 * The behavior of the first case is a result of runtime <a
	 * href="http://en.wikipedia.org/wiki/Generics_in_Java#Problems_with_type_erasure">type erasure</a>. Only declared generic types in the
	 * class or interface signature, as in the latter case, are discoverable in runtime, as well as generic type declared in a {@link Field}, {@link Method} or {@link Constructor}.
	 * 
	 * @param subject The object to populate with dummy values.
	 * @param knownInstances A list of known instances to keep track of already processed classes (to avoid infinite loop).
	 * @param classBindings See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param exceptions Not used, but will be passed to {@link ClassBasedFactory} instances when producing dummies for arrays, collections
	 *            and maps.
	 * @see #populateCollection(Object, Type, Map, ClassBindings, List)
	 * @see #populateMap(Object, Type, Map, ClassBindings, List)
	 */
	@SuppressWarnings({ "unchecked" })
	private void populateObject(final T subject, Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances,
			ClassBindings classBindings, List<Exception> exceptions) {
		if (subject instanceof Collection) {
			populateCollection((Collection<Object>) subject, genericMetaData, knownInstances, classBindings, exceptions);
		} else if (subject instanceof Map) {
			populateMap((Map<Object, Object>) subject, genericMetaData, knownInstances, classBindings, exceptions);
		} else {
			// populate POJO using it's bean setters (which should always contain exactly one parameter)
			// by creating a new dummy using a ClassBasedFactory for each Method's parameter and finally invoke the method itself
			for (Method setter : discoverSetters(subject.getClass())) {
				// collect generics meta data if available
				ClassBasedFactory<T> factory = new ClassBasedFactory<T>((Class<T>) setter.getParameterTypes()[0]);
				Type genericParameterType = setter.getGenericParameterTypes()[0];
				boolean isRawClassItself = genericParameterType instanceof Class;
				Type[] nextGenericsMetaData = (isRawClassItself) ? null : ((ParameterizedType) genericParameterType)
						.getActualTypeArguments();
				// finally create the parameter with or without generics meta data
				Object parameter = factory.createDummy(nextGenericsMetaData, knownInstances, classBindings, exceptions);
				try {
					setter.invoke(subject, parameter);
				} catch (Exception e) {
					logger.error("error calling setter method [" + setter.getName() + "]", e);
				}
			}
		}
	}

	/**
	 * A random number (2 or 3) of items will be added. The type will be determined as follows:
	 * <ol>
	 * <li>Using generics meta data straight from the subject: this is possible when the subject is the <em>root class</em> passed into
	 * Dummy Creator, and has generics declared in its interface, such as: <code>FooList extends ArrayList<Foo></code></li>
	 * <li>Using generics meta data from a field declaration (parameter <code>genericMetaData</code>), where this data has been passed in by
	 * a parent factory</li>
	 * <li>By resorting to a <code>String</code> dummy object as a last resort, when we can't determine a type.</li>
	 * </ol>
	 * 
	 * @param subject The collection to populate with dummy objects.
	 * @param genericMetaData If not null, contains the generics meta data for the collection type as declared in the collections field.
	 * @param knownInstances A list of known instances to keep track of already processed classes (to avoid infinite loop).
	 * @param classBindings See {@link DummyFactory#createDummy(Map, ClassBindings, List)}.
	 * @param exceptions Not used, but will be passed to {@link ClassBasedFactory} instances when producing dummies for arrays, collections
	 *            and maps.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateCollection(final Collection<Object> subject, Type[] genericMetaData,
			Map<String, ClassUsageInfo<?>> knownInstances, ClassBindings classBindings, List<Exception> exceptions) {
		for (int i = 0; i < RandomCreator.getInstance().getRandomInt(2) + 2; i++) {
			// detect generic declarations
			Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();

			final ClassBasedFactory<?> factory;
			Type[] nextGenericMetaData = null;

			if (genericTypes.length > 0 && (genericTypes[0] instanceof Class)) {
				// uses generic type if available, Integer for '<T extends List<Integer>>'
				factory = new ClassBasedFactory<T>((Class<T>) genericTypes[0]);
			} else if (genericMetaData != null) {
				// use the generics meta data passed in as a field in the context of a List or Map
				final Class<?> classFromGenerics;
				nextGenericMetaData = null;

				if (genericMetaData[0] instanceof Class) {
					classFromGenerics = (Class<?>) genericMetaData[0];
				} else {
					classFromGenerics = (Class<?>) ((ParameterizedType) genericMetaData[0]).getRawType();
					nextGenericMetaData = ((ParameterizedType) genericMetaData[0]).getActualTypeArguments();
				}

				factory = new ClassBasedFactory(classFromGenerics);
			} else {
				// use default String value for raw type Collection
				factory = new ClassBasedFactory<String>(String.class);
			}
			Object dummyValue = factory.createDummy(nextGenericMetaData, knownInstances, classBindings, exceptions);
			subject.add(dummyValue);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void populateMap(final Map<Object, Object> subject, Type[] genericMetaData, Map<String, ClassUsageInfo<?>> knownInstances,
			ClassBindings classBindings, List<Exception> exceptions) {
		for (int i = 0; i < RandomCreator.getInstance().getRandomInt(2) + 2; i++) {
			// detect generic declarations
			Type[] genericTypes = ((ParameterizedType) subject.getClass().getGenericSuperclass()).getActualTypeArguments();

			final ClassBasedFactory<?> factory1;
			final ClassBasedFactory<?> factory2;
			Type[] nextGenericMetaDataKeyClass = null;
			Type[] nextGenericMetaDataValueClass = null;

			if (genericTypes.length > 1 && genericTypes[0] instanceof Class) {
				// uses generic type if available, String and Integer for '<T extends Map<String, Double>>'
				factory1 = new ClassBasedFactory<T>((Class<T>) genericTypes[0]);
			} else if (genericMetaData != null) {
				// use the generics meta data passed in as a field in the context of a List or Map
				final Class<?> keyClassFromGenerics;

				if (genericMetaData[0] instanceof Class) {
					keyClassFromGenerics = (Class<?>) genericMetaData[0];
				} else {
					keyClassFromGenerics = (Class<?>) ((ParameterizedType) genericMetaData[0]).getRawType();
					nextGenericMetaDataKeyClass = ((ParameterizedType) genericMetaData[0]).getActualTypeArguments();
				}

				factory1 = new ClassBasedFactory(keyClassFromGenerics);
			} else {
				// use default String and String value for raw type Collection
				factory1 = new ClassBasedFactory<String>(String.class);
			}

			if (genericTypes.length > 1 && genericTypes[1] instanceof Class) {
				// uses generic type if available, String and Integer for '<T extends Map<String, Double>>'
				factory2 = new ClassBasedFactory<T>((Class<T>) genericTypes[1]);
			} else if (genericMetaData != null) {
				// use the generics meta data passed in as a field in the context of a List or Map
				final Class<?> ValueClassFromGenerics;

				if (genericMetaData[1] instanceof Class) {
					ValueClassFromGenerics = (Class<?>) genericMetaData[1];
				} else {
					ValueClassFromGenerics = (Class<?>) ((ParameterizedType) genericMetaData[1]).getRawType();
					nextGenericMetaDataValueClass = ((ParameterizedType) genericMetaData[1]).getActualTypeArguments();
				}

				factory2 = new ClassBasedFactory(ValueClassFromGenerics);
			} else {
				// use default String and String value for raw type Collection
				factory2 = new ClassBasedFactory<String>(String.class);
			}

			Object dummyKey = factory1.createDummy(nextGenericMetaDataKeyClass, knownInstances, classBindings, exceptions);
			Object dummyValue = factory2.createDummy(nextGenericMetaDataValueClass, knownInstances, classBindings, exceptions);
			subject.put(dummyKey, dummyValue);
		}
	}

	private List<Method> discoverSetters(final Class<?> clazz) {
		List<Method> setters = constructorCache.getMethodCache(clazz);
		if (setters == null) {
			setters = new ArrayList<Method>();
			Map<Class<?>, List<FieldWrapper>> fields = FieldUtils.collectFields(clazz, Object.class, EnumSet.allOf(Visibility.class),
					EnumSet.of(BeanRestriction.YES_SETTER));
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