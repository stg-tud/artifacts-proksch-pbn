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

import java.util.Map;

import cc.recommenders.evaluation.data.Averager;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.usages.DefinitionSiteKind;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class DefF1Evaluator extends AbstractF1Evaluator implements
		Evaluator<Usage, Map<DefinitionSiteKind, Boxplot>, Query> {

	private Map<DefinitionSiteKind, BoxplotData> results;

	private DefinitionSiteKind lastType;
	private Averager intermediateResults;

	@Inject
	public DefF1Evaluator(QueryBuilderFactory queryBuilderFactory, QueryOptions qOpts) {
		super(queryBuilderFactory, qOpts);
	}

	@Override
	public void reinit() {
		intermediateResults = new Averager();
		results = Maps.newHashMap();
	}

	@Override
	protected void startProcessingOfNewUsage(Usage usage) {
		lastType = usage.getDefinitionSite().getKind();
	}

	@Override
	protected void addIntermediateResult(Usage usage, Query query, double f1) {
		intermediateResults.add(f1);
	}

	@Override
	protected void storeResult() {
		double avgF1 = intermediateResults.getAverage();

		BoxplotData data = results.get(lastType);
		if (data == null) {
			data = new BoxplotData();
			results.put(lastType, data);
		}

		data.add(avgF1);
	}

	@Override
	public boolean hasResults() {
		return !results.isEmpty();
	}

	@Override
	public Map<DefinitionSiteKind, Boxplot> getResults() {

		Map<DefinitionSiteKind, Boxplot> out = Maps.newLinkedHashMap();

		for (DefinitionSiteKind type : results.keySet()) {
			Boxplot boxplot = results.get(type).getBoxplot();
			out.put(type, boxplot);
		}

		return out;
	}

	public Map<DefinitionSiteKind, double[]> getRawResults() {

		Map<DefinitionSiteKind, double[]> out = Maps.newLinkedHashMap();

		for (DefinitionSiteKind type : results.keySet()) {
			BoxplotData bpd = results.get(type);
			out.put(type, bpd.getRawValues());
		}

		return out;
	}
}