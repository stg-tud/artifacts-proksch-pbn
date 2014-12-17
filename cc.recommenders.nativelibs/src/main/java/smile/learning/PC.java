package smile.learning;

import smile.Wrapper;

public class PC extends Wrapper
{
	public native Pattern learn(DataSet data);

	public native void setMaxAdjacency(int adjacency);
	public native int getMaxAdjacency();
	
	public native void setSignificance(double sig);
    public native double getSignificance();
		
	public native BkKnowledge getBkKnowledge();
	public native void setBkKnowledge(BkKnowledge bkk);

	protected native long createNative(Object param);
	protected native void deleteNative(long nativePtr);
}
