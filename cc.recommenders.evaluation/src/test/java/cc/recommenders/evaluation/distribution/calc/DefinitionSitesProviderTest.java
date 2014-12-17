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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.OptionsUtils.OptionsBuilder;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.testutils.LoggerUtils;
import cc.recommenders.usages.DefinitionSiteKind;

import com.google.common.collect.Maps;

public class DefinitionSitesProviderTest {

	private ProjectFoldedUsageStore store;
	private OutputUtils output;

	private DefinitionSitesTask task;
	private DefinitionSitesProvider sut;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		output = mock(OutputUtils.class);
		store = mock(ProjectFoldedUsageStore.class);
		task = getBasicTask();
		sut = new DefinitionSitesProvider(store, output);
	}

	private static DefinitionSitesTask getBasicTask() {
		DefinitionSitesTask task = new DefinitionSitesTask();
		task.app = "APP1";
		task.currentFold = 4;
		task.numFolds = 13;
		task.options = "OPTS";
		task.processingTimeInS = 0.123;
		task.typeName = "TYPE";
		task.results = Maps.newLinkedHashMap();
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
	public void fileHint() {
		assertEquals("plots/data/definition-sites.txt", sut.getFileHint());
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
		assertNotNull(sut.createWorker(new DefinitionSitesTask()));
	}

	@Test
	public void addResultLogging() {
		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.1, 0.3 });
		task.results.put(DefinitionSiteKind.THIS, new double[] { 0.4 });
		sut.addResult2(task);

		assertLogContains(0, "f1(definition site kind):");
		assertLogContains(1, "\tNEW: [2 values (avg: 0.200) - 0.10; 0.10; 0.20; 0.30; 0.30]");
		assertLogContains(2, "\tTHIS: [1 values (avg: 0.400) - 0.40; 0.40; 0.40; 0.40; 0.40]");
	}

	@Test
	public void resultMerging_differentApps() {

		task.results.clear();
		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.8 });
		task.results.put(DefinitionSiteKind.THIS, new double[] { 0.9 });
		task.results.put(DefinitionSiteKind.FIELD, new double[] { 1.0 });
		sut.addResult2(task);

		task.app = "APP2";

		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.1 });
		task.results.put(DefinitionSiteKind.THIS, new double[] { 0.2 });
		task.results.put(DefinitionSiteKind.FIELD, new double[] { 0.3 });
		sut.addResult2(task);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "type\tcount", "\tAPP1", "\tAPP2", "\tnometa\n");
		LoggerUtils.assertLogContains(4, "NEW\t1", "\t0.8", "\t0.1", "\t~\n");
		LoggerUtils.assertLogContains(8, "THIS\t1", "\t0.9", "\t0.2", "\t~\n");
		LoggerUtils.assertLogContains(12, "FIELD\t1", "\t1.0", "\t0.3", "\t~\n");
	}

	@Test
	public void resultMerging_differentTypes() {

		task.typeName = "LT1";
		task.results.clear();
		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.1 });
		sut.addResult2(task);

		task.typeName = "LT2";
		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.3 });
		sut.addResult2(task);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "type\tcount", "\tAPP1", "\tnometa\n");
		LoggerUtils.assertLogContains(3, "NEW\t2", "\t0.2", "\t~\n");
	}

	@Test
	public void resultMerging_differentFolds() {

		task.typeName = "LT1";
		task.currentFold = 1;
		task.results.clear();
		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.1 });
		sut.addResult2(task);

		task.typeName = "LT1";
		task.currentFold = 2;
		task.results.put(DefinitionSiteKind.NEW, new double[] { 0.3 });
		sut.addResult2(task);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "type\tcount", "\tAPP1", "\tnometa\n");
		LoggerUtils.assertLogContains(3, "NEW\t2", "\t0.2", "\t~\n");
	}
}