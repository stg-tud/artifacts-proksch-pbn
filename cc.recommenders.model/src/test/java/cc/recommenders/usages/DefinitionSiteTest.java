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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cc.recommenders.names.IFieldName;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;

public class DefinitionSiteTest {

	private static final IFieldName FIELD1 = VmFieldName.get("Lsome/Type.name;Lfield1/Type");
	private static final IFieldName FIELD2 = VmFieldName.get("Lsome/Type.name;Lfield2/Type");

	private static final IMethodName METHOD1 = VmMethodName.get("Lsome/Type.m1()V");
	private static final IMethodName METHOD2 = VmMethodName.get("Lsome/Type.m2()V");

	// toString implementation is tested in DefinitionSitesTest

	@Test
	public void defaultValues() {
		DefinitionSite sut = new DefinitionSite();
		assertEquals(-1, sut.getArgIndex());
		assertEquals(null, sut.getField());
		assertEquals(null, sut.getKind());
		assertEquals(null, sut.getMethod());
	}

	@Test
	public void eqAndHashCodeOnNullValues() {
		DefinitionSite a = new DefinitionSite();
		DefinitionSite b = new DefinitionSite();
		assertEquals(a, b);
		assertTrue(a.hashCode() == b.hashCode());
	}

	@Test
	public void equalsHashCode_equals() {
		DefinitionSite a = newDefinitionSite();
		DefinitionSite b = newDefinitionSite();
		assertEquals(a, b);
		assertTrue(a.hashCode() == b.hashCode());
	}

	@Test
	public void equalsHashCode_argIdxDifferent() {
		DefinitionSite a = newDefinitionSite();
		a.setArgIndex(0);
		DefinitionSite b = newDefinitionSite();
		assertNotEquals(a, b);
		assertFalse(a.hashCode() == b.hashCode());
	}

	@Test
	public void equalsHashCode_fieldDifferent() {
		DefinitionSite a = newDefinitionSite();
		a.setField(FIELD2);
		DefinitionSite b = newDefinitionSite();
		assertNotEquals(a, b);
		assertFalse(a.hashCode() == b.hashCode());
	}

	@Test
	public void equalsHashCode_kindDifferent() {
		DefinitionSite a = newDefinitionSite();
		a.setKind(DefinitionSiteKind.NEW);
		DefinitionSite b = newDefinitionSite();
		assertNotEquals(a, b);
		assertFalse(a.hashCode() == b.hashCode());
	}

	@Test
	public void equalsHashCode_methodDifferent() {
		DefinitionSite a = newDefinitionSite();
		a.setMethod(METHOD2);
		DefinitionSite b = newDefinitionSite();
		assertNotEquals(a, b);
		assertFalse(a.hashCode() == b.hashCode());
	}

	private DefinitionSite newDefinitionSite() {
		DefinitionSite d = new DefinitionSite();
		d.setArgIndex(13);
		d.setField(FIELD1);
		d.setKind(DefinitionSiteKind.CONSTANT);
		d.setMethod(METHOD1);
		return d;
	}
}