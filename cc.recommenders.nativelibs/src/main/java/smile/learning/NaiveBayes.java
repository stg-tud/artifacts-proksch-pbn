package smile.learning;

import smile.Network;
import smile.Wrapper;

public class NaiveBayes extends Wrapper
{
    public class PriorsType 
	{
		public final static int K2 = 0;
		public final static int BDeu = 1;
	}

    public native Network learn(DataSet data);

	public native void setClassVariableId(String id);
	public native String getClassVariableId();
	
	public native void setFeatureSelection(boolean value);
	public native boolean getFeatureSelection();
	
	public native void setNetWeight(double weight);
	public native double getNetWeight();
	
	public native void setPriorsMethod(int method);
	public native int getPriorsMethod();

	protected native long createNative(Object param);
	protected native void deleteNative(long nativePtr);
}
