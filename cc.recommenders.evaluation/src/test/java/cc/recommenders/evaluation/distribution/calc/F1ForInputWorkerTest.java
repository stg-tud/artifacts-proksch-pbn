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
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.evaluators.F1Evaluator;
import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.mining.calls.bmn.BMNMiner;
import cc.recommenders.testutils.LoggerUtils;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;

public class F1ForInputWorkerTest {

	private List<Usage> trainingData;
	private List<Usage> validationData;
	private F1ForInputTask task;
	private Miner<Usage, Query> miner;

	private F1ForInputWorker sut;
	private double[] expectedResult;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);

		trainingData = Lists.newLinkedList();
		validationData = Lists.newLinkedList();
		addUsages(9000, trainingData);
		addUsages(1000, validationData);

		task = new F1ForInputTask();
		task.numFolds = 10;
		task.inputSize = 2900;
		sut = new TestF1ForInputWorker();

		miner = mock(BMNMiner.class);
		sut.minerFactory = mock(MinerFactory.class);
		when(sut.minerFactory.get()).thenReturn(miner);
		sut.evaluator = mock(F1Evaluator.class);
		expectedResult = new double[0];
		when(sut.evaluator.getRawResults()).thenReturn(expectedResult);
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
	public void evaluatorIsReinitted() {
		sut.call2();
		verify(sut.evaluator).reinit();
	}

	@Test
	public void largeNumberOfQueriesOverAllFolds() {
		assertEquals(40000, F1ForInputWorker.NUM_OF_QUERIES_OVER_ALL_FOLDS);
	}

	@Test(expected = AssertionException.class)
	public void checkForTrainingDataSize() {
		trainingData.clear();
		addUsages(1, trainingData);
		task.inputSize = 2;
		sut.call2();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void correctNumberOfIterations() {
		sut.call2();
		verify(sut.evaluator, times(12)).query(any(ICallsRecommender.class), anyListOf(Usage.class));
		verify(sut.evaluator).getRawResults();
	}

	@Test
	public void resultsAreSetInTask() {
		sut.call2();
		assertSame(expectedResult, task.f1s);
	}

	@Test
	public void loggingManyIterations() {
		task.numFolds = 1;
		sut.call2();
		LoggerUtils.assertLogContains(0, "124 iterations");
		LoggerUtils.assertLogContains(1, "\n");
		for (int i = 2; i < 122; i++) {
			LoggerUtils.assertLogContains(i, ".");
		}
		LoggerUtils.assertLogContains(122, "\n");
		for (int i = 123; i <= 126; i++) {
			LoggerUtils.assertLogContains(i, ".");
		}
	}

	@Test
	public void loggingFewIterations() {
		task.inputSize = 8000;
		sut.call2();
		LoggerUtils.assertLogContains(0, "4 iterations");
		for (int i = 1; i <= 4; i++) {
			LoggerUtils.assertLogContains(i, String.format("- iteration %d/4", i));
		}
	}

	public class TestF1ForInputWorker extends F1ForInputWorker {

		private static final long serialVersionUID = -3107399215083418495L;

		public TestF1ForInputWorker() {
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