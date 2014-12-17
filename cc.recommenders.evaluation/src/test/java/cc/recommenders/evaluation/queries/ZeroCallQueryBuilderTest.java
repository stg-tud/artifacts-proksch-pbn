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
package cc.recommenders.evaluation.queries;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

public class ZeroCallQueryBuilderTest {

	private ITypeName expectedType = VmTypeName.get("Lorg/bla/blubb/SomeClass");
	private ITypeName expectedClassContext = VmTypeName.get("Lorg/bla/blubb/SomeClass");
	private IMethodName expectedMethodContext = VmMethodName.get("Lorg/bla/blubb/FirstClass.method(I)V");
	private DefinitionSite expectedDefinition = DefinitionSites.createDefinitionByReturn(VmMethodName
			.get("Lorg/bla/Creator.get(I)V"));

	private Usage example;

	private ZeroCallQueryBuilder sut;

	@Before
	public void setup() {
		sut = new ZeroCallQueryBuilder();
		example = createUsage(3);
	}

	@Test
	public void typeIsNotChanged() {
		List<Query> queries = sut.createQueries(example);
		Query query = assertSingleQuery(queries);
		assertEquals(expectedType, query.getType());
	}

	@Test
	public void superTypeIsNotChanged() {
		List<Query> queries = sut.createQueries(example);
		Query query = assertSingleQuery(queries);
		assertEquals(expectedClassContext, query.getClassContext());
	}

	@Test
	public void firstMethodIsNotChanged() {
		List<Query> queries = sut.createQueries(example);
		Query query = assertSingleQuery(queries);
		assertEquals(expectedMethodContext, query.getMethodContext());
	}

	@Test
	public void definitionIsNotChanged() {
		List<Query> queries = sut.createQueries(example);
		Query query = assertSingleQuery(queries);
		assertEquals(expectedDefinition, query.getDefinitionSite());
	}

	@Test
	public void pathsAreEmpty() {
		List<Query> queries = sut.createQueries(example);
		Query query = assertSingleQuery(queries);
		assertEquals(Sets.newHashSet(), query.getAllCallsites());
	}

	private Query assertSingleQuery(List<Query> queries) {
		assertEquals(1, queries.size());
		return queries.get(0);
	}

	private Usage createUsage(int numCalls) {
		Query ou = new Query();
		ou.setType(expectedType);
		ou.setClassContext(expectedClassContext);
		ou.setMethodContext(expectedMethodContext);
		ou.setDefinition(expectedDefinition);

		for (int i = 0; i < numCalls; i++) {
			ou.addCallSite(createCallSite(i));
		}
		return ou;
	}

	private static CallSite createCallSite(int i) {
		CallSite site = CallSites.createReceiverCallSite(VmMethodName.get("Lorg/bla/blubb/FirstClass.m" + i + "(I)V"));
		return site;
	}
}