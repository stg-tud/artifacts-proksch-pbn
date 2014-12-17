package smile;

public class UnrollResults {
	public UnrollResults(Network unrolled, int[] mapping) {
		this.unrolled = unrolled;
		this.mapping = mapping;
	}

	public Network unrolled;
	public int[] mapping;
}
