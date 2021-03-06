/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.usages;

import cc.recommenders.names.IFieldName;
import cc.recommenders.names.IMethodName;

/**
 * @see cc.recommenders.usages.DefinitionSites
 */
public class DefinitionSite {

	// ensure consistent naming with hard-coded names in "UsageTypeAdapter"

	private DefinitionSiteKind kind;
	private IMethodName method;
	private IFieldName field;
	private int argIndex = -1;

	DefinitionSite() {
		// not meant to be instantiated manually
	}

	public DefinitionSiteKind getKind() {
		return this.kind;
	}

	public void setKind(final DefinitionSiteKind kind) {
		this.kind = kind;
	}

	public IMethodName getMethod() {
		return method;
	}

	public void setMethod(final IMethodName method) {
		this.method = method;
	}

	public IFieldName getField() {
		return field;
	}

	public void setField(final IFieldName field) {
		this.field = field;
	}

	public int getArgIndex() {
		return argIndex;
	}

	public void setArgIndex(final int argIndex) {
		this.argIndex = argIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + argIndex;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefinitionSite other = (DefinitionSite) obj;
		if (argIndex != other.argIndex)
			return false;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (kind != other.kind)
			return false;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (kind == null) {
			return "INVALID";
		}

		boolean isFieldMissing = kind == DefinitionSiteKind.FIELD && field == null;
		boolean isInit = kind == DefinitionSiteKind.NEW;
		boolean isParam = kind == DefinitionSiteKind.PARAM;
		boolean isReturn = kind == DefinitionSiteKind.RETURN;
		boolean isMethodMissing = method == null && (isInit || isParam || isReturn);

		if (isFieldMissing || isMethodMissing) {
			return "INVALID";
		}

		StringBuffer sb = new StringBuffer();
		switch (kind) {
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
			sb.append(argIndex);
			sb.append("):");
			sb.append(method);
			break;
		case RETURN:
			sb.append("RETURN:");
			sb.append(method);
			break;
		case THIS:
			sb.append(DefinitionSiteKind.THIS);
			break;
		case UNKNOWN:
			sb.append(DefinitionSiteKind.UNKNOWN);
			break;
		}
		return sb.toString();
	}
}