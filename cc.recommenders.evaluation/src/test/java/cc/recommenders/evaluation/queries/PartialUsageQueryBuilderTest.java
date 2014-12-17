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

import static cc.recommenders.usages.CallSiteKind.PARAMETER;
import static cc.recommenders.usages.CallSiteKind.RECEIVER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.CallSiteKind;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

public class PartialUsageQueryBuilderTest {

	private PartialUsageQueryBuilder sut;
	private UsageBuilder builder;

	@Before
	public void setup() {
		builder = new UsageBuilder();
		sut = new PartialUsageQueryBuilder();
		sut.setNumOfQueries(1);
	}

	@Test
	public void defaultNumberOfQueries() {
		assertEquals(3, new PartialUsageQueryBuilder().getNumOfQueries());
	}

	@Test
	public void defaultsCanBeChanged() {
		assertEquals(0.5, sut.getPercentage(), 0.0001);
		sut.setPercentage(0.6);
		assertEquals(0.6, sut.getPercentage(), 0.0001);

		assertEquals(1, sut.getNumOfQueries());
		sut.setNumOfQueries(2);
		assertEquals(2, sut.getNumOfQueries());
	}

	@Test
	public void ensureAllValuesAreCopied() {
		Usage expected = builder.build();
		Query actual = sut.createQueries(expected).get(0);
		assertNotSame(expected, actual);
		assertEquals(expected.getType(), actual.getType());
		assertEquals(expected.getClassContext(), actual.getClassContext());
		assertEquals(expected.getMethodContext(), actual.getMethodContext());
		assertEquals(expected.getDefinitionSite(), actual.getDefinitionSite());
	}

	@Test
	public void trailingParametersAreAlsoReturned() {
		builder.call("a").param("b");

		sut.setPercentage(1.0);
		Query q = assertSingleQuery();

		assertCallSites(q.getAllCallsites(), "a", "b");
	}

	@Test
	public void halfOfTheCallsAreRemovedForPathsWithEvenLength() {
		builder.call().call().call().call();
		Query q = assertSingleQuery();

		assertEquals(2, q.getReceiverCallsites().size());
		assertEquals(0, q.getParameterCallsites().size());
	}

	@Test
	public void halfOfTheCallsAreRemovedForPathsWithOddLength() {
		builder.call().call().call();
		Query q = assertSingleQuery();

		assertEquals(1, q.getReceiverCallsites().size());
		assertEquals(0, q.getParameterCallsites().size());
	}

	@Test
	public void halfOfTheCallsAreRemovedForPathsContainingParameters() {
		builder.param("a").call("b").param("c").call().param();
		Query q = assertSingleQuery();

		assertEquals(1, q.getReceiverCallsites().size());
		assertEquals(1, q.getParameterCallsites().size());
	}

	@Test
	public void halfOfTheCallsAreRemovedForPathsWithLeadingParameters() {
		builder.param("a").param("b").param("c").call("d").call().call();
		Query q = assertSingleQuery();

		assertEquals(1, q.getReceiverCallsites().size());
		assertEquals(1, q.getParameterCallsites().size());
	}

	@Test
	public void halfOfTheCallsAreRemovedForPathsWithTrailingParameters() {
		builder.call().call().call().param().param().param();
		Query q = assertSingleQuery();

		assertEquals(1, q.getReceiverCallsites().size());
		assertEquals(1, q.getParameterCallsites().size());
	}

	@Test
	public void pathsWithOnlyOneCall() {
		builder.call("a");
		Query q = assertSingleQuery();

		assertCallSites(q.getAllCallsites());
	}

	@Test
	public void parametersAreReturnedForPathsWithOnlyOneCall() {
		builder.param("a").call().param();
		Query q = assertSingleQuery();

		assertEquals(0, q.getReceiverCallsites().size());
		assertEquals(1, q.getParameterCallsites().size());
	}

	@Test
	public void multipleQueries_0() {
		builder.param("a").call("b").param("c").call("d").param("e").call("f");
		List<Query> queries = createQueries(2);
		assertEquals(2, queries.size());

		Set<CallSite> a = queries.get(0).getAllCallsites();
		Set<CallSite> b = queries.get(1).getAllCallsites();
		assertNotEquals(a, b);

		assertEquals(1, count(RECEIVER, a));
		assertEquals(1, count(PARAMETER, a));

		assertEquals(1, count(RECEIVER, b));
		assertEquals(1, count(PARAMETER, b));
	}

	@Test
	public void multipleQueries_1() {
		builder.param("a").call("b").param("c").call("d").param("e").param("f");
		List<Query> queries = createQueries(2);
		assertEquals(2, queries.size());

		Set<CallSite> a = queries.get(0).getAllCallsites();
		Set<CallSite> b = queries.get(1).getAllCallsites();
		assertNotEquals(a, b);

		assertEquals(1, count(RECEIVER, a));
		assertEquals(2, count(PARAMETER, a));

		assertEquals(1, count(RECEIVER, b));
		assertEquals(2, count(PARAMETER, b));
	}

	@Test
	public void multipleQueries_2() {
		builder.call("a").call("b").param("c").call("d").param("e").call("f");
		List<Query> queries = createQueries(2);
		assertEquals(2, queries.size());

		Set<CallSite> a = queries.get(0).getAllCallsites();
		Set<CallSite> b = queries.get(1).getAllCallsites();
		assertNotEquals(a, b);

		assertEquals(2, count(RECEIVER, a));
		assertEquals(1, count(PARAMETER, a));

		assertEquals(2, count(RECEIVER, b));
		assertEquals(1, count(PARAMETER, b));
	}

	@Test
	public void multipleQueries_3() {
		builder.call("a").param("b").param("c");
		List<Query> queries = createQueries(10);
		assertEquals(2, queries.size());

		Set<CallSite> a = queries.get(0).getAllCallsites();
		Set<CallSite> b = queries.get(1).getAllCallsites();
		assertNotEquals(a, b);

		assertEquals(0, count(RECEIVER, a));
		assertEquals(1, count(PARAMETER, a));

		assertEquals(0, count(RECEIVER, b));
		assertEquals(1, count(PARAMETER, b));
	}

	@Test
	public void multipleQueries_4() {
		builder.call().call().param().param();
		List<Query> queries = createQueries(2);
		assertEquals(2, queries.size());

		Set<CallSite> a = queries.get(0).getAllCallsites();
		Set<CallSite> b = queries.get(1).getAllCallsites();
		assertNotEquals(a, b);

		assertEquals(1, count(RECEIVER, a));
		assertEquals(1, count(PARAMETER, a));

		assertEquals(1, count(RECEIVER, b));
		assertEquals(1, count(PARAMETER, b));
	}

	private List<Query> createQueries(int numOfQueries) {
		sut.setNumOfQueries(numOfQueries);
		return sut.createQueries(builder.build());
	}

	private Query assertSingleQuery() {
		List<Query> queries = sut.createQueries(builder.build());
		assertEquals(1, queries.size());
		return queries.get(0);
	}

	private void assertCallSites(Collection<CallSite> path, String... suffixes) {
		assertEquals(suffixes.length, path.size());
		for (String suffix : suffixes) {
			IMethodName methodName = getMethodName(suffix);
			assertTrue(containsMethod(path, methodName));
		}
	}

	private boolean containsMethod(Collection<CallSite> path, IMethodName methodName) {
		for (CallSite site : path) {
			if (site.getMethod().equals(methodName)) {
				return true;
			}
		}
		return false;
	}

	public static IMethodName getMethodName(String name) {
		return VmMethodName.get(String.format("LType.method_%s()V", name));
	}

	private static int count(CallSiteKind kind, Iterable<CallSite> cs) {
		int count = 0;
		for (CallSite c : cs) {
			if (kind.equals(c.getKind())) {
				count++;
			}
		}
		return count;
	}

	public static class UsageBuilder {

		private int methodNumber = 0;

		Set<CallSite> sites = Sets.newLinkedHashSet();

		public Usage build() {
			Query q = new Query();
			q.setType(VmTypeName.get("LType"));
			q.setClassContext(VmTypeName.get("LSuperType"));
			q.setMethodContext(VmMethodName.get("LFirstType.method()V"));
			q.setDefinition(DefinitionSites.createDefinitionByConstant());
			q.setAllCallsites(sites);
			return q;
		}

		private String getUniqueName() {
			return "uniqueMethod" + methodNumber++;
		}

		public UsageBuilder param() {
			return param(getUniqueName());
		}

		public UsageBuilder param(String name) {
			CallSite site = CallSites.createParameterCallSite(getMethodName(name), 1);
			sites.add(site);
			return this;
		}

		public UsageBuilder call() {
			return call(getUniqueName());
		}

		public UsageBuilder call(String name) {
			CallSite site = CallSites.createReceiverCallSite(getMethodName(name));
			sites.add(site);
			return this;
		}
	}
}