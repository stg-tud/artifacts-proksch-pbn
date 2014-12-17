/*******************************************************************************
 * Copyright (c) 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sebastian Proksch - initial API and implementation
 ******************************************************************************/
package cc.recommenders.evaluation.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cc.recommenders.evaluation.data.Point;

public class PointTest {

	private static final double DOUBLE_DELTA = 0.0001;

	@Test
	public void defaultValues() {
		Point p = new Point();

		assertEquals(0.0, p.recall, DOUBLE_DELTA);
		assertEquals(0.0, p.precision, DOUBLE_DELTA);
	}

	@Test
	public void customConstructor() {
		Point p = new Point(0.3, 0.8);

		assertEquals(0.3, p.recall, DOUBLE_DELTA);
		assertEquals(0.8, p.precision, DOUBLE_DELTA);
	}

	@Test
	public void equalPointsEqual() {
		Point p1 = new Point(0.1, 0.2);
		Point p2 = new Point(0.1, 0.2);

		assertEquals(p1, p2);
		assertTrue(p1.hashCode() == p2.hashCode());
	}

	@Test
	public void differentPointsAreNotEqual() {
		Point p1 = new Point(0.1, 0.2);
		Point p2 = new Point(0.3, 0.4);

		assertFalse(p1.equals(p2));
		assertFalse(p1.hashCode() == p2.hashCode());
	}

	@Test
	public void differentTypesAreNotEqual() {
		Point p1 = new Point(0.1, 0.2);
		String p2 = "blubb";

		assertFalse(p1.equals(p2));
		assertFalse(p1.hashCode() == p2.hashCode());
	}

	@Test
	public void valuesAreCorrectlyPrinted() {
		Point p = new Point(0.2, 0.7);
		String actual = p.toString();
		String expected = String.format("[%f,%f]", 0.2, 0.7);
		assertEquals(expected, actual);
	}
}