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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.data.NM;
import cc.recommenders.evaluation.evaluators.NMF1Evaluator;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.mining.calls.bmn.BMNMiner;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class QueryTyeWorkerTest {

	private List<Usage> trainingData;
	private List<Usage> validationData;
	private QueryTypeTask task;
	private Miner<Usage, Query> miner;

	private QueryTypeWorker sut;
	private Map<NM, double[]> expectedResult;

	@Before
	public void setup() {

		trainingData = Lists.newLinkedList();
		validationData = Lists.newLinkedList();

		task = new QueryTypeTask();
		sut = new TestQueryTypeWorker();

		miner = mock(BMNMiner.class);
		sut.minerFactory = mock(MinerFactory.class);
		when(sut.minerFactory.get()).thenReturn(miner);
		sut.evaluator = mock(NMF1Evaluator.class);
		expectedResult = Maps.newHashMap();
		when(sut.evaluator.getRawResults()).thenReturn(expectedResult);
	}

	@Test
	public void evaluatorIsReinitted() {
		sut.call2();
		verify(sut.evaluator).reinit();
	}

	@Test
	public void correctResult() {
		sut.call2();
		assertSame(expectedResult, task.results);
	}

	@Test
	public void usesCorrectNmValues() {
		Set<NM> actuals = sut.getInterestingValues();
		Set<NM> expecteds = Sets.newHashSet(new NM(0, 1), new NM(0, 2), new NM(0, 3), new NM(0, 4), new NM(0, 5),
				new NM(0, 6), new NM(1, 2), new NM(1, 3), new NM(2, 4), new NM(2, 5), new NM(3, 6));
		assertEquals(expecteds, actuals);
	}

	public class TestQueryTypeWorker extends QueryTypeWorker {

		private static final long serialVersionUID = -3107399215083418495L;

		public TestQueryTypeWorker() {
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