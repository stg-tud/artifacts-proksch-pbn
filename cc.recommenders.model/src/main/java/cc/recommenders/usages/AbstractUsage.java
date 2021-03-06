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

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

import java.util.Set;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;

import com.google.common.collect.Sets;

public abstract class AbstractUsage implements Usage {

	public abstract ITypeName getType();

	public abstract ITypeName getClassContext();

	public abstract IMethodName getMethodContext();

	public abstract DefinitionSite getDefinitionSite();

	/**
	 * @return concatenation of paths of the underlying usage, which contains
	 *         each callsite exactly once
	 */
	public abstract Set<CallSite> getAllCallsites();

	/**
	 * @return concatenation of paths of the underlying usage, which contains
	 *         each receiver callsite exactly once
	 */
	public Set<CallSite> getReceiverCallsites() {
		Set<CallSite> filtered = Sets.newLinkedHashSet();
		for (CallSite site : getAllCallsites()) {
			boolean isReceiverCall = site.getKind().equals(CallSiteKind.RECEIVER);
			if (isReceiverCall) {
				filtered.add(site);
			}
		}
		return filtered;
	}

	/**
	 * @return concatenation of paths of the underlying usage, which contains
	 *         each parameter callsite exactly once
	 */
	public Set<CallSite> getParameterCallsites() {
		Set<CallSite> filtered = Sets.newLinkedHashSet();
		for (CallSite site : getAllCallsites()) {
			boolean isReceiverCall = site.getKind().equals(CallSiteKind.PARAMETER);
			if (isReceiverCall) {
				filtered.add(site);
			}
		}
		return filtered;
	}

	@Override
	public String toString() {
		return reflectionToString(this, MULTI_LINE_STYLE);
	}

	@Override
	public boolean equals(Object obj) {
		return reflectionEquals(this, obj);
	}

	@Override
	public int hashCode() {
		return reflectionHashCode(this);
	}
}