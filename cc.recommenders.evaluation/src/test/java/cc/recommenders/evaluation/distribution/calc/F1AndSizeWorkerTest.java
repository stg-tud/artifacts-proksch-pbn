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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.evaluators.SizeAndF1Evaluator;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.MiningOptions.Algorithm;
import cc.recommenders.mining.calls.bmn.BMNMiner;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;

public class F1AndSizeWorkerTest {
	private List<Usage> trainingData;
	private List<Usage> validationData;
	private F1AndSizeTask task;
	private F1AndSizeWorker sut;
	private BMNMiner miner;

	@Before
	public void setup() {
		MiningOptions mOpts = new MiningOptions();
		mOpts.setAlgorithm(Algorithm.BMN);

		trainingData = Lists.newLinkedList();
		validationData = Lists.newLinkedList();
		task = new F1AndSizeTask();
		sut = new TestF1AndSizeWorker();

		miner = mock(BMNMiner.class);
		sut.minerFactory = mock(MinerFactory.class);
		when(sut.minerFactory.get()).thenReturn(miner);
		sut.evaluator = mock(SizeAndF1Evaluator.class);
		setResult(1, 0.0);
	}

	@Test
	public void evaluatorIsReinitted() {
		sut.call2();
		verify(sut.evaluator).reinit();
	}

	@Test
	public void correctValuesAreStored() {
		setResult(17254, 0.0, 0.1, 0.2, 0.3, 1234.5678);
		sut.call2();
		assertEquals(17254, task.sizeInB);
		assertArrayEquals(new double[] { 0.0, 0.1, 0.2, 0.3, 1234.5678 }, task.f1s, 0.00001);
	}

	private void setResult(int sizeInB, double... values) {
		when(sut.evaluator.getRawResults()).thenReturn(Pair.of(values, sizeInB));
	}

	public class TestF1AndSizeWorker extends F1AndSizeWorker {

		private static final long serialVersionUID = 7832989415294374931L;

		public TestF1AndSizeWorker() {
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