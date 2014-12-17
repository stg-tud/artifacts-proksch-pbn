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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.data.ElevenPointCurve;
import cc.recommenders.evaluation.data.Point;

public class ElevenPointCurveTest {

	private static final double DOUBLE_TRESH = 0.001;
	private ElevenPointCurve uut;

	@Before
	public void setup() {
		uut = new ElevenPointCurve();
	}

	@Test
	public void defaultCurveIsAZeroCurve() {
		double[] actuals = uut.getAsArray();
		double[] expecteds = new double[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		assertArrayEquals(expecteds, actuals, DOUBLE_TRESH);
	}

	@Test
	public void defaultCurveHasACountOfOne() {
		int actual = uut.getCount();
		int expected = 1;

		assertEquals(expected, actual);
	}

	@Test
	public void countCanBeSetToPositiveIntegers() {
		uut.setCount(3);
		int actual = uut.getCount();
		int expected = 3;
		assertEquals(expected, actual);
	}

	@Test(expected = RuntimeException.class)
	public void settingCountToZeroThrowsARuntimeException() {
		uut.setCount(0);
	}

	@Test(expected = RuntimeException.class)
	public void settingANegativeCountThrowsARuntimeException() {
		uut.setCount(-1);
	}

	@Test
	public void equalCurvesAreDetected() {
		ElevenPointCurve c1 = createCurve();
		ElevenPointCurve c2 = createCurve();

		assertEquals(c1, c2);
		assertTrue(c1.hashCode() == c2.hashCode());
	}

	@Test
	public void differentCurveValuesAreDetected() {
		ElevenPointCurve c1 = createCurve();
		ElevenPointCurve c2 = createCurveWithDifferentValues();

		assertFalse(c1.equals(c2));
		assertFalse(c1.hashCode() == c2.hashCode());
	}

	@Test
	public void differentCurveCountsAreDetected() {
		ElevenPointCurve c1 = createCurve();
		ElevenPointCurve c2 = createCurveWithDifferentCount();

		assertFalse(c1.equals(c2));
		assertFalse(c1.hashCode() == c2.hashCode());
	}

	@Test
	public void differentTypesAreNotEqualToCurves() {
		ElevenPointCurve c1 = createCurve();
		String c2 = "some string";

		assertFalse(c1.equals(c2));
		assertFalse(c1.hashCode() == c2.hashCode());
	}

	@Test
	public void precisionsCanBeSetByProvidingPoints() {

		double[] expecteds = new double[11];

		List<Point> points = new LinkedList<Point>();
		for (int i = 0; i < 11; i++) {
			double val = i * 0.1;
			points.add(new Point(val, val));
			expecteds[i] = val;
		}
		uut.setPoints(points);
		double[] actuals = uut.getAsArray();

		assertArrayEquals(expecteds, actuals, DOUBLE_TRESH);
	}

	@Test(expected = RuntimeException.class)
	public void settingAWrongCountOfPointsThrowsAnError() {
		List<Point> points = new LinkedList<Point>();
		points.add(mock(Point.class));
		uut.setPoints(points);
	}

	@Test(expected = RuntimeException.class)
	public void settingAWrongCountOfPrecisionsThrowsAnError() {
		uut.setPrecisions(new double[] { 0.0, 0.1 });
	}

	@Test
	public void averageCanBeCalculated() {
		uut = createCurve();
		double actual = uut.getAverage();
		double expected = 0.54;
		assertEquals(expected, actual, DOUBLE_TRESH);
	}

	private ElevenPointCurve createCurve() {

		ElevenPointCurve curve = new ElevenPointCurve();
		curve.setCount(12);
		curve.setPrecisions(new double[] { 0.0, 0.12, 0.23, 0.34, 0.45, 0.56, 0.67, 0.78, 0.89, 0.9, 1.0 });

		return curve;
	}

	private ElevenPointCurve createCurveWithDifferentCount() {

		ElevenPointCurve curve = createCurve();
		curve.setCount(23);
		return curve;
	}

	private ElevenPointCurve createCurveWithDifferentValues() {

		ElevenPointCurve curve = createCurve();
		curve.set(1, 0.123);
		return curve;
	}
}