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
package cc.recommenders.evaluation.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;

public class AveragerTest {

	private static final double DOUBLE_THRESHOLD = 0.001;
	private Averager sut;

	@Before
	public void setup() {
		sut = new Averager();
	}

	@Test
	public void doesNotHaveValuesByDefault() {
		assertFalse(sut.hasValues());
		sut.add(1);
		assertTrue(sut.hasValues());
	}

	@Test
	public void canBeReset() {
		sut.add(1);
		sut.reinit();
		assertFalse(sut.hasValues());
	}

	@Test(expected = AssertionException.class)
	public void averageCannotBeComputedWithoutValues() {
		sut.getAverage();
	}

	@Test
	public void correctCalculation() {
		sut.add(0.0);
		sut.add(1.0);
		sut.add(0.25);
		sut.add(0.75);
		sut.add(0.5);
		double actual = sut.getAverage();
		double expected = 0.5;
		assertEquals(expected, actual, DOUBLE_THRESHOLD);
	}

	@Test
	public void correctCalculationWithLotsOfCommas() {
		sut.add(0.0);
		sut.add(0.0);
		sut.add(1.0);
		double actual = sut.getAverage();
		double expected = 1.0 / 3.0;
		assertEquals(expected, actual, 0.000000000000001);
	}

	@Test
	public void correctRounding1() {
		sut.add(0.0);
		sut.add(0.0);
		sut.add(1.0);
		double actual = sut.getRoundedAverage(3);
		double expected = 0.333;
		assertEquals(expected, actual, 0.0001);
	}

	@Test
	public void correctRounding2() {
		sut.add(0.0);
		sut.add(1.0);
		sut.add(1.0);
		double actual = sut.getRoundedAverage(4);
		double expected = 0.6667;
		assertEquals(expected, actual, 0.00001);
	}

	@Test
	public void getAsInt1() {
		sut.add(0.0);
		sut.add(0.0);
		sut.add(1.0);
		int actual = sut.getIntAverage();
		int expected = 0;
		assertEquals(expected, actual);
	}

	@Test
	public void getAsInt2() {
		sut.add(0.0);
		sut.add(1.0);
		sut.add(1.0);
		int actual = sut.getIntAverage();
		int expected = 1;
		assertEquals(expected, actual);
	}
}