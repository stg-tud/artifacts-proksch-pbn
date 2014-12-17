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

import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;

public class AssertsTest {

	@Test(expected = AssertionException.class)
	public void nullThrowsException() {
		Asserts.assertNotNull(null);
	}

	@Test
	public void nonNullDoesNotThrowsException() {
		Asserts.assertNotNull("non null");
	}

	@Test
	public void positivValues() {
		Asserts.assertNotNegative(1);
	}

	@Test
	public void zeroValue() {
		Asserts.assertNotNegative(0);
	}

	@Test(expected = AssertionException.class)
	public void negativeValues() {
		Asserts.assertNotNegative(-1);
	}

	@Test
	public void equalInts() {
		Asserts.assertEquals(1, 1);
	}

	@Test(expected = AssertionException.class)
	public void unequalInts() {
		Asserts.assertEquals(1, 2);
	}

	@Test
	public void assertNotNegative() {
		Asserts.assertNotNegative(1.0);
		Asserts.assertNotNegative(1);
	}

	@Test(expected = AssertionException.class)
	public void assertNotNegativeCanFailForDoubles() {
		Asserts.assertNotNegative(-1.0);
	}

	@Test(expected = AssertionException.class)
	public void assertNotNegativeCanFailForInts() {
		Asserts.assertNotNegative(-1);
	}

	@Test
	public void assertGreaterThan() {
		Asserts.assertGreaterThan(3.0, 2.0);
		Asserts.assertGreaterThan(3, 2);
	}

	@Test(expected = AssertionException.class)
	public void assertGreaterThanCanFailForDoubles() {
		Asserts.assertGreaterThan(3.0, 3.0);
	}

	@Test(expected = AssertionException.class)
	public void assertGreaterThanCanFailForInts() {
		Asserts.assertGreaterThan(2, 2);
	}

	@Test
	public void assertLessOrEqual() {
		Asserts.assertLessOrEqual(1.0, 2.0);
		Asserts.assertLessOrEqual(4.0, 4.0);
	}

	@Test(expected = AssertionException.class)
	public void assertLessOrEqualCanFail() {
		Asserts.assertLessOrEqual(4.0, 3.0);
	}

	@Test
	public void initAssertsForCodeCoverage() {
		// don't blame the coder, blame the metric :P
		new Asserts();
	}
}