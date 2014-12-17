package cc.recommenders.evaluation.optimization;

import static cc.recommenders.evaluation.optimization.Vector.v;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class BoundsMatcherTest {
	private BoundsMatcher uut;
	
	@Before
	public void beforeEach() {
		uut = new BoundsMatcher();
	}
	
	@Test
	public void testMatchesInBounds() {
		assertThat(uut.matches(v(0.1, 0.1, 0.1, 0.1, 0.1)), is(true));
	}
	
	@Test
	public void testDoesNotMatchOutOfLowerBounds() {
		assertThat(uut.matches(v(0.1, 0.1, -0.01, 0.1, 0.1)), is(false));
	}
	
	@Test
	public void testDoesNotMatchOutOfUpperBounds() {
		assertThat(uut.matches(v(0.1, 0.1, 0.1, 1.1, 0.1)), is(false));
	}
	
	@Test
	public void testDoesAllowDifferentValuesForT1() {
		assertThat(uut.matches(v(0.1, 0.1, 0.1, 0.1, 0.1, 2.0)), is(true));
	}
	
	@Test
	public void testDoesAllowDifferentValuesForT2() {
		assertThat(uut.matches(v(0.1, 0.1, 0.1, 0.1, 0.1, 2.0, 1.2)), is(true));
	}
}
