package org.dummycreator.helperutils;

public class TestChainBinding {
	public static abstract class A {

		public final int arg;

		public A(int arg) {
			this.arg = arg;
		}
	};

	public static abstract class B extends A {

		public B(int arg) {
			super(arg);
		}
	};

	public static class C extends B {

		public C(int arg) {
			super(arg);
		}

	};
}