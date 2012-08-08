DummyCreator
Developed by Alexander Muthmann (http://www.dev-eth0.de)

1. Description
--------------------------------------------------------------------------------

The DummyCreator is a library, which provides a possibility to automatically
create dummy-objects. Those dummy objects are created with all of their
attributes set. So you can use them as a helper while testing your application.

For example, if you want to test a class, which processes data from a database,
you might want to use random instances instead of data from the real database
(which might not be ready yet or something like that). Now you could write a
method for each class to create such a dummy instance.


2. How to use
--------------------------------------------------------------------------------

If you want to use the DummyCreator, you just have to integrate the jar-file to
your project. Afterwards you can call it by:
    DummyCreator.createDummyOfClass(The class you need)

There are several possibilities to take influence on the creation-process.

2.1 Register Interfaces
--------------------------------------------------------------------------------

Normally you would often use a interface instead of the real class if you
define an attribute of a class. For example, you would write

private List myList;

instead of

private ArrayList myList;

Now, there is a problem, when we try to create a dummy instance of myList.
To solve this, you need to bind an implementation of the interface to the
interface with the ClassBindings?:

This can be done by:

ClassBindings.add(List.class, ArrayList.class);

By doing this, you can bind every implementation to an interface you want.


2.2 Select a Constructor
--------------------------------------------------------------------------------

In the normal creation process of a dummy object, the creator tries to use all
constructors of the class until one worked. If you want to preselect the
constructor that should be used, you can bind it to the ClassBindings:

ClassBindings.add(Class clazz, Constructor c);

To get the constructor, you normally would use reflection and then choose
the needed one.

Constructor[] constructors = clazz.getConstructors();


2.3 Preselect which object to use
--------------------------------------------------------------------------------

If you want to use a specific object for a certain class, you can register it
at the ClassBindings:

ClassBindings.add(Class clazz, Object o);

Every time the DummyCreator is called with this class, it will return the given
object.


2.4 Use a creation-method for classes
--------------------------------------------------------------------------------

You can also register a method which returns a certain class. If you now want
to create an object of this class with the DummyCreator, it uses the given
class:

ClassBindings.add(Class clazz, Method m);


3. Help
--------------------------------------------------------------------------------

If you have any questions, feel free to contact me at dummyCreator@dev-eth0.de