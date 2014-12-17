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

import org.apache.commons.lang3.tuple.Pair;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class SizeAndF1Evaluator implements Evaluator<Usage, Pair<Boxplot, Integer>, Query> {

	private final F1Evaluator f1Evaluator;
	private final SizeEvaluator sizeAverager;

	@Inject
	public SizeAndF1Evaluator(F1Evaluator f1Evaluator, SizeEvaluator sizeAverager) {
		this.f1Evaluator = f1Evaluator;
		this.sizeAverager = sizeAverager;
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
	public Pair<Boxplot, Integer> getResults() {
		Boxplot f1 = f1Evaluator.getResults();
		int avgModelSize = sizeAverager.getResults();
		return Pair.of(f1, avgModelSize);
	}

	public Pair<double[], Integer> getRawResults() {
		double[] f1s = f1Evaluator.getRawResults();
		int avgModelSize = sizeAverager.getResults();
		return Pair.of(f1s, avgModelSize);
	}
}