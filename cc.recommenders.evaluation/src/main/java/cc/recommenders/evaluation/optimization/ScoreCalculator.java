package cc.recommenders.evaluation.optimization;

import java.util.List;

import cc.recommenders.usages.Usage;

public interface ScoreCalculator {
	public double eval(EvaluationOptions options, List<Usage> usages);
}
