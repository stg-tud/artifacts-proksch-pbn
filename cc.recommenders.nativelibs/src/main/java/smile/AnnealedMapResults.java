// AnnealedMapTuning.java

package smile;

// output from annealed map
public class AnnealedMapResults {
	AnnealedMapResults(double probM1E, double probE, int[] mapStates) {
		this.probM1E = probM1E;
		this.probE = probE;
		this.mapStates = mapStates;
	}

	public double probM1E; // P(m|e)
	public double probE; // P(e)
	public int[] mapStates; // most likely states of specified map nodes
}
