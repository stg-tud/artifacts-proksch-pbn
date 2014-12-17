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

import static cc.recommenders.testutils.LoggerUtils.assertLogContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.evaluation.io.TypeStore;
import cc.recommenders.evaluation.queries.QueryBuilder;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.io.Logger;
import cc.recommenders.mining.calls.Miner;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;
import cc.recommenders.utils.Timer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AbstractWorkerTest {

	private static final ITypeName TYPE = VmTypeName.get("LT");

	@Mock
	private Miner<Usage, Query> miner;
	@Mock
	private QueryBuilder<Usage, Query> queryBuilder;

	private Map<String, Usage> usageMocks;

	private TestWorker sut;

	private TestTask task;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);

		MockitoAnnotations.initMocks(this);
		task = TestTask.create();
		sut = new TestWorker(task);

		usageMocks = Maps.newLinkedHashMap();

		sut.mOpts = mock(MiningOptions.class, "#mopts#");
		sut.qOpts = mock(QueryOptions.class, "#qopts#");
		sut.taskDurationTimer = mock(Timer.class);
		sut.usageStore = mock(ProjectFoldedUsageStore.class);

		sut.queryBuilderFactory = new QueryBuilderFactory(null, null, null) {
			@Override
			public QueryBuilder<Usage, Query> get() {
				return queryBuilder;
			}
		};
		// TODO get rid of funny instantiation pattern
		sut.minerFactory = new MinerFactory(null, null, null) {
			@Override
			public Miner<Usage, Query> get() {
				return miner;
			}
		};
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	private void mockUsages(int numTraining, int numValidation) throws IOException {
		TypeStore typeStore = mock(TypeStore.class);
		when(typeStore.getTrainingData(anyInt())).thenReturn(listOf(numTraining));
		when(typeStore.getValidationData(anyInt())).thenReturn(listOf(numValidation));
		when(sut.usageStore.createTypeStore(any(ITypeName.class), anyInt())).thenReturn(typeStore);
	}

	private List<Usage> listOf(int num) {
		List<Usage> usages = Lists.newLinkedList();
		for (int i = 0; i < num; i++) {
			usages.add(mockUsage("u" + i));
		}
		return usages;
	}

	private Usage mockUsage(String name) {
		if (usageMocks.containsKey(name)) {
			return usageMocks.get(name);
		} else {
			Usage u = mock(Usage.class, name);
			usageMocks.put(name, u);
			return u;
		}
	}

	@Test
	public void timingIsStored() throws Exception {
		when(sut.taskDurationTimer.getDurationInSeconds()).thenReturn(123d);
		sut.call();
		assertEquals(123, task.processingTimeInS, 0.0001);

		verify(sut.taskDurationTimer).startNew();
		verify(sut.taskDurationTimer).stop();
		verify(sut.taskDurationTimer).getDurationInSeconds();
	}

	@Test
	public void callIsCascaded() throws Exception {
		sut.call();
		assertTrue(sut.hasCalledCall2);
	}

	@Test
	public void callOutput() throws Exception {
		when(sut.taskDurationTimer.getDurationInSeconds()).thenReturn(0.456);
		sut.call();
		assertLogContains(1, "task: TestTaskX");
		assertLogContains(2, "options: #mopts##qopts#");
		assertLogContains(3, "miner: class cc.recommenders.mining.calls.Miner$$EnhancerByMockitoWithCGLIB");
		assertLogContains(4,
				"queryBuilder: class cc.recommenders.evaluation.queries.QueryBuilder$$EnhancerByMockitoWithCGLIB");
		assertLogContains(5, "processing took 0.5s");
		assertLogContains(6, "");
	}

	@Test
	public void toStringIsOverridden() {
		String actual = sut.toString();
		String expected = "TestWorker: TestTaskX";
		assertEquals(expected, actual);
	}

	@Test
	public void getTrainingData() throws IOException {
		mockUsages(54, 6);
		List<Usage> actual = sut.getTrainingData();
		List<Usage> expected = listOf(54);
		assertEquals(expected, actual);
		assertLogContains(0, "number of usages:");
		assertLogContains(1, "training: 54");
		assertLogContains(2, "validation: 6");
	}

	@Test
	public void getTrainingDataIsLazy() throws IOException {
		mockUsages(1, 2);
		sut.getTrainingData();
		sut.getTrainingData();
		verify(sut.usageStore).createTypeStore(any(ITypeName.class), anyInt());
		assertEquals(3, Logger.getCapturedLog().size());
	}

	@Test
	public void getValidationData() throws IOException {
		mockUsages(54, 6);
		List<Usage> actual = sut.getValidationData();
		List<Usage> expected = listOf(6);
		assertEquals(expected, actual);
		assertLogContains(0, "number of usages:");
		assertLogContains(1, "training: 54");
		assertLogContains(2, "validation: 6");
	}

	@Test
	public void getValidationDataIsLazy() throws IOException {
		mockUsages(1, 2);
		sut.getValidationData();
		sut.getValidationData();
		verify(sut.usageStore).createTypeStore(any(ITypeName.class), anyInt());
		assertEquals(3, Logger.getCapturedLog().size());
	}

	@Test
	public void getValidationAndTrainingDataAreMutuallyLazy() throws IOException {
		mockUsages(13, 4);
		sut.getTrainingData();
		sut.getValidationData();
		sut.getTrainingData();
		sut.getValidationData();
		sut.getTrainingData();
		sut.getValidationData();
		verify(sut.usageStore).createTypeStore(any(ITypeName.class), anyInt());
		assertEquals(3, Logger.getCapturedLog().size());
	}

	@Test(expected = RuntimeException.class)
	public void ioExceptionsOnLoadDoNotVanish() throws IOException {
		when(sut.usageStore.createTypeStore(any(ITypeName.class), anyInt())).thenThrow(new IOException());
		sut.getTrainingData();
	}

	public static class TestTask extends AbstractTask {
		private static final long serialVersionUID = 1L;

		public static TestTask create() {
			TestTask t = new TestTask();
			t.app = "app";
			t.options = "options";
			t.currentFold = 3;
			t.numFolds = 13;
			t.typeName = TYPE.toString();
			return t;
		}

		@Override
		public String toString() {
			return "TestTaskX";
		}

		@Override
		protected boolean hasResult() {
			return false;
		}

		@Override
		protected String resultToString() {
			return null;
		}
	}

	public static class TestWorker extends AbstractWorker<TestTask> {

		private static final long serialVersionUID = 1L;

		private boolean hasCalledCall2;

		public TestWorker(TestTask task) {
			super(task);
		}

		@Override
		protected void call2() {
			hasCalledCall2 = true;
		}
	}
}