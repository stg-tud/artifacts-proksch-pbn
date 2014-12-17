/**
 * Copyright (c) 2011-2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.assertions;

import static java.lang.String.format;
import cc.recommenders.exceptions.AssertionException;

public class Asserts {
	public static void assertNotNull(Object o) {
		assertNotNull(o, format("reference must not be null"));
	}

	public static void assertNotNull(Object o, String message) {
		if (o == null) {
			throw new AssertionException(message);
		}
	}

	public static void assertNotNegative(int arg) {
		if (arg < 0) {
			throw new AssertionException(format("parameter %d must not be negative", arg));
		}
	}

	public static void assertNotNegative(double arg) {
		if (arg < 0) {
			throw new AssertionException(format("parameter %f must not be negative", arg));
		}
	}

	public static void assertGreaterThan(double a, double b) {
		if (a <= b) {
			throw new AssertionException(format("parameter %f must be greater than parameter %f", a, b));
		}
	}

	public static void assertGreaterThan(int a, int b) {
		if (a <= b) {
			throw new AssertionException(format("first parameter (%d) must be > second parameter (%d)", a, b));
		}
	}

	public static void assertGreaterOrEqual(int a, int b) {
		if (a < b) {
			throw new AssertionException(format("first parameter (%d) must be >= second parameter (%d)", a, b));
		}
	}

	public static void assertGreaterOrEqual(double a, double b) {
		if (a < b) {
			throw new AssertionException(format("first parameter (%f) must be >= second parameter (%f)", a, b));
		}
	}

	public static void assertEquals(int a, int b) {
		if (a != b) {
			throw new AssertionException(format("parameters %d and %d must be equal", a, b));
		}
	}

	public static void assertLessOrEqual(double a, double b) {
		if (a > b) {
			throw new AssertionException(format("parameter %f must be less or equal to %f", a, b));
		}
	}

	public static void assertPositive(int a) {
		if (a < 1) {
		}
	}

	public static void fail(String cause) {
		throw new AssertionException(cause);
	}

	public static void assertEquals(double a, double b, double threshold) {
		if (Math.abs(a - b) > threshold) {
			throw new AssertionException(format("assertion failed: a > b (a: %f -- b:  %f)", a, b));
		}
	}

	public static void assertEquals(Object a, Object b, String message) {
		if (!a.equals(b)) {
			throw new AssertionException(message);
		}
	}

	public static void assertTrue(boolean condition, String message) {
		if (!condition) {
			throw new AssertionException(message);
		}
	}

	public static void assertTrue(boolean condition) {
		assertTrue(condition, "unexpected condition");
	}

	public static void assertFalse(boolean condition) {
		assertFalse(condition, "unexpected condition");
	}

	public static void assertFalse(boolean condition, String message) {
		if (condition) {
			throw new AssertionException(message);
		}
	}
}