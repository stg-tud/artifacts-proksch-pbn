package smile.learning;

import smile.Network;
import smile.Wrapper;

public class Pattern extends Wrapper
{
	public class EdgeType
	{
		public static final int None = 0;
		public static final int Undirected = 1;
		public static final int Directed = 2;
	}

	public native int getSize();
    public native void setSize(int size);
    public native int getEdge(int from, int to);
    public native void setEdge(int from, int to, int type);
    public native boolean hasCycle();
    public native boolean isDAG();
    public native Network makeNetwork(DataSet ds);
	
	protected native long createNative(Object param);
	protected native void deleteNative(long nativePtr);
}

