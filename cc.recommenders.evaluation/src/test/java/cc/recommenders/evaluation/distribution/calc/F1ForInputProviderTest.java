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
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class F1ForInputProviderTest {

	private ProjectFoldedUsageStore store;
	private OutputUtils output;

	private F1ForInputTask task;
	private F1ForInputProvider sut;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		store = mock(ProjectFoldedUsageStore.class);
		output = mock(OutputUtils.class);
		task = getBasicTask();
		sut = new F1ForInputProvider(store, output);
	}

	private static F1ForInputTask getBasicTask() {
		F1ForInputTask task = new F1ForInputTask();
		task.app = "APP1";
		task.currentFold = 4;
		task.numFolds = 13;
		task.options = "OPTS";
		task.processingTimeInS = 0.123;
		task.typeName = "TYPE";
		return task;
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void fileHint() {
		assertEquals("plots/data/f1-for-input.txt", sut.getFileHint());
	}

	@Test
	public void numFolds() {
		assertEquals(10, sut.getNumFolds());
	}

	@Test
	public void newTask() {
		assertNotNull(sut.newTask());
	}

	@Test
	public void newWorker() {
		assertNotNull(sut.createWorker(new F1ForInputTask()));
	}

	@Test
	public void useType() {
		assertTrue(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button")));
		assertFalse(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/Composite")));
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
	public void createTasksFor() {
		assertArrayEquals(new int[] { 10 }, assertTasksAndGetSizes(29));
		assertArrayEquals(new int[] { 10, 30, 100, 300, 1000 }, assertTasksAndGetSizes(2999));
		assertArrayEquals(new int[] { 10, 30, 100, 300, 1000, 3000, 9000, 10000, 15000, 20000, 30000 },
				assertTasksAndGetSizes(39999));
	}

	@Test
	public void addResultLogging() {
		task.inputSize = 100;
		task.f1s = new double[] { 0.123 };
		sut.addResult2(task);

		assertLogContains(0, "f1(100): [1 values (avg: 0.123) - 0.12; 0.12; 0.12; 0.12; 0.12]");
	}

	@Test
	public void mergeResults() {

		task.inputSize = 100;
		task.f1s = new double[] { 0.1 };
		sut.addResult2(task);

		task.inputSize = 300;
		task.f1s = new double[] { 0.2 };
		sut.addResult2(task);

		task.app = "APP2";

		task.inputSize = 100;
		task.f1s = new double[] { 0.3 };
		sut.addResult2(task);
		task.inputSize = 300;
		task.f1s = new double[] { 0.4 };
		sut.addResult2(task);

		Logger.clearLog();
		sut.logResults();

		assertLogContains(0, "inputSize", "\tAPP1", "\tAPP2", "\n");
		assertLogContains(4, "100", "\t0.10000", "\t0.30000", "\n");
		assertLogContains(8, "300", "\t0.20000", "\t0.40000", "\n");
	}

	private int[] assertTasksAndGetSizes(int num) {
		String typeName = "LType";
		Collection<F1ForInputTask> tasks = sut.createTasksFor("APP", VmTypeName.get(typeName), 3,
				createTrainingData(num));
		int i = 0;
		int[] sizes = new int[tasks.size()];
		for (F1ForInputTask task : tasks) {
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
}