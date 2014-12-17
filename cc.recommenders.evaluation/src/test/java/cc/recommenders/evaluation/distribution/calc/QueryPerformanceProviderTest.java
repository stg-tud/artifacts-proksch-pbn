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

import static cc.recommenders.evaluation.OptionsUtils.bmn;
import static cc.recommenders.evaluation.OptionsUtils.pbn;
import static cc.recommenders.testutils.LoggerUtils.assertLogContains;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.OptionsUtils.OptionsBuilder;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.testutils.LoggerUtils;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class QueryPerformanceProviderTest {

	private QueryPerformanceProvider sut;
	private QueryPerformanceTask task;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		task = getBasicTask();
		sut = new QueryPerformanceProvider(mock(ProjectFoldedUsageStore.class), mock(OutputUtils.class));
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	private QueryPerformanceTask getBasicTask() {
		QueryPerformanceTask task = new QueryPerformanceTask();
		task.app = "APP1";
		task.currentFold = 4;
		task.numFolds = 13;
		task.options = "OPTS";
		task.processingTimeInS = 0.123;
		task.typeName = "TYPE";
		task.inputSize = 1000;
		return task;
	}

	@Test
	public void addResultLogging() {
		task.modelSize = 123;
		task.learningDurationInS = 2.345;
		task.perQueryDurationInMS = 34.567;

		sut.addResult2(task);

		LoggerUtils.assertLogContains(0, "model size: 123 B");
		LoggerUtils.assertLogContains(1, "learning: 2.35s");
		LoggerUtils.assertLogContains(2, "per query: 34.57ms");
	}

	@Test
	public void correctMergingAndResultLogging_sameAppSameSize() {
		addResult("APP1", 1000, 100, 1, 10);
		addResult("APP1", 1000, 200, 2, 20);

		Logger.clearLog();
		sut.logResults();

		assertLogContains(0, "300 queries per fold\n");
		assertLogContains(1, "filtered for Lorg/eclipse/swt/widgets/Button\n");
		assertLogContains(2, "units:\n", "model size: B\n", "learning speed: s\n", "query speed: ms\n\n");
		assertLogContains(6, "input", "\tAPP1_size", "\tAPP1_learn", "\tAPP1_query", "\n");
		assertLogContains(11, "1000", "\t150", "\t1.50", "\t15.00", "\n");
	}

	@Test
	public void correctMergingAndResultLogging_sameAppDiffSize() {
		addResult("APP1", 1000, 100, 1, 10);
		addResult("APP1", 3000, 200, 2, 20);

		Logger.clearLog();
		sut.logResults();

		assertLogContains(0, "300 queries per fold\n");
		assertLogContains(1, "filtered for Lorg/eclipse/swt/widgets/Button\n");
		assertLogContains(2, "units:\n", "model size: B\n", "learning speed: s\n", "query speed: ms\n\n");
		assertLogContains(6, "input", "\tAPP1_size", "\tAPP1_learn", "\tAPP1_query", "\n");
		assertLogContains(11, "1000", "\t100", "\t1.00", "\t10.0", "\n");
		assertLogContains(16, "3000", "\t200", "\t2.00", "\t20.0", "\n");
	}

	@Test
	public void correctMergingAndResultLogging_sameSizeDiffApp() {
		addResult("APP1", 1000, 100, 1, 10);
		addResult("APP2", 1000, 200, 2, 20);

		Logger.clearLog();
		sut.logResults();

		assertLogContains(0, "300 queries per fold\n");
		assertLogContains(1, "filtered for Lorg/eclipse/swt/widgets/Button\n");
		assertLogContains(2, "units:\n", "model size: B\n", "learning speed: s\n", "query speed: ms\n\n");
		assertLogContains(6, "input", "\tAPP1_size", "\tAPP1_learn", "\tAPP1_query");
		assertLogContains(10, "\tAPP2_size", "\tAPP2_learn", "\tAPP2_query", "\n");
		assertLogContains(14, "1000", "\t100", "\t1.00", "\t10.00");
		assertLogContains(18, "\t200", "\t2.00", "\t20.00", "\n");
	}

	private void addResult(String app, int inputSize, int size, double learningSpeed, double inferenceSpeed) {
		task.inputSize = inputSize;
		task.app = app;
		task.modelSize = size;
		task.learningDurationInS = learningSpeed;
		task.perQueryDurationInMS = inferenceSpeed;
		sut.addResult2(task);
	}

	@Test
	public void createTasksFor() {
		assertArrayEquals(new int[] { 10 }, assertTasksAndGetSizes(29));
		assertArrayEquals(new int[] { 10, 30, 100, 300, 1000 }, assertTasksAndGetSizes(2999));
		assertArrayEquals(new int[] { 10, 30, 100, 300, 1000, 3000, 10000, 15000, 20000, 30000 },
				assertTasksAndGetSizes(39999));
	}

	private int[] assertTasksAndGetSizes(int num) {
		String typeName = "LType";
		Collection<QueryPerformanceTask> tasks = sut.createTasksFor("APP", VmTypeName.get(typeName), 3,
				createTrainingData(num));
		int i = 0;
		int[] sizes = new int[tasks.size()];
		for (QueryPerformanceTask task : tasks) {
			assertEquals("APP", task.app);
			assertEquals(typeName, task.typeName);
			assertEquals(3, task.currentFold);
			assertTrue(task.inputSize != 0);
			sizes[i++] = task.inputSize;
		}

		return sizes;
	}

	private List<Usage> createTrainingData(int num) {
		List<Usage> usages = Lists.newLinkedList();
		for (int i = 0; i < num; i++) {
			usages.add(mock(Usage.class));
		}
		return usages;
	}

	@Test
	public void numFolds() {
		assertEquals(10, sut.getNumFolds());
	}

	@Test
	public void fileHint() {
		assertEquals("plots/data/query_performance.txt", sut.getFileHint());
	}

	@Test
	public void options() {
		Map<String, String> expecteds = Maps.newLinkedHashMap();
		expecteds.put("BMN", opt(bmn(), false, false, false));
		expecteds.put("PBN0", opt(pbn(0), false, false, false));
		expecteds.put("PBN15", opt(pbn(15), false, false, false));
		expecteds.put("PBN25", opt(pbn(25), false, false, false));
		expecteds.put("PBN30", opt(pbn(30), false, false, false));
		expecteds.put("PBN40", opt(pbn(40), false, false, false));
		expecteds.put("PBN60", opt(pbn(60), false, false, false));

		expecteds.put("BMN+DEF", opt(bmn(), false, true, false));
		expecteds.put("PBN0+DEF", opt(pbn(0), false, true, false));
		expecteds.put("PBN15+DEF", opt(pbn(15), false, true, false));
		expecteds.put("PBN25+DEF", opt(pbn(25), false, true, false));
		expecteds.put("PBN30+DEF", opt(pbn(30), false, true, false));
		expecteds.put("PBN40+DEF", opt(pbn(40), false, true, false));
		expecteds.put("PBN60+DEF", opt(pbn(60), false, true, false));

		expecteds.put("BMN+ALL", opt(bmn(), true, true, true));
		expecteds.put("PBN0+ALL", opt(pbn(0), true, true, true));
		expecteds.put("PBN15+ALL", opt(pbn(15), true, true, true));
		expecteds.put("PBN25+ALL", opt(pbn(25), true, true, true));
		expecteds.put("PBN30+ALL", opt(pbn(30), true, true, true));
		expecteds.put("PBN40+ALL", opt(pbn(40), true, true, true));
		expecteds.put("PBN60+ALL", opt(pbn(60), true, true, true));

		assertEquals(expecteds, sut.getOptions());
	}

	private String opt(OptionsBuilder algo, boolean hasC, boolean hasD, boolean hasP) {
		return algo.c(hasC).d(hasD).p(hasP).useFloat().qNM().init(false).ignore(false).min(30).get();
	}

	@Test
	public void newTask() {
		assertNotNull(sut.newTask());
	}

	@Test
	public void newWorker() {
		assertNotNull(sut.createWorker(task));
	}

	@Test
	public void onlyButtons() {
		assertTrue(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button")));
		assertFalse(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/Other")));
	}

	@Test
	public void allSizes() {
		int[] expecteds = new int[] { 10, 30, 100, 300, 1000, 3000, 10000, 15000, 20000, 30000, 40000, 100000 };
		assertArrayEquals(expecteds, QueryPerformanceProvider.ALL_SIZES);
	}
}