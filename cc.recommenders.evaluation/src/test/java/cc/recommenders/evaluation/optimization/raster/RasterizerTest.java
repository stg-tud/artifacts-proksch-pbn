package cc.recommenders.evaluation.optimization.raster;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;
import static cc.recommenders.evaluation.optimization.Vector.v;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.optimization.Vector;

public class RasterizerTest {
	private Rasterizer uut;

	@Before
	public void before() {
		uut = new Rasterizer();
	}

	@Test
	public void testContainsTwoDimWeightCombinations() {
		Vector e1 = v(0.2, 0.3);
		Vector e2 = v(0.5, 0.7);
		Set<Vector> combined = uut.rasterize(e1, e2);
		assertThat(combined,
				hasItems(v(0.2, 0.3), v(0.2, 0.7), v(0.5, 0.3), v(0.5, 0.7)));
	}

	@Test
	public void testContainsTwoDimWeightCombinationsOneInnerStep() {
		Vector e1 = v(0.2, 0.3);
		Vector e2 = v(0.5, 0.7);
		Set<Vector> combined = uut.rasterize(e1, e2, 1);
		assertThat(
				combined,
				hasItems(v(0.2, 0.3), v(0.2, 0.7), v(0.5, 0.3), v(0.5, 0.7),
						v(0.35, 0.3), v(0.35, 0.7), v(0.2, 0.5), v(0.5, 0.5),
						v(0.35, 0.5)));
	}

	@Test
	public void testRasterizeMultiple() {
		Vector e1 = v(0.2, 0.3);
		Vector e2 = v(0.5, 0.7);
		Vector e3 = v(0.35, 0.5);
		Set<Vector> combined = uut.rasterize(e1, e2, e3);
		assertThat(
				combined,
				hasItems(v(0.2, 0.3), v(0.2, 0.7), v(0.5, 0.3), v(0.5, 0.7),
						v(0.35, 0.3), v(0.35, 0.7), v(0.2, 0.5), v(0.5, 0.5),
						v(0.35, 0.5)));
	}

	@Test
	public void testContainsThreeDimWeightCombinations() {
		Vector e1 = v(0.2, 0.3, 0.4);
		Vector e2 = v(0.5, 0.7, 0.9);
		Set<Vector> combined = uut.rasterize(e1, e2);
		assertThat(combined, hasItems(v(0.2, 0.3, 0.4), // 000
				v(0.2, 0.3, 0.9), // 001
				v(0.2, 0.7, 0.4), // 010
				v(0.2, 0.7, 0.9), // 011
				v(0.5, 0.3, 0.4), // 100
				v(0.5, 0.3, 0.9), // 101
				v(0.5, 0.7, 0.4), // 110
				v(0.5, 0.7, 0.9) // 111
				));
	}
}
