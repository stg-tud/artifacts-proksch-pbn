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
package cc.recommenders.evaluation;

import static cc.recommenders.collections.SublistSelector.pickRandomSublist;
import static cc.recommenders.evaluation.evaluators.Validations.nFoldCrossValidation;
import static cc.recommenders.evaluation.optimization.OptimizationOptions.newBuilder;
import static cc.recommenders.mining.calls.MiningOptions.newMiningOptions;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.evaluators.SizeAndF1Evaluator;
import cc.recommenders.evaluation.io.DecoratedObjectUsageStore;
import cc.recommenders.evaluation.optimization.EvaluationOptions;
import cc.recommenders.evaluation.optimization.OptimizationOptions;
import cc.recommenders.evaluation.optimization.raster.RasterSearch;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.pbn.PBNMiner;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class OverviewOfSizeLambdas {

	private DecoratedObjectUsageStore ouStore;
	private RasterSearch rasterSearch;
	private MiningOptions minOpts;
	private QueryOptions qOpts;
	private PBNMiner ouMiner;
	private SizeAndF1Evaluator evaluator;
	private Map<Double, Pair<Boxplot, Integer>> results = Maps.newLinkedHashMap();
	private Map<Double, String> lambdaOptions = Maps.newLinkedHashMap();

	@Inject
	public OverviewOfSizeLambdas(MiningOptions minOpts, QueryOptions qOpts, DecoratedObjectUsageStore ouStore,
			RasterSearch rasterSearch, SizeAndF1Evaluator evaluator, PBNMiner ouMiner) {
		this.minOpts = minOpts;
		this.qOpts = qOpts;
		this.ouStore = ouStore;
		this.rasterSearch = rasterSearch;
		this.evaluator = evaluator;
		this.ouMiner = ouMiner;
	}

	public void run() {

		ITypeName type = VmTypeName.get("Lorg/eclipse/swt/widgets/Button");
		List<Usage> usages = ouStore.read(type);
		List<Usage> sublist = pickRandomSublist(usages, 100);
		Logger.log("found %d usages for type %s...\n", usages.size(), type);

		for (int i = 0; i <= 10; i++) {
			// int i = 9;

			resetEvaluatorAndOptions();

			double lambda = 0.1 * i;
			// minOpts.setLambda(lambda);

			if (usages.size() >= 3) {
				findAndSetOptimalOptions(sublist);
				nFoldCrossValidation(3, ouMiner, evaluator, usages);
			}

			results.put(lambda, evaluator.getResults());
			lambdaOptions.put(lambda, String.format("%s%s", minOpts, qOpts));
		}

		visualizeResults();
	}

	private void resetEvaluatorAndOptions() {
		evaluator.reinit();
		minOpts.setFrom(new MiningOptions());
		qOpts.setFrom(new QueryOptions());
	}

	private void findAndSetOptimalOptions(List<Usage> usages) {

		OptimizationOptions options = newBuilder().maxIterations(10).convergence(0.001).allSteps(0.1)
				.stepMinProbability(0.05).build();

		EvaluationOptions startOpts = new EvaluationOptions(
				newMiningOptions("CANOPY[0.45; 0.3]+W[0.3; 0.3; 0.3; 0.3]+L0.00"), new QueryOptions());
		EvaluationOptions optimum = rasterSearch.findOptimalOptions(usages, options, Sets.newHashSet(startOpts));

		minOpts.setFrom(optimum.miningOptions);
		qOpts.setFrom(optimum.queryOptions);
	}

	private void visualizeResults() {
		for (Double lambda : results.keySet()) {
			System.out.println("## Lambda: " + lambda);
			System.out.println("best options: " + lambdaOptions.get(lambda));
			Pair<Boxplot, Integer> pair = results.get(lambda);
			System.out.println("avg. f1: " + pair.getLeft());
			System.out.println("avg. size: " + pair.getRight());
			System.out.println();
		}
	}
}