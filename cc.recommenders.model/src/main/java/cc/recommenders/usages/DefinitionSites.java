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

import cc.recommenders.assertions.Asserts;
import cc.recommenders.names.IFieldName;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;

/**
 * helper class to instantiate DefinitionSite instances with correct parameters
 */
public class DefinitionSites {

	public static DefinitionSite createDefinitionByConstructor(String constructor) {
		return createDefinitionByConstructor(VmMethodName.get(constructor));
	}

	public static DefinitionSite createDefinitionByConstructor(IMethodName constructor) {
		Asserts.assertTrue(constructor.isInit());
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.NEW);
		definitionSite.setMethod(constructor);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByReturn(String method) {
		return createDefinitionByReturn(VmMethodName.get(method));
	}

	public static DefinitionSite createDefinitionByReturn(IMethodName method) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.RETURN);
		definitionSite.setMethod(method);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByField(String field) {
		return createDefinitionByField(VmFieldName.get(field));
	}

	public static DefinitionSite createDefinitionByField(IFieldName field) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.FIELD);
		definitionSite.setField(field);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByParam(String method, int argIndex) {
		return createDefinitionByParam(VmMethodName.get(method), argIndex);
	}

	public static DefinitionSite createDefinitionByParam(IMethodName method, int argIndex) {
		Asserts.assertNotNull(method);
		Asserts.assertGreaterOrEqual(argIndex, -1);
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.PARAM);
		definitionSite.setMethod(method);
		definitionSite.setArgIndex(argIndex);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByThis() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.THIS);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByConstant() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.CONSTANT);
		return definitionSite;
	}

	public static DefinitionSite createUnknownDefinitionSite() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionSiteKind.UNKNOWN);
		return definitionSite;
	}
}