package cc.recommenders.evaluation.optimization;

public class BoundsMatcher {

	public boolean matches(Vector v) {
		for (int id = 0; id < Vector.NUM_OPTS; id++) {
			if (!inBounds(id, v)) {
				return false;
			}
		}
		return true;
	}

	private boolean inBounds(int i, Vector v) {
		if (i < 5) {
			return isInRangeZeroOne(v.get(i));
		} else {
			return true;
		}
	}

	private boolean isInRangeZeroOne(Double val) {
		return val >= 0.0 && val <= 1.0;
	}
}
