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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.recommenders.datastructures.Tuple;
import cc.recommenders.evaluation.data.NM;
import cc.recommenders.evaluation.queries.PartialUsageQueryBuilder;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class NMF1EvaluatorTest {

	@Mock
	private ICallsRecommender<Query> rec;
	@Mock
	private PartialUsageQueryBuilder queryBuilder;

	private QueryOptions qOpts;
	private List<Usage> usages;

	private NMF1Evaluator sut;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		usages = Lists.newLinkedList();
		qOpts = new QueryOptions();

		QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory(qOpts, null, queryBuilder);
		sut = new NMF1Evaluator(queryBuilderFactory, qOpts);
	}

	@Test
	public void noResultsWithoutData() {
		assertFalse(sut.hasResults());
	}

	@Test
	public void resultsWithData() {
		Usage u = addUsage(1, 2);
		Query q = addQuery(u, 1);
		addProposals(q, 3, 2);

		sut.query(rec, usages);

		assertTrue(sut.hasResults());
	}

	@Test
	public void resultsAreCategorizedIfInteresting() {
		qOpts.isIgnoringAfterFullRecall = true;
		Usage u = addUsage(1, 2);
		Query q = addQuery(u, 1);
		addProposals(q, 3, 2);

		sut.setInterestingValues(Sets.newHashSet(new NM(1, 2)));
		sut.query(rec, usages);

		Map<NM, double[]> results = sut.getRawResults();
		assertEquals(Sets.newHashSet(new NM(1, 2)), results.keySet());
	}

	@Test
	public void resultsAreNotCategorizedIfNotInteresting_nm() {
		qOpts.isIgnoringAfterFullRecall = true;
		Usage u = addUsage(1, 2);
		Query q = addQuery(u, 1);
		addProposals(q, 3, 2);

		sut.query(rec, usages);

		Map<NM, double[]> results = sut.getRawResults();
		assertEquals(Sets.newHashSet(NM.ELSE_NM), results.keySet());
	}

	@Test
	public void resultsAreNotCategorizedIfNotInteresting_zero() {
		qOpts.isIgnoringAfterFullRecall = true;
		Usage u = addUsage(1, 2);
		Query q = addQuery(u);
		addProposals(q, 3, 2);

		sut.query(rec, usages);

		Map<NM, double[]> results = sut.getRawResults();
		assertEquals(Sets.newHashSet(NM.ELSE_0M), results.keySet());
	}

	private Usage addUsage(int... calls) {
		Usage u = u(calls);
		usages.add(u);
		return u;
	}

	private Query addQuery(Usage u, int... calls) {
		Query q = u(calls);
		when(queryBuilder.createQueries(eq(u))).thenReturn(Lists.newArrayList(q));
		return q;
	}

	private void addProposals(Query q, int... calls) {
		Set<Tuple<IMethodName, Double>> proposals = Sets.newLinkedHashSet();
		for (int i = 0; i < calls.length; i++) {

			Tuple<IMethodName, Double> prop = Tuple.newTuple(m(calls[i]), 0.123);
			proposals.add(prop);
		}
		when(rec.query(eq(q))).thenReturn(proposals);
	}

	public Query u(int... receiver) {
		Query u = mock(Query.class);
		Set<CallSite> sites = Sets.newLinkedHashSet();
		for (int i = 0; i < receiver.length; i++) {
			int num = receiver[i];
			CallSite site = mock(CallSite.class);
			when(site.getMethod()).thenReturn(m(num));
			sites.add(site);
		}
		when(u.getReceiverCallsites()).thenReturn(sites);
		return u;
	}

	private VmMethodName m(int num) {
		return VmMethodName.get("LType.m" + num + "()V");
	}
}