package cc.recommenders.evaluation.optimization;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;

public class EvaluationOptions {
	public MiningOptions miningOptions;
	public QueryOptions queryOptions;

	public EvaluationOptions(MiningOptions miningOptions, QueryOptions queryOptions) {
		this.miningOptions = miningOptions;
		this.queryOptions = queryOptions;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}

	@Override
	public String toString() {
		return String.format("%s%s", miningOptions, queryOptions);
	}

	public static EvaluationOptions copy(EvaluationOptions other) {
		MiningOptions mOpts = new MiningOptions().setFrom(other.miningOptions);
		QueryOptions qOpts = new QueryOptions().setFrom(other.queryOptions);
		return new EvaluationOptions(mOpts, qOpts);
	}
}