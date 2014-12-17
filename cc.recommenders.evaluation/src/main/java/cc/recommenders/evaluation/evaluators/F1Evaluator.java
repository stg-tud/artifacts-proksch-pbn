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

import cc.recommenders.evaluation.data.Averager;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

/**
 * calculates the f1 values for all generated queries, the results are averaged
 * for all queries from the same usage.
 */
public class F1Evaluator extends AbstractF1Evaluator implements Evaluator<Usage, Boxplot, Query> {

	private BoxplotData boxplotData;

	private Averager intermediateResults;

	@Inject
	public F1Evaluator(QueryBuilderFactory queryBuilderFactory, QueryOptions qOpts) {
		super(queryBuilderFactory, qOpts);
		reinit();
	}

	@Override
	public void reinit() {
		boxplotData = new BoxplotData();
	}

	@Override
	protected void startProcessingOfNewUsage(Usage usage) {
		intermediateResults = new Averager();
	}

	@Override
	protected void addIntermediateResult(Usage usage, Query query, double f1) {
		intermediateResults.add(f1);
	}

	@Override
	protected void storeResult() {
		boxplotData.add(intermediateResults.getAverage());
	}

	@Override
	public boolean hasResults() {
		return boxplotData.hasData();
	}

	@Override
	public Boxplot getResults() {
		return boxplotData.getBoxplot();
	}

	public double[] getRawResults() {
		return boxplotData.getRawValues();
	}
}