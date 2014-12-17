package cc.recommenders.evaluation.optimization;

import java.util.Collection;
import java.util.Set;

public interface CandidateSelector {

	public abstract Set<EvaluationOptions> selectNextCandidates(
			Vector stepSize, EvaluationOptions... evaluationOptions);
	public abstract Set<EvaluationOptions> selectNextCandidates(
			Vector stepSize, Collection<EvaluationOptions> evaluationOptions);
}