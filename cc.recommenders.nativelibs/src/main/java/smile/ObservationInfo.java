// ObservationInfo.java

package smile;

public class ObservationInfo {
	public ObservationInfo(int node, double entropy, double cost,
			double infoGain) {
		this.node = node;
		this.entropy = entropy;
		this.cost = cost;
		this.infoGain = infoGain;
	}

	public int node;
	public double entropy;
	public double cost;
	public double infoGain;
}
