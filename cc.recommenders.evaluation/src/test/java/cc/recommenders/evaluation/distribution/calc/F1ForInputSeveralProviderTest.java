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

import static cc.recommenders.assertions.Asserts.assertNotNull;
import static cc.recommenders.evaluation.OptionsUtils.bmn;
import static cc.recommenders.evaluation.OptionsUtils.pbn;
import static cc.recommenders.evaluation.distribution.calc.F1ForInputSeveralProvider.TYPES;
import static cc.recommenders.testutils.LoggerUtils.assertLogContains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.datastructures.Map2D;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.testutils.LoggerUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class F1ForInputSeveralProviderTest {

	private static final String BUTTON = "Lorg/eclipse/swt/widgets/Button";
	private ProjectFoldedUsageStore store;
	private OutputUtils output;

	private F1ForInputSeveralProvider sut;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		store = mock(ProjectFoldedUsageStore.class);
		output = mock(OutputUtils.class);
		sut = new F1ForInputSeveralProvider(store, output);
	}

	private F1ForInputTask getBasicTask() {
		F1ForInputTask task = new F1ForInputTask();
		task.app = "APP";
		task.options = "OPTIONS";
		task.typeName = BUTTON;
		task.currentFold = 4;
		task.numFolds = 13;
		task.processingTimeInS = 1.23;
		return task;
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void typesAreCompleteAndOrderIsCorrect() {
		assertEquals(20, F1ForInputSeveralProvider.TYPES.size());

		Iterator<ITypeName> it = F1ForInputSeveralProvider.TYPES.iterator();

		assertEquals(VmTypeName.get(BUTTON), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Composite"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Text"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Label"), it.next());
		// ---------------------------------
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Display"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Table"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Combo"), it.next());
		// ---------------------------------
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Control"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Shell"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Tree"), it.next());
		// ---------------------------------
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Menu"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Group"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/TableColumn"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/ToolItem"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/MenuItem"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/ScrollBar"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/Canvas"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/List"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/TableItem"), it.next());
		assertEquals(VmTypeName.get("Lorg/eclipse/swt/widgets/TreeItem"), it.next());
	}

	@Test
	public void fileHint() {
		assertEquals("plots/data/f1-for-input-several-«num».txt", sut.getFileHint());
	}

	@Test
	public void useType() {
		assertTrue(sut.useType(VmTypeName.get(BUTTON)));
		assertFalse(sut.useType(VmTypeName.get("Lorg/eclipse/swt/widgets/SomethingElse")));
	}

	@Test
	public void options() {
		Map<String, String> expecteds = Maps.newLinkedHashMap();
		expecteds.put("BMN+DEF", bmn().c(false).d(true).p(false).init(false).qNM().useFloat().ignore(false).min(30).get());
		expecteds.put("PBN0+DEF", pbn(0).c(false).d(true).p(false).init(false).qNM().useFloat().ignore(false).min(30).get());
		expecteds.put("PBN25+DEF", pbn(25).c(false).d(true).p(false).init(false).qNM().useFloat().ignore(false).min(30).get());
		expecteds.put("PBN40+DEF", pbn(40).c(false).d(true).p(false).init(false).qNM().useFloat().ignore(false).min(30).get());
		expecteds.put("PBN60+DEF", pbn(60).c(false).d(true).p(false).init(false).qNM().useFloat().ignore(false).min(30).get());
		assertEquals(expecteds, sut.getOptions());
	}

	@Test
	public void addResultLogging() {
		F1ForInputTask task = getBasicTask();
		task.inputSize = 1000;
		task.f1s = new double[] { 0.0, 0.2, 0.4 };
		sut.addResult2(task);
		assertLogContains(0, "f1: [3 values (avg: 0.200) - 0.00; 0.00; 0.20; 0.40; 0.40]");
	}

	@Test
	public void logResultsLogging() {
		sut = new F1ForInputSeveralProvider(store, output) {
			@Override
			protected void logResults(Map2D<ITypeName, Integer, BoxplotData> results, String filter,
					List<ITypeName> types) {
				Logger.append("%s\n", filter);
			}
		};
		F1ForInputTask task = getBasicTask();
		task.inputSize = 1000;
		task.f1s = new double[] { 0.0, 0.2, 0.4 };
		task.app = "APP1";
		sut.addResult2(task);
		task.app = "APP2";
		sut.addResult2(task);

		Logger.clearLog();
		sut.logResults();

		LoggerUtils.assertLogContains(0, "app: APP1");
		LoggerUtils.assertLogContains(1, "all");
		LoggerUtils.assertLogContains(2, "20000");
		LoggerUtils.assertLogContains(3, "9000");
		LoggerUtils.assertLogContains(4, "3000");
		LoggerUtils.assertLogContains(5, "1000");
		LoggerUtils.assertLogContains(6, "app: APP2");
		LoggerUtils.assertLogContains(7, "all");
		LoggerUtils.assertLogContains(8, "20000");
		LoggerUtils.assertLogContains(9, "9000");
		LoggerUtils.assertLogContains(10, "3000");
		LoggerUtils.assertLogContains(11, "1000");
	}

	@Test
	public void correctInvocationOfSubLog() {
		final List<Params> actualParams = Lists.newLinkedList();
		sut = new F1ForInputSeveralProvider(store, output) {
			@Override
			protected void logResults(Map2D<ITypeName, Integer, BoxplotData> results, String filter,
					List<ITypeName> types) {
				assertNotNull(results);
				Params p = new Params(filter, types);
				actualParams.add(p);
			}
		};

		F1ForInputTask task = getBasicTask();
		task.inputSize = 1000;
		task.f1s = new double[] { 0.0, 0.2, 0.4 };
		sut.addResult2(task);
		sut.logResults();

		List<Params> expectedParams = Lists.newLinkedList();
		expectedParams.add(new Params("all", TYPES));
		expectedParams.add(new Params("20000", types(0, 1, 2)));
		expectedParams.add(new Params("9000", types(3, 4, 5, 6)));
		expectedParams.add(new Params("3000", types(7, 8, 9)));
		expectedParams.add(new Params("1000", types(10, 11, 12, 13, 14, 15, 16, 17, 18, 19)));

		assertEquals(expectedParams, actualParams);
	}

	private List<ITypeName> types(int... idxs) {
		List<ITypeName> types = Lists.newArrayListWithCapacity(idxs.length);
		for (int idx : idxs) {
			types.add(TYPES.get(idx));
		}
		return types;
	}

	@Test
	public void mergingAndLoggingOfResults() {
		sut = new F1ForInputSeveralProvider(store, output) {
			@Override
			protected void logResults(Map2D<ITypeName, Integer, BoxplotData> results, String filter,
					List<ITypeName> types) {
				if (types.contains(VmTypeName.get(BUTTON))) {
					super.logResults(results, filter, types);
				}
			}
		};
		F1ForInputTask task = getBasicTask();
		for (int inputSize : new int[] { 1000, 3000, 9000, 10000, 15000, 20000, 30000, 40000 }) {
			task.inputSize = inputSize;
			task.f1s = new double[] { 0.01 * (inputSize / 1000) };
			sut.addResult(task);
		}

		Logger.clearLog();
		sut.logResults();
		
		int i = 0;
		LoggerUtils.assertLogContains(i++, "app: APP");
		LoggerUtils.assertLogContains(i++, "«num» == all");
		LoggerUtils.assertLogContains(i, "input", "Button", "\n");
		LoggerUtils.assertLogContains(i += 3, "1000", "0.01000", "\n");
		LoggerUtils.assertLogContains(i += 3, "3000", "0.03000", "\n");
		LoggerUtils.assertLogContains(i += 3, "9000", "0.09000", "\n");
		LoggerUtils.assertLogContains(i += 3, "10000", "0.10000", "\n");
		LoggerUtils.assertLogContains(i += 3, "15000", "0.15000", "\n");
		LoggerUtils.assertLogContains(i += 3, "20000", "0.20000", "\n");
		LoggerUtils.assertLogContains(i += 3, "30000", "0.30000", "\n");
		LoggerUtils.assertLogContains(i += 3, "40000", "0.40000", "\n");
		LoggerUtils.assertLogContains(i += 3, "\n");

		LoggerUtils.assertLogContains(i += 1, "«num» == 20000");
		LoggerUtils.assertLogContains(i += 1, "input", "Button", "\n");
		LoggerUtils.assertLogContains(i += 3, "1000", "0.01000", "\n");
		LoggerUtils.assertLogContains(i += 3, "3000", "0.03000", "\n");
		LoggerUtils.assertLogContains(i += 3, "9000", "0.09000", "\n");
		LoggerUtils.assertLogContains(i += 3, "10000", "0.10000", "\n");
		LoggerUtils.assertLogContains(i += 3, "15000", "0.15000", "\n");
		LoggerUtils.assertLogContains(i += 3, "20000", "0.20000", "\n");
		LoggerUtils.assertLogContains(i += 3, "30000", "0.30000", "\n");
		LoggerUtils.assertLogContains(i += 3, "40000", "0.40000", "\n");
		LoggerUtils.assertLogContains(i += 3, "\n");
	}

	@SuppressWarnings("unused")
	private static class Params {

		public Map2D<ITypeName, Integer, BoxplotData> results;
		public String filter;
		public List<ITypeName> types;

		public Params(String filter, List<ITypeName> types) {
			this.filter = filter;
			this.types = types;

		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}
	}
}