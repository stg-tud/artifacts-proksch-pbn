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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.evaluation.io.TypeStore;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AbstractTaskProviderTest {

	private static final ITypeName TYPE1 = VmTypeName.get("LT1");
	private static final ITypeName TYPE2 = VmTypeName.get("LT2");
	private static final ITypeName TYPE3 = VmTypeName.get("LT3");

	private ProjectFoldedUsageStore store;
	private OutputUtils output;
	private TestProvider sut;
	private Map<String, String> options;

	@Before
	public void setup() throws IOException {
		Logger.reset();
		Logger.setCapturing(true);
		store = mock(ProjectFoldedUsageStore.class);
		Set<ITypeName> types = Sets.newLinkedHashSet();
		types.add(TYPE1);
		types.add(TYPE2);
		types.add(TYPE3);
		when(store.getTypes()).thenReturn(types);
		mockStore(TYPE1, true, 1, 2);
		mockStore(TYPE2, true, 3, 4);
		mockStore(TYPE3, false);
		output = mock(OutputUtils.class);

		options = Maps.newLinkedHashMap();
		options.put("A", "AAAA");
		options.put("B", "BBBB");

		sut = new TestProvider();
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	private void mockStore(ITypeName type, boolean isAvailable, int... sizes) throws IOException {

		TypeStore typeStore = mock(TypeStore.class);
		for (int i = 0; i < sizes.length; i++) {
			when(typeStore.getTrainingData(eq(i))).thenReturn(listOf(sizes[i] * 9));
			when(typeStore.getValidationData(eq(i))).thenReturn(listOf(sizes[i]));
		}

		when(store.isAvailable(eq(type), anyInt())).thenReturn(isAvailable);
		when(store.createTypeStore(eq(type), anyInt())).thenReturn(typeStore);
	}

	private List<Usage> listOf(int num) {
		List<Usage> usages = Lists.newLinkedList();
		for (int i = 0; i < num; i++) {
			usages.add(mock(Usage.class));
		}
		return usages;
	}

	@Test
	public void taskCreation() {
		Set<TestTask> actuals = sut.createTasks();

		Set<TestTask> expecteds = Sets.newLinkedHashSet();
		for (ITypeName type : new ITypeName[] { TYPE1, TYPE2 }) {
			for (String app : options.keySet()) {
				for (int foldNum : new int[] { 0, 1 }) {
					TestTask task = new TestTask();
					task.app = app;
					task.options = options.get(app);
					task.typeName = type.toString();
					task.currentFold = foldNum;
					task.numFolds = 2;
					expecteds.add(task);
				}
			}
		}

		assertEquals(expecteds, actuals);
	}

	@Test
	public void taskCreationTypesCanBeDisabled() {
		sut.useType = false;
		Set<TestTask> actuals = sut.createTasks();

		Set<TestTask> expecteds = Sets.newLinkedHashSet();
		for (String app : options.keySet()) {
			for (int foldNum : new int[] { 0, 1 }) {
				TestTask task = new TestTask();
				task.app = app;
				task.options = options.get(app);
				task.typeName = TYPE1.toString();
				task.currentFold = foldNum;
				task.numFolds = 2;
				expecteds.add(task);
			}
		}
		assertEquals(expecteds, actuals);
	}

	@Test
	public void allCreatedWorkersAreReturned() {
		Collection<Callable<TestTask>> actuals = sut.createWorkers();
		assertFalse(actuals.isEmpty());
		assertEquals(sut.workers, actuals);
	}

	@Test
	public void outputCallsForWorkerCreation() {
		sut.createWorkers();
		verify(output).startEvaluation();
		verify(output).setNumTasks(8);
		verify(output, times(2)).count(TYPE1, 0, 1);
		verify(output, times(2)).count(TYPE1, 1, 2);
		verify(output, times(2)).count(TYPE2, 0, 3);
		verify(output, times(2)).count(TYPE2, 1, 4);
	}

	@Test(expected = RuntimeException.class)
	public void ioCrashesForTypeStoreAreCascaded() throws IOException {
		when(store.createTypeStore(any(ITypeName.class), anyInt())).thenThrow(new IOException());
		sut.createWorkers();
	}

	@Test
	public void processingTimeStartsByZero() {
		assertEquals(0, sut.getAggregatedProcessingTimeInS(), 0.00001);
	}

	@Test
	public void addResult_addsProcessingTime() {
		TestTask t = new TestTask();
		t.processingTimeInS = 234;
		sut.addResult(t);
		assertEquals(234, sut.getAggregatedProcessingTimeInS(), 0.00001);
	}

	@Test
	public void addResult_correctLogging() {
		TestTask t = new TestTask() {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return "XYZ";
			}
		};
		t.processingTimeInS = 345;
		sut.addResult(t);
		verify(output).printProgress(eq("### intermediate result, progress: %s"));
		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("task: XYZ"));
		assertTrue(log.get(1).contains("duration: 345"));
	}

	@Test
	public void addResult_cascading() {
		TestTask t = new TestTask();
		sut.addResult(t);
		assertTrue(sut.finishedTasks.size() == 1);
		assertTrue(sut.finishedTasks.contains(t));
	}

	@Test
	public void crashHandling() {
		String task = "TTT";
		Exception e = mock(Exception.class, "EEE");
		sut.addCrash(task, e);
		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("evaluation has crashed whild processing TTT\n"));
		// TODO test e.printStackTrace -- move to logger
	}

	@Test
	public void done_output() {
		sut.done();
		verify(output).stopEvaluation();
		verify(output).printSpeedup(anyInt());
		verify(output).printTypeCounts();
	}

	@Test
	public void done_cascading() {
		sut.done();
		assertTrue(sut.hasCalledLogResults);
	}

	@Test
	public void exceptions_addResult2() {
		sut.doThrow = true;
		sut.addResult(new TestTask() {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return "TTT";
			}
		});
		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(2).contains("EE error during execution of addResult(TTT):\n"));
		// TODO test e.printStackTrace -- move to logger
	}

	@Test
	public void exceptions_logResults() {
		sut.doThrow = true;
		sut.done();
		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("EE error during execution of done:\n"));
		// TODO test e.printStackTrace -- move to logger
	}

	@Test
	@Ignore
	public void exceptions_logging() {
		fail("test for e.printStackTrace() in all methods that are applicable...");
	}
	
	public static class TestTask extends AbstractTask {

		private static final long serialVersionUID = 1L;

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public String toString() {
			return ToStringBuilder.reflectionToString(this);
		}
	}

	public class TestProvider extends AbstractTaskProvider<TestTask> {

		public boolean doThrow;
		private Set<Callable<TestTask>> workers = Sets.newLinkedHashSet();
		private List<TestTask> finishedTasks = Lists.newLinkedList();
		private boolean useType = true;
		private boolean hasCalledLogResults = false;

		public TestProvider() {
			super(store, output);
		}

		@Override
		protected int getNumFolds() {
			return 2;
		}

		@Override
		protected Map<String, String> getOptions() {
			return options;
		}

		@Override
		protected boolean useType(ITypeName type) {
			return TYPE2.equals(type) ? useType : super.useType(type);
		}

		@Override
		protected Callable<TestTask> createWorker(final TestTask task) {
			@SuppressWarnings("unchecked")
			Callable<AbstractTaskProviderTest.TestTask> c = mock(Callable.class);
			workers.add(c);
			return c;
		}

		@Override
		protected TestTask newTask() {
			return new TestTask();
		}

		@Override
		protected void addResult2(TestTask r) {
			finishedTasks.add(r);
			if (doThrow) {
				throw new RuntimeException();
			}
		}

		@Override
		protected void logResults() {
			hasCalledLogResults = true;
			if (doThrow) {
				throw new RuntimeException();
			}
		}

		@Override
		protected String getFileHint() {
			return "TestFile";
		}
	}
}