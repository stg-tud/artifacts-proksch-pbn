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
import cc.recommenders.evaluation.data.NM;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.testutils.LoggerUtils;

public class QueryTypeProviderTest {

	private QueryTypeProvider sut;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		sut = new QueryTypeProvider(mock(ProjectFoldedUsageStore.class), mock(OutputUtils.class));
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
	public void getFileHint() {
		assertEquals("plots/data/query-types-(0|n)m[-ext].txt", sut.getFileHint());
	}

	@Test
	public void creationOfTask() {
		assertNotNull(sut.newTask());

	}

	@Test
	public void creationOfWorker() {
		assertNotNull(sut.createWorker(new QueryTypeTask()));
	}

	@Test
	public void options() {
		assertEquals(42, sut.getOptions().size());

		for (String prefix : new String[] { "Q0_", "QNM_" }) {
			boolean useNM = "QNM_".equals(prefix);
			String name = prefix + "BMN";
			assertOption(name, opt(bmn(), false, false, false, useNM));
			assertOption(name + "+DEF", opt(bmn(), false, true, false, useNM));
			assertOption(name + "+ALL", opt(bmn(), true, true, true, useNM));
			for (int i : new int[] { 0, 15, 25, 30, 40, 60 }) {
				name = prefix + "PBN" + i;
				assertOption(name, opt(pbn(i), false, false, false, useNM));
				assertOption(name + "+DEF", opt(pbn(i), false, true, false, useNM));
				assertOption(name + "+ALL", opt(pbn(i), true, true, true, useNM));
			}
		}
	}

	@Test
	public void addResult2Output() {
		QueryTypeTask task = new QueryTypeTask();
		for (int i = 0; i < 13; i++) {
			task.results.put(new NM(i, 13), new double[] { i * 0.01 });
		}
		sut.addResult2(task);

		LoggerUtils.assertLogContains(0, "f1(nm):");
		for (int i = 0; i < 13; i++) {
			Boxplot bp = BoxplotData.from(new double[] { i * 0.01 }).getBoxplot();
			String line = String.format("- %d|13: %s", i, bp);
			LoggerUtils.assertLogContains((i + 1), line);
		}
	}

	@Test
	public void resultMerging() {
		QueryTypeTask t1 = newTask("Q0_A");
		t1.results.put(new NM(0, 2), new double[] { 0.02 });
		t1.results.put(new NM(0, 3), new double[] { 0.03 });
		sut.addResult2(t1);

		QueryTypeTask t2 = newTask("Q0_A");
		t2.results.put(new NM(0, 3), new double[] { 0.05 });
		t2.results.put(new NM(0, 4), new double[] { 0.06 });
		sut.addResult2(t2);

		QueryTypeTask t3 = newTask("QNM_A");
		t3.results.put(new NM(1, 4), new double[] { 0.31 });
		t3.results.put(new NM(2, 4), new double[] { 0.32 });
		t3.results.put(new NM(3, 4), new double[] { 0.33 });
		sut.addResult2(t3);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "prepend 'Q0_'");
		LoggerUtils.assertLogContains(1, "type\tcount");
		LoggerUtils.assertLogContains(2, "\tA");
		LoggerUtils.assertLogContains(3, "\n");
		LoggerUtils.assertLogContains(4, "0$|$2\t1");
		LoggerUtils.assertLogContains(5, "\t0.020");
		LoggerUtils.assertLogContains(6, "\n");
		LoggerUtils.assertLogContains(7, "0$|$3\t2");
		LoggerUtils.assertLogContains(8, "\t0.040");
		LoggerUtils.assertLogContains(9, "\n");
		LoggerUtils.assertLogContains(10, "0$|$4\t1");
		LoggerUtils.assertLogContains(11, "\t0.060");
		LoggerUtils.assertLogContains(12, "\n");

		LoggerUtils.assertLogContains(13, "prepend 'QNM_'");
		LoggerUtils.assertLogContains(14, "type\tcount");
		LoggerUtils.assertLogContains(15, "\tA");
		LoggerUtils.assertLogContains(16, "\n");

		LoggerUtils.assertLogContains(17, "1$|$4\t1");
		LoggerUtils.assertLogContains(18, "\t0.310");
		LoggerUtils.assertLogContains(19, "\n");

		LoggerUtils.assertLogContains(20, "2$|$4\t1");
		LoggerUtils.assertLogContains(21, "\t0.320");
		LoggerUtils.assertLogContains(22, "\n");
		LoggerUtils.assertLogContains(23, "3$|$4\t1");
		LoggerUtils.assertLogContains(24, "\t0.330");
		LoggerUtils.assertLogContains(25, "\n");
	}

	@Test
	public void specialLoggingOfElseCases() {
		QueryTypeTask t1 = newTask("X");
		t1.results.put(NM.ELSE_0M, new double[] { 0 });
		t1.results.put(NM.ELSE_NM, new double[] { 0.2 });
		sut.addResult2(t1);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "prepend 'Q0_'");
		LoggerUtils.assertLogContains(1, "type\tcount");
		LoggerUtils.assertLogContains(3, "0$|$7+\t1");

		LoggerUtils.assertLogContains(5, "prepend 'QNM_'");
		LoggerUtils.assertLogContains(6, "type\tcount");
		LoggerUtils.assertLogContains(8, "*$|$7+\t1");
	}

	private QueryTypeTask newTask(String app) {
		QueryTypeTask task = new QueryTypeTask();
		task.app = app;
		return task;
	}

	private String opt(OptionsBuilder algo, boolean hasClass, boolean hasDef, boolean hasParam, boolean useNM) {
		OptionsBuilder opt = algo.c(hasClass).d(hasDef).p(hasParam).init(false).useFloat().ignore(false).min(30);
		if (useNM) {
			opt.qNM();
		} else {
			opt.q0();
		}
		return opt.useFloat().get();
	}

	private void assertOption(String name, String expected) {
		String err = format("does not contain entry for '%s'", name);
		assertTrue(err, sut.getOptions().containsKey(name));
		String actual = sut.getOptions().get(name);
		assertEquals(expected, actual);
	}

}