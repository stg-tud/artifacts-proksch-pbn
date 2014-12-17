package cc.recommenders.evaluation.optimization;

import static cc.recommenders.evaluation.optimization.Vector.CANOPY_T1_ID;
import static cc.recommenders.evaluation.optimization.Vector.CANOPY_T2_ID;
import static cc.recommenders.evaluation.optimization.Vector.KMEANS_CLUSTER_ID;
import static cc.recommenders.evaluation.optimization.Vector.KMEANS_CONVERSIONS_THRESHOLD_ID;
import static cc.recommenders.evaluation.optimization.Vector.KMEANS_ITERATIONS_ID;
import static cc.recommenders.evaluation.optimization.Vector.MIN_PROBABILITY_ID;
import static cc.recommenders.evaluation.optimization.Vector.WEIGHT_CLASS_CONTEXT_ID;
import static cc.recommenders.evaluation.optimization.Vector.WEIGHT_DEFINITION_ID;
import static cc.recommenders.evaluation.optimization.Vector.WEIGHT_METHOD_CONTEXT_ID;
import static cc.recommenders.evaluation.optimization.Vector.WEIGHT_PARAMETER_SITES_ID;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.utils.LocaleUtils;


public class VectorTest {
	private static final double WEIGHT_CLASS_CONTEXT = 0.1;
	private static final double WEIGHT_METHOD_CONTEXT = 0.2;
	private static final double WEIGHT_DEFINITION = 0.3;
	private static final double WEIGHT_PARAMETER_SITES = 0.4;
	private static final double MIN_PROBABILITY = 0.5;
	private static final double CANOPY_T1 = 4.0;
	private static final double CANOPY_T2 = 2.0;
	private static final double KMEANS_CLUSTER = 20.0;
	private static final double KMEANS_THRESHOLD = 0.2;
	private static final double KMEANS_ITERATIONS = 5.0;

	@Before
	public void setup() {
		LocaleUtils.setDefaultLocale();
	}
	
	@Test
	public void testGetClassContext() {
		Vector vect = fixture();
		assertEquals(WEIGHT_CLASS_CONTEXT, vect.get(WEIGHT_CLASS_CONTEXT_ID),
				0.01);
	}

	@Test
	public void testGetMethodContext() {
		Vector vect = fixture();
		assertEquals(WEIGHT_METHOD_CONTEXT, vect.get(WEIGHT_METHOD_CONTEXT_ID),
				0.01);
	}

	@Test
	public void testGetDefinition() {
		Vector vect = fixture();
		assertEquals(WEIGHT_DEFINITION, vect.get(WEIGHT_DEFINITION_ID), 0.01);
	}

	@Test
	public void testGetParameterSites() {
		Vector vect = fixture();
		assertEquals(WEIGHT_PARAMETER_SITES,
				vect.get(WEIGHT_PARAMETER_SITES_ID), 0.01);
	}

	@Test
	public void testGetMinProbability() {
		Vector vect = fixture();
		assertEquals(MIN_PROBABILITY, vect.get(MIN_PROBABILITY_ID), 0.01);
	}
	
	@Test
	public void testGetT1() {
		Vector vect = fixture();
		assertEquals(CANOPY_T1, vect.get(CANOPY_T1_ID), 0.01);
	}
	
	@Test
	public void testGetT2() {
		Vector vect = fixture();
		assertEquals(CANOPY_T2, vect.get(CANOPY_T2_ID), 0.01);
	}
	
	@Test
	public void testGetKmeansClusters() {
		Vector vect = fixture();
		assertEquals(KMEANS_CLUSTER, vect.get(KMEANS_CLUSTER_ID), 0.01);
	}
	
	@Test
	public void testGetKmeansThreshold() {
		Vector vect = fixture();
		assertEquals(KMEANS_THRESHOLD, vect.get(KMEANS_CONVERSIONS_THRESHOLD_ID), 0.01);
	}
	
	@Test
	public void testGetKmeansIterations() {
		Vector vect = fixture();
		assertEquals(KMEANS_ITERATIONS, vect.get(KMEANS_ITERATIONS_ID), 0.01);
	}

	@Test
	public void testSetClassContext() {
		Vector vect = build();
		vect.set(WEIGHT_CLASS_CONTEXT_ID, WEIGHT_CLASS_CONTEXT);
		assertEquals(WEIGHT_CLASS_CONTEXT,
				vect.getEvaluationOptions().miningOptions
						.getWeightClassContext(), 0.01);
	}

	@Test
	public void testSetMethodContext() {
		Vector vect = build();
		vect.set(WEIGHT_METHOD_CONTEXT_ID, WEIGHT_METHOD_CONTEXT);
		assertEquals(WEIGHT_METHOD_CONTEXT,
				vect.getEvaluationOptions().miningOptions
						.getWeightMethodContext(), 0.01);
	}

	@Test
	public void testSetDefinition() {
		Vector vect = build();
		vect.set(WEIGHT_DEFINITION_ID, WEIGHT_DEFINITION);
		assertEquals(
				WEIGHT_DEFINITION,
				vect.getEvaluationOptions().miningOptions.getWeightDefinition(),
				0.01);
	}

	@Test
	public void testSetParameterSites() {
		Vector vect = build();
		vect.set(WEIGHT_PARAMETER_SITES_ID, WEIGHT_PARAMETER_SITES);
		assertEquals(WEIGHT_PARAMETER_SITES,
				vect.getEvaluationOptions().miningOptions
						.getWeightParameterSites(), 0.01);
	}

	@Test
	public void testSetMinProbability() {
		Vector vect = build();
		vect.set(MIN_PROBABILITY_ID, MIN_PROBABILITY);
		assertEquals(MIN_PROBABILITY,
				vect.getEvaluationOptions().queryOptions.minProbability, 0.01);
	}
	
	@Test
	public void testSetT1() {
		Vector vect = build();
		vect.set(CANOPY_T1_ID, CANOPY_T1);
		assertEquals(CANOPY_T1,
				vect.getEvaluationOptions().miningOptions.getT1(), 0.01);
	}
	
	@Test
	public void testSetT2() {
		Vector vect = build();
		vect.set(CANOPY_T2_ID, CANOPY_T2);
		assertEquals(CANOPY_T2,
				vect.getEvaluationOptions().miningOptions.getT2(), 0.01);
	}
	
	@Test
	public void testSetKmeansCluster() {
		Vector vect = build();
		vect.set(KMEANS_CLUSTER_ID, KMEANS_CLUSTER);
		assertEquals(KMEANS_CLUSTER,
				vect.getEvaluationOptions().miningOptions.getClusterCount(), 0.01);
	}
	
	@Test
	public void testSetKmeansThreshold() {
		Vector vect = build();
		vect.set(KMEANS_CONVERSIONS_THRESHOLD_ID, KMEANS_THRESHOLD);
		assertEquals(KMEANS_THRESHOLD,
				vect.getEvaluationOptions().miningOptions.getConvergenceThreshold(), 0.01);
	}
	
	@Test
	public void testSetKmeansIterations() {
		Vector vect = build();
		vect.set(KMEANS_ITERATIONS_ID, KMEANS_ITERATIONS);
		assertEquals(KMEANS_ITERATIONS,
				vect.getEvaluationOptions().miningOptions.getNumberOfIterations(), 0.01);
	}

	@Test
	public void testDoesNotChangesOthers() {
		Vector vect = fixture();
		vect.set(WEIGHT_CLASS_CONTEXT_ID, 0.88);
		assertEquals(WEIGHT_PARAMETER_SITES,
				vect.getEvaluationOptions().miningOptions
						.getWeightParameterSites(), 0.01);
	}

	@Test
	@Ignore
	public void testAddition() {
		Vector a = new Vector(0.1, 0.2, 0.3, 0.4, 0.5);
		Vector b = new Vector(0.8, 0.7, 0.6, 0.5, 0.4);
		Vector actual = Vector.add(a, b);
		Vector expected = new Vector(0.9, 0.9, 0.9, 0.9, 0.9);
		assertEquals(expected, actual);
	}
	
	@Test
	@Ignore
	public void testSubtraction() {
		Vector a = new Vector(0.9, 0.8, 0.7, 0.6, 0.5);
		Vector b = new Vector(0.1, 0.2, 0.3, 0.4, 0.5);
		Vector actual = Vector.sub(a, b);
		Vector expected = new Vector(0.8, 0.6, 0.4, 0.2, 0.0);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testScalarMult() {
		Vector a = new Vector(0.1, 0.2, 0.3, 0.4, 0.5);
		Vector b = new Vector(0.2, 0.4, 0.6, 0.8, 1.0);
		assertEquals(b, Vector.mult(a, 2.0));
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testFailFastSet() {
		Vector a = new Vector();
		a.set(1242, 42.0);
	}
	
	@Test(expected=IndexOutOfBoundsException.class)
	public void testFailFastGet() {
		Vector a = new Vector();
		a.get(1242);
	}
	
	@Test
	public void testToString() {
		Vector v = new Vector(0.1, 0.2, 0.3, 0.4, 0.5, 2.0, 1.0, 10.0, 0.3, 15.0);
		assertEquals("(0.1000, 0.2000, 0.3000, 0.4000, 0.5000, 2.0000, 1.0000, 10, 0.3000, 15)", v.toString());
	}

	private Vector build() {
		return new Vector(new EvaluationOptions(
				new MiningOptions(), new QueryOptions()));
	}

	private Vector fixture() {
		return new Vector(WEIGHT_CLASS_CONTEXT,
				WEIGHT_METHOD_CONTEXT, WEIGHT_DEFINITION,
				WEIGHT_PARAMETER_SITES, MIN_PROBABILITY,
				CANOPY_T1, CANOPY_T2,
				KMEANS_CLUSTER, KMEANS_THRESHOLD, KMEANS_ITERATIONS);
	}
}
