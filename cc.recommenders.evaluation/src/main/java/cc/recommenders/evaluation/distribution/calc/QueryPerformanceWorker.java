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

import static cc.recommenders.collections.SublistSelector.pickRandomSublist;

import java.util.List;

import cc.recommenders.collections.SublistSelector;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;
import cc.recommenders.utils.Timer;

import com.google.common.collect.Lists;
import com.google.inject.Inject;

public class QueryPerformanceWorker extends AbstractWorker<QueryPerformanceTask> {

	private static final long serialVersionUID = 667108711001557184L;

	public static final int NUMBER_OF_QUERIES_PER_FOLD = 300;

	@Inject
	public Timer performanceTimer;
	private QueryPerformanceTask task;

	public QueryPerformanceWorker(QueryPerformanceTask task) {
		super(task);
		this.task = task;
	}

	@Override
	protected void call2() {

		Logger.log("working on %s", task);

		ICallsRecommender<Query> rec = createRecommender();
		task.modelSize = rec.getSize();
		task.learningDurationInS = performanceTimer.getDurationInSeconds();

		List<Query> queries = stressRecommender(rec);
		final double cumulatedQueryDurationInMS = performanceTimer.getDurationInMilliSeconds();
		task.perQueryDurationInMS = cumulatedQueryDurationInMS / (queries.size() * 1.0);
	}

	private ICallsRecommender<Query> createRecommender() {
		Miner<Usage, Query> miner = minerFactory.get();
		List<Usage> trainingSublist = SublistSelector.pickRandomSublist(getTrainingData(), task.inputSize);
		Logger.log("learning... (from %d usages)", trainingSublist.size());

		performanceTimer.startNew();
		ICallsRecommender<Query> rec = miner.createRecommender(trainingSublist);
		performanceTimer.stop();

		return rec;
	}

	private List<Query> stressRecommender(ICallsRecommender<Query> rec) {
		List<Query> queries = createQueries();
		Logger.log("querying... (with %d querys)", queries.size());

		performanceTimer.startNew();
		for (Query q : queries) {
			rec.query(q);
		}
		performanceTimer.stop();

		return queries;
	}

	private List<Query> createQueries() {
		List<Query> tmpQueries = Lists.newLinkedList();
		for (Usage u : getValidationData()) {
			tmpQueries.addAll(queryBuilderFactory.get().createQueries(u));
		}
		List<Query> queries = pickRandomSublist(tmpQueries, NUMBER_OF_QUERIES_PER_FOLD);
		return queries;
	}
}