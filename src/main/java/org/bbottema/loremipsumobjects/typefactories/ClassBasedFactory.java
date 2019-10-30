package org.bbottema.loremipsumobjects.typefactories;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.bbottema.javareflection.BeanUtils;
import org.bbottema.javareflection.BeanUtils.BeanRestriction;
import org.bbottema.javareflection.BeanUtils.Visibility;
import org.bbottema.javareflection.model.FieldWrapper;
import org.bbottema.loremipsumobjects.ClassBindings;
import org.bbottema.loremipsumobjects.ClassUsageInfo;
import org.bbottema.loremipsumobjects.typefactories.util.LoremIpsumGenerator;
import org.bbottema.loremipsumobjects.typefactories.util.ReflectionCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.sort;
import static java.util.EnumSet.allOf;
import static java.util.EnumSet.of;

/**
 * Creates a populated dummy object of a given class <code>T</code>. First tries to defer creation using <em>classBindings</em>, if no class
 * bindings can be found for the given type, it will try to determine if a specific factory is needed (for primitives, strings, arrays and
 * other common types). If no specific factory is applicable, this factory will try to find constructors to create an instance with. Then
 * any fields are being created the same way.
 * <p>
 * This class relies on all the other factories to complete creating and fully populating a dummy object of the given type.
 *
 * @param <T> The type of object that should be created by the dummy factory.
 */
@Slf4j
public class ClassBasedFactory<T> extends LoremIpsumObjectFactory<T> {
	/**
	 * The class to create (and populate).
	 */
	private final Class<T> clazz;

	/**
	 * A cache for previously found {@link Constructor} and {@link Method} instances and preferred constructor for previous successfully
	 * invoked constructors.
	 */
	private static final ReflectionCache constructorCache = new ReflectionCache();

	public ClassBasedFactory(final Class<T> clazz) {
		this.clazz = clazz;
	}

	/**
	 * Will try to create a new object for the given type, while maintaining a track record of already created - and - populated objects to
	 * avoid recursive loops.
	 * <p>
	 * Will first try to defer dummy object creation to a bound {@link LoremIpsumObjectFactory} and if found will defer dummy object creation to
	 * {@link LoremIpsumObjectFactory#createLoremIpsumObject(ClassBindings)}.
	 *
	 * @param nextGenericsMetaData See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param knownInstances       See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param classBindings        See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param exceptions           See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured).
	 * @throws IllegalArgumentException Thrown if class could not be instantiated. Constructor invocation exceptions are logged separately.
	 */
	@Override
	@Nullable
	@SuppressWarnings("unchecked")
	public T createLoremIpsumObject(@Nullable final Type[] nextGenericsMetaData,
	                                final Map<String, ClassUsageInfo<?>> knownInstances,
	                                final ClassBindings classBindings,
	                                final List<Exception> exceptions) {
		// list of classes, we already used for population. by remembering, we can avoid recursive looping
		final String typeMarker = createTypeMarker(clazz, nextGenericsMetaData);
		if (knownInstances.get(typeMarker) == null || !knownInstances.get(typeMarker).isPopulated()) {
			final T ret = create(nextGenericsMetaData, knownInstances, classBindings, exceptions);

			if (ret != null) {
				return ret;
			} else {
				log.error("tried but failed to produce dummy object...");
				if (!exceptions.isEmpty()) {
					log.error("errors logged:");
					for (final Exception e : exceptions) {
						log.error(e.getMessage(), e);
					}
				}
				throw new IllegalArgumentException(String.format("Could not instantiate object for type [%s], is it abstract and missing a binding?", clazz));
			}
		} else {
			return (T) knownInstances.get(typeMarker).getInstance();
		}
	}

	/**
	 * Will try to create a new object for the given type, while maintaining a track record of already created - and - populated objects to
	 * avoid recursive loops.
	 * <p>
	 * Will first try to defer dummy object creation to a bound {@link LoremIpsumObjectFactory} and if found will defer dummy object creation to
	 * {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)} .
	 *
	 * @param genericMetaData See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param knownInstances  See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param classBindings   See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param exceptions      See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @return The instantiated and populated object (can be a sub type, depending how the {@link ClassBindings} are configured).
	 */
	@SuppressWarnings({"unchecked"})
	@Nullable
	private T create(@Nullable final Type[] genericMetaData,
	                 final Map<String, ClassUsageInfo<?>> knownInstances,
	                 final ClassBindings classBindings,
	                 final List<Exception> exceptions) {
		T ret = null;

		// first try possibly bound dummy factory
		final LoremIpsumObjectFactory<T> factory = classBindings.find(clazz);
		if (factory != null) {
			ret = factory.createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions);
		}

		if (ret == null && clazz.isArray()) {
			ret = new RandomArrayFactory<>(clazz).createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions);
		}

		// if null, we need to create it ourself
		if (ret == null) {
			final ClassUsageInfo<T> usedInfo = new ClassUsageInfo<>();

			if (!isKnownLoopSafeType(clazz)) {
				knownInstances.put(createTypeMarker(clazz, genericMetaData), usedInfo);
			}

			// is the class an enum?
			if (clazz.isEnum()) {
				return (T) new RandomEnumFactory(clazz).createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions);
			}

			// load the constructors
			List<Constructor<?>> cachedConstructors = constructorCache.getConstructorCache(clazz);
			if (cachedConstructors == null) {
				final Constructor<?>[] foundConstructors = clazz.getConstructors();
				sort(foundConstructors, new ConstructorComparator());
				cachedConstructors = new ArrayList<>(Arrays.asList(foundConstructors));
				constructorCache.add(clazz, cachedConstructors.toArray(new Constructor<?>[]{}));
			}

			// check if we have a prefered Constructor and try it
			final Constructor<T> preferedConstructor = (Constructor<T>) constructorCache.getPreferedConstructor(clazz);
			if (preferedConstructor != null) {
				ret = new ConstructorBasedFactory<>(preferedConstructor).createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions);
			}

			if (ret == null) {
				for (final Constructor<?> co : cachedConstructors) {
					ret = new ConstructorBasedFactory<>((Constructor<T>) co).createLoremIpsumObject(genericMetaData, knownInstances, classBindings, exceptions);
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
	 * @param clazz           The raw class, used if no generic meta data is available.
	 * @param genericMetaData The generics meta data, if not null, used to create a unique string including all nested types.
	 * @return A unique key based on a given <code>Class</code> or its generic meta data if available.
	 * @see #createTypeMarker(Type[])
	 */
	static String createTypeMarker(final Class<?> clazz, @Nullable final Type[] genericMetaData) {
		return "|" + clazz.getName() + "|" + createTypeMarker(genericMetaData);
	}

	/**
	 * Creates a unique key for a generics type, including nested generic types.
	 */
	private static String createTypeMarker(@Nullable final Type[] genericMetaData) {
		StringBuilder typeMarker = new StringBuilder();
		if (genericMetaData != null) {
			// if only one type to process
			if (genericMetaData.length == 1) {
				final Type nextGenericMetaData = genericMetaData[0];
				if (nextGenericMetaData instanceof Class) {
					// use the classname
					typeMarker.append(((Class<?>) nextGenericMetaData).getName()).append("|");
				} else {
					// go deeper using the next generics declarations
					typeMarker.append(((Class<?>) ((ParameterizedType) nextGenericMetaData).getRawType()).getName()).append("|");
					typeMarker.append(createTypeMarker(((ParameterizedType) nextGenericMetaData).getActualTypeArguments()));
				}
			} else {
				// split up multiple types
				for (final Type type : genericMetaData) {
					typeMarker.append(createTypeMarker(new Type[]{type}));
				}
			}
		}
		return typeMarker.toString();
	}

	/**
	 * Determines whether the given <code>Class</code> represents a known loop-safe type. A loop-safe type is a type that does not refer to
	 * itself or contains a method or constructor (that LoremIpsumObjectCreator would invoke) that refers to itself.
	 * <p>
	 * This mechanism ensures that <code>Number</code> instances for example don't get locked down to a single known instance. Without this
	 * mechanism, an <code>Integer</code> would be cached and reused for all fields Dummy Creator would encounter. By marking
	 * <code>Integer.class</code> as loop-safe, a new Integer will be created each time a dummy should be created.
	 *
	 * @return Whether the given type is known not to cause an infinite recursive loop.
	 */
	private boolean isKnownLoopSafeType(final Class<T> clazz) {
		final List<Class<?>> safeClasses = new ArrayList<>();
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
	 * Will be deferred {@link #populateCollection(Collection, Type[], Map, ClassBindings, List)}.</li>
	 * <li><strong>Map</strong>: if the type is a subtype of {@link Map}, a random number (2 or 3) of key/value entries will be added. Will
	 * be deferred {@link #populateMap(Map, Type[], Map, ClassBindings, List)}.</li>
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
	 * @param subject        The object to populate with dummy values.
	 * @param knownInstances A list of known instances to keep track of already processed classes (to avoid infinite loop).
	 * @param classBindings  See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param exceptions     Not used, but will be passed to {@link ClassBasedFactory} instances when producing dummies for arrays, collections
	 *                       and maps.
	 * @see #populateCollection(Collection, Type[], Map, ClassBindings, List)
	 * @see #populateMap(Map, Type[], Map, ClassBindings, List)
	 */
	@SuppressWarnings({"unchecked"})
	private void populateObject(final T subject, @Nullable final Type[] genericMetaData, final Map<String, ClassUsageInfo<?>> knownInstances, final ClassBindings classBindings, final List<Exception> exceptions) {
		if (subject instanceof Collection) {
			populateCollection((Collection<Object>) subject, genericMetaData, knownInstances, classBindings, exceptions);
		} else if (subject instanceof Map) {
			populateMap((Map<Object, Object>) subject, genericMetaData, knownInstances, classBindings, exceptions);
		} else {
			// populate POJO using it's bean setters (which should always contain exactly one parameter)
			// by creating a new dummy using a ClassBasedFactory for each Method's parameter and finally invoke the method itself
			for (final Method setter : discoverSetters(subject.getClass())) {
				// collect generics meta data if available
				final ClassBasedFactory<T> factory = new ClassBasedFactory<>((Class<T>) setter.getParameterTypes()[0]);
				final Type genericParameterType = setter.getGenericParameterTypes()[0];
				final boolean isRawClassItself = genericParameterType instanceof Class;
				final Type[] nextGenericsMetaData = isRawClassItself ? null : ((ParameterizedType) genericParameterType).getActualTypeArguments();
				// finally create the parameter with or without generics meta data
				final Object parameter = factory.createLoremIpsumObject(nextGenericsMetaData, knownInstances, classBindings, exceptions);
				try {
					setter.invoke(subject, parameter);
				} catch (final Exception e) {
					log.error("error calling setter method [" + setter.getName() + "]", e);
				}
			}
		}
	}

	/**
	 * A random number of items (2 or 3) will be added. The type will be determined as follows:
	 * <ol>
	 * <li>Using generics meta data straight from the subject: this is possible when the subject is the <em>root class</em> passed into
	 * Dummy Creator, and has generics declared in its interface, such as: <code>FooList extends ArrayList<Foo></code></li>
	 * <li>Using generics meta data from a field declaration (parameter <code>genericMetaData</code>), where this data has been passed in by
	 * a parent factory</li>
	 * <li>By resorting to a <code>String</code> dummy object as a last resort, when we can't determine a type.</li>
	 * </ol>
	 *
	 * @param subject         The collection to populate with dummy objects.
	 * @param genericMetaData If not null, contains the generics meta data for the collection type as declared in the collections field.
	 * @param knownInstances  A list of known instances to keep track of already processed classes (to avoid infinite loop).
	 * @param classBindings   See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param exceptions      Not used, but will be passed to {@link ClassBasedFactory} instances when producing dummies for arrays, collections
	 *                        and maps.
	 */
	private void populateCollection(final Collection<Object> subject, @Nullable final Type[] genericMetaData, final Map<String, ClassUsageInfo<?>> knownInstances, final ClassBindings classBindings,
	                                final List<Exception> exceptions) {
		for (int i = 0; i < LoremIpsumGenerator.getInstance().getRandomInt(2) + 2; i++) {
			// detect dummy class and generic info
			final ClassAndGenericMetaData<?> classInfo = extractClassInfo(subject.getClass(), genericMetaData, 0);
			// create dummy and add to list
			@SuppressWarnings("unchecked") final ClassBasedFactory<?> factory = new ClassBasedFactory<>((Class<Object>) classInfo.getClazz());
			final Object dummyObject = factory.createLoremIpsumObject(classInfo.getGenericMetaData(), knownInstances, classBindings, exceptions);
			subject.add(dummyObject);
		}
	}

	/**
	 * A random number (2 or 3) of entries will be added. The type will be determined as follows:
	 * <ol>
	 * <li>Using generics meta data straight from the subject: this is possible when the subject is the <em>root class</em> passed into
	 * Dummy Creator, and has generics declared in its interface, such as: <code>FooMap extends HashMap<Foo, Bar></code></li>
	 * <li>Using generics meta data from a field declaration (parameter <code>genericMetaData</code>), where this data has been passed in by
	 * a parent factory</li>
	 * <li>By resorting to a <code>String</code> dummy object as a last resort, when we can't determine a type.</li>
	 * </ol>
	 *
	 * @param subject         The map to populate with dummy entries.
	 * @param genericMetaData If not null, contains the generics meta data for the map type as declared in the collections field.
	 * @param knownInstances  A list of known instances to keep track of already processed classes (to avoid infinite loop).
	 * @param classBindings   See {@link LoremIpsumObjectFactory#createLoremIpsumObject(Type[], Map, ClassBindings, List)}.
	 * @param exceptions      Not used, but will be passed to {@link ClassBasedFactory} instances when producing dummies for the map
	 *                        <em>keys</em> or <em>values</em>.
	 */
	private void populateMap(final Map<Object, Object> subject, @Nullable final Type[] genericMetaData, final Map<String, ClassUsageInfo<?>> knownInstances, final ClassBindings classBindings,
	                         final List<Exception> exceptions) {
		for (int i = 0; i < LoremIpsumGenerator.getInstance().getRandomInt(2) + 2; i++) {
			// detect dummy class and generic info
			final ClassAndGenericMetaData<?> keyClassInfo = extractClassInfo(subject.getClass(), genericMetaData, 0);
			final ClassAndGenericMetaData<?> valueClassInfo = extractClassInfo(subject.getClass(), genericMetaData, 1);
			// create dummies and add to map
			@SuppressWarnings("unchecked") final ClassBasedFactory<?> dummyKeyFactory = new ClassBasedFactory<>((Class<Object>) keyClassInfo.getClazz());
			@SuppressWarnings("unchecked") final ClassBasedFactory<?> dummyValueFactory = new ClassBasedFactory<>((Class<Object>) valueClassInfo.getClazz());
			final Object dummyKey = dummyKeyFactory.createLoremIpsumObject(keyClassInfo.getGenericMetaData(), knownInstances, classBindings, exceptions);
			final Object dummyValue = dummyValueFactory.createLoremIpsumObject(valueClassInfo.getGenericMetaData(), knownInstances, classBindings, exceptions);
			subject.put(dummyKey, dummyValue);
		}
	}

	/**
	 * Returns the {@link Class} and its generic meta data for dummy used to fill a {@link Collection} or {@link Map}.
	 * <p>
	 * First tries to get the generics meta data from the subject's class signature itself, rather than from the fiel, method or constructor
	 * parameter declaration. Ie. <code>MyApples extends ArrayList<Apple></code> will first be tried and if unable to find genercis meta
	 * data this way, try to find it from the declaration if it was declared by a parent class (only in this case should
	 * <code>genericMetaData</code> not be null).
	 *
	 * @param clazz           The Collection or Map class which may contain generics meta data as the result of an explicit class signature (ie. class
	 *                        MyApples extends ArrayList<Apple>).
	 * @param genericMetaData Optional generics meta data from a field, method or constructor parameter declaration.
	 * @param index           The index of the generics types list (a <code>List</code> has only one, but a <code>Map</code> can have two, one for
	 *                        <em>key</em> and one for <em>value</em>.
	 * @return A <code>Class</code> and its generics meta data if available.
	 */
	@SuppressWarnings({"rawtypes", "unchecked"})
	private ClassAndGenericMetaData<?> extractClassInfo(Class<?> clazz, @Nullable Type[] genericMetaData, int index) {
		final ClassAndGenericMetaData<Object> keyClassInfo = new ClassAndGenericMetaData<>();

		// detect generic declarations
		final Type[] genericTypes = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
		if (genericTypes.length > index && genericTypes[index] instanceof Class) {
			// uses generic type if available, String and Integer for '<T extends Map<String, Double>>'
			keyClassInfo.setClazz((Class<Object>) genericTypes[index]);
		} else if (genericMetaData != null) {
			// use the generics meta data passed in as a field in the context of a List or Map
			final Class<?> keyClassFromGenerics;

			if (genericMetaData[index] instanceof Class) {
				keyClassFromGenerics = (Class<?>) genericMetaData[index];
			} else {
				keyClassFromGenerics = (Class<?>) ((ParameterizedType) genericMetaData[index]).getRawType();
				keyClassInfo.setGenericMetaData(((ParameterizedType) genericMetaData[index]).getActualTypeArguments());
			}

			keyClassInfo.setClazz((Class<Object>) keyClassFromGenerics);
		} else {
			// use default String and String value for raw type Collection
			keyClassInfo.setClazz((Class) String.class);
		}
		return keyClassInfo;
	}

	private List<Method> discoverSetters(final Class<?> clazz) {
		List<Method> setters = constructorCache.getMethodCache(clazz);
		if (setters == null) {
			setters = new ArrayList<>();
			final Map<Class<?>, List<FieldWrapper>> fields = BeanUtils.collectFields(clazz, Object.class, allOf(Visibility.class), of(BeanRestriction.YES_SETTER));
			for (final List<FieldWrapper> fieldWrappers : fields.values()) {
				for (final FieldWrapper fieldWrapper : fieldWrappers) {
					setters.add(fieldWrapper.getSetter());
				}
			}

			constructorCache.add(clazz, setters.toArray(new Method[]{}));
		}
		return setters;
	}

	/**
	 * Helper class to hold together a {@link Class} and its generics meta data. Used as a return type.
	 *
	 * @see ClassBasedFactory#extractClassInfo(Class, Type[], int)
	 */
	@Data
	@SuppressFBWarnings(justification = "Generated code")
	private static class ClassAndGenericMetaData<T> {
		private Class<T> clazz;
		@Nullable
		private Type[] genericMetaData;
	}
	
	@SuppressFBWarnings(value = "SE_COMPARATOR_SHOULD_BE_SERIALIZABLE", justification = "comparator will never be serialized")
	private static class ConstructorComparator implements Comparator<Constructor<?>> {
		/**
		 * Comparator to sort constructors by their number of parameters
		 * (and then name for order consistency in junit testing)
		 */
		@Override
		public int compare(final Constructor<?> o1, final Constructor<?> o2) {
			final int lenCmp = Integer.compare(o1.getParameterTypes().length, o2.getParameterTypes().length);
			return lenCmp != 0 ? lenCmp : signatureHash(o1).compareTo(signatureHash(o2));
		}
		
		private String signatureHash(@NotNull final Constructor<?> o1) {
			final StringBuilder s = new StringBuilder();
			for (Class<?> parameterType : o1.getParameterTypes()) {
				s.append(parameterType.getSimpleName());
			}
			return s.toString();
		}
	}
}