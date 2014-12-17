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

import cc.recommenders.evaluation.data.Averager;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

public class SizeEvaluator implements Evaluator<Usage, Integer, Query> {

	private Averager averager = new Averager();

	@Override
	public void reinit() {
		averager.reinit();
	}

	@Override
	public void query(ICallsRecommender<Query> rec, List<Usage> validationData) {
		averager.add(rec.getSize());
	}

	@Override
	public boolean hasResults() {
		return averager.hasValues();
	}

	@Override
	public Integer getResults() {
		return averager.getIntAverage();
	}
}