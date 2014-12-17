package cc.recommenders.mining.calls;

import java.util.Set;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.datastructures.Tuple;
import cc.recommenders.names.IMethodName;

public class AbstractCallsRecommender<T> implements ICallsRecommender<T> {

	@Override
	public Set<Tuple<IMethodName, Double>> query(T query) {
		Asserts.fail("not implemented yet");
		return null;
	}

	@Override
	public Set<Tuple<String, Double>> getPatternsWithProbability() {
		Asserts.fail("not implemented yet");
		return null;
	}

	@Override
	public Set<Tuple<IMethodName, Double>> queryPattern(String patternName) {
		Asserts.fail("not implemented yet");
		return null;
	}

	@Override
	public int getSize() {
		Asserts.fail("not implemented yet");
		return -1;
	}
}