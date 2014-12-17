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

import static cc.recommenders.evaluation.evaluators.Validations.nFoldCrossValidation;
import static cc.recommenders.evaluation.optimization.OptimizationOptions.newBuilder;

import java.util.List;

import cc.recommenders.evaluation.evaluators.F1Evaluator;
import cc.recommenders.evaluation.io.DecoratedObjectUsageStore;
import cc.recommenders.evaluation.optimization.EvaluationOptions;
import cc.recommenders.evaluation.optimization.OptimizationOptions;
import cc.recommenders.evaluation.optimization.raster.RasterSearch;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.pbn.PBNMiner;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class AllTypesOptimized {

	private DecoratedObjectUsageStore ouStore;
	private RasterSearch rasterSearch;
	private MiningOptions minOpts;
	private QueryOptions qOpts;
	private PBNMiner ouMiner;
	private F1Evaluator evaluator;

	@Inject
	public AllTypesOptimized(MiningOptions minOpts, QueryOptions qOpts, DecoratedObjectUsageStore ouStore,
			RasterSearch rasterSearch, F1Evaluator evaluator, PBNMiner ouMiner) {
		this.minOpts = minOpts;
		this.qOpts = qOpts;
		this.ouStore = ouStore;
		this.rasterSearch = rasterSearch;
		this.evaluator = evaluator;
		this.ouMiner = ouMiner;
	}

	public void run() {
		evaluator.reinit();

		for (ITypeName type : ouStore.getKeys()) {
			if (isInteresting(type)) {
				List<Usage> usages = ouStore.read(type);
				if (usages.size() >= 3) {
					findAndSetOptimalOptions(usages);
					nFoldCrossValidation(3, ouMiner, evaluator, usages);
				}
			}
		}

		System.out.println(evaluator.getResults().toString());
	}

	private void findAndSetOptimalOptions(List<Usage> usages) {
		OptimizationOptions options = newBuilder().convergence(0.01).allSteps(0.5).build();
		EvaluationOptions optimum = rasterSearch.findOptimalOptions(usages, options);

		minOpts.setFrom(optimum.miningOptions);
		qOpts.setFrom(optimum.queryOptions);
	}

	private boolean isInteresting(ITypeName type) {
		return type.toString().startsWith("Lorg/eclipse/swt/widgets/Button");
	}
}