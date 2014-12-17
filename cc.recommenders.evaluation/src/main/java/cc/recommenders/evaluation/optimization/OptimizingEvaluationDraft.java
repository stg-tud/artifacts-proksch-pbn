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
package cc.recommenders.evaluation.optimization;

import static cc.recommenders.evaluation.evaluators.Validations.nFoldCrossValidation;

import java.util.List;

import cc.recommenders.evaluation.evaluators.F1Evaluator;
import cc.recommenders.evaluation.io.DecoratedObjectUsageStore;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.pbn.PBNMiner;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class OptimizingEvaluationDraft {
	private DecoratedObjectUsageStore ouStore;
	private F1Evaluator evaluator;
	private PBNMiner ouMiner;
	private QueryOptions queryOptions;
	private MiningOptions miningOptions;

	@Inject
	public OptimizingEvaluationDraft(DecoratedObjectUsageStore ouStore, F1Evaluator evaluator,
			PBNMiner ouMiner, QueryOptions queryOptions, MiningOptions miningOptions) {
		this.ouStore = ouStore;
		this.evaluator = evaluator;
		this.ouMiner = ouMiner;
		this.queryOptions = queryOptions;
		this.miningOptions = miningOptions;
	}

	public void run() {

		while (fancyCriterion()) {

			evaluator.reinit();

			for (ITypeName type : ouStore.getKeys()) {
				List<Usage> observations = ouStore.read(type);
				if (observations.size() >= 3) {
					nFoldCrossValidation(3, ouMiner, evaluator, observations);
				}
			}

			double f1 = evaluator.getResults().getMedian();

			modifyParameters();
		}

	}

	public void run2() {
		double[] bestSetup = new double[0];
		double bestF1 = 0;

		for (double[] setup : findInterestingPoints()) {
			double f1 = eval(setup);

			if (f1 > bestF1) {
				bestF1 = f1;
				bestSetup = setup;
			}
		}

		findInterestingPoints(bestSetup);

	}

	private void findInterestingPoints(double[] bestSetup) {
		// TODO Auto-generated method stub

	}

	private double eval(double[] setup) {
		// TODO Auto-generated method stub
		return 0;
	}

	private List<double[]> findInterestingPoints() {
		// TODO Auto-generated method stub
		return null;
	}

	private void modifyParameters() {
		miningOptions.setConvergenceThreshold(0.1); // qualitativ untersuchen
		miningOptions.setNumberOfIterations(10); // qualitativ untersuchen

		miningOptions.setT1(0.0);
		miningOptions.setT2(0.0);
		queryOptions.minProbability = 0.12;
		miningOptions.setWeightClassContext(0.0);
		miningOptions.setWeightMethodContext(0.0);
		miningOptions.setWeightDefinition(0.0);
		miningOptions.setWeightParameterSites(0.0);
	}

	private boolean fancyCriterion() {
		// TODO Auto-generated method stub
		return false;
	}

}