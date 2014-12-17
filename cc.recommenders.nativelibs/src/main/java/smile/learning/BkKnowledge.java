package smile.learning;

public class BkKnowledge 
{
    public BkKnowledge() {}
    
	public BkKnowledge(BkArcInfo[] forcedArcs, BkArcInfo[] forbiddenArcs, BkTierInfo[] tiers) 
	{
		this.forcedArcs = forcedArcs;
		this.forbiddenArcs = forbiddenArcs;
		this.tiers = tiers;
	}
	
	public BkArcInfo[] forcedArcs;
    public BkArcInfo[] forbiddenArcs;
	public BkTierInfo[] tiers;
}
