package cc.recommenders.evaluation.optimization;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;

public class Vector {
	public static final int WEIGHT_CLASS_CONTEXT_ID = 0;
	public static final int WEIGHT_METHOD_CONTEXT_ID = 1;
	public static final int WEIGHT_DEFINITION_ID = 2;
	public static final int WEIGHT_PARAMETER_SITES_ID = 3;
	public static final int MIN_PROBABILITY_ID = 4;
	
	public static final int CANOPY_T1_ID = 5;
	public static final int CANOPY_T2_ID = 6;
	
	public static final int KMEANS_CLUSTER_ID = 7;
	public static final int KMEANS_CONVERSIONS_THRESHOLD_ID = 8;
	public static final int KMEANS_ITERATIONS_ID = 9;
	
	public static final int NUM_OPTS = 10;

	private EvaluationOptions eOpts;

	public Vector() {
		MiningOptions mOpts = new MiningOptions();
		QueryOptions qOpts = new QueryOptions();
		eOpts = new EvaluationOptions(mOpts, qOpts);
		for (int i = 0; i < NUM_OPTS; i++) {
			set(i, 0.0);
		}
	}

	public Vector(EvaluationOptions eOpts) {
		this.eOpts = eOpts;
	}

	public Vector(double... values) {
		this();
		for (int i = 0; i < values.length; i++) {
			set(i, values[i]);
		}
	}

	public Double get(int i) {
		switch (i) {
		case WEIGHT_CLASS_CONTEXT_ID:
			return eOpts.miningOptions.getWeightClassContext();
		case WEIGHT_METHOD_CONTEXT_ID:
			return eOpts.miningOptions.getWeightMethodContext();
		case WEIGHT_DEFINITION_ID:
			return eOpts.miningOptions.getWeightDefinition();
		case WEIGHT_PARAMETER_SITES_ID:
			return eOpts.miningOptions.getWeightParameterSites();
		case MIN_PROBABILITY_ID:
			return eOpts.queryOptions.minProbability;
		case CANOPY_T1_ID:
			return eOpts.miningOptions.getT1();
		case CANOPY_T2_ID:
			return eOpts.miningOptions.getT2();
		case KMEANS_CLUSTER_ID:
			return (double) eOpts.miningOptions.getClusterCount();
		case KMEANS_CONVERSIONS_THRESHOLD_ID:
			return eOpts.miningOptions.getConvergenceThreshold();
		case KMEANS_ITERATIONS_ID:
			return (double) eOpts.miningOptions.getNumberOfIterations();
		}
		throw new IndexOutOfBoundsException(String.format(
				"%d is not a valid index for an EvalutionOptionsVector", i));
	}

	public EvaluationOptions getEvaluationOptions() {
		return eOpts;
	}

	public Vector set(int i, double val) {
		switch (i) {
		case WEIGHT_CLASS_CONTEXT_ID:
			eOpts.miningOptions.setWeightClassContext(val);
			break;

		case WEIGHT_DEFINITION_ID:
			eOpts.miningOptions.setWeightDefinition(val);
			break;

		case WEIGHT_METHOD_CONTEXT_ID:
			eOpts.miningOptions.setWeightMethodContext(val);
			break;

		case WEIGHT_PARAMETER_SITES_ID:
			eOpts.miningOptions.setWeightParameterSites(val);
			break;

		case MIN_PROBABILITY_ID:
			eOpts.queryOptions.minProbability = val;
			break;
			
		case CANOPY_T1_ID:
			eOpts.miningOptions.setT1(val);
			break;
			
		case CANOPY_T2_ID:
			eOpts.miningOptions.setT2(val);
			break;
			
		case KMEANS_CLUSTER_ID:
			eOpts.miningOptions.setClusterCount((int) Math.round(val));
			break;
			
		case KMEANS_CONVERSIONS_THRESHOLD_ID:
			eOpts.miningOptions.setConvergenceThreshold(val);
			break;
			
		case KMEANS_ITERATIONS_ID:
			eOpts.miningOptions.setNumberOfIterations((int) Math.round(val));
			break;

		default:
			throw new IndexOutOfBoundsException(String.format(
					"%d is not a valid index for an EvalutionOptionsVector", i));
		}
		return this;
	}

	public static Vector add(Vector a,
			Vector b) {
		Vector c = copy(a);
		return c.add(b);
	}

	public static Vector sub(Vector a,
			Vector b) {
		Vector c = copy(a);
		return c.sub(b);
	}

	public static Vector mult(Vector a,
			double scalar) {
		Vector c = copy(a);
		return c.mult(scalar);
	}

	private Vector add(Vector b) {
		for (int i = 0; i < NUM_OPTS; i++) {
			this.set(i, get(i) + b.get(i));
		}
		return this;
	}

	private Vector sub(Vector b) {
		for (int i = 0; i < NUM_OPTS; i++) {
			this.set(i, get(i) - b.get(i));
		}
		return this;
	}

	private Vector mult(double b) {
		for (int i = 0; i < NUM_OPTS; i++) {
			this.set(i, get(i) * b);
		}
		return this;
	}

	public static Vector copy(Vector a) {
		return new Vector(EvaluationOptions.copy(a
				.getEvaluationOptions()));
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Vector)) {
			return false;
		}
		Vector o = (Vector) other;
		return getEvaluationOptions().equals(o.getEvaluationOptions());
	}

	public int hashCode() {
		return getEvaluationOptions().hashCode();
	}
	
	public String toString() {
		return format("(%.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %.4f, %d, %.4f, %d)",
				get(WEIGHT_CLASS_CONTEXT_ID),
				get(WEIGHT_METHOD_CONTEXT_ID),
				get(WEIGHT_DEFINITION_ID),
				get(WEIGHT_PARAMETER_SITES_ID),
				get(MIN_PROBABILITY_ID),
				get(CANOPY_T1_ID), 
				get(CANOPY_T2_ID),
				Math.round(get(KMEANS_CLUSTER_ID)),
				get(KMEANS_CONVERSIONS_THRESHOLD_ID),
				Math.round(get(KMEANS_ITERATIONS_ID)));
	}

	public static Vector v(double... values) {
		return new Vector(values);
	}

	public static Vector v(EvaluationOptions eOpts) {
		return new Vector(eOpts);
	}

	public static Set<EvaluationOptions> extract(
			Collection<Vector> vectors) {
		Set<EvaluationOptions> ret = new HashSet<EvaluationOptions>();
		for (Vector vect : vectors) {
			ret.add(vect.getEvaluationOptions());
		}
		return ret;
	}
	
	public static Set<EvaluationOptions> extract(Vector... vectors) {
		return extract(Arrays.asList(vectors));
	}
	
	public static EvaluationOptions extract(Vector v) {
		return v.getEvaluationOptions();
	}
}
