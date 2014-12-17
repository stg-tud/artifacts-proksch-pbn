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
package com.codetrails.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ObjectUsageValidatorTest {

	private ObjectUsageValidator sut;
	private ObjectUsage ou;

	@Before
	public void setup() {
		ou = createValidObjectUsage();
		sut = new ObjectUsageValidator();
	}

	private static ObjectUsage createValidObjectUsage() {
		ObjectUsage ou = new ObjectUsage();
		ou.setUuid(UUID.randomUUID());

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
		calls.add(createReceiverCallSite());
		paths.add(calls);

		ou.setPaths(paths);
		return ou;
	}

	private static CallSite createReceiverCallSite() {
		CallSite site = new CallSite();
		site.setKind(CallSiteKind.RECEIVER_CALL_SITE);
		site.setCall(mock(IMethodName.class));
		return site;
	}

	@Test
	public void normalUsagesPass() {
		assertValid();
	}

	@Test
	public void arraysPass() {
		ou.getDef().setType(VmTypeName.get("[LSomeType"));
		assertValid();
	}

	@Test
	public void anonymousClassesPass() {
		ou.getDef().setType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button$1"));
		assertValid();
	}

	@Test
	public void nestedClassesPass() {
		ou.getDef().setType(VmTypeName.get("Lorg/eclipse/swt/widgets/Button$Nested"));
		assertValid();
	}

	@Test
	public void nullIsMalformed() {
		ou = null;
		assertInvalid("usage is null");
	}

	@Test
	public void havingUuidIsMandatory() {
		ou.setUuid(null);
		assertInvalid("uuid is null");
	}

	@Test
	public void nullTypeIsMalformed() {
		ou.getDef().setType(null);
		assertInvalid("type is null");
	}

	@Test
	public void nullSupertypeIfNotOverridden() {
		ou.getContext().setSuperclass(null);
		assertValid();
	}

	@Test
	public void nullFirstImplementationIfNotOverridden() {
		ou.getContext().setIntroducedBy(null);
		assertValid();
	}

	@Test
	public void missingConstructorIsMalformedForNew() {
		DefinitionSite ds = DefinitionSites.createDefinitionByConstructor(null);
		ds.setType(ou.getType());
		ou.setDef(ds);
		assertInvalid("definition site lacks method");
	}

	@Test
	public void missingMethodIsMalformedForReturn() {
		DefinitionSite ds = DefinitionSites.createDefinitionByReturn(null);
		ds.setType(ou.getType());
		ou.setDef(ds);
		assertInvalid("definition site lacks method");
	}

	@Test
	public void malformed_noContextName() {
		ou.getContext().setName(null);
		assertInvalid("methodContext is missing");
	}

	@Test
	public void unnecessaryCallInDefIsRemoved() {
		ou.getDef().setKind(DefinitionKind.THIS);
		assertValid();
		assertNull(ou.getDef().getMethod());
	}

	@Test
	public void argIndexIsFixedForNonParameterDefinition() {
		ou.getDef().setArgumentIndex(3);
		assertValid();
		assertEquals(-1, ou.getDef().getArgumentIndex());
	}

	@Test
	public void argIndexIsUntouchedForParameterDefinition() {
		ou.getDef().setKind(DefinitionKind.PARAM);
		ou.getDef().setArgumentIndex(3);
		assertValid();
		assertEquals(3, ou.getDef().getArgumentIndex());
		assertNotNull(ou.getDef().getMethod());
	}

	@Test
	public void callsiteWithoutValueIsMalformed() {
		List<CallSite> firstPath = ou.getPaths().iterator().next();
		CallSite firstCallSite = firstPath.iterator().next();
		firstCallSite.setCall(null);
		assertInvalid("a CallSite is null");
	}

	@Test
	public void count_isZeroByDefault() {
		assertEquals(0, sut.getMalformedCount());
	}

	@Test
	public void count_malformedUsagesAreCounted() {
		assertEquals(0, sut.getMalformedCount());
		malformed_noContextName();
		assertEquals(1, sut.getMalformedCount());
	}

	@Test
	public void errorsCanBeResetted() {
		malformed_noContextName();
		sut.reset();
		assertEquals(0, sut.getMalformedCount());
		assertEquals("", sut.getLastError());
	}

	private void assertValid() {
		assertTrue(sut.isValid(ou));
	}

	private void assertInvalid(String cause) {
		assertFalse(sut.isValid(ou));
		String actualCause = sut.getLastError();
		String expectedCause;
		if (ou != null && ou.getUuid() != null) {
			expectedCause = cause + " (" + ou.getUuid() + ")";
		} else {
			expectedCause = cause;
		}
		assertEquals(expectedCause, actualCause);
	}
}