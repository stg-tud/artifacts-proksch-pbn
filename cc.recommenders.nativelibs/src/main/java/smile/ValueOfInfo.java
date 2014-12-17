// ValueOfInfo.java

package smile;

public class ValueOfInfo extends Wrapper {
	public ValueOfInfo(Network net) {
		super(net);
		this.net = net;
	}

	public native void update();

	public native void addNode(int nodeHandle);

	public native void addNode(String nodeId);

	public native void removeNode(int nodeHandle);

	public native void removeNode(String nodeId);

	public native int[] getAllNodes();

	public native String[] getAllNodeIds();

	public native void setDecision(int nodeHandle);

	public native void setDecision(String nodeId);

	public native int getDecision();

	public native String getDecisionId();

	public native int[] getAllDecisions();

	public native String[] getAllDecisionIds();

	public native int[] getAllActions();

	public native String[] getAllActionIds();

	public native void setPointOfView(int nodeHandle);

	public native void setPointOfView(String nodeId);

	public native int getPointOfView();

	public native String getPointOfViewId();

	public native int[] getIndexingNodes();

	public native String[] getIndexingNodeIds();

	public native double[] getValues();

	protected native long createNative(Object param);

	protected native void deleteNative(long nativePtr);

	private Network net;
}
