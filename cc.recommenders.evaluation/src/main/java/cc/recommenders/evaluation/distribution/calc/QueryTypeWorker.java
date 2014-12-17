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

import java.util.Set;

import cc.recommenders.evaluation.data.NM;
import cc.recommenders.evaluation.evaluators.NMF1Evaluator;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.usages.Query;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class QueryTypeWorker extends AbstractWorker<QueryTypeTask> {

	private static final long serialVersionUID = 4240150916774600955L;

	@Inject
	public transient NMF1Evaluator evaluator;

	private final QueryTypeTask task;

	public QueryTypeWorker(QueryTypeTask task) {
		super(task);
		this.task = task;
	}

	@Override
	protected void call2() {
		evaluator.reinit();
		evaluator.setInterestingValues(getInterestingValues());

		ICallsRecommender<Query> rec = minerFactory.get().createRecommender(getTrainingData());
		evaluator.query(rec, getValidationData());

		task.results = evaluator.getRawResults();
	}

	public Set<NM> getInterestingValues() {
		Set<NM> nms = Sets.newLinkedHashSet();
		nms.add(new NM(0, 1));
		nms.add(new NM(0, 2));
		nms.add(new NM(0, 3));
		nms.add(new NM(0, 4));
		nms.add(new NM(0, 5));
		nms.add(new NM(0, 6));
		nms.add(new NM(1, 2));
		nms.add(new NM(1, 3));
		nms.add(new NM(2, 4));
		nms.add(new NM(2, 5));
		nms.add(new NM(3, 6));
		return nms;
	}
}