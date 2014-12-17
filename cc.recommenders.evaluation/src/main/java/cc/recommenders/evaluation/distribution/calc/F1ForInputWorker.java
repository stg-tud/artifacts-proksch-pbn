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
package cc.recommenders.evaluation.distribution.calc;

import static cc.recommenders.io.Logger.append;
import static cc.recommenders.io.Logger.log;

import java.util.List;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.collections.SublistSelector;
import cc.recommenders.evaluation.evaluators.F1Evaluator;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class F1ForInputWorker extends AbstractWorker<F1ForInputTask> {

	private static final long serialVersionUID = 7591266312749598621L;
	public static final int NUM_OF_QUERIES_OVER_ALL_FOLDS = 40000;

	@Inject
	public transient F1Evaluator evaluator;

	private F1ForInputTask task;

	public F1ForInputWorker(F1ForInputTask task) {
		super(task);
		this.task = task;
	}

	@Override
	public void call2() {

		evaluator.reinit();

		List<Usage> trainingData = getTrainingData();
		List<Usage> validationData = getValidationData();

		Asserts.assertGreaterOrEqual(trainingData.size(), task.inputSize);
		int numQueries = getNumQueries(validationData.size());
		int numIterations = getNumIterations(numQueries);

		for (int i = 0; i < numIterations; i++) {
			logIteration(i, numQueries);
			List<Usage> trainingSubset = SublistSelector.pickRandomSublist(trainingData, task.inputSize);
			List<Usage> validationSubset = SublistSelector.pickRandomSublist(validationData, numQueries);
			Miner<Usage, Query> miner = minerFactory.get();
			ICallsRecommender<Query> rec = miner.createRecommender(trainingSubset);
			evaluator.query(rec, validationSubset);

		}

		task.f1s = evaluator.getRawResults();

	}

	private int getNumQueries(int validationSize) {
		int queries = Math.min(validationSize, task.inputSize / 9);
		return Math.min(queries, 2000);
	}

	private int getNumIterations(int numQueries) {
		return (NUM_OF_QUERIES_OVER_ALL_FOLDS / task.numFolds) / numQueries;
	}

	private void logIteration(int i, int numQueries) {
		int numIterations = getNumIterations(numQueries);
		if (i == 0) {
			log("%d iterations: ", numIterations);
		}
		if (numIterations > 10) {
			if (i % 120 == 0) {
				append("\n");
			}
			append(".");
		} else {
			log("- iteration %d/%d", (i + 1), numIterations);
		}
	}
}