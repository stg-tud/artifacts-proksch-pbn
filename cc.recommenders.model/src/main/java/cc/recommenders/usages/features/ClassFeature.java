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

import static cc.recommenders.assertions.Asserts.assertNotNull;
import cc.recommenders.names.ITypeName;

public class ClassFeature extends UsageFeature {

	private final ITypeName typeName;

	public ClassFeature(ITypeName typeName) {
		assertNotNull(typeName);
		this.typeName = typeName;
	}

	public ITypeName getTypeName() {
		return typeName;
	}

	@Override
	public void accept(ObjectUsageFeatureVisitor v) {
		v.visit(this);
	}

	@Override
	public String toString() {
		return "Class@" + hashCode() + ":" + typeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
		ClassFeature other = (ClassFeature) obj;
		if (typeName == null) {
			if (other.typeName != null)
				return false;
		} else if (!typeName.equals(other.typeName))
			return false;
		return true;
	}
}