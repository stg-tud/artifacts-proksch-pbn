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
import java.util.Set;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.data.NM;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class NMF1Evaluator extends AbstractF1Evaluator implements Evaluator<Usage, Map<NM, Boxplot>, Query> {

	private Map<NM, BoxplotData> results;
	private Map<NM, BoxplotData> intermediateResults;

	private Set<NM> interestingValues = Sets.newLinkedHashSet();

	@Inject
	public NMF1Evaluator(QueryBuilderFactory queryBuilderFactory, QueryOptions qOpts) {
		super(queryBuilderFactory, qOpts);
		reinit();
	}

	@Override
	public void reinit() {
		results = Maps.newLinkedHashMap();
	}

	@Override
	protected void startProcessingOfNewUsage(Usage usage) {
		intermediateResults = Maps.newLinkedHashMap();
	}

	@Override
	protected void addIntermediateResult(Usage usage, Query query, double f1) {
		int numQueried = query.getReceiverCallsites().size();
		int numOriginal = usage.getReceiverCallsites().size();
		NM nm = new NM(numQueried, numOriginal);

		add(intermediateResults, nm, f1);
	}

	@Override
	protected void storeResult() {
		for (NM nm : intermediateResults.keySet()) {
			double avgF1 = intermediateResults.get(nm).getMean();
			add(results, nm, avgF1);
		}
	}

	@Override
	public boolean hasResults() {
		return !results.isEmpty();
	}

	@Override
	public Map<NM, Boxplot> getResults() {
		Map<NM, Boxplot> out = Maps.newLinkedHashMap();
		for (NM nm : results.keySet()) {
			Boxplot boxplot = results.get(nm).getBoxplot();
			out.put(nm, boxplot);
		}
		return out;
	}

	public Map<NM, double[]> getRawResults() {
		Map<NM, double[]> out = Maps.newLinkedHashMap();
		for (NM nm : results.keySet()) {
			double[] values = results.get(nm).getRawValues();
			out.put(nm, values);
		}
		return out;
	}

	private void add(Map<NM, BoxplotData> map, NM _key, double f1) {
		NM elseKey = _key.getNumQueried() == 0 ? NM.ELSE_0M : NM.ELSE_NM;
		NM key = interestingValues.contains(_key) ? _key : elseKey;
		BoxplotData data = map.get(key);
		if (data == null) {
			data = new BoxplotData();
			map.put(key, data);
		}
		data.add(f1);
	}

	public void setInterestingValues(Set<NM> interestingValues) {
		this.interestingValues = interestingValues;
	}
}