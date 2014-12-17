// DiagResults.java

package smile;

public class DiagResults {
	public DiagResults(ObservationInfo[] observations, FaultInfo[] faults) {
		this.observations = observations;
		this.faults = faults;
	}

	public ObservationInfo[] observations;
	public FaultInfo[] faults;
}
