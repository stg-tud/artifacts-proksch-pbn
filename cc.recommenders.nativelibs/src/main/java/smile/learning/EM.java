package smile.learning;

import smile.Network;
import smile.Wrapper;

public class EM extends Wrapper
{
	public native void learn(DataSet data, Network net, DataMatch[] matching, int[] fixedNodes);
	public native void learn(DataSet data, Network net, DataMatch[] matching, String[] fixedNodes);
	
	public void learn(DataSet data, Network net, DataMatch[] matching) { learn(data, net, matching, (int [])null); }
		
	public native void setEqSampleSize(int size);
	public native int getEqSampleSize();
	
	public native void setRandomizeParameters(boolean value);
	public native boolean getRandomizeParameters();

	public native void setRelevance(boolean value);
	public native boolean getRelevance();

	protected native long createNative(Object param);
	protected native void deleteNative(long nativePtr);
}
