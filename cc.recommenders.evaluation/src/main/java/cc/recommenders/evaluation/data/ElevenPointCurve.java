/*******************************************************************************
 * Copyright (c) 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 ******************************************************************************/
package cc.recommenders.evaluation.data;

import java.util.Arrays;
import java.util.List;

public class ElevenPointCurve {

	private int count = 1;
	private double[] precisions = new double[11];

	public ElevenPointCurve() {
		for (int i = 0; i < 11; i++) {
			precisions[i] = 0.0;
		}
	}

	public void setPoints(List<Point> points) {
		if (points.size() != 11) {
			throw new RuntimeException("invalid count of points for a 11 point curve");
		}

		int i = 0;
		for (Point p : points) {
			set(i++, p.precision);
		}
	}

	public void setPrecisions(double[] precisions) {
		if (precisions.length != 11) {
			throw new RuntimeException("invalid count of points for a 11 point curve");
		}

		this.precisions = precisions;
	}

	public void setCount(int count) {
		if (count > 0)
			this.count = count;
		else
			throw new RuntimeException("invalid count");
	}

	public int getCount() {
		return count;
	}

	public void set(int i, double precision) {
		precisions[i] = precision;
	}

	public Point get(int i) {
		Point p = new Point();
		p.recall = i * 0.1;
		p.precision = precisions[i];
		return p;
	}

	public Double getAverage() {

		Double average = 0.0;

		for (Double point : precisions) {
			average += point;
		}

		average = average / 11.0;

		return average;
	}

	public double[] getAsArray() {

		double[] res = new double[11];

		for (int i = 0; i < 11; i++) {
			Point point = get(i);
			res[i] = point.precision;
		}

		return res;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash = hash * 37 + new Integer(count).hashCode();
		for (int i = 0; i < 11; i++) {
			hash = hash * 37 + new Double(precisions[i]).hashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ElevenPointCurve) {
			ElevenPointCurve other = (ElevenPointCurve) obj;

			boolean eq = true;
			eq = other.count == count ? eq : false;

			for (int i = 0; i < 11; i++) {
				eq = other.precisions[i] == precisions[i] ? eq : false;
			}

			return eq;

		} else
			return false;
	}

	@Override
	public String toString() {
		return String.format("[%dx -- %s]", count, Arrays.toString(precisions));
	}
}