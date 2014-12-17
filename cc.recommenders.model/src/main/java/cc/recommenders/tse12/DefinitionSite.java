/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package cc.recommenders.tse12;

import cc.recommenders.names.IFieldName;
import cc.recommenders.names.IMethodName;

/**
 * a site is a location in source code
 */
public class DefinitionSite {
	public static enum DefinitionType {
		THIS, RETURN, NEW, PARAM, FIELD, CONSTANT, UNKNOWN
	}

	public DefinitionType type;
	/**
	 * Defined IFF {@link DefinitionType#RETURN} {@link DefinitionType#INIT}
	 */
	public IMethodName method;
	public IFieldName field;
	public int argumentIndex;

	public static DefinitionSite createDefinitionByConstructor(final IMethodName constructor) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.NEW;
		definitionSite.method = constructor;
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByReturn(final IMethodName method) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.RETURN;
		definitionSite.method = method;
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByField(final IFieldName field) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.FIELD;
		definitionSite.field = field;
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByParam(final IMethodName method, final int argumentIndex) {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.PARAM;
		definitionSite.method = method;
		definitionSite.argumentIndex = argumentIndex;
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByThis() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.THIS;
		definitionSite.argumentIndex = -1;
		return definitionSite;
	}

	public static DefinitionSite createDefinitionByConstant() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.CONSTANT;
		return definitionSite;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		switch (type) {
		case CONSTANT:
			sb.append("CONSTANT");
			break;
		case FIELD:
			sb.append("FIELD:");
			sb.append(field);
			break;
		case NEW:
			sb.append("INIT:");
			sb.append(method);
			break;
		case PARAM:
			sb.append("PARAM(");
			sb.append(argumentIndex);
			sb.append("):");
			sb.append(method);
			break;
		case RETURN:
			sb.append("RETURN:");
			sb.append(method);
			break;
		case THIS:
			sb.append(DefinitionType.THIS);
			break;
		case UNKNOWN:
			sb.append(DefinitionType.UNKNOWN);
			break;
		}
		return sb.toString();
		// return reflectionToString(this, MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + argumentIndex;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final DefinitionSite other = (DefinitionSite) obj;
		if (argumentIndex != other.argumentIndex) {
			return false;
		}
		if (field == null) {
			if (other.field != null) {
				return false;
			}
		} else if (!field.equals(other.field)) {
			return false;
		}
		if (method == null) {
			if (other.method != null) {
				return false;
			}
		} else if (!method.equals(other.method)) {
			return false;
		}
		if (type != other.type) {
			return false;
		}
		return true;
	}

	public static DefinitionSite createUnknownDefinitionSite() {
		final DefinitionSite definitionSite = new DefinitionSite();
		definitionSite.type = DefinitionType.UNKNOWN;
		return definitionSite;
	}
}