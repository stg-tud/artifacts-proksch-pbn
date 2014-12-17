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

import org.apache.commons.lang3.tuple.Pair;

import cc.recommenders.evaluation.evaluators.SizeAndF1Evaluator;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.usages.Query;

import com.google.inject.Inject;

public class F1AndSizeWorker extends AbstractWorker<F1AndSizeTask> {

	private static final long serialVersionUID = 8022876258830189110L;

	@Inject
	public transient SizeAndF1Evaluator evaluator;

	private F1AndSizeTask task;

	public F1AndSizeWorker(F1AndSizeTask task) {
		super(task);
		this.task = task;
	}

	@Override
	public void call2() {
		evaluator.reinit();

		ICallsRecommender<Query> rec = minerFactory.get().createRecommender(getTrainingData());
		evaluator.query(rec, getValidationData());

		Pair<double[], Integer> res = evaluator.getRawResults();
		task.f1s = res.getLeft();
		task.sizeInB = res.getRight();
	}
}