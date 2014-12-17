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

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.evaluators.DefF1Evaluator;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.mining.calls.bmn.BMNMiner;
import cc.recommenders.usages.DefinitionSiteKind;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DefinitionSitesWorkerTest {

	private List<Usage> trainingData;
	private List<Usage> validationData;
	private DefinitionSitesTask task;
	private Miner<Usage, Query> miner;

	private DefinitionSitesWorker sut;
	private Map<DefinitionSiteKind, double[]> expectedResult;

	@Before
	public void setup() {

		trainingData = Lists.newLinkedList();
		validationData = Lists.newLinkedList();

		task = new DefinitionSitesTask();
		sut = new TestDefinitionSitesWorker();

		miner = mock(BMNMiner.class);
		sut.minerFactory = mock(MinerFactory.class);
		when(sut.minerFactory.get()).thenReturn(miner);
		sut.evaluator = mock(DefF1Evaluator.class);
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

	public class TestDefinitionSitesWorker extends DefinitionSitesWorker {

		private static final long serialVersionUID = -3107399215083418495L;

		public TestDefinitionSitesWorker() {
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