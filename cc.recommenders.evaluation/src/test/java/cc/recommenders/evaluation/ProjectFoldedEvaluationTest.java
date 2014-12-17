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
package cc.recommenders.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.evaluation.io.TypeStore;
import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Usage;
import cc.recommenders.utils.DateProvider;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ProjectFoldedEvaluationTest {

	public static final Date EXPECTED_DATE = new Date();
	public static final ITypeName TYPE1 = VmTypeName.get("La/Type1");
	public static final ITypeName TYPE2 = VmTypeName.get("Lb/Type2");

	@Mock
	private ProjectFoldedUsageStore store;
	@Mock(name = "type1Store")
	private TypeStore type1Store;
	@Mock(name = "type2Store")
	private TypeStore type2Store;
	@Mock
	private DateProvider dateProvider;

	private ProjectFoldedEvaluationImpl sut;

	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);
		Logger.reset();
		Logger.setCapturing(true);

		Set<ITypeName> types = Sets.newLinkedHashSet();
		types.add(TYPE1);
		types.add(TYPE2);
		when(store.getTypes()).thenReturn(types);
		when(store.isAvailable(any(ITypeName.class), anyInt())).thenReturn(true);
		when(store.createTypeStore(eq(TYPE1), anyInt())).thenReturn(type1Store);
		when(store.createTypeStore(eq(TYPE2), anyInt())).thenReturn(type2Store);
		when(dateProvider.getDate()).thenReturn(EXPECTED_DATE);

		sut = new ProjectFoldedEvaluationImpl();
	}

	@After
	public void teardown() {
		Logger.setCapturing(false);
	}

	@Test
	public void typesAreFilteredIfNotAvailable() throws IOException {
		when(store.isAvailable(TYPE1, 13)).thenReturn(false);

		sut.run();

		verify(store).getTypes();
		verify(store).isAvailable(TYPE1, 13);
		verify(store).isAvailable(TYPE2, 13);
		verify(store).createTypeStore(TYPE2, 13);
	}

	@Test
	public void initIsCalled() throws IOException {
		sut.run();
		assertTrue(sut.hasCalledInit);
	}

	@Test
	public void logResultsIsCalled() throws IOException {
		sut.run();
		assertTrue(sut.hasCalledLogResults);
	}

	@Test
	public void correctArgumentsArePassedToRunFold() throws IOException {
		List<List<Usage>> allTraining = Lists.newLinkedList();
		List<List<Usage>> allValidation = Lists.newLinkedList();
		for (int i = 0; i < 13; i++) {
			allTraining.add(createListWithNMocks(100 + i));
			allValidation.add(createListWithNMocks(200 + i));
			when(type1Store.getTrainingData(i)).thenReturn(allTraining.get(i));
			when(type1Store.getValidationData(i)).thenReturn(allValidation.get(i));
		}

		sut.run();

		for (int i = 0; i < 13; i++) {
			Arguments args = sut.arguments.get(i);
			assertEquals(TYPE1, args.type);
			assertEquals(i, args.foldNum);
			assertEquals(allTraining.get(i), args.training);
			assertEquals(allValidation.get(i), args.validation);
		}
	}

	@Test
	public void headerContainsDefaultInformation() throws IOException {
		when(store.isAvailable(any(ITypeName.class), anyInt())).thenReturn(false);

		sut.run();

		List<String> log = Logger.getCapturedLog();
		String clazz = ProjectFoldedEvaluationImpl.class.toString();
		assertTrue(log.get(0).contains("####"));
		assertTrue(log.get(1).contains("#### running analysis " + clazz + " (" + EXPECTED_DATE + ")..."));
		assertTrue(log.get(2).contains("####"));
		assertTrue(log.get(3).contains(""));
		assertTrue(log.get(4).contains("project-folded cross validation, 13 folds"));
		assertTrue(log.get(5).contains("all types seen in >=13 projects with >=1 usage"));
		assertTrue(log.get(6).contains("--> put outputs into: path/to/file"));
		assertTrue(log.get(7).contains("% do not edit manually, auto-generated on " + EXPECTED_DATE));
		assertTrue(log.get(8).contains("%\n"));
		assertTrue(log.get(9).contains("% results for " + clazz));
		assertTrue(log.get(10).contains("% - project-folded cross validation"));
		assertTrue(log.get(11).contains("% - num folds: 13"));
		assertTrue(log.get(12).contains("% - all types seen in >=13 projects with >=1 usage"));
		assertTrue(log.get(13).contains("LOG_RESULTS"));
	}

	@Test
	public void correctFoldSizesAreRegisteredAndPrinted() throws IOException {
		sut.numFolds = 3;
		for (int i = 0; i < 3; i++) {
			when(type1Store.getTrainingData(i)).thenReturn(createListWithNMocks(10 + i));
			when(type1Store.getValidationData(i)).thenReturn(createListWithNMocks(20 + i));
			when(type2Store.getTrainingData(i)).thenReturn(createListWithNMocks(30 + i));
			when(type2Store.getValidationData(i)).thenReturn(createListWithNMocks(40 + i));
		}

		sut.run();

		List<String> log = Logger.getCapturedLog();

		int t1 = findMatchingLine(log, "## La/Type1");
		assertTrue(log.get(t1 + 1).contains("1/3: 10 training, 20 validation"));
		assertTrue(log.get(t1 + 2).contains("2/3: 11 training, 21 validation"));
		assertTrue(log.get(t1 + 3).contains("3/3: 12 training, 22 validation"));

		int t2 = findMatchingLine(log, "## Lb/Type2");
		assertTrue(log.get(t2 + 1).contains("1/3: 30 training, 40 validation"));
		assertTrue(log.get(t2 + 2).contains("2/3: 31 training, 41 validation"));
		assertTrue(log.get(t2 + 3).contains("3/3: 32 training, 42 validation"));

		int start = findMatchingLine(log, "LOG_RESULTS") + 1;

		assertTrue(log.get(start + 0).contains("186 usages total (from types seen in >= 3 projects with >=1 usage)"));
		assertTrue(log.get(start + 1).contains("----"));
		assertTrue(log.get(start + 2).contains("63 La/Type1"));
		assertTrue(log.get(start + 3).contains("20"));
		assertTrue(log.get(start + 4).contains("21"));
		assertTrue(log.get(start + 5).contains("22"));
		assertTrue(log.get(start + 7).contains("123 Lb/Type2"));
		assertTrue(log.get(start + 8).contains("40"));
		assertTrue(log.get(start + 9).contains("41"));
		assertTrue(log.get(start + 10).contains("42"));
	}

	@Test
	public void typeAndFoldCombinationsAreOnlyCountedOnce() {
		sut.count(TYPE1, 1, 4);
		sut.count(TYPE1, 2, 5);
		sut.count(TYPE1, 1, 6);

		sut.logResultsTypeCounts();

		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(2).contains("9 La/Type1 ("));
		assertTrue(log.get(3).contains("4"));
		assertTrue(log.get(4).contains(", 5"));
		assertTrue(log.get(5).contains(")"));
	}

	@Test
	public void typeLoadingCanBeDeactivated() throws IOException {
		sut = new ProjectFoldedEvaluationImpl() {
			@Override
			protected void foldType(ITypeName type) throws IOException {
				if (!TYPE1.equals(type)) {
					super.foldType(type);
				}
			}
		};
		sut.foldAllTypes();

		int actual = findMatchingLine(Logger.getCapturedLog(), TYPE1.toString());
		int expected = -1;
		assertEquals(expected, actual);
	}

	@Test
	public void typeLoadingCanBeDeactivatedasd() throws IOException {
		sut = new ProjectFoldedEvaluationImpl() {
			@Override
			protected boolean shouldAnalyze(ITypeName type, TypeStore typeStore) {
				return !TYPE1.equals(type);
			}
		};
		sut.foldAllTypes();

		int actual = findMatchingLine(Logger.getCapturedLog(), TYPE1.toString());
		int expected = -1;
		assertEquals(expected, actual);
	}

	@Test
	public void unavailableTypesCauseNotAvailableCall() throws IOException {
		when(store.isAvailable(eq(TYPE1), anyInt())).thenReturn(false);

		final Set<ITypeName> actual = Sets.newHashSet();
		sut = new ProjectFoldedEvaluationImpl() {
			@Override
			protected void notAvailable(ITypeName type) {
				actual.add(type);
			}
		};
		sut.foldAllTypes();

		Set<ITypeName> expected = Sets.newHashSet(TYPE1);
		assertEquals(expected, actual);
	}

	// TODO move to new LoggerTestUtils class
	public static int findMatchingLine(List<String> log, String substring) {
		int lineNum = 0;
		for (String line : log) {
			if (line.contains(substring)) {
				return lineNum;
			}
			lineNum++;
		}
		return -1;
	}

	private List<Usage> createListWithNMocks(int num) {
		List<Usage> l = Lists.newArrayList();
		for (int i = 0; i < num; i++) {
			l.add(mock(Usage.class));
		}
		return l;
	}

	@Test(expected = AssertionException.class)
	public void atLeastTwoFolds() {
		new ProjectFoldedEvaluation(store, dateProvider) {
			protected void runFold(ITypeName type, int foldNum, List<Usage> training, List<Usage> validation) {
			}

			protected void logResults() {
			}

			protected int getNumFolds() {
				return 1;
			}

			protected String getFileHint() {
				return null;
			}
		};
	}

	private class ProjectFoldedEvaluationImpl extends ProjectFoldedEvaluation {

		public int numFolds = 13;

		public List<Arguments> arguments = Lists.newLinkedList();

		public boolean hasCalledLogResults;
		public boolean hasCalledInit;
		public boolean hasCalledShouldAnalyze;
		public boolean hasCalledNotAvailable;

		public ProjectFoldedEvaluationImpl() {
			super(store, dateProvider);
			reset();
		}

		public void reset() {
			arguments.clear();
			hasCalledLogResults = false;
			hasCalledInit = false;
			hasCalledNotAvailable = false;
			hasCalledShouldAnalyze = false;
		}

		@Override
		protected void init() {
			hasCalledInit = true;
		}

		@Override
		protected void notAvailable(ITypeName type) {
			hasCalledNotAvailable = true;
			super.notAvailable(type);
		}

		@Override
		protected boolean shouldAnalyze(ITypeName type, TypeStore typeStore) {
			hasCalledShouldAnalyze = true;
			return super.shouldAnalyze(type, typeStore);
		}

		@Override
		protected void runFold(ITypeName type, int foldNum, List<Usage> training, List<Usage> validation) {
			Arguments p = new Arguments();
			p.type = type;
			p.foldNum = foldNum;
			p.training = training;
			p.validation = validation;
			arguments.add(p);
		}

		@Override
		protected void logResults() {
			hasCalledLogResults = true;
			Logger.log("LOG_RESULTS");
		}

		@Override
		protected int getNumFolds() {
			if (numFolds < 1) {
				return 2;
			} else {
				return numFolds;
			}
		}

		@Override
		protected String getFileHint() {
			return "path/to/file";
		}
	}

	public class Arguments {
		public ITypeName type;
		public int foldNum;
		public List<Usage> training;
		public List<Usage> validation;
	}
}