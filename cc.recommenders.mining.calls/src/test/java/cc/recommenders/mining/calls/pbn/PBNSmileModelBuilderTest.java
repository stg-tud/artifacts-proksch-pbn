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

import static cc.recommenders.mining.calls.pbn.PBNModelConstants.CALL_PREFIX;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.CLASS_CONTEXT_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.DEFINITION_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.DUMMY_DEFINITION;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.DUMMY_METHOD;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.DUMMY_TYPE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.METHOD_CONTEXT_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.PARAMETER_PREFIX;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.PATTERN_TITLE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.UNKNOWN_DEFINITION;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.UNKNOWN_METHOD;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.UNKNOWN_TYPE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.newDefinition;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.newMethodContext;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.CALL1;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.CALL2;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.DEF1;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.DEF2;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.METHOD1;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.METHOD2;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.PARAM1;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.PARAM1_ARGNUM;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.PARAM2;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.PARAM2_ARGNUM;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.SUPERCLASS1;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.SUPERCLASS2;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.assertBooleanNode;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.assertNodeExists;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.assertNodesExist;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.assertProbabilities;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.assertStates;
import static cc.recommenders.mining.calls.pbn.PBNSmileModelBuilderFixture.assertNodeName;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import smile.Network;
import cc.recommenders.mining.calls.Pattern;
import cc.recommenders.mining.calls.pbn.PBNModelConstants;
import cc.recommenders.mining.calls.pbn.PBNSmileModelBuilder;
import cc.recommenders.usages.features.ClassFeature;
import cc.recommenders.usages.features.DefinitionFeature;
import cc.recommenders.usages.features.FirstMethodFeature;
import cc.recommenders.usages.features.UsageFeature;
import cc.recommenders.utils.dictionary.Dictionary;

public class PBNSmileModelBuilderTest {

	private PBNSmileModelBuilderFixture fix;
	private PBNSmileModelBuilder sut;

	private Network network;
	private Dictionary<UsageFeature> dictionary;
	private List<Pattern<UsageFeature>> patterns;

	@Before
	public void setup() {
		fix = new PBNSmileModelBuilderFixture();
		sut = new PBNSmileModelBuilder();

		dictionary = fix.getDictionary();
		patterns = fix.getPatterns();
	}

	@Test
	public void dummyStatesAreAddedToDictionary() {
		network = sut.build(patterns, dictionary);

		assertTrue(dictionary.contains(new ClassFeature(PBNModelConstants.DUMMY_TYPE)));
		assertTrue(dictionary.contains(new FirstMethodFeature(PBNModelConstants.DUMMY_METHOD)));
		assertTrue(dictionary.contains(new DefinitionFeature(UNKNOWN_DEFINITION)));
		assertTrue(dictionary.contains(new DefinitionFeature(PBNModelConstants.DUMMY_DEFINITION)));
	}

	@Test(expected = RuntimeException.class)
	public void crashesIfNoPatternIsProvided() {
		patterns = newArrayList();
		network = sut.build(patterns, dictionary);
	}

	@Test
	public void secondPatternIsCreatedIfOnlyOneIsProvided() {
		dictionary = fix.getDictionaryForSinglePattern();
		patterns = fix.getSinglePattern();
		network = sut.build(patterns, dictionary);

		Pattern<UsageFeature> lastPattern = patterns.get(patterns.size() - 1);
		assertTrue(lastPattern.getName().equals("other"));
	}

	@Test
	public void secondPatternIsOnlyCreatedIfOnlyOneIsProvided() {
		network = sut.build(patterns, dictionary);

		Pattern<UsageFeature> lastPattern = patterns.get(patterns.size() - 1);
		assertFalse(lastPattern.getName().equals("other"));
	}

	@Test
	public void aBayesianNetworkIsReturned() {
		network = sut.build(patterns, dictionary);

		assertNotNull(network);
	}

	@Test
	public void patternNodeExists() {
		network = sut.build(patterns, dictionary);

		assertNodeExists(network, PATTERN_TITLE);
	}

	@Test
	public void patternNodeDoesntHaveParent() {
		network = sut.build(patterns, dictionary);

		int[] actual = network.getParents(PATTERN_TITLE);
		int[] expected = new int[0];

		assertArrayEquals(expected, actual);
	}

	@Test
	public void patternNodeHasCorrectStates() {
		network = sut.build(patterns, dictionary);

		assertStates(network, PATTERN_TITLE, "p1", "p2");
	}

	@Test
	public void patternNodeHasCorrectProbabilities() {
		network = sut.build(patterns, dictionary);
		assertProbabilities(network, PATTERN_TITLE, 0.5, 0.5);
	}
	
	@Test
	public void patternNodeHasCorrectName() {
		network = sut.build(patterns, dictionary);
		assertNodeName(network, PATTERN_TITLE, PATTERN_TITLE);
	}
	
	@Test
	public void classContextNodeExists() {
		network = sut.build(patterns, dictionary);

		assertNodeExists(network, PBNModelConstants.CLASS_CONTEXT_TITLE);
	}

	@Test
	public void classContextNodeHasParent() {
		network = sut.build(patterns, dictionary);

		int[] actual = network.getParents(CLASS_CONTEXT_TITLE);
		int[] expected = new int[] { network.getNode(PBNModelConstants.PATTERN_TITLE) };

		assertArrayEquals(expected, actual);
	}

	@Test
	public void classContextNodeHasCorrectStates() {
		network = sut.build(patterns, dictionary);

		assertStates(network, CLASS_CONTEXT_TITLE, SUPERCLASS1.toString(), SUPERCLASS2.toString(),
				DUMMY_TYPE.toString(), UNKNOWN_TYPE.toString());
	}

	@Test
	public void classContextNodeHasCorrectProbabilities() {
		network = sut.build(patterns, dictionary);
		assertProbabilities(network, CLASS_CONTEXT_TITLE, 0.99999, 1E-5, 1E-5, 1E-5, 1E-5, 0.99999, 1E-5, 1E-5);
	}
	
	@Test
	public void classContextNodeHasCorrectName() {
		network = sut.build(patterns, dictionary);
		assertNodeName(network, CLASS_CONTEXT_TITLE, CLASS_CONTEXT_TITLE);
	}

	@Test
	public void methodContextNodeExists() {
		network = sut.build(patterns, dictionary);

		assertNodeExists(network, PBNModelConstants.METHOD_CONTEXT_TITLE);
	}

	@Test
	public void methodContextNodeHasCorrectStates() {
		network = sut.build(patterns, dictionary);

		assertStates(network, PBNModelConstants.METHOD_CONTEXT_TITLE, newMethodContext(METHOD1),
				newMethodContext(METHOD2), newMethodContext(DUMMY_METHOD), newMethodContext(UNKNOWN_METHOD));
	}

	@Test
	public void methodContextNodeHasCorrectProbabilities() {
		network = sut.build(patterns, dictionary);

		assertProbabilities(network, METHOD_CONTEXT_TITLE, 0.99999, 1E-5, 1E-5, 1E-5, 1E-5, 0.99999, 1E-5, 1E-5);
	}
	
	@Test
	public void methodContextNodeHasCorrectName() {
		network = sut.build(patterns, dictionary);
		assertNodeName(network, METHOD_CONTEXT_TITLE, METHOD_CONTEXT_TITLE);
	}

	@Test
	public void definitionNodeExists() {
		network = sut.build(patterns, dictionary);

		assertNodeExists(network, PBNModelConstants.DEFINITION_TITLE);
	}

	@Test
	public void definitionNodeHasCorrectStates() {
		network = sut.build(patterns, dictionary);

		assertStates(network, DEFINITION_TITLE, newDefinition(DEF1), newDefinition(DEF2),
				newDefinition(DUMMY_DEFINITION), newDefinition(UNKNOWN_DEFINITION));
	}

	@Test
	public void definitionNodeHasCorrectProbabilities() {
		network = sut.build(patterns, dictionary);

		assertProbabilities(network, DEFINITION_TITLE, 0.99999, 1E-5, 1E-5, 1E-5, 1E-5, 0.99999, 1E-5, 1E-5);
	}
	
	@Test
	public void definitionNodeHasCorrectName() {
		network = sut.build(patterns, dictionary);
		assertNodeName(network, DEFINITION_TITLE, DEFINITION_TITLE);
	}

	@Test
	public void callNodesExist() {
		network = sut.build(patterns, dictionary);

		String call1 = CALL_PREFIX + CALL1;
		String call2 = CALL_PREFIX + CALL2;
		assertNodesExist(network, call1, call2);
	}

	@Test
	public void callNodesHaveCorrectStates() {
		network = sut.build(patterns, dictionary);

		String call1 = CALL_PREFIX + CALL1;
		assertBooleanNode(network, call1);
		String call2 = CALL_PREFIX + CALL2;
		assertBooleanNode(network, call2);
	}

	@Test
	public void callNodesHaveCorrectProbability() {
		network = sut.build(patterns, dictionary);

		String call1 = CALL_PREFIX + CALL1;
		assertProbabilities(network, call1, 0.99999, 1e-5, 1e-5, 0.99999);

		String call2 = CALL_PREFIX + CALL2;
		assertProbabilities(network, call2, 1e-5, 0.99999, 0.99999, 1e-5);
	}
	
	@Test
	public void callNodesHaveCorrectNames() {
		network = sut.build(patterns, dictionary);
		
		String call1 = CALL_PREFIX + CALL1;
		assertNodeName(network, call1, CALL1.toString());

		String call2 = CALL_PREFIX + CALL2;
		assertNodeName(network, call2, CALL2.toString());
	}

	@Test
	public void parameterNodesExist() {
		network = sut.build(patterns, dictionary);

		String param1 = PARAMETER_PREFIX + PARAM1 + "#" + PARAM1_ARGNUM;
		String param2 = PARAMETER_PREFIX + PARAM2 + "#" + PARAM2_ARGNUM;
		assertNodesExist(network, param1, param2);
	}

	@Test
	public void parameterNodesHaveCorrectStates() {
		network = sut.build(patterns, dictionary);

		String param1 = PARAMETER_PREFIX + PARAM1 + "#" + PARAM1_ARGNUM;
		assertBooleanNode(network, param1);
		String param2 = PARAMETER_PREFIX + PARAM2 + "#" + PARAM2_ARGNUM;
		assertBooleanNode(network, param2);
	}

	@Test
	public void parameterNodesHaveCorrectProbabilities() {
		network = sut.build(patterns, dictionary);

		String param1 = PARAMETER_PREFIX + PARAM1 + "#" + PARAM1_ARGNUM;
		assertProbabilities(network, param1, 0.99999, 1e-5, 1e-5, 0.99999);

		String param2 = PARAMETER_PREFIX + PARAM2 + "#" + PARAM2_ARGNUM;
		assertProbabilities(network, param2, 1e-5, 0.99999, 0.99999, 1e-5);
	}
	
	@Test
	public void parameterNodesHaveCorrectNames() {
		network = sut.build(patterns, dictionary);
		
		String param1 = PARAM1 + "#" + PARAM1_ARGNUM;
		assertNodeName(network, PARAMETER_PREFIX + param1, param1);

		String param2 = PARAM2 + "#" + PARAM2_ARGNUM;
		assertNodeName(network, PARAMETER_PREFIX + param2, param2);
	}

	@Test
	public void correctNumberOfNodesIsCreated() {
		network = sut.build(patterns, dictionary);

		int actual = network.getNodeCount();
		int expected = 8; // patterns, classContext, methodContext, definition,
							// 2*call, 2* param

		assertEquals(expected, actual);
	}
}
