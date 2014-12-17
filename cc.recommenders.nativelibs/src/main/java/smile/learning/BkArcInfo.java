package smile.learning;

public class BkArcInfo 
{
	public BkArcInfo() {}
	
	public BkArcInfo(int parent, int child) 
	{
		this.parent = parent;
		this.child = child;
	}

    public int parent;
	public int child;
}
