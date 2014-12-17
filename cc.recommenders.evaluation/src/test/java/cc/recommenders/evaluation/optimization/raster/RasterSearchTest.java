package cc.recommenders.evaluation.optimization.raster;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;
import static java.lang.Double.NaN;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import cc.recommenders.evaluation.optimization.CandidateSelector;
import cc.recommenders.evaluation.optimization.EvaluationOptions;
import cc.recommenders.evaluation.optimization.OptimizationOptions;
import cc.recommenders.evaluation.optimization.ScoreCalculator;
import cc.recommenders.evaluation.optimization.Vector;
import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

@Ignore
@RunWith(org.mockito.runners.MockitoJUnitRunner.class)
public class RasterSearchTest {

	@Mock
	private CandidateSelector candidateSelector;
	@Mock
	private ScoreCalculator scoreCalculator;
	@Mock
	private List<Usage> usages;

	private Map<String, EvaluationOptions> nameCandidateMap = newHashMap();
	private RasterSearch uut;

	@Before
	public void beforeEach() {
		uut = new RasterSearch(candidateSelector, scoreCalculator);
	}

	@Test
	public void testFindsWithBestScore() {
		mockCd("s1", 0.02);
		mockCd("a1", 0.04);
		mockCd("a2", 0.07);
		mockCd("a3", 0.03);
		mockCd("b1", 0.12);
		mockCd("b2", 0.13);
		mockCd("b3", 0.11);
		mockCds("s1", "a1", "a2", "a3");
		mockCds("a2", "b1", "b2", "b3");

		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1")));

		assertThat(result, is(equalTo(cd("b2"))));
	}

	@Test
	public void testUsesEachStartValue() {
		mockCd("s1", 0.02);
		mockCd("s2", 0.11);
		mockCd("a1", 0.04);
		mockCd("a2", 0.07);
		mockCd("a3", 0.03);
		mockCd("b1", 0.12);
		mockCd("b2", 0.14);
		mockCd("b3", 0.11);
		mockCds("s1", "a1", "a2", "a3");
		mockCds("s2", "b1", "b2", "b3");

		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1"), cd("s2")));

		assertThat(result, is(equalTo(cd("b2"))));
	}

	@Test
	public void testRespectsThreshold() {
		mockCd("s1", 0.02);
		mockCd("a1", 0.04);
		mockCd("a2", 0.07);
		mockCd("a3", 0.03);
		mockCd("b1", 0.12);
		mockCd("b2", 0.14);
		mockCd("b3", 0.11);
		mockCds("s1", "a1", "a2", "a3");
		mockCds("a2", "b1", "b2", "b3");

		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.1), newHashSet(cd("s1")));

		assertThat(result, is(equalTo(cd("a2"))));
	}

	@Test
	public void testStopsWhenNoProgress() {
		mockCd("s1", 0.03);
		mockCd("a1", 0.03);
		mockCd("b1", 0.03);
		mockCds("s1", "a1");
		mockCds("a1", "b1");

		uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1")));

		verify(candidateSelector).selectNextCandidates(any(Vector.class), eq(cd("s1")));
		verify(scoreCalculator).eval(cd("s1"), usages);
		verify(scoreCalculator).eval(cd("a1"), usages);
	}

	@Test
	public void testRespectsNumIterations() {
		mockCd("s1", 0.02);
		mockCd("a1", 0.04);
		mockCd("a2", 0.07);
		mockCd("b1", 0.14);
		mockCd("b2", 0.11);
		mockCd("c1", 0.15);
		mockCd("c2", 0.11);
		mockCds("s1", "a1", "a2");
		mockCds("a2", "b1", "b2");
		mockCds("b1", "c1", "c2");

		fail("uncomment the following line");
//		uut.setMaxNumIterations(2);
		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1")));

		assertThat(result, is(equalTo(cd("b1"))));
	}

	@Test
	public void testDoesNotCalculateScoreTwice() {
		mockCd("s1", 0.02);
		mockCd("a1", 0.04);
		mockCd("a2", 0.07);
		mockCd("b1", 0.14);
		mockCd("b2", 0.11, 0.99);
		mockCd("c1", 0.15);
		mockCd("c2", 0.11);
		mockCds("s1", "a1", "a2");
		mockCds("a2", "b1", "b2");
		mockCds("b1", "b2", "c1", "c2");

		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1")));

		assertThat(result, is(equalTo(cd("c1"))));
	}

	@Test
	public void testIgnoresMessedUpScores() {
		mockCd("s1", 0.02);
		mockCd("a1", NaN);
		mockCd("a2", 0.73);
		mockCd("a3", NaN);
		mockCds("s1", "a1", "a2", "a3");

		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1")));

		assertThat(result, is(equalTo(cd("a2"))));
	}

	@Test
	public void testHandlesMessyStartValuesWithoutError() {
		mockCd("s1", 0.02);
		mockCd("s2", NaN);
		mockCd("s3", 0.03);

		EvaluationOptions result = uut.findOptimalOptions(usages, createOptions(0.0),
				newHashSet(cd("s1"), cd("s2"), cd("s3")));

		assertThat(result, is(equalTo(cd("s3"))));
	}

	@Test(expected = AssertionException.class)
	public void testReportsFailure() {
		mockCd("s1", NaN);
		mockCd("s2", NaN);
		uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(cd("s1"), cd("s2")));
	}

	@Test(expected = AssertionException.class)
	public void testComplainAboutMissingStartValues() {
		Set<EvaluationOptions> startValues = newHashSet();
		uut.findOptimalOptions(usages, createOptions(0.0), startValues);
	}

	@Test(expected = AssertionException.class)
	public void testComplainMissingUsages() {
		List<Usage> usages = newArrayList();
		uut.findOptimalOptions(usages, createOptions(0.0), newHashSet(mockCd("s1", 0.02)));
	}

	private EvaluationOptions cd(String name) {
		return nameCandidateMap.get(name);
	}

	private void mockCds(String candidateName, String... candidateNames) {
		EvaluationOptions candidate = nameCandidateMap.get(candidateName);

		when(candidateSelector.selectNextCandidates(any(Vector.class), eq(candidate))).thenReturn(cds(candidateNames));
	}

	private Set<EvaluationOptions> cds(String... candidateNames) {
		Set<EvaluationOptions> candidates = Sets.newHashSet();
		for (String name : candidateNames) {
			candidates.add(nameCandidateMap.get(name));
		}
		return candidates;
	}

	private EvaluationOptions mockCd(String name, double score) {
		EvaluationOptions candidate = mock(EvaluationOptions.class, name);
		nameCandidateMap.put(name, candidate);
		when(scoreCalculator.eval(candidate, usages)).thenReturn(score);
		return candidate;
	}

	private EvaluationOptions mockCd(String name, double score1, double score2) {
		EvaluationOptions candidate = mock(EvaluationOptions.class, name);
		nameCandidateMap.put(name, candidate);
		when(scoreCalculator.eval(candidate, usages)).thenReturn(score1, score2);
		return candidate;
	}

	private OptimizationOptions createOptions(double d) {
		return OptimizationOptions.newBuilder().convergence(d).build();
	}
}
