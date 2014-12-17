package smile.learning;

import smile.Network;
import smile.Wrapper;

public class GreedyThickThinning extends Wrapper
{
    public class PriorsType 
	{
		public final static int K2 = 0;
		public final static int BDeu = 1;
	}

	public native Network learn(DataSet data);

	public native void setMaxParents(int count);
	public native int getMaxParents();
	
	public native void setNetWeight(double weight);
	public native double getNetWeight();
	
	public native void setPriorsMethod(int method);
	public native int getPriorsMethod();
	
	public native BkKnowledge getBkKnowledge();
	public native void setBkKnowledge(BkKnowledge bkk);
	
	protected native long createNative(Object param);
	protected native void deleteNative(long nativePtr);
}
