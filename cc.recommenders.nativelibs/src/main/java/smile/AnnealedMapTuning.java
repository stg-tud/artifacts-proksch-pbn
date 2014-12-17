// AnnealedMapTuning.java

package smile;

// optional parameters for fine-tuning the annealed map
// pass null as 2nd param to Network.AnnealedMap to use the defaults

public class AnnealedMapTuning {
	double speed; // Annealing speed
	double Tmin; // Mininum temperature
	double Tinit; // Initial temperature
	double kReheat; // RFC coefficient
	int kMAP; // Number of best solutions we want
	double kRFC; // coefficient for RFC
	int numCycle; // Number of iterations per cycle;
	int iReheatSteps; // Number of no-improvement iterations before reheating
	int iStopSteps; // Number of no-improvement iterations before stopping
	int randSeed; // pass non-zero to ensure repeatability for given input
}
