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
package cc.recommenders.usages.features;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.features.UsageFeature.ObjectUsageFeatureVisitor;

public class DefinitionFeatureTest {

	private DefinitionSite definitionSite;
	private DefinitionFeature sut;

	@Before
	public void setup() {
		definitionSite = DefinitionSites.createDefinitionByConstant();
		sut = new DefinitionFeature(definitionSite);
	}

	@Test(expected = RuntimeException.class)
	public void definitionMustNotBeNull() {
		new DefinitionFeature(null);
	}

	@Test
	public void assignedMethodIsReturned() {
		DefinitionSite actual = sut.getDefinitionSite();
		DefinitionSite expected = definitionSite;

		assertEquals(expected, actual);
	}

	@Test
	public void twoDefinitionFeaturesWithTheSameDefinitionAreEqual() {
		DefinitionFeature df1 = createDefinition("A");
		DefinitionFeature df2 = createDefinition("A");
		assertNotSame(df1, df2);
		assertTrue(df1.equals(df2));
	}

	@Test
	public void twoDefinitionFeaturesWithDifferentDefinitionAreNotEqual() {
		DefinitionFeature df1 = createDefinition("A");
		DefinitionFeature df2 = createDefinition("B");
		assertNotSame(df1, df2);
		assertFalse(df1.equals(df2));
	}

	@Test
	public void parameterFeaturesAreEqualIfMethodIsOnlyDifference() {

		IMethodName m1 = VmMethodName.get("LBla.blubb()V");
		IMethodName m2 = VmMethodName.get("LBla.blubb()V");

		DefinitionFeature df1 = createParamDefinition(m1, 1);
		DefinitionFeature df2 = createParamDefinition(m2, 1);

		assertNotSame(df1, df2);
		assertTrue(df1.equals(df2));
		assertTrue(df1.hashCode() == df2.hashCode());
	}

	@Test
	public void parameterFeaturesAreUnequalIfArgIndexIsDifferent() {

		IMethodName methodName = mock(IMethodName.class);
		DefinitionFeature df1 = createParamDefinition(methodName, 1);
		DefinitionFeature df2 = createParamDefinition(methodName, 2);

		assertNotSame(df1, df2);
		assertFalse(df1.equals(df2));
		assertFalse(df1.hashCode() == df2.hashCode());
	}

	@Test
	public void visitorIsImplemented() {
		final boolean[] res = new boolean[] { false };
		sut.accept(new ObjectUsageFeatureVisitor() {
			@Override
			public void visit(DefinitionFeature f) {
				res[0] = true;
			}
		});
		assertTrue(res[0]);
	}

	private static DefinitionFeature createDefinition(String fieldName) {
		VmFieldName vmFieldName = VmFieldName.get("LType." + fieldName + ";LOther");
		return new DefinitionFeature(DefinitionSites.createDefinitionByField(vmFieldName));
	}

	private static DefinitionFeature createParamDefinition(IMethodName methodName, int argIndex) {
		DefinitionSite ds = DefinitionSites.createDefinitionByParam(methodName, argIndex);
		return new DefinitionFeature(ds);
	}

	// TODO write tests for hashCode + equals
}