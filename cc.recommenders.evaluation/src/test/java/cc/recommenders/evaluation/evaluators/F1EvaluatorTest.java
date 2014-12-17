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

import static cc.recommenders.datastructures.Tuple.newTuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.OngoingStubbing;

import cc.recommenders.datastructures.Tuple;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.queries.PartialUsageQueryBuilder;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class F1EvaluatorTest {

	private IMethodName callQueried1;
	private IMethodName callQueried2;
	private IMethodName callProposal1;
	private IMethodName callProposal2;
	private IMethodName callProposal3;

	private PartialUsageQueryBuilder queryBuilder;
	private ICallsRecommender<Query> recommender;
	private OngoingStubbing<Set<Tuple<IMethodName, Double>>> queryStub;
	private List<Usage> usages;

	private QueryOptions queryOptions;
	private F1Evaluator sut;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() {
		callQueried1 = VmMethodName.get("Lmy/Type.mQ1()V");
		callQueried2 = VmMethodName.get("Lmy/Type.mQ2()V");
		callProposal1 = VmMethodName.get("Lmy/Type.mP1()V");
		callProposal2 = VmMethodName.get("Lmy/Type.mP2()V");
		callProposal3 = VmMethodName.get("Lmy/Type.mP3()V");

		mockQueryBuilder();

		queryOptions = new QueryOptions();

		QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory(queryOptions, null, queryBuilder);
		sut = new F1Evaluator(queryBuilderFactory, queryOptions);

		recommender = mock(ICallsRecommender.class);

		usages = Lists.newLinkedList();

	}

	private void mockQueryBuilder() {
		queryBuilder = mock(PartialUsageQueryBuilder.class);
		List<Query> queries = Lists.newLinkedList();
		queries.add(createQuery(callQueried1, callQueried2));
		when(queryBuilder.createQueries(any(Usage.class))).thenReturn(queries);
	}

	@Test
	public void allProposalsHavePerfectPrecisionButCallsAreMissing() {

		addUsages(100, callQueried1, callQueried2, callProposal1, callProposal2);
		addProposals(100, callProposal1); // p:1, r:0.5, f1:0.666

		sut.query(recommender, usages);

		Boxplot actual = sut.getResults();
		Boxplot expected = new Boxplot(100, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0);
		assertEquals(expected, actual);
	}

	@Test
	public void allProposalsHavePerfectRecallButUnnecessaryCallsAreProposed() {

		addUsages(100, callQueried1, callQueried2, callProposal1);
		addProposals(100, callProposal2, callProposal1); // p:0.5, r:1, f1:0.666

		sut.query(recommender, usages);

		Boxplot actual = sut.getResults();
		Boxplot expected = new Boxplot(100, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0, 2.0 / 3.0);
		assertEquals(expected, actual);
	}

	@Test
	public void mixedProposals() {

		addUsages(10, callQueried1, callQueried2, callProposal1, callProposal2);
		addProposals(1, callProposal3); // p:0, r:0, f1:0.0
		addProposals(4, callProposal1); // p:1, r:0.5, f1:0.666
		addProposals(4, callProposal1, callProposal3); // p:0.5, r:0.5, f1:0.5
		addProposals(1, callProposal1, callProposal2); // p:1, r:1, f1:1.0

		sut.query(recommender, usages);

		Boxplot actual = sut.getResults();
		// median is in between two numbers
		double expectedMedian = (0.5 + 2.0 / 3.0) / 2.0;
		Boxplot expected = new Boxplot(10, 0.56666667, 0.0, 0.5, expectedMedian, 2.0 / 3.0, 1.0);
		assertEquals(expected, actual);
	}

	@Test
	public void initiallyThereAreNoResults() {
		assertFalse(sut.hasResults());
	}

	@Test
	public void thereAreResultsAfterTestrun() {
		mixedProposals();
		assertTrue(sut.hasResults());
	}

	@Test
	public void resultsAreAveragedIfMoreThanOneQueryIsCreatedPerUsage() {

		addUsages(1, callQueried1, callQueried2, callProposal1, callProposal2);
		addProposals(2, callProposal1, callProposal3);

		List<Query> queries = Lists.newLinkedList();
		queries.add(createQuery(callQueried1, callQueried2)); // p:0.5, r:0.5,
																// f1:0.5
		queries.add(createQuery(callQueried1)); // p:0.5, r:0.333, f1:0.4
		when(queryBuilder.createQueries(any(Usage.class))).thenReturn(queries);

		sut.query(recommender, usages);
		Boxplot actual = sut.getResults();

		double x = (0.5 + 0.4) / 2.0;
		Boxplot expected = new Boxplot(1, x, x, x, x, x, x);
		assertEquals(expected, actual);
	}

	private void addUsages(int numUsages, IMethodName... calls) {
		for (int i = 0; i < numUsages; i++) {
			Query q = createQuery(calls);

			usages.add(q);
		}
	}

	private static Query createQuery(IMethodName... calls) {
		Query q = new Query();

		for (IMethodName call : calls) {
			CallSite site = CallSites.createReceiverCallSite(call);
			q.addCallSite(site);
		}
		return q;
	}

	private void addProposals(int numCalls, IMethodName... calls) {

		if (queryStub == null) {
			queryStub = when(recommender.query(any(Query.class)));
		}

		Set<Tuple<IMethodName, Double>> proposals = Sets.newLinkedHashSet();

		for (IMethodName call : calls) {
			Tuple<IMethodName, Double> tuple = newTuple(call, 1.0);
			proposals.add(tuple);
		}

		for (int i = 0; i < numCalls; i++) {
			queryStub = queryStub.thenReturn(proposals);
		}
	}
}