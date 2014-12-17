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

import static cc.recommenders.evaluation.data.Measure.newMeasure;
import static com.google.common.collect.Sets.newLinkedHashSet;

import java.util.List;
import java.util.Set;

import cc.recommenders.datastructures.Tuple;
import cc.recommenders.evaluation.data.Measure;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.names.IMethodName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

public abstract class AbstractF1Evaluator {

	private final QueryBuilderFactory queryBuilder;
	private QueryOptions qOpts;

	public AbstractF1Evaluator(QueryBuilderFactory queryBuilder, QueryOptions qOpts) {
		this.queryBuilder = queryBuilder;
		this.qOpts = qOpts;
	}

	public void query(ICallsRecommender<Query> rec, List<Usage> validationData) {
		for (Usage usage : validationData) {

			startProcessingOfNewUsage(usage);

			for (Query query : queryBuilder.get().createQueries(usage)) {

				Set<IMethodName> expected = getExpected(usage, query);
				Set<IMethodName> proposed = getProposed(rec, query);

				if (qOpts.isIgnoringAfterFullRecall) {
					proposed = Measure.dropAfterTotalRecall(expected, proposed);
				}

				double f1 = newMeasure(expected, proposed).getF1();

				addIntermediateResult(usage, query, f1);
			}

			storeResult();
		}
	}

	private Set<IMethodName> getExpected(Usage usage, Query query) {
		Set<IMethodName> expected = newLinkedHashSet();

		for (CallSite site : usage.getReceiverCallsites()) {
			boolean isQueried = query.getReceiverCallsites().contains(site);
			if (!isQueried) {
				expected.add(site.getMethod());
			}
		}

		return expected;
	}

	private Set<IMethodName> getProposed(ICallsRecommender<Query> rec, Query query) {
		Set<Tuple<IMethodName, Double>> proposals = rec.query(query);
		Set<IMethodName> ms = newLinkedHashSet();
		for (Tuple<IMethodName, ?> t : proposals) {
			ms.add(t.getFirst());
		}
		return ms;
	}

	protected abstract void startProcessingOfNewUsage(Usage usage);

	protected abstract void addIntermediateResult(Usage usage, Query query, double f1);

	protected abstract void storeResult();
}