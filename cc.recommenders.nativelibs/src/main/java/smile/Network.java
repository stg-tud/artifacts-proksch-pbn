// Network.java

package smile;

import java.awt.Color;
import java.awt.Rectangle;

public class Network extends Wrapper {
	// Bayesian network algoritm types
	public class BayesianAlgorithmType {
		public static final int Lauritzen = 0;
		public static final int Henrion = 1;
		public static final int Pearl = 2;
		public static final int LSampling = 3;
		public static final int SelfImportance = 4;
		public static final int HeuristicImportance = 5;
		public static final int BackSampling = 6;
		public static final int AisSampling = 7;
		public static final int EpisSampling = 8;
		public static final int LBP = 9;
	}

	// Influence diagram algoritm types
	public class InfluenceDiagramAlgorithmType {
		public static final int PolicyEvaluation = 0;
		public static final int FindBestPolicy = 1;
	}

	// Node types
	public class NodeType {
		public static final int Cpt = 18;
		public static final int TruthTable = 20;
		public static final int List = 17;
		public static final int Table = 8;
		public static final int Mau = 520;
		public static final int NoisyMax = 146;
		public static final int NoisyAdder = 274;
		public static final int Equation = 4;
		public static final int DeMorgan = 82;
	}

	// type of arc between two DeMorgan nodes
	public class DeMorganParentType {
		public static final int Inhibitor = 0;
		public static final int Requirement = 1;
		public static final int Cause = 2;
		public static final int Barrier = 3;
	}

	// Temporal (DBN) node types
	public class NodeTemporalType {
		public static final int Contemporal = 0;
		public static final int Anchor = 1;
		public static final int Terminal = 2;
		public static final int Plate = 3;

	}

	// Diagnostic types
	public class NodeDiagType {
		public static final int Fault = 0;
		public static final int Observation = 1;
		public static final int Auxiliary = 2;
	}

	// -------------------------------------------------------------------------------

	public native void readFile(String fileName);

	public native void writeFile(String fileName);

	public native void setId(String id);

	public native String getId();

	public native void setName(String name);

	public native String getName();

	public native void setDescription(String description);

	public native String getDescription();

	public native void setBayesianAlgorithm(int algorithmType);

	public native int getBayesianAlgorithm();

	public native void setInfluenceDiagramAlgorithm(int algorithmType);

	public native int getInfluenceDiagramAlgorithm();

	public native void updateBeliefs();

	public native double probEvidence();

	public native AnnealedMapResults annealedMap(int[] mapNodes,
			AnnealedMapTuning tuning);

	public native AnnealedMapResults annealedMap(String[] mapNodes,
			AnnealedMapTuning tuning);

	public native int getSampleCount();

	public native void setSampleCount(int sampleCount);

	public native int addNode(int nodeType);

	public native int addNode(int nodeType, String nodeId);

	public native int getNodeType(int nodeHandle);

	public native int getNodeType(String nodeId);

	public native void setNodeType(int nodeHandle, int type);

	public native void setNodeType(String nodeId, int type);

	public native void deleteNode(int nodeHandle);

	public native void deleteNode(String nodeId);

	public native int getNodeCount();

	public native int getFirstNode();

	public native int getNextNode(int nodeHandle);

	public native int getNode(String nodeId);

	public native void setNodeId(int nodeHandle, String id);

	public native void setNodeId(String oldId, String newId);

	public native String getNodeId(int nodeHandle);

	public native void setNodeName(int nodeHandle, String name);

	public native void setNodeName(String nodeId, String name);

	public native String getNodeName(int nodeHandle);

	public native String getNodeName(String nodeId);

	public native void setNodeDescription(int nodeHandle, String description);

	public native void setNodeDescription(String nodeId, String description);

	public native String getNodeDescription(int nodeHandle);

	public native String getNodeDescription(String nodeId);

	public native int[] getAllNodes();

	public native String[] getAllNodeIds();

	public native void addArc(int parentHandle, int childHandle);

	public native void addArc(String parentId, String childId);

	public native void deleteArc(int parentHandle, int childHandle);

	public native void deleteArc(String parentId, String childId);

	public native int getOutcomeCount(int nodeHandle);

	public native int getOutcomeCount(String nodeId);

	public native void addOutcome(int nodeHandle, String outcomeId);

	public native void addOutcome(String nodeId, String outcomeId);

	public native void insertOutcome(int nodeHandle, int position,
			String outcomeId);

	public native void insertOutcome(String nodeId, int position,
			String outcomeId);

	public native void deleteOutcome(int nodeHandle, int outcomeIndex);

	public native void deleteOutcome(String nodeId, int outcomeIndex);

	public native void deleteOutcome(int nodeHandle, String outcomeId);

	public native void deleteOutcome(String nodeId, String outcomeId);

	public native void setOutcomeId(int nodeHandle, int outcomeIndex, String id);

	public native void setOutcomeId(String nodeId, int outcomeIndex, String id);

	public native String getOutcomeId(int nodeHandle, int outcomeIndex);

	public native String getOutcomeId(String nodeId, int outcomeIndex);

	public native String[] getOutcomeIds(int nodeHandle);

	public native String[] getOutcomeIds(String nodeId);

	public native int[] getParents(int nodeHandle);

	public native int[] getParents(String nodeId);

	public native String[] getParentIds(int nodeHandle);

	public native String[] getParentIds(String nodeId);

	public native int[] getChildren(int nodeHandle);

	public native int[] getChildren(String nodeId);

	public native String[] getChildIds(int nodeHandle);

	public native String[] getChildIds(String nodeId);

	public native void setNodeDefinition(int nodeHandle, double[] definition);

	public native void setNodeDefinition(String nodeId, double[] definition);

	public native double[] getNodeDefinition(int nodeHandle);

	public native double[] getNodeDefinition(String nodeId);

	public native void setNodeEquation(int nodeHandle, String equation);

	public native void setNodeEquation(String nodeId, String equation);

	public native String getNodeEquation(int nodeHandle);

	public native String getNodeEquation(String nodeId);

	public native void setNodeEquationBounds(int nodeHandle, double lo,
			double hi);

	public native void setNodeEquationBounds(String nodeId, double lo, double hi);

	public native double[] getNodeEquationBounds(int nodeHandle);

	public native double[] getNodeEquationBounds(String nodeId);

	public native double[] getNodeValue(int nodeHandle);

	public native double[] getNodeValue(String nodeId);

	public native boolean isValueValid(int nodeHandle);

	public native boolean isValueValid(String nodeId);

	public native int[] getValueIndexingParents(int nodeHandle);

	public native int[] getValueIndexingParents(String nodeId);

	public native String[] getValueIndexingParentIds(int nodeHandle);

	public native String[] getValueIndexingParentIds(String nodeId);

	public native void setNoisyParentStrengths(int nodeHandle, int parentIndex,
			int[] strengths);

	public native void setNoisyParentStrengths(String nodeId, int parentIndex,
			int[] strengths);

	public native void setNoisyParentStrengths(int nodeHandle, String parentId,
			int[] strengths);

	public native void setNoisyParentStrengths(String nodeId, String parentId,
			int[] strengths);

	public native int[] getNoisyParentStrengths(int nodeHandle, int parentIndex);

	public native int[] getNoisyParentStrengths(String nodeId, int parentIndex);

	public native int[] getNoisyParentStrengths(int nodeHandle, String parentId);

	public native int[] getNoisyParentStrengths(String nodeId, String parentId);

	public native void setDeMorganPriorBelief(int nodeHandle, double belief);

	public native void setDeMorganPriorBelief(String nodeId, double belief);

	public native double getDeMorganPriorBelief(int nodeHandle);

	public native double getDeMorganPriorBelief(String nodeId);

	public native void setDeMorganParentType(int nodeHandle, int parentIndex,
			int parentType);

	public native void setDeMorganParentType(String nodeId, int parentIndex,
			int parentType);

	public native void setDeMorganParentType(int nodeHandle, String parentId,
			int parentType);

	public native void setDeMorganParentType(String nodeId, String parentId,
			int parentType);

	public native int getDeMorganParentType(int nodeHandle, int parentIndex);

	public native int getDeMorganParentType(String nodeId, int parentIndex);

	public native int getDeMorganParentType(int nodeHandle, String parentId);

	public native int getDeMorganParentType(String nodeId, String parentId);

	public native void setDeMorganParentWeight(int nodeHandle, int parentIndex,
			double parentWeight);

	public native void setDeMorganParentWeight(String nodeId, int parentIndex,
			double parentWeight);

	public native void setDeMorganParentWeight(int nodeHandle, String parentId,
			double parentWeight);

	public native void setDeMorganParentWeight(String nodeId, String parentId,
			double parentWeight);

	public native double getDeMorganParentWeight(int nodeHandle, int parentIndex);

	public native double getDeMorganParentWeight(String nodeId, int parentIndex);

	public native double getDeMorganParentWeight(int nodeHandle, String parentId);

	public native double getDeMorganParentWeight(String nodeId, String parentId);

	public native String[] getMauExpressions(int nodeHandle);

	public native String[] getMauExpressions(String nodeId);

	public native void setMauExpressions(int nodeHandle, String[] expressions);

	public native void setMauExpressions(String nodeId, String[] expressions);

	public native void setTarget(int nodeHandle, boolean target);

	public native void setTarget(String nodeId, boolean target);

	public native boolean isTarget(int nodeHandle);

	public native boolean isTarget(String nodeId);

	public native void clearAllTargets();

	public native void setEvidence(int nodeHandle, int outcomeIndex);

	public native void setEvidence(String nodeId, int outcomeIndex);

	public native void setEvidence(int nodeHandle, String outcomeId);

	public native void setEvidence(String nodeId, String outcomeId);

	public native int getEvidence(int nodeHandle);

	public native int getEvidence(String nodeId);

	public native String getEvidenceId(int nodeHandle);

	public native String getEvidenceId(String nodeId);

	public native void setContEvidence(int nodeHandle, double evidence);

	public native void setContEvidence(String nodeId, double evidence);

	public native double getContEvidence(int nodeHandle);

	public native double getContEvidence(String nodeId);

	public native boolean isEvidence(int nodeHandle);

	public native boolean isEvidence(String nodeId);

	public native boolean isRealEvidence(int nodeHandle);

	public native boolean isRealEvidence(String nodeId);

	public native boolean isPropagatedEvidence(int nodeHandle);

	public native boolean isPropagatedEvidence(String nodeId);

	public native void clearEvidence(int nodeHandle);

	public native void clearEvidence(String nodeId);

	public native void clearAllEvidence();

	// -- dynamic networks --
	public native UnrollResults unroll();

	public native int getSliceCount();

	public native void setSliceCount(int sliceCount);

	public native int getNodeTemporalType(int nodeHandle);

	public native int getNodeTemporalType(String nodeId);

	public native void setNodeTemporalType(int nodeHandle, int temporalType);

	public native void setNodeTemporalType(String nodeId, int temporalType);

	public native void addTemporalArc(int parentHandle, int childHandle,
			int order);

	public native void addTemporalArc(String parentId, String childId, int order);

	public native void deleteTemporalArc(int parentHandle, int childHandle,
			int order);

	public native void deleteTemporalArc(String parentId, String childId,
			int order);

	public native boolean temporalArcExists(int parentHandle, int childHandle,
			int order);

	public native boolean temporalArcExists(String parentId, String childId,
			int order);

	public native int getMaxTemporalOrder();

	public native int getMaxNodeTemporalOrder(int nodeHandle);

	public native int getMaxNodeTemporalOrder(String nodeId);

	public native int[] getTemporalOrders(int nodeHandle);

	public native int[] getTemporalOrders(String nodeId);

	public native TemporalInfo[] getTemporalChildren(int nodeHandle);

	public native TemporalInfo[] getTemporalChildren(String nodeId);

	public native TemporalInfo[] getTemporalParents(int nodeHandle, int order);

	public native TemporalInfo[] getTemporalParents(String nodeId, int order);

	public native TemporalInfo[] getUnrolledParents(int nodeHandle, int order);

	public native TemporalInfo[] getUnrolledParents(String nodeHandle, int order);

	public native TemporalInfo[] getUnrolledParents(int nodeHandle);

	public native TemporalInfo[] getUnrolledParents(String nodeHandle);

	public native boolean hasTemporalEvidence(int nodeHandle);

	public native boolean hasTemporalEvidence(String nodeId);

	public native boolean isTemporalEvidence(int nodeHandle, int slice);

	public native boolean isTemporalEvidence(String nodeId, int slice);

	public native int getTemporalEvidence(int nodeHandle, int slice);

	public native int getTemporalEvidence(String nodeId, int slice);

	public native String getTemporalEvidenceId(int nodeHandle, int slice);

	public native String getTemporalEvidenceId(String nodeId, int slice);

	public native void setTemporalEvidence(int nodeHandle, int slice,
			int outcomeIndex);

	public native void setTemporalEvidence(String nodeId, int slice,
			int outcomeIndex);

	public native void setTemporalEvidence(int nodeHandle, int slice,
			String outcomeId);

	public native void setTemporalEvidence(String nodeId, int slice,
			String outcomeId);

	public native void clearTemporalEvidence(int nodeHandle, int slice);

	public native void clearTemporalEvidence(String nodeId, int slice);

	public native double[] getNodeTemporalDefinition(int nodeHandle, int order);

	public native double[] getNodeTemporalDefinition(String nodeId, int order);

	public native void setNodeTemporalDefinition(int nodeHandle, int order,
			double[] definition);

	public native void setNodeTemporalDefinition(String nodeId, int order,
			double[] definition);

	public native int[][] getNoisyTemporalParentStrengths(int nodeHandle,
			int order);

	public native int[][] getNoisyTemporalParentStrengths(String nodeId,
			int order);

	public native void setNoisyTemporalParentStrengths(int nodeHandle,
			int order, int[][] strengths);

	public native void setNoisyTemporalParentStrengths(String nodeId,
			int order, int[][] strengths);

	// --- graphical appearance ---
	public native void setNodePosition(int nodeHandle, int x, int y, int width,
			int height);

	public native void setNodePosition(String nodeId, int x, int y, int width,
			int height);

	public void setNodePosition(int nodeHandle, Rectangle rect) {
		setNodePosition(nodeHandle, rect.x, rect.y, rect.width, rect.height);
	}

	public void setNodePosition(String nodeId, Rectangle rect) {
		setNodePosition(nodeId, rect.x, rect.y, rect.width, rect.height);
	}

	public native Rectangle getNodePosition(int nodeHandle);

	public native Rectangle getNodePosition(String nodeId);

	public native void setNodeBgColor(int nodeHandle, Color color);

	public native void setNodeBgColor(String nodeId, Color color);

	public native Color getNodeBgColor(int nodeHandle);

	public native Color getNodeBgColor(String nodeId);

	public native void setNodeTextColor(int nodeHandle, Color color);

	public native void setNodeTextColor(String nodeId, Color color);

	public native Color getNodeTextColor(int nodeHandle);

	public native Color getNodeTextColor(String nodeId);

	public native void setNodeBorderColor(int nodeHandle, Color color);

	public native void setNodeBorderColor(String nodeId, Color color);

	public native Color getNodeBorderColor(int nodeHandle);

	public native Color getNodeBorderColor(String nodeId);

	public native void setNodeBorderWidth(int nodeHandle, int width);

	public native void setNodeBorderWidth(String nodeId, int width);

	public native int getNodeBorderWidth(int nodeHandle);

	public native int getNodeBorderWidth(String nodeId);

	// --- Submodels ---
	public native int getSubmodel(String id);

	public native int getMainSubmodel();

	public native String getMainSubmodelId();

	public native int getSubmodelCount();

	public native int getFirstSubmodel();

	public native int getNextSubmodel(int handle);

	public native int addSubmodel(int parentHandle, String id);

	public native int addSubmodel(String parentId, String id);

	public native void deleteSubmodel(int handle);

	public native void deleteSubmodel(String id);

	public native void setSubmodelId(int handle, String id);

	public native String getSubmodelId(int handle);

	public native void setSubmodelName(int handle, String name);

	public native void setSubmodelName(String id, String name);

	public native String getSubmodelName(int handle);

	public native String getSubmodelName(String id);

	public native void setSubmodelDescription(int handle, String descrption);

	public native void setSubmodelDescription(String id, String descrption);

	public native String getSubmodelDescription(int handle);

	public native String getSubmodelDescription(String id);

	public native void setSubmodelOfNode(int submodelHandle, int nodeHandle);

	public native void setSubmodelOfNode(String submodelId, String nodeId);

	public native void setSubmodelOfSubmodel(int parentHandle, int childHandle);

	public native void setSubmodelOfSubmodel(String parentId, String childId);

	public native int getSubmodelOfNode(int nodeHandle);

	public native int getSubmodelOfNode(String nodeId);

	public native int getSubmodelOfSubmodel(int submodelHandle);

	public native int getSubmodelOfSubmodel(String submodelId);

	public native Rectangle getSubmodelPosition(int submodelHandle);

	public native Rectangle getSubmodelPosition(String submodelID);

	public native void setSubmodelPosition(int submodelHandle, int x, int y,
			int width, int height);

	public native void setSubmodelPosition(String submodelId, int x, int y,
			int width, int height);

	public void setSubmodelPosition(int submodelHandle, Rectangle rect) {
		setSubmodelPosition(submodelHandle, rect.x, rect.y, rect.width,
				rect.height);
	}

	public void setSubmodelPosition(String submodelId, Rectangle rect) {
		setSubmodelPosition(submodelId, rect.x, rect.y, rect.width, rect.height);
	}

	// --- Diagnosis ---
	public native int getNodeDiagType(int nodeHandle);

	public native int getNodeDiagType(String nodeId);

	public native void setNodeDiagType(int nodeHandle, int type);

	public native void setNodeDiagType(String nodeId, int type);

	public native String getNodeQuestion(int nodeHandle);

	public native String getNodeQuestion(String nodeId);

	public native void SetNodeQuestion(int nodeHandle, String question);

	public native void setNodeQuestion(String nodeId, String question);

	public native String getOutcomeFix(int nodeHandle, int outcomeIndex);

	public native String getOutcomeFix(String nodeId, int outcomeIndex);

	public native String getOutcomeFix(int nodeHandle, String outcomeId);

	public native String getOutcomeFix(String nodeId, String outcomeId);

	public native void setOutcomeFix(int nodeHandle, int outcomeIndex,
			String treatment);

	public native void setOutcomeFix(String nodeId, int outcomeIndex,
			String treatment);

	public native void setOutcomeFix(int nodeHandle, String outcomeId,
			String treatment);

	public native void setOutcomeFix(String nodeId, String outcomeId,
			String treatment);

	public native String getOutcomeDescription(int nodeHandle, int outcomeIndex);

	public native String getOutcomeDescription(String nodeId, int outcomeIndex);

	public native String getOutcomeDescription(int nodeHandle, String outcomeId);

	public native String getOutcomeDescription(String nodeId, String outcomeId);

	public native void setOutcomeDescription(int nodeHandle, int outcomeIndex,
			String description);

	public native void setOutcomeDescription(String nodeId, int outcomeIndex,
			String description);

	public native void setOutcomeDescription(int nodeHandle, String outcomeId,
			String description);

	public native void setOutcomeDescription(String nodeId, String outcomeId,
			String description);

	public native String getOutcomeLabel(int nodeHandle, int outcomeIndex);

	public native String getOutcomeLabel(String nodeId, int outcomeIndex);

	public native String getOutcomeLabel(int nodeHandle, String outcomeId);

	public native String getOutcomeLabel(String nodeId, String outcomeId);

	public native boolean setOutcomeLabel(int nodeHandle, int outcomeIndex,
			String label);

	public native boolean setOutcomeLabel(String nodeId, int outcomeIndex,
			String label);

	public native boolean setOutcomeLabel(int nodeHandle, String outcomeId,
			String label);

	public native boolean setOutcomeLabel(String nodeId, String outcomeId,
			String label);

	public native boolean isFaultOutcome(int nodeHandle, int outcomeIndex);

	public native boolean isFaultOutcome(String nodeId, int outcomeIndex);

	public native boolean isFaultOutcome(int nodeHandle, String outcomeId);

	public native boolean isFaultOutcome(String nodeId, String outcomeId);

	public native void setFaultOutcome(int nodeHandle, int outcomeIndex,
			boolean fault);

	public native void setFaultOutcome(String nodeId, int outcomeIndex,
			boolean fault);

	public native void setFaultOutcome(int nodeHandle, String outcomeId,
			boolean fault);

	public native void setFaultOutcome(String nodeId, String outcomeId,
			boolean fault);

	public native int getDefaultOutcome(int nodeHandle);

	public native int getDefaultOutcome(String nodeId);

	public native String getDefaultOutcomeId(int nodeHandle);

	public native String getDefaultOutcomeId(String nodeId);

	public native void setDefaultOutcome(int nodeHandle, int defOutcome);

	public native void setDefaultOutcome(String nodeId, int defOutcome);

	public native void setDefaultOutcome(int nodeHandle, String defOutcomeId);

	public native void setDefaultOutcome(String nodeId, String defOutcomeId);

	public native boolean isRanked(int nodeHandle);

	public native boolean isRanked(String nodeId);

	public native void setRanked(int nodeHandle, boolean ranked);

	public native void setRanked(String nodeId, boolean ranked);

	public native boolean isMandatory(int nodeHandle);

	public native boolean isMandatory(String nodeId);

	public native void setMandatory(int nodeHandle, boolean mandatory);

	public native void setMandatory(String nodeId, boolean mandatory);

	public native void addCostArc(int parentHandle, int childHandle);

	public native void addCostArc(String parentId, String childId);

	public native void deleteCostArc(int parentHandle, int childHandle);

	public native void deleteCostArc(String parentId, String childId);

	public native int[] getCostParents(int nodeHandle);

	public native int[] getCostParents(String nodeId);

	public native String[] getCostParentIds(int nodeHandle);

	public native String[] getCostParentIds(String nodeId);

	public native int[] getCostChildren(int nodeHandle);

	public native int[] getCostChildren(String nodeId);

	public native String[] getCostChildIds(int nodeHandle);

	public native String[] getCostChildIds(String nodeId);

	public native double[] getNodeCost(int nodeHandle);

	public native double[] getNodeCost(String nodeId);

	public native void setNodeCost(int nodeHandle, double[] cost);

	public native void setNodeCost(String nodeId, double[] cost);

	// --- User Properties ---
	public native UserProperty[] getUserProperties();

	public native void setUserProperties(UserProperty[] properties);

	public native UserProperty[] getNodeUserProperties(int nodeHandle);

	public native UserProperty[] getNodeUserProperties(String nodeId);

	public native void setNodeUserProperties(int nodeHandle,
			UserProperty[] properties);

	public native void setNodeUserProperties(String nodeId,
			UserProperty[] properties);

	// --- Documentation ---
	public native DocItemInfo[] getNodeDocumentation(int nodeHandle);

	public native DocItemInfo[] getNodeDocumentation(String nodeId);

	public native void setNodeDocumentation(int nodeHandle,
			DocItemInfo[] documentation);

	public native void setNodeDocumentation(String nodeId,
			DocItemInfo[] documentation);

	public native DocItemInfo[] getOutcomeDocumentation(int nodeHandle,
			int outcomeIndex);

	public native DocItemInfo[] getOutcomeDocumentation(String nodeId,
			int outcomeIndex);

	public native DocItemInfo[] getOutcomeDocumentation(String nodeId,
			String outcomeId);

	public native DocItemInfo[] getOutcomeDocumentation(int nodeHandle,
			String outcomeId);

	public native void setOutcomeDocumentation(int nodeHandle,
			int outcomeIndex, DocItemInfo[] documentation);

	public native void setOutcomeDocumentation(int nodeHandle,
			String outcomeId, DocItemInfo[] documentation);

	public native void setOutcomeDocumentation(String nodeId, String outcomeId,
			DocItemInfo[] documentation);

	public native void setOutcomeDocumentation(String nodeId, int outcomeIndex,
			DocItemInfo[] documentation);

	protected native long createNative(Object param);

	protected native void deleteNative(long nativePtr);
}
