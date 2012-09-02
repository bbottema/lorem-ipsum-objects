package org.dummycreator.helperutils;

public class TestChainBinding {
	public static abstract class A {

		public final int arg;

		public A(final int arg) {
			this.arg = arg;
		}
	};

	public static abstract class B extends A {

		public B(final int arg) {
			super(arg);
		}
	};

	public static class C extends B {

		public C(final int arg) {
			super(arg);
		}

	};
}