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

import java.util.List;

import org.apache.commons.math.util.MathUtils;

import cc.recommenders.assertions.Asserts;

import com.google.common.collect.Lists;

public class Averager {

	private List<Double> data = Lists.newLinkedList();

	public void reinit() {
		data.clear();
	}

	public boolean hasValues() {
		return !data.isEmpty();
	}

	public void add(double value) {
		data.add(value);
	}

	public double getAverage() {
		Asserts.assertFalse(data.isEmpty());

		double avg = 0.0;
		for (Double v : data) {
			avg += v;
		}
		avg = avg / data.size();

		return avg;
	}

	public double getRoundedAverage(int scale) {
		return MathUtils.round(getAverage(), scale);
	}

	public int getIntAverage() {
		return (int) Math.round(getAverage());
	}
}