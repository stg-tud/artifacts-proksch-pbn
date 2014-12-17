package cc.recommenders.evaluation.optimization.raster;

import static cc.recommenders.evaluation.optimization.Vector.sub;
import static com.google.common.collect.Maps.newHashMap;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.recommenders.evaluation.optimization.Vector;

public class Rasterizer {
	private static final double RELEVANCE_THRESHOLD = 0.0001;

	public Set<Vector> rasterize(Vector v1, Vector v2) {
		Set<Vector> opts = new HashSet<Vector>();
		Map<Integer, Integer> bitToIdMap = determineBitToIdMap(v1, v2);
		int numBits = bitToIdMap.size();
		int numPermutations = (int) Math.ceil(Math.pow(2.0, numBits));
		for (int i = 0; i < numPermutations; i++) {
			String bitString = padLeft(Integer.toBinaryString(i), numBits);
			opts.add(buildFromVector(bitString, bitToIdMap, v1, v2));
		}
		return opts;
	}

	private Map<Integer, Integer> determineBitToIdMap(Vector v1, Vector v2) {
		Vector diff = sub(v2, v1);
		
		Map<Integer, Integer> bitToIdMap = newHashMap();
		int bitPos = 0;
		for (int i = 0 ; i < Vector.NUM_OPTS; i++) {
			if (Math.abs(diff.get(i)) >= RELEVANCE_THRESHOLD) {
				bitToIdMap.put(bitPos, i);
				bitPos++;
			}
		}
		return bitToIdMap;
	}

	public Set<Vector> rasterize(Vector v1,
			Vector v2, int steps) {
		
		double scale = 1.0 / (steps + 1);
		
		List<Vector> subVects = new LinkedList<Vector>();
		subVects.add(v1);
		for (int i = 0; i < steps; i++) {
			subVects.add(buildSubVect(v1, v2, scale * (i+1)));
		}
		subVects.add(v2);
		
		return rasterize(subVects);
	}

	public Set<Vector> rasterize(
			List<Vector> vectorSet) {
		Set<Vector> result = new HashSet<Vector>();
		for (int i = 0; i < vectorSet.size(); i++) {
			for (int j = i + 1; j < vectorSet.size(); j++) {
				result.addAll(rasterize(vectorSet.get(i), vectorSet.get(j)));
			}
		}
		return result;
	}

	private Vector buildSubVect(Vector v1,
			Vector v2, double scale) {
		Vector diff = Vector.sub(v2, v1);
		Vector scaled = Vector.mult(diff,
				scale);
		return Vector.add(v1, scaled);
	}

	private Vector buildFromVector(String bitString, Map<Integer, Integer> bitToIdMap, Vector v1, Vector v2) {
		Vector target = Vector.copy(v1);

		for (int i = 0; i < bitString.length(); i++) {
			int id = bitToIdMap.get(i);
			if (bitString.charAt(i) == '0') {
				target.set(id, v1.get(id));
			} else {
				target.set(id, v2.get(id));
			}
		}

		return target;
	}

	private String padLeft(String s, int numDigits) {
		StringBuffer sb = new StringBuffer(s);
		int numZeros = numDigits - s.length();
		while (numZeros-- > 0) {
			sb.insert(0, "0");
		}
		return sb.toString();
	}

	public Set<Vector> rasterize(Vector... vectors) {
		return rasterize(Arrays.asList(vectors));
	}
}
