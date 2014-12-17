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
package com.codetrails.data;

import static org.junit.Assert.assertFalse;
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

import com.codetrails.data.CallSite;
import com.codetrails.data.CallSiteKind;
import com.codetrails.data.ObjectUsageFilter;
import com.codetrails.data.DefinitionKind;
import com.codetrails.data.DefinitionSite;
import com.codetrails.data.DefinitionSites;
import com.codetrails.data.EnclosingMethodContext;
import com.codetrails.data.ObjectUsage;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ObjectUsageFilterTest {

	private ObjectUsageFilter sut;

	@Before
	public void setup() {
		sut = new ObjectUsageFilter();
	}

	@Test
	public void normalUsagesPass() {
		assertTrue(sut.apply(createValidObjectUsage()));
	}

	@Test
	public void arraysAreFiltered() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getDef().setType(VmTypeName.get("[LSomeType"));
		assertFalse(sut.apply(ou));
	}

	@Test
	public void anonymousClassesAreFiltered() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getDef().setType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button$1"));
		assertFalse(sut.apply(ou));
	}

	@Test
	public void nestedClassesAreNotFiltered() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getDef().setType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button$Nested"));
		assertTrue(sut.apply(ou));
	}

	@Test
	public void nullSupertypeIfNotOverridden() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getContext().setSuperclass(null);
		assertTrue(sut.apply(ou));
	}

	@Test
	public void nullFirstImplementationIfNotOverridden() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getContext().setIntroducedBy(null);
		assertTrue(sut.apply(ou));
	}

	@Test
	public void nonSwtTypesAreFiltered() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getDef().setType(VmTypeName.get("LType"));
		assertFalse(sut.apply(ou));
	}

	@Test
	public void usageWithUnknownDefIsFiltered() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getDef().setKind(DefinitionKind.UNKNOWN);
		assertFalse(sut.apply(ou));
	}

	@Test
	public void usageWithoutCallsIsFiltered() {
		ObjectUsage ou = createValidObjectUsage();
		ou.getPaths().iterator().next().clear();;
		assertFalse(sut.apply(ou));
	}

	private static ObjectUsage createValidObjectUsage() {
		ObjectUsage ou = new ObjectUsage();

		DefinitionSite definition = DefinitionSites.createDefinitionByReturn(VmMethodName.get("LType.get()V"));
		definition.setType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button"));
		ou.setDef(definition);

		EnclosingMethodContext ctx = new EnclosingMethodContext();
		ctx.setSuperclass(VmTypeName.get("LSuperType"));
		ctx.setIntroducedBy(mock(ITypeName.class));
		ctx.setName(mock(IMethodName.class));
		ou.setContext(ctx);

		Set<List<CallSite>> paths = Sets.newHashSet();
		List<CallSite> calls = Lists.newArrayList();
		calls.add(createParameterCallSite());
		calls.add(createReceiverCallSite());
		paths.add(calls);

		ou.setPaths(paths);
		return ou;
	}

	private static CallSite createParameterCallSite() {
		CallSite site = new CallSite();
		site.setKind(CallSiteKind.PARAM_CALL_SITE);
		site.setCall(mock(IMethodName.class));
		site.setArgumentIndex(12);
		return site;
	}

	private static CallSite createReceiverCallSite() {
		CallSite site = new CallSite();
		site.setKind(CallSiteKind.RECEIVER_CALL_SITE);
		site.setCall(mock(IMethodName.class));
		return site;
	}
}