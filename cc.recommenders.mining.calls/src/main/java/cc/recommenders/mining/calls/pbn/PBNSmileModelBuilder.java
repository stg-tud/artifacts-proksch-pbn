/**
 * Copyright (c) 2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Uli Fahrer - Inital implementation
 */
package cc.recommenders.mining.calls.pbn;

import static cc.recommenders.assertions.Checks.ensureIsGreaterOrEqualTo;
import static cc.recommenders.assertions.Checks.ensureIsNotNull;
import static cc.recommenders.mining.calls.NetworkMathUtils.ensureAllProbabilitiesInValidRange;
import static cc.recommenders.mining.calls.NetworkMathUtils.getProbabilityInMinMaxRange;
import static cc.recommenders.mining.calls.NetworkMathUtils.safeDivMaxMin;
import static cc.recommenders.mining.calls.NetworkMathUtils.scaleMaximalValue;
import static cc.recommenders.mining.calls.pbn.DictionaryHelper.UNKNOWN_IN_CLASS;
import static cc.recommenders.mining.calls.pbn.DictionaryHelper.UNKNOWN_IN_DEFINITION;
import static cc.recommenders.mining.calls.pbn.DictionaryHelper.UNKNOWN_IN_METHOD;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.CLASS_CONTEXT_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.DEFINITION_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.METHOD_CONTEXT_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.PATTERN_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.STATE_FALSE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.STATE_TRUE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.getTitle;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.newCallSite;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.newParameterSite;
import static java.lang.System.arraycopy;

import java.util.List;
import java.util.Set;

import smile.Network;
import smile.utils.SmileNameConverter;
import cc.recommenders.assertions.Asserts;
import cc.recommenders.mining.calls.ModelBuilder;
import cc.recommenders.mining.calls.NetworkMathUtils;
import cc.recommenders.mining.calls.Pattern;
import cc.recommenders.names.IMethodName;
import cc.recommenders.usages.features.CallFeature;
import cc.recommenders.usages.features.ParameterFeature;
import cc.recommenders.usages.features.UsageFeature;
import cc.recommenders.utils.dictionary.Dictionary;

public class PBNSmileModelBuilder implements ModelBuilder<UsageFeature, Network> {
	
	private DictionaryHelper dictionary;
	private List<Pattern<UsageFeature>> patterns;
	
	private Network network;
	private int patternNodeHandle;
	private int classNodeHandle;
	private int methodNodeHandle;
	private int defNodeHandle;
	
	@Override
	public Network build(List<Pattern<UsageFeature>> _patterns, Dictionary<UsageFeature> _dictionary) {
		patterns = _patterns;
		dictionary = new DictionaryHelper(_dictionary);
		
		ensureValidData();
		createNetwork();
		
		return network;
	}
	
	private void ensureValidData() {
		dictionary.addDummyStatesToEnsureAtLeastTwoStatesPerNode();
		ensureAtLeastTwoPatternsExist();
	}

	private void ensureAtLeastTwoPatternsExist() {
		int numPatterns = patterns.size();
		ensureIsGreaterOrEqualTo(numPatterns, 1, "no pattern provided");

		if (numPatterns < 2) {
			patterns.add(patterns.get(0).clone("other"));
		}
	}
	
	private void createNetwork() {
		network = new Network();
		
		createPatternNode();
		createClassContextNode();
		createMethodContextNode();
		createDefinitionNode();
		createCallNodes();
		createParameterNodes();
	}
	
	private void createPatternNode() {
		patternNodeHandle = network.addNode(Network.NodeType.Cpt);
		network.setNodeId(patternNodeHandle, PATTERN_TITLE);
		network.setNodeName(patternNodeHandle, PATTERN_TITLE);
		
		int numTotalObservations = sumUpObservations();
		String[] states = new String[patterns.size()];
		double[] probabilities = new double[patterns.size()];
		
		int i = 0;
		for (Pattern<UsageFeature> p : patterns) {
			states[i] = p.getName();
			double probability = safeDivMaxMin(p.getNumberOfObservations(), numTotalObservations);
			probabilities[i] = NetworkMathUtils.roundToDefaultPrecision(probability);
			i++;
		}
		
		scaleMaximalValue(probabilities);
		ensureAllProbabilitiesInValidRange(probabilities);
		
		setStates(patternNodeHandle, states);
		network.setNodeDefinition(patternNodeHandle, probabilities);
	}
	
	private int sumUpObservations() {
		int num = 0;
		for (Pattern<UsageFeature> p : patterns) {
			num += p.getNumberOfObservations();
		}
		return num;
	}
	
	private void setStates(int handle, String[] states) {
		for (String s : states) {
			String convertedName = SmileNameConverter.convertToLegalSmileName(s);
			network.addOutcome(handle, convertedName);
		}
		//Remove default states
		network.deleteOutcome(handle, "State0");
		network.deleteOutcome(handle, "State1");
	}
	
	private void createClassContextNode() {
		classNodeHandle = createNodeAndAddToNetwork(CLASS_CONTEXT_TITLE);
		addGenericPropabilities(classNodeHandle, dictionary.getClassContexts(), UNKNOWN_IN_CLASS);
	}
	
	private void createMethodContextNode() {
		methodNodeHandle = createNodeAndAddToNetwork(METHOD_CONTEXT_TITLE);
		addGenericPropabilities(methodNodeHandle, dictionary.getMethodContexts(), UNKNOWN_IN_METHOD);
	}
	
	private void createDefinitionNode() {
		defNodeHandle = createNodeAndAddToNetwork(DEFINITION_TITLE);
		addGenericPropabilities(defNodeHandle, dictionary.getDefinitions(), UNKNOWN_IN_DEFINITION);
	}
	
	private void createCallNodes() {

		for (CallFeature call : dictionary.getCallSites()) {

			IMethodName methodName = call.getMethodName();
			String nodeId = newCallSite(methodName);
			String nodeName = nodeId.substring(PBNModelConstants.CALL_PREFIX.length());

			addBooleanNode(call, nodeId, nodeName);
		}
	}
	
	private void createParameterNodes() {
		for (ParameterFeature param : dictionary.getParameterSites()) {
			IMethodName methodName = param.getMethodName();
			int argNum = param.getArgNum();
			String nodeId = newParameterSite(methodName, argNum);
			String nodeName = nodeId.substring(PBNModelConstants.PARAMETER_PREFIX.length());
			
			addBooleanNode(param, nodeId, nodeName);
		}
	}
	
	private int createNodeAndAddToNetwork(String nodeId) {
		return createNodeAndAddToNetwork(nodeId, nodeId);
	}
	
	private int createNodeAndAddToNetwork(String nodeId, String nodeName) {
		int handle = network.addNode(Network.NodeType.Cpt);
		
		String smileId = SmileNameConverter.convertToLegalSmileName(nodeId);
		
		network.addArc(patternNodeHandle, handle);
		network.setNodeId(handle, smileId);
		network.setNodeName(handle, nodeName);
		
		return handle;
	}
	
	private void addGenericPropabilities(int nodeHandle, Set<UsageFeature> statesSet,
			UsageFeature stateForUnknowns) {
		
		String[] states = new String[statesSet.size()];
		double[] probabilities = new double[patterns.size() * statesSet.size()];

		int i = 0;
		for (UsageFeature f : statesSet) {
			final String state = getTitle(f);
			ensureIsNotNull(state);
			states[i++] = state;
		}

		int j = 0;
		for (Pattern<UsageFeature> pattern : patterns) {

			double sumOfProbs = 0.0;

			double[] subprobs = new double[statesSet.size()];
			int k = 0;
			for (UsageFeature state : statesSet) {
				double probability = pattern.getProbability(state);

				probability = getProbabilityInMinMaxRange(probability);
				probability = NetworkMathUtils.roundToDefaultPrecision(probability);

				subprobs[k++] = probability;
				sumOfProbs += probability;
			}

			boolean isSumOfProbabilitiesToSmall = sumOfProbs < 1;
			if (isSumOfProbabilitiesToSmall) {
				// then add it to unknown
				double diff = Math.max(0, 1 - sumOfProbs);
				int idx = findIndex(statesSet, stateForUnknowns);
				subprobs[idx] = subprobs[idx] + diff;
			}

			scaleMaximalValue(subprobs);
			arraycopy(subprobs, 0, probabilities, j, statesSet.size());

			j += statesSet.size();
		}
		
		setStates(nodeHandle, states);
		network.setNodeDefinition(nodeHandle, probabilities);
		
	}
	
	private int findIndex(Set<UsageFeature> statesSet, UsageFeature stateForUnknowns) {
		int i = 0;
		for (UsageFeature f : statesSet) {
			if (f.equals(stateForUnknowns)) {
				return i;
			}
			i++;
		}
		Asserts.fail("unknown state not found");
		return -1;
	}
	
	private void addBooleanNode(UsageFeature feature, String nodeId, String nodeName) {

		int nodeHandle = createNodeAndAddToNetwork(nodeId, nodeName);
		setStates(nodeHandle, new String[] { STATE_TRUE, STATE_FALSE });

		double[] probabilities = new double[2 * patterns.size()];

		int i = 0;
		for (Pattern<UsageFeature> pattern : patterns) {
			double probability = pattern.getProbability(feature);

			probability = getProbabilityInMinMaxRange(probability);

			probabilities[i++] = NetworkMathUtils.roundToDefaultPrecision(probability);
			probabilities[i++] = NetworkMathUtils.roundToDefaultPrecision(1.0 - probability);
		}

		ensureAllProbabilitiesInValidRange(probabilities);

		network.setNodeDefinition(nodeHandle, probabilities);
	}
}
