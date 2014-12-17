/**
 * Copyright (c) 2011-2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.usages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;

import com.codetrails.data.EnclosingMethodContext;
import com.codetrails.data.ObjectUsage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class DecoratedObjectUsageTest {

	private ObjectUsage usage;
	private ObjectUsageBuilder builder;

	private DecoratedObjectUsage sut;

	@Before
	public void setup() {
		usage = new ObjectUsage();
		builder = new ObjectUsageBuilder();
		sut = new DecoratedObjectUsage(usage);
	}

	@Test
	public void decoratedUsageIsAccessible() {
		ObjectUsage actual = sut.getOriginal();
		assertSame(usage, actual);
	}

	@Test
	public void typeIsPropagated() {
		ITypeName expected = mock(ITypeName.class);
		com.codetrails.data.DefinitionSite ds = new com.codetrails.data.DefinitionSite();
		ds.setType(expected);
		usage.setDef(ds);
		ITypeName actual = sut.getType();
		assertEquals(expected, actual);
	}

	@Test
	public void classContextIsPropagated() {
		ITypeName expected = mock(ITypeName.class);
		EnclosingMethodContext ctx = new EnclosingMethodContext();
		ctx.setSuperclass(expected);
		usage.setContext(ctx);
		ITypeName actual = sut.getClassContext();
		assertEquals(expected, actual);
	}

	@Test
	public void methodContextIsPropagated() {
		IMethodName method = VmMethodName.get("LClient.doit()V");
		ITypeName intro = VmTypeName.get("LFramework");
		IMethodName expected = VmMethodName.get("LFramework.doit()V");

		EnclosingMethodContext ctx = new EnclosingMethodContext();
		ctx.setName(method);
		ctx.setIntroducedBy(intro);
		usage.setContext(ctx);

		IMethodName actual = sut.getMethodContext();
		assertEquals(expected, actual);
	}

	@Test
	public void definitionSite_constant() {
		usage.setDef(com.codetrails.data.DefinitionSites.createDefinitionByConstant());
		DefinitionSite actual = sut.getDefinitionSite();
		DefinitionSite expected = DefinitionSites.createDefinitionByConstant();
		assertEquals(expected, actual);
	}

	@Test
	public void definitionSite_return() {
		usage.setDef(com.codetrails.data.DefinitionSites.createDefinitionByReturn(getMethodName("a")));
		DefinitionSite actual = sut.getDefinitionSite();
		DefinitionSite expected = DefinitionSites.createDefinitionByReturn(getMethodName("a"));
		assertEquals(expected, actual);
	}

	@Test
	public void callsitesArePropagated() {
		builder.newPath().call("a").param("b");
		builder.newPath().param("c").call("d");

		sut = new DecoratedObjectUsage(builder.build());

		assertSites(sut.getAllCallsites(), "a", "b", "c", "d");

	}

	@Test
	public void callsitesAreUniquelyPropagated() {
		builder.newPath().call("a").param("b");
		builder.newPath().param("c").call("a").call("d");

		sut = new DecoratedObjectUsage(builder.build());

		assertSites(sut.getAllCallsites(), "a", "b", "c", "d");
	}

	@Test
	public void receiverCallsitesArePropagated() {
		builder.newPath().call("a").param();
		builder.newPath().param().call("d");

		sut = new DecoratedObjectUsage(builder.build());

		assertSites(sut.getReceiverCallsites(), "a", "d");

	}

	@Test
	public void receiverCallsitesAreUniquelyPropagated() {
		builder.newPath().call("a").param();
		builder.newPath().param().call("a").call("d");

		sut = new DecoratedObjectUsage(builder.build());

		assertSites(sut.getReceiverCallsites(), "a", "d");
	}

	@Test
	public void parameterCallsitesArePropagated() {
		builder.newPath().call("a").param("b");
		builder.newPath().param("c").call("d");

		sut = new DecoratedObjectUsage(builder.build());

		assertSites(sut.getParameterCallsites(), "b", "c");

	}

	@Test
	public void parameterCallsitesAreUniquelyPropagated() {
		builder.newPath().call().param("b");
		builder.newPath().param("b").param("c").call();

		sut = new DecoratedObjectUsage(builder.build());

		assertSites(sut.getParameterCallsites(), "b", "c");
	}

	private static IMethodName getMethodName(String name) {
		return VmMethodName.get(String.format("LType.method_%s()V", name));
	}

	private static void assertSites(Set<CallSite> sites, String... names) {
		assertEquals(names.length, sites.size());
		for (String suffix : names) {
			IMethodName methodName = getMethodName(suffix);
			assertTrue(containsMethod(sites, methodName));
		}
	}

	private static boolean containsMethod(Set<CallSite> path, IMethodName methodName) {
		for (CallSite site : path) {
			if (site.getMethod().equals(methodName)) {
				return true;
			}
		}
		return false;
	}

	private static class ObjectUsageBuilder {

		private List<PathBuilder> pathBuilders = Lists.newLinkedList();

		public ObjectUsage build() {
			ObjectUsage result = new ObjectUsage();

			com.codetrails.data.DefinitionSite ds = com.codetrails.data.DefinitionSites.createUnknownDefinitionSite();
			ds.setType(VmTypeName.get("LType"));
			result.setDef(ds);

			EnclosingMethodContext ctx = new EnclosingMethodContext();

			ctx.setSuperclass(VmTypeName.get("LSuperType"));
			ctx.setName(VmMethodName.get("LType.method()V"));
			ctx.setIntroducedBy(VmTypeName.get("LFirstType"));

			Set<List<com.codetrails.data.CallSite>> paths = Sets.newLinkedHashSet();

			for (PathBuilder pathBuilder : pathBuilders) {
				paths.add(pathBuilder.build());
			}

			result.setPaths(paths);
			return result;
		}

		public PathBuilder newPath() {
			PathBuilder pathBuilder = new PathBuilder();
			pathBuilders.add(pathBuilder);
			return pathBuilder;
		}

		private static class PathBuilder {
			private int methodNumber = 0;
			List<com.codetrails.data.CallSite> sites = Lists.newLinkedList();

			public PathBuilder param() {
				return param(getUniqueName());
			}

			public PathBuilder param(String name) {
				com.codetrails.data.CallSite site = com.codetrails.data.CallSites.createParameterCallSite(
						getMethodName(name), 1);
				sites.add(site);
				return this;
			}

			public PathBuilder call() {
				return call(getUniqueName());
			}

			public PathBuilder call(String name) {
				com.codetrails.data.CallSite site = com.codetrails.data.CallSites
						.createReceiverCallSite(getMethodName(name));
				sites.add(site);
				return this;
			}

			public List<com.codetrails.data.CallSite> build() {
				return sites;
			}

			private String getUniqueName() {
				return "uniqueMethod" + methodNumber++;
			}
		}
	}
}