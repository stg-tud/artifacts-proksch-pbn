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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

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

import com.google.common.collect.Maps;

public class F1AndSizeProviderTest {

	private ProjectFoldedUsageStore store;
	private OutputUtils output;
	private F1AndSizeTask task;

	private F1AndSizeProvider sut;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		output = mock(OutputUtils.class);
		store = mock(ProjectFoldedUsageStore.class);
		task = getBasicTask();
		sut = new F1AndSizeProvider(store, output);
	}

	private static F1AndSizeTask getBasicTask() {
		F1AndSizeTask task = new F1AndSizeTask();
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
	public void options() {
		Map<String, String> expecteds = Maps.newLinkedHashMap();
		expecteds.put("BMN", opt(bmn(), false, false, false));
		for (int n = 0; n < 26; n++) {
			expecteds.put("PBN" + n, opt(pbn(n), false, false, false));
		}
		for (int n = 26; n < 50; n = n + 2) {
			expecteds.put("PBN" + n, opt(pbn(n), false, false, false));
		}
		for (int n = 50; n <= 100; n = n + 5) {
			expecteds.put("PBN" + n, opt(pbn(n), false, false, false));
		}
		expecteds.put("BMN+DEF", opt(bmn(), false, true, false));
		for (int n = 0; n < 26; n++) {
			expecteds.put("PBN" + n + "+DEF", opt(pbn(n), false, true, false));
		}
		for (int n = 26; n < 50; n = n + 2) {
			expecteds.put("PBN" + n + "+DEF", opt(pbn(n), false, true, false));
		}
		for (int n = 50; n <= 100; n = n + 5) {
			expecteds.put("PBN" + n + "+DEF", opt(pbn(n), false, true, false));
		}

		assertEquals(expecteds, sut.getOptions());
	}

	@Test
	public void useTypeIsOverridden() {
		assertTrue(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button")));
		assertFalse(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/SomethingElse")));
	}

	private String opt(OptionsBuilder algo, boolean hasC, boolean hasD, boolean hasP) {
		return algo.c(hasC).d(hasD).p(hasP).useFloat().qNM().init(false).ignore(false).min(30).get();
	}

	@Test
	public void fileHint() {
		assertEquals("plots/data/quality-and-size.txt", sut.getFileHint());
	}

	@Test
	public void numFolds() {
		assertEquals(10, sut.getNumFolds());
	}

	@Test
	public void taskIsNotNull() {
		assertNotNull(sut.newTask());
	}

	@Test
	public void workerIsNotNull() {
		assertNotNull(sut.createWorker(new F1AndSizeTask()));
	}

	@Test
	public void addResultLogging() {
		task.f1s = new double[] { 0.3 };
		task.sizeInB = 102400;
		sut.addResult2(task);

		LoggerUtils.assertLogContains(0, "size: 100.0 KiB (raw: 102400 B)");
		LoggerUtils.assertLogContains(1, "f1:   [1 values (avg: 0.300) - 0.30; 0.30; 0.30; 0.30; 0.30]");
	}

	@Test
	public void resultMerging() {
		task.f1s = new double[] { 0.2 };
		task.sizeInB = 102400;
		sut.addResult2(task);
		task.f1s = new double[] { 0.6 };
		task.sizeInB = 307200;
		sut.addResult2(task);
		task.app = "APP2";
		task.f1s = new double[] { 0.3 };
		task.sizeInB = 30720;
		sut.addResult2(task);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "rec\tsize\tf1\t% boxplot\n");
		LoggerUtils.assertLogContains(1, "APP1\t204800\t0.40000\t% [2 values (avg: 0.400) - 0.20; 0.20; 0.40; 0.60; 0.60]");
		LoggerUtils.assertLogContains(2, "APP2\t30720\t0.30000\t% [1 values (avg: 0.300) - 0.30; 0.30; 0.30; 0.30; 0.30]");
	}
}