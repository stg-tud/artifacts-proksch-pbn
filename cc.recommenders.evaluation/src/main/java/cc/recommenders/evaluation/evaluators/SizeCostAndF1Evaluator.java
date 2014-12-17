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
package cc.recommenders.evaluation.evaluators;

import java.util.List;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class SizeCostAndF1Evaluator implements Evaluator<Usage, Double, Query> {

	private final F1Evaluator f1Evaluator;
	private final SizeEvaluator sizeAverager;

	// private final double lambda;
	private final double min;
	private final double max;

	@Inject
	public SizeCostAndF1Evaluator(F1Evaluator f1Evaluator, SizeEvaluator sizeAverager, MiningOptions miningOptions,
			int min, int max) {

		this.f1Evaluator = f1Evaluator;
		this.sizeAverager = sizeAverager;

		// this.lambda = miningOptions.getLambdaSize();
		this.min = min;
		this.max = max;

		// Asserts.assertLessOrEqual(0.0, lambda);
		// Asserts.assertLessOrEqual(lambda, 1.0);
		Asserts.assertPositive(min);
		Asserts.assertGreaterThan(max, min);
	}

	@Override
	public void reinit() {
		f1Evaluator.reinit();
		sizeAverager.reinit();
	}

	@Override
	public void query(ICallsRecommender<Query> rec, List<Usage> validationData) {
		f1Evaluator.query(rec, validationData);
		sizeAverager.query(rec, validationData);
	}

	@Override
	public boolean hasResults() {
		return f1Evaluator.hasResults();
	}

	@Override
	public Double getResults() {
		double f1 = f1Evaluator.getResults().getMean();
		double s = calcSize();
		Logger.log("f1 %f, s %f\n", f1, s);
		// return lambda * f1 + (1 - lambda) * s;
		throw new RuntimeException("currently broken?!");
	}

	private double calcSize() {
		int avgSize = sizeAverager.getResults();
		// Logger.log("avg. size %d\n", avgSize);
		double val = (avgSize - min) / (max - min);
		val = Math.abs(val);
		return Math.max(Math.min(1.0, val), 0.0);
	}
}