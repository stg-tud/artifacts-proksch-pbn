/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.evaluation.evaluators;

import static cc.recommenders.evaluation.evaluators.Validations.nFoldCrossValidation;
import static cc.recommenders.evaluation.evaluators.Validations.sizedNFoldCrossValidation;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.Miner;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ValidationsTest {

	private static final int FOLD_SIZE = 10;

	private static final int NUM_FOLDS = 3;

	private Miner<String, String> builder;
	private Evaluator<String, String, String> evaluator;

	private List<List<String>> capturedValidationData;

	private List<List<String>> capturedTrainingData;

	@Before
	public void setup() {
		capturedValidationData = newArrayList();
		capturedTrainingData = newArrayList();

		builder = mock(Miner.class);
		ICallsRecommender<String> r1 = mock(ICallsRecommender.class);
		ICallsRecommender<String> r2 = mock(ICallsRecommender.class);
		ICallsRecommender<String> r3 = mock(ICallsRecommender.class);
		when(builder.learnModel(anyList())).thenReturn("model1").thenReturn("model2").thenReturn("model3");
		when(builder.createRecommender(anyList())).thenReturn(r1).thenReturn(r2).thenReturn(r3);
		evaluator = mock(Evaluator.class);
	}

	@Test
	public void a3FoldHas3Folds() {
		List<String> in = createListWithSize(18);
		nFoldCrossValidation(NUM_FOLDS, builder, evaluator, in);

		verify(builder, times(NUM_FOLDS)).createRecommender(anyList());
		verify(evaluator, times(NUM_FOLDS)).query(anyRecommender(), anyList());
	}

	@Test
	public void aSized3FoldHas3Folds() {
		List<String> in = createListWithSize(36);
		sizedNFoldCrossValidation(NUM_FOLDS, builder, evaluator, in, FOLD_SIZE);

		verify(builder, times(NUM_FOLDS * 2)).createRecommender(anyList());
		verify(evaluator, times(NUM_FOLDS * 2)).query(anyRecommender(), anyList());
	}

	@Test
	public void trainingDataHasExpectedSize() {
		List<String> in = createListWithSize(13);
		nFoldCrossValidation(NUM_FOLDS, builder, evaluator, in);

		verify(builder).createRecommender(anyListOfSize(8));
		verify(builder, times(2)).createRecommender(anyListOfSize(9));

		verify(evaluator).query(anyRecommender(), anyListOfSize(5));
		verify(evaluator, times(2)).query(anyRecommender(), anyListOfSize(4));
	}

	@Test
	public void sizedTrainingDataHasExpectedSize() {
		List<String> in = createListWithSize(36); // -> 3 * (12 + 24)
		sizedNFoldCrossValidation(NUM_FOLDS, builder, evaluator, in, FOLD_SIZE);

		verify(builder, times(6)).createRecommender(anyListOfSize(10));
		verify(evaluator, times(6)).query(anyRecommender(), anyListOfSize(12));
	}

	@Test
	public void trainingAndCorrespondingValidationSetsAreDisjunct() {
		List<String> in = createListWithSize(36); // -> 3 * (12 + 24)
		nFoldCrossValidation(NUM_FOLDS, builder, evaluator, in);

		verify(builder, atLeastOnce()).createRecommender(matchAnyListAndStore(capturedTrainingData));
		verify(evaluator, atLeastOnce()).query(anyRecommender(), matchAnyListAndStore(capturedValidationData));

		assertEquals(capturedTrainingData.size(), capturedValidationData.size());

		for (int i = 0; i < capturedTrainingData.size(); i++) {
			List<String> training = capturedTrainingData.get(i);
			List<String> validation = capturedValidationData.get(i);

			for (String tString : training) {
				assertFalse(validation.contains(tString));
			}

			for (String vString : validation) {
				assertFalse(training.contains(vString));
			}
		}
	}

	@Test
	public void theValidationSetsAreDisjunct() {
		List<String> in = createListWithSize(18); // -> 3 * (2 + 4)
		nFoldCrossValidation(NUM_FOLDS, builder, evaluator, in);

		ArgumentCaptor<List> c = ArgumentCaptor.forClass(List.class);
		verify(evaluator, atLeastOnce()).query(anyRecommender(), c.capture());
		List<List> allValues = c.getAllValues();

		for (int i = 0; i < allValues.size(); i++) {
			List<String> training = allValues.get(i);
			List<String> others = newArrayList();
			for (int j = 0; j < allValues.size(); j++) {
				if (i != j) {
					List<String> list = allValues.get(j);
					others.addAll(list);
				}
			}

			for (String tString : training) {
				assertFalse(others.contains(tString));
			}

			for (String vString : others) {
				assertFalse(training.contains(vString));
			}
		}
	}

	@Test
	public void threeDifferentModelsAreLearned() {
		List<String> in = createListWithSize(18); // -> 3 * (2 + 4)
		nFoldCrossValidation(NUM_FOLDS, builder, evaluator, in);

		ArgumentCaptor<ICallsRecommender> captor = ArgumentCaptor.forClass(ICallsRecommender.class);
		verify(evaluator, times(3)).query(captor.capture(), anyList());
		List<ICallsRecommender> allValues = captor.getAllValues();

		ICallsRecommender first = allValues.get(0);
		ICallsRecommender second = allValues.get(1);
		ICallsRecommender third = allValues.get(2);

		assertNotSame(first, second);
		assertNotSame(second, third);
		assertNotSame(third, first);
	}

	@Test
	public void forFullCoverageClassNeedsToBeInitializedOnce() {
		Validations v = new Validations();
		assertNotNull(v);
	}

	@Test
	public void worksIfSizeOfInputListIsAtLeastTheNumOfIterations() {
		Validations.nFoldCrossValidation(2, builder, evaluator, createListWithSize(2));
		Validations.nFoldCrossValidation(2, builder, evaluator, createListWithSize(3));

		Validations.sizedNFoldCrossValidation(2, builder, evaluator, createListWithSize(2), 100);
		Validations.sizedNFoldCrossValidation(2, builder, evaluator, createListWithSize(3), 100);
	}

	@Test(expected = AssertionException.class)
	public void sizeOfInputListMustBeAtLeastTheNumOfIterations() {
		Validations.nFoldCrossValidation(3, builder, evaluator, createListWithSize(2));
	}

	@Test(expected = AssertionException.class)
	public void min2FoldAreNeededForCrossFold() {
		Validations.nFoldCrossValidation(1, builder, evaluator, createListWithSize(2));
	}

	@Test(expected = AssertionException.class)
	public void min2FoldAreNeededForSizedCrossFold() {
		Validations.sizedNFoldCrossValidation(1, builder, evaluator, createListWithSize(2), 100);
	}

	@Test(expected = AssertionException.class)
	public void sizeOfSizedInputListMustBeAtLeastTheNumOfIterations() {
		Validations.sizedNFoldCrossValidation(3, builder, evaluator, createListWithSize(2), 100);
	}

	@Test(expected = AssertionException.class)
	public void sizeMustBePositiv() {
		Validations.sizedNFoldCrossValidation(3, builder, evaluator, createListWithSize(2), 0);
	}

	@Test
	@Ignore
	public void someTestsForNFoldValidation() {
		fail();
	}

	private static List<String> anyList() {
		return anyListOf(String.class);
	}

	private static ICallsRecommender<String> anyRecommender() {
		return any(ICallsRecommender.class);
	}

	private static List<String> createListWithSize(int size) {
		List<String> list = newArrayList();
		for (int i = 0; i < size; i++) {
			list.add("i" + i);
		}
		return list;
	}

	private List<String> anyListOfSize(final int expectedSize) {
		return argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object list) {
				return ((List<?>) list).size() == expectedSize;
			}
		});
	}

	private List<String> matchAnyListAndStore(final List<List<String>> store) {
		return argThat(new ArgumentMatcher<List<String>>() {
			@Override
			public boolean matches(Object argument) {
				List<String> list = (List<String>) argument;
				store.add(list);
				return true;
			}
		});
	}
}