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

import cc.recommenders.names.IFieldName;
import cc.recommenders.names.IMethodName;

import com.codetrails.data.DefinitionSite;
import com.codetrails.data.DefinitionKind;;

public class DefinitionSites {

	public static DefinitionSite createDefinitionByConstructor(final IMethodName constructor) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.NEW);
		definitionSite.setMethod(constructor);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByReturn(final IMethodName method) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.RETURN);
		definitionSite.setMethod(method);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByField(final IFieldName field) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.FIELD);
		definitionSite.setField(field);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByParam(final IMethodName method, final int argumentIndex) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.PARAM);
		definitionSite.setMethod(method);
		definitionSite.setArgumentIndex(argumentIndex);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByThis() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.THIS);
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByConstant() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.CONSTANT);
		return definitionSite;
	}

	public static DefinitionSite createUnknownDefinitionSite() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.setKind(DefinitionKind.UNKNOWN);
		return definitionSite;
	}
}