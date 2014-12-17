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

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import cc.recommenders.evaluation.queries.QueryBuilder;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.testutils.LoggerUtils;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;
import cc.recommenders.utils.Timer;

import com.google.common.collect.Lists;

public class QueryPerformanceWorkerTest {

	private List<Usage> trainingData;
	private List<Usage> validationData;
	private QueryPerformanceTask task;

	private TestQueryPerformanceWorker sut;
	@Mock
	private Miner<Usage, Query> miner;
	@Mock
	private ICallsRecommender<Query> recommender;
	@Mock
	private QueryBuilder<Usage, Query> queryBuilder;

	@Before
	public void setup() {
		initMocks(this);

		Logger.reset();
		Logger.setCapturing(true);
		trainingData = Lists.newLinkedList();
		validationData = Lists.newLinkedList();
		addUsages(180, trainingData);
		addUsages(20, validationData);
		task = getBasicTask();
		sut = new TestQueryPerformanceWorker(task);

		sut.minerFactory = mock(MinerFactory.class);
		sut.queryBuilderFactory = mock(QueryBuilderFactory.class);
		sut.performanceTimer = mock(Timer.class);

		when(sut.queryBuilderFactory.get()).thenReturn(queryBuilder);
		List<Query> queries = Lists.newArrayList(mock(Query.class), mock(Query.class));
		when(queryBuilder.createQueries(any(Usage.class))).thenReturn(queries);

		when(sut.minerFactory.get()).thenReturn(miner);
		when(miner.createRecommender(anyListOf(Usage.class))).thenReturn(recommender);
		sut.taskDurationTimer = mock(Timer.class);
	}

	private QueryPerformanceTask getBasicTask() {
		QueryPerformanceTask task = new QueryPerformanceTask();
		task.app = "APP";
		task.inputSize = 123;
		task.options = "OPTIONS";
		task.typeName = "LType";
		task.currentFold = 4;
		task.numFolds = 13;
		return task;
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	private List<Usage> addUsages(int num, List<Usage> usages) {
		for (int i = 0; i < num; i++) {
			usages.add(mock(Usage.class, "usage-" + usages.size()));
		}
		return usages;
	}

	@Test
	public void correctLogging() {
		sut.call2();
		LoggerUtils.assertLogContains(0, "working on QueryPerformanceTask: APP - LType (fold 5/13) - input size: 123");
		LoggerUtils.assertLogContains(1, "learning... (from 123 usages)");
		LoggerUtils.assertLogContains(2, "querying... (with 40 querys)");
	}

	@Test
	public void numberOfQueriesIsLimited() {
		validationData.clear();
		addUsages(1001, validationData);
		sut.call2();
		LoggerUtils.assertLogContains(2, "querying... (with 300 querys)");
	}

	@Test
	public void correctResults() {
		when(recommender.getSize()).thenReturn(2000);
		when(sut.performanceTimer.getDurationInSeconds()).thenReturn(2d);
		when(sut.performanceTimer.getDurationInMilliSeconds()).thenReturn(200d);
		sut.call2();
		assertEquals(2000, task.modelSize);
		assertEquals(2, task.learningDurationInS, 0.0001);
		// 40 queries
		assertEquals(200 / 40, task.perQueryDurationInMS, 0.0001);
	}

	@Test
	public void timerIsCalledInCorrectOrder() {
		sut.call2();
		InOrder order = inOrder(sut.performanceTimer);
		order.verify(sut.performanceTimer).startNew();
		order.verify(sut.performanceTimer).stop();
		order.verify(sut.performanceTimer).getDurationInSeconds();
		order.verify(sut.performanceTimer).startNew();
		order.verify(sut.performanceTimer).stop();
		order.verify(sut.performanceTimer).getDurationInMilliSeconds();
	}

	private class TestQueryPerformanceWorker extends QueryPerformanceWorker {

		private static final long serialVersionUID = -1812333637791718512L;

		public TestQueryPerformanceWorker(QueryPerformanceTask task) {
			super(task);
		}

		@Override
		protected List<Usage> getTrainingData() {
			return trainingData;
		}

		@Override
		protected List<Usage> getValidationData() {
			return validationData;
		}
	}
}