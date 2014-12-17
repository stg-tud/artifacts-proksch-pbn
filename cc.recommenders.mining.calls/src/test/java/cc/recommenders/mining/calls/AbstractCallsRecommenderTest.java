package cc.recommenders.mining.calls;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;

public class AbstractCallsRecommenderTest {

	private AbstractCallsRecommender sut;

	@Before
	public void setup() {
		sut = new AbstractCallsRecommender();
	}

	@Test(expected = AssertionException.class)
	public void queryingFails() {
		sut.query(null);
	}

	@Test(expected = AssertionException.class)
	public void gettingSizeFails() {
		sut.getSize();
	}

	@Test(expected = AssertionException.class)
	public void queryingPatternsFails() {
		sut.getPatternsWithProbability();
	}

	@Test(expected = AssertionException.class)
	public void queryingSpecificPatternFails() {
		sut.queryPattern(null);
	}
}