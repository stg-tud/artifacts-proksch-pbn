/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.mining.calls.pbn;

import static cc.recommenders.mining.calls.NetworkMathUtils.MAX_PROBABILTY_DELTA;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.STATE_FALSE;
import static cc.recommenders.mining.calls.pbn.PBNModelConstants.STATE_TRUE;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.eclipse.recommenders.commons.bayesnet.BayesianNetwork;
import org.eclipse.recommenders.commons.bayesnet.Node;

import cc.recommenders.mining.calls.Pattern;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.features.CallFeature;
import cc.recommenders.usages.features.ClassFeature;
import cc.recommenders.usages.features.DefinitionFeature;
import cc.recommenders.usages.features.FirstMethodFeature;
import cc.recommenders.usages.features.ParameterFeature;
import cc.recommenders.usages.features.TypeFeature;
import cc.recommenders.usages.features.UsageFeature;
import cc.recommenders.utils.dictionary.Dictionary;

public class PBNModelBuilderFixture {

	public static final ITypeName TYPE = VmTypeName.get("La/type/Blubb");
	public static final ITypeName SUPERCLASS1 = VmTypeName.get("La/super/Type");
	public static final ITypeName SUPERCLASS2 = VmTypeName.get("Lother/super/Type");
	public static final IMethodName METHOD1 = VmMethodName.get("Lsome/Context.m1()V");
	public static final IMethodName METHOD2 = VmMethodName.get("Lother/Context.m2()V");
	public static final DefinitionSite DEF1 = createDef1();
	public static final DefinitionSite DEF2 = createDef2();
	public static final IMethodName CALL1 = VmMethodName.get("La/type/Blubb.m3()V");
	public static final IMethodName CALL2 = VmMethodName.get("La/type/Blubb.m4()V");
	public static final IMethodName CALL_WITH_DIFFERENT_TYPE = VmMethodName.get("Lanother/type/Blubb.m5()V");
	public static final IMethodName PARAM1 = VmMethodName.get("Lcompletely/different/Type.m6(Blubb;)V");
	public static final int PARAM1_ARGNUM = 135;
	public static final IMethodName PARAM2 = VmMethodName.get("Lyat/Type.m7(Blubb;)V");
	public static final int PARAM2_ARGNUM = 246;

	private static DefinitionSite createDef1() {
		// DeclaringType'.'fieldName;FieldType
		return DefinitionSites.createDefinitionByField(VmFieldName.get("LFoo.field;Lbla"));
	}

	private static DefinitionSite createDef2() {
		return DefinitionSites.createDefinitionByConstructor(VmMethodName.get("La/type/Blubb.<init>()V"));
	}

	public List<Pattern<UsageFeature>> getSinglePattern() {
		List<Pattern<UsageFeature>> patterns = newArrayList();

		Pattern<UsageFeature> p = new Pattern<UsageFeature>();
		p.setName("p1");
		p.setNumberOfObservations(1);
		patterns.add(p);

		p.setProbability(new TypeFeature(TYPE), 1.0);
		p.setProbability(new ClassFeature(SUPERCLASS1), 1.0);
		p.setProbability(new FirstMethodFeature(METHOD1), 1.0);
		p.setProbability(new DefinitionFeature(DEF1), 1.0);
		p.setProbability(new CallFeature(CALL1), 1.0);
		p.setProbability(new ParameterFeature(PARAM1, PARAM1_ARGNUM), 1.0);

		return patterns;
	}

	public Dictionary<UsageFeature> getDictionaryForSinglePattern() {
		Dictionary<UsageFeature> dictionary = new Dictionary<UsageFeature>();
		dictionary.add(new TypeFeature(TYPE));
		dictionary.add(new ClassFeature(SUPERCLASS1));
		dictionary.add(new FirstMethodFeature(METHOD1));
		dictionary.add(new DefinitionFeature(DEF1));
		dictionary.add(new CallFeature(CALL1));
		dictionary.add(new ParameterFeature(PARAM1, PARAM1_ARGNUM));
		return dictionary;
	}

	public List<Pattern<UsageFeature>> getPatterns() {
		List<Pattern<UsageFeature>> patterns = newArrayList();

		Pattern<UsageFeature> p = new Pattern<UsageFeature>();
		p.setName("p1");
		p.setNumberOfObservations(1);
		patterns.add(p);

		p.setProbability(new TypeFeature(TYPE), 1.0);
		p.setProbability(new ClassFeature(SUPERCLASS1), 1.0);
		p.setProbability(new FirstMethodFeature(METHOD1), 1.0);
		p.setProbability(new DefinitionFeature(DEF1), 1.0);
		p.setProbability(new CallFeature(CALL1), 1.0);
		p.setProbability(new ParameterFeature(PARAM1, PARAM1_ARGNUM), 1.0);

		p = new Pattern<UsageFeature>();
		p.setName("p2");
		p.setNumberOfObservations(1);
		patterns.add(p);

		p.setProbability(new TypeFeature(TYPE), 1.0);
		p.setProbability(new ClassFeature(SUPERCLASS2), 1.0);
		p.setProbability(new FirstMethodFeature(METHOD2), 1.0);
		p.setProbability(new DefinitionFeature(DEF2), 1.0);
		p.setProbability(new CallFeature(CALL2), 1.0);
		p.setProbability(new ParameterFeature(PARAM2, PARAM2_ARGNUM), 1.0);

		return patterns;
	}

	public Dictionary<UsageFeature> getDictionary() {
		Dictionary<UsageFeature> dictionary = new Dictionary<UsageFeature>();
		dictionary.add(new TypeFeature(TYPE));
		dictionary.add(new ClassFeature(SUPERCLASS1));
		dictionary.add(new FirstMethodFeature(METHOD1));
		dictionary.add(new DefinitionFeature(DEF1));
		dictionary.add(new CallFeature(CALL1));
		dictionary.add(new ParameterFeature(PARAM1, PARAM1_ARGNUM));
		dictionary.add(new ClassFeature(SUPERCLASS2));
		dictionary.add(new FirstMethodFeature(METHOD2));
		dictionary.add(new DefinitionFeature(DEF2));
		dictionary.add(new CallFeature(CALL2));
		dictionary.add(new ParameterFeature(PARAM2, PARAM2_ARGNUM));
		return dictionary;
	}

	public static void assertNodeExists(BayesianNetwork net, String title) {
		Node node = net.getNode(title);
		assertNotNull(node);
	}

	public static void assertNodesExist(BayesianNetwork net, String... titles) {
		for (String title : titles) {
			assertNodeExists(net, title);
		}
	}

	public static void assertProbabilities(BayesianNetwork network, String nodeTitle, double... expectedProbabilities) {
		Node node = network.getNode(nodeTitle);
		double[] actual = node.getProbabilities();

		assertArrayEquals(expectedProbabilities, actual, MAX_PROBABILTY_DELTA);
	}

	public static void assertStates(BayesianNetwork network, String nodeTitle, String... expectedStates) {
		Node node = network.getNode(nodeTitle);
		String[] actual = node.getStates();

		assertArrayEquals(expectedStates, actual);
	}

	public static void assertBooleanNode(BayesianNetwork network, String nodeTitle) {
		Node node = network.getNode(nodeTitle);
		String[] actuals = node.getStates();
		String[] expecteds = new String[] { STATE_TRUE, STATE_FALSE };
		assertArrayEquals(expecteds, actuals);
	}
}