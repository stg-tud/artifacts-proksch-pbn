package cc.recommenders.evaluation.optimization.raster;

import static cc.recommenders.evaluation.optimization.Vector.v;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.evaluation.optimization.CandidateSelector;
import cc.recommenders.evaluation.optimization.EvaluationOptions;
import cc.recommenders.evaluation.optimization.EvaluationOptionsSearcher;
import cc.recommenders.evaluation.optimization.OptimizationOptions;
import cc.recommenders.evaluation.optimization.ScoreCalculator;
import cc.recommenders.evaluation.optimization.Vector;
import cc.recommenders.io.Logger;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class RasterSearch extends EvaluationOptionsSearcher {

	private CandidateSelector candidateSelector;
	private ScoreCalculator scoreCalculator;

	private Vector initialStepSize;
	private int maxNumIterations;
	private double bestScoreOfLastIteration;
	private double threshold;
	private List<Usage> usages;

	private int numIterations = 0;
	private Vector currentStepSize;

	private EvaluationOptions bestCandidate;
	private Double bestScore;
	private Double secondBestScore;

	private Map<EvaluationOptions, Double> scores;
	private Map<EvaluationOptions, Double> newScores;
	private Set<EvaluationOptions> candidates;

	@Inject
	public RasterSearch(CandidateSelector candidateSelector, ScoreCalculator scoreCalculator) {
		this.candidateSelector = candidateSelector;
		this.scoreCalculator = scoreCalculator;
	}

	@Override
	public EvaluationOptions findOptimalOptions(List<Usage> usages, OptimizationOptions options,
			Set<EvaluationOptions> startValues) {

		Asserts.assertFalse(startValues.isEmpty());
		Asserts.assertFalse(usages.isEmpty());

		reinit(usages, options);

		Set<EvaluationOptions> bestFromStartValues = Sets.newHashSet();

		for (EvaluationOptions startValue : startValues) {
			bestFromStartValues.add(findOptimalOptions(startValue));
		}

		EvaluationOptions best = bestFrom(bestFromStartValues);
		Asserts.assertNotNull(best);

		Logger.log("Optimum: %s --> %2.4f\n", best, scores.get(best));

		return best;
	}

	private void reinit(List<Usage> usages, OptimizationOptions options) {
		this.usages = usages;
		this.threshold = options.convergenceThreshold;
		this.maxNumIterations = options.maxIterations;
		// TODO get rid of Vector.v(...) style
		initialStepSize = v(options.stepSizeClassContext, options.stepSizeMethodContext, options.stepSizeDefinition,
				options.stepSizeParameterSites, options.stepSizeMinProbability);

		scores = new HashMap<EvaluationOptions, Double>();
		newScores = new HashMap<EvaluationOptions, Double>();
		candidates = new HashSet<EvaluationOptions>();

		bestScore = 0.0;
		secondBestScore = 0.0;
		bestCandidate = null;
	}

	private EvaluationOptions bestFrom(Set<EvaluationOptions> candidates) {
		EvaluationOptions best = null;
		double bestScore = 0.0;

		for (EvaluationOptions candidate : candidates) {
			Double score = scores.get(candidate);
			if (score == null) {
				continue;
			}
			if (score > bestScore) {
				best = candidate;
				bestScore = score;
			}
		}

		return best;
	}

	private EvaluationOptions findOptimalOptions(EvaluationOptions options) {
		this.currentStepSize = initialStepSize;
		bestCandidate = options;
		bestScore = scoreCalculator.eval(options, usages);
		scores.put(bestCandidate, bestScore);
		secondBestScore = 0.0;
		numIterations = 0;

		if (bestScore.equals(Double.NaN)) {
			return null;
		}

		do {
			Logger.log("Iteration #%d -- based on %s\n", numIterations, options);
			bestScoreOfLastIteration = bestScore;
			// Vector oldStepSize = currentStepSize;
			// currentStepSize = adaptStepSize();
			// Logger.log("Adapting step size from %s to %s\n", oldStepSize,
			// currentStepSize);
			candidates = candidateSelector.selectNextCandidates(currentStepSize, bestCandidate);
			processNewCandidates(candidates);
			Logger.log("Best score is now %f (+%f)\n", bestScore, scoreDiff());
			numIterations++;
		} while (shouldRunAgain());

		return bestCandidate;
	}

	private boolean shouldRunAgain() {
		boolean isIterationLeft = numIterations < maxNumIterations;
		boolean existNewScore = !newScores.isEmpty();
		boolean isConverged = scoreDiff() < threshold;
		boolean hasValidBestScore = !bestScore.equals(Double.NaN);
		return isIterationLeft && existNewScore && !isConverged && hasValidBestScore;
	}

	private void processNewCandidates(Set<EvaluationOptions> candidates) {
		newScores = new HashMap<EvaluationOptions, Double>();
		for (EvaluationOptions candidate : candidates) {
			if (!scores.containsKey(candidate)) {
				Double newScore = scoreCalculator.eval(candidate, usages);
				determineBestValues(candidate, newScore);
				scores.put(candidate, newScore);
				newScores.put(candidate, newScore);
			}
		}
	}

	// private Vector adaptStepSize() {
	// return mult(initialStepSize, (1.0 / (numIterations + 1)));
	// }

	private void determineBestValues(EvaluationOptions candidate, Double newScore) {
		if (newScore > bestScore) {
			secondBestScore = bestScore;
			bestScore = newScore;
			bestCandidate = candidate;
			Logger.log("Improved score!\n", secondBestScore, bestScore, candidate);
		}
	}

	private double scoreDiff() {
		return bestScore - bestScoreOfLastIteration;
	}
}