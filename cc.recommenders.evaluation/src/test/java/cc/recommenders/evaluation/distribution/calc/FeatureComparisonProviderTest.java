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
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.OptionsUtils.OptionsBuilder;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.testutils.LoggerUtils;

public class FeatureComparisonProviderTest {

	private ProjectFoldedUsageStore store;
	private OutputUtils output;

	private FeatureComparisonProvider sut;

	@Before
	public void setup() {
		Logger.reset();

		store = mock(ProjectFoldedUsageStore.class);
		output = mock(OutputUtils.class);
		sut = new FeatureComparisonProvider(store, output);
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void numFolds() {
		assertEquals(10, sut.getNumFolds());
	}

	@Test
	public void fileHint() {
		assertEquals("plots/data/comparison-features.txt", sut.getFileHint());
	}

	@Test
	public void taskCreation() {
		assertNotNull(sut.newTask());
	}

	@Test
	public void workerCreation() {
		assertNotNull(sut.createWorker(sut.newTask()));
	}

	@Test
	public void optionsAreNotNull() {
		assertNotNull(sut.getOptions());
	}

	@Test
	public void getOptions() {
		assertEquals(56, sut.getOptions().size());

		assertOption("BMN+___", opt(bmn(), false, false, false));
		assertOption("BMN+__P", opt(bmn(), false, false, true));
		assertOption("BMN+_D_", opt(bmn(), false, true, false));
		assertOption("BMN+_DP", opt(bmn(), false, true, true));
		assertOption("BMN+C__", opt(bmn(), true, false, false));
		assertOption("BMN+C_P", opt(bmn(), true, false, true));
		assertOption("BMN+CD_", opt(bmn(), true, true, false));
		assertOption("BMN+CDP", opt(bmn(), true, true, true));

		for (int i : new int[] { 0, 15, 25, 30, 40, 60 }) {
			assertOption("PBN" + i + "+___", opt(pbn(i), false, false, false));
			assertOption("PBN" + i + "+__P", opt(pbn(i), false, false, true));
			assertOption("PBN" + i + "+_D_", opt(pbn(i), false, true, false));
			assertOption("PBN" + i + "+_DP", opt(pbn(i), false, true, true));
			assertOption("PBN" + i + "+C__", opt(pbn(i), true, false, false));
			assertOption("PBN" + i + "+C_P", opt(pbn(i), true, false, true));
			assertOption("PBN" + i + "+CD_", opt(pbn(i), true, true, false));
			assertOption("PBN" + i + "+CDP", opt(pbn(i), true, true, true));
		}
	}

	@Test
	public void logForAdding() {
		Logger.setCapturing(true);
		sut.addResult2(r("APP", 123456, 0));

		LoggerUtils.assertLogContains(0, "f1:   " + BoxplotData.from(new double[] { 0 }).getBoxplot());
		LoggerUtils.assertLogContains(1, "size: " + 123456);
	}

	@Test
	public void integrationTest() {
		sut.addResult2(r("A", 10, 0.0));
		sut.addResult2(r("A", 20, 0.6));
		sut.addResult2(r("B", 30, 0.7));

		Logger.setCapturing(true);
		sut.logResults();

		LoggerUtils.assertLogContains(0, "option\tf1\tmodel_size\t% approach: boxplot");
		LoggerUtils.assertLogContains(1, "1\t0.30000\t15\t% A: " + bp(0, 0.6));
		LoggerUtils.assertLogContains(2, "2\t0.70000\t30\t% B: " + bp(0.7));
	}

	@Test
	public void orderIsPreserved() {
		for (int i = 1; i < 10; i++) {
			sut.addResult2(r("APP" + i, 1, 1));
		}

		Logger.setCapturing(true);
		sut.logResults();

		for (int i = 1; i < 10; i++) {
			LoggerUtils.assertLogContains(i, "% APP" + i + ":");
		}
	}

	private static String opt(OptionsBuilder algo, boolean hasClass, boolean hasDef, boolean hasParam) {
		return algo.c(hasClass).d(hasDef).p(hasParam).useFloat().init(false).qNM().ignore(false).min(30).get();
	}

	private void assertOption(String name, String expected) {
		String err = format("does not contain entry for '%s'", name);
		assertTrue(err, sut.getOptions().containsKey(name));
		String actual = sut.getOptions().get(name);
		assertEquals(expected, actual);
	}

	private static Boxplot bp(double... vs) {
		return BoxplotData.from(vs).getBoxplot();
	}

	private static F1AndSizeTask r(String app, int sizeInB, double... f1s) {
		F1AndSizeTask t = new F1AndSizeTask();
		t.app = app;
		t.f1s = f1s;
		t.sizeInB = sizeInB;
		return t;
	}
}