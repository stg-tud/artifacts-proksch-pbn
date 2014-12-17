/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.evaluation.evaluators;

import static cc.recommenders.assertions.Asserts.assertGreaterOrEqual;
import static cc.recommenders.assertions.Asserts.assertGreaterThan;
import static cc.recommenders.assertions.Asserts.assertPositive;
import static com.google.common.collect.Iterables.partition;
import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import cc.recommenders.collections.SublistSelector;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;

public class Validations {

	/**
	 * only used for testing purposes
	 */
	public static <In, Out, Query> void testValidation(Miner<In, Query> miner, Evaluator<In, ?, Query> e,
			List<In> trainingData, List<In> validationData) {

		assertPositive(trainingData.size());
		assertPositive(validationData.size());

		ICallsRecommender<Query> rec = miner.createRecommender(trainingData);
		e.query(rec, validationData);
	}

	public static <In, Out, Query> void nFoldCrossValidation(int numFolds, Miner<In, Query> miner,
			Evaluator<In, ?, Query> e, List<In> in) {

		assertGreaterOrEqual(in.size(), numFolds);
		assertGreaterThan(numFolds, 1);

		for (int i = 0; i < numFolds; i++) {
			List<In> trainingData = createTrainingData(i, numFolds, in);
			List<In> validationData = createValidationData(i, numFolds, in);

			ICallsRecommender<Query> rec = miner.createRecommender(trainingData);
			e.query(rec, validationData);
		}
	}

	/**
	 * sometimes it is not necessary to do a full n-fold cross-validation (e.g.
	 * performance measurement). Here, only a single fold is calculated and at
	 * most <i>maxNumberOfQueries</i> queries are placed for validation
	 */
	public static <In, Out, Query> void fuzzyOneFoldValidation(int numFolds, Miner<In, Query> miner,
			Evaluator<In, ?, Query> e, List<In> in, int maxNumberOfQueries) {

		assertGreaterOrEqual(in.size(), numFolds);
		assertGreaterThan(numFolds, 1);

		List<In> trainingData = createTrainingData(0, numFolds, in);
		List<In> validationData = createValidationData(0, numFolds, in);

		ICallsRecommender<Query> rec = miner.createRecommender(trainingData);
		if (validationData.size() < maxNumberOfQueries) {
			e.query(rec, validationData);
		} else {
			List<In> sublist = SublistSelector.pickRandomSublist(validationData, maxNumberOfQueries);
			e.query(rec, sublist);
		}
	}

	public static <In, Out, Query> void sizedNFoldCrossValidation(int numFolds, Miner<In, Query> miner,
			Evaluator<In, ?, Query> e, List<In> in, int size) {

		assertGreaterOrEqual(in.size(), numFolds);
		assertPositive(size);
		assertGreaterThan(numFolds, 1);

		for (int i = 0; i < numFolds; i++) {
			List<In> allTestData = createValidationData(i, numFolds, in);
			List<In> allTrainingData = createTrainingData(i, numFolds, in);

			for (List<In> trainingData : partition(allTrainingData, size)) {

				boolean isListBigEnough = trainingData.size() == size;
				if (isListBigEnough) {
					ICallsRecommender<Query> rec = miner.createRecommender(trainingData);
					e.query(rec, allTestData);
				}
			}
		}
	}

	private static <In> List<In> createValidationData(int fold, int numFolds, List<In> ins) {

		List<In> outs = newArrayList();

		int i = 0;
		for (In in : ins) {
			if (i == fold) {
				outs.add(in);
			}
			i = (i + 1) % numFolds;
		}

		return outs;
	}

	private static <In> List<In> createTrainingData(int fold, int numFolds, List<In> ins) {

		List<In> outs = newArrayList();

		int i = 0;
		for (In in : ins) {
			if (i != fold) {
				outs.add(in);
			}
			i = (i + 1) % numFolds;
		}

		return outs;
	}
}