[![APACHE v2 License](https://img.shields.io/badge/license-apachev2-blue.svg?style=flat)](LICENSE-2.0.txt) 
[![Latest Release](https://img.shields.io/maven-central/v/com.github.bbottema/lorem-ipsum-objects.svg?style=flat)](http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.github.bbottema%22%20AND%20a%3A%22lorem-ipsum-objects%22) 
[![Javadocs](http://www.javadoc.io/badge/com.github.bbottema/lorem-ipsum-objects.svg)](http://www.javadoc.io/doc/com.github.bbottema/lorem-ipsum-objects)
[![Codacy](https://img.shields.io/codacy/grade/189c035f5cd549f2a936b3b3b215df1f?style=flat)](https://www.codacy.com/app/b-bottema/lorem-ipsum-objects)

# Description

LoremIpsumObjectCreator is a small tool that helps you generate test data by creating 
populated dummy objects of a given type. Those dummy objects are created with 
all of their attributes set. So you can use them as a helper while testing your 
application.

```
<dependency>
  <groupId>com.github.bbottema</groupId>
  <artifactId>lorem-ipsum-objects</artifactId>
  <version>2.0.0</version>
</dependency>
```

See [RELEASE.txt](RELEASE.txt) for the release notes.

---
Note: this library is a reincarnation of [deveth0/dummycreator](https://github.com/deveth0/dummycreator) 
(auto-migrated by Google Code), which was started by Alex Muthmann and further developed by myself but was 
mostly abandoned in late 2012.

# How to use

Basic usage: 

```java
LoremIpsumObjectCreator creator = new LoremIpsumObjectCreator();

create.createLoremIpsumObject(clazz);
```

Usage with custom factories:

```java
ClassBindings classBindings = new ClassBindings(); // or:
ClassBindings classBindings = ClassBindings.defaultBindings(); // defaults for collections

classBindings.add(List.class, new ClassBasedFactory<>(ArrayList.class));

LoremIpsumObjectCreator creator = new LoremIpsumObjectCreator(classBindings);
create.createLoremIpsumObject(clazz);
```

lorem-ipsum-objects is very flexible and allows you to control how objects are created or reused. 
This is done using one of the built in factories or you can provide your own.

The following factories are available:

 * ClassBasedFactory
 * ConstructorBasedFactory
 * MethodBasedFactory
 * FixedInstanceFactory
 
Below examples demonstrate their usage...

## Register Interfaces

Tie concrete classes to interfaces:

```java
classBindings.add(List.class, new ClassBasedFactory<>(ArrayList.class));

LoremIpsumObjectCreator.createLoremIpsumObject(List.class); // returns an ArrayList
```

Default settings:

```java
classBindings.add(List.class, new ClassBasedFactory<>(ArrayList.class));
classBindings.add(Map.class, new ClassBasedFactory<>(HashMap.class));
classBindings.add(Set.class, new ClassBasedFactory<>(HashSet.class));
```

## Preselect which object to use

If you want to use a specific object for a certain class, you can register it
with a Class binding:

```java
classBindings.add(Foo.class, new FixedInstanceFactory<>(new Foo()));
classBindings.add(Bar.class, new FixedInstanceFactory<>(new SubclassOfBar()));
```

Every time the LoremIpsumObjectCreator is called with these class, it will return 
the defined fixed object instances.

## Select a Constructor
       
In a regular creation process of a dummy object, the library tries every visible 
constructor until one worked. If you want to preselect the
constructor that should be used, you can bind it with a class binding:

```java
classBindings.add(clazz, new ConstructorBasedFactory<>(yourConstructor));
```

If you don't already have your own constructor then [java-reflection](https://github.com/bbottema/java-reflection), 
the library used by lorem-ipsum-objects, has many useful methods for finding the constructor you want.

For example:

```java
// finds the first constructor that is compatible with int and String arguments 
// (so a constructor with long, Pear also matches)
EnumSet<LookupMode> lookupMode = of(AUTOBOX, CAST_TO_SUPER, CAST_TO_INTERFACE);
Constructor c = MethodUtils.findCompatibleConstructor(clazz, lookupMode, int.class, Fruit.class);
```

## Use a creation-method for classes

You can also register a method which returns a certain class. If you now want
to create an object of this class with the LoremIpsumObjectCreator, it uses the given
class:

```java
classBindings.add(clazz, new MethodBasedFactory<>(yourMethod));
```

For methods, [java-reflection](https://github.com/bbottema/java-reflection) has even more helper functions:

```java
// for example:
Method m = ClassUtils.findFirstMethodByName(clazz, clazz, of(PUBLIC), "myFactoryMethod");
Method m = MethodUtils.findCompatibleMethod(clazz, lookupMode, 5, "my value");
Method m = MethodUtils.findCompatibleMethod(clazz, lookupMode, double.class, String.class);
```
    
## Use a custom factory for classes

Finally, for maximum freedom, you can also use your own factory:

```java
classBindings.add(Class clazz, LoremIpsumObjectFactory yourCustomObjectFactory);
```