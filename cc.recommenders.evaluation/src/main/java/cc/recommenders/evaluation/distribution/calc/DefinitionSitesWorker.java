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

import cc.recommenders.evaluation.evaluators.DefF1Evaluator;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.usages.Query;

import com.google.inject.Inject;

public class DefinitionSitesWorker extends AbstractWorker<DefinitionSitesTask> {

	private static final long serialVersionUID = 6906767241336001470L;

	@Inject
	public transient DefF1Evaluator evaluator;

	private DefinitionSitesTask task;

	public DefinitionSitesWorker(DefinitionSitesTask task) {
		super(task);
		this.task = task;
	}

	@Override
	protected void call2() {
		evaluator.reinit();

		ICallsRecommender<Query> rec = minerFactory.get().createRecommender(getTrainingData());
		evaluator.query(rec, getValidationData());

		task.results = evaluator.getRawResults();
	}
}