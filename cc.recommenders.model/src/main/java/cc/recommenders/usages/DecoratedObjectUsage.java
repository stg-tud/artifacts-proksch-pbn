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

import java.util.Set;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;

import com.codetrails.data.EnclosingMethodContext;
import com.codetrails.data.ObjectUsage;
import com.codetrails.data.UsageConverter;

public class DecoratedObjectUsage extends AbstractUsage {

	private com.codetrails.data.ObjectUsage usage;
	private UsageConverter converter = new UsageConverter();

	public DecoratedObjectUsage(com.codetrails.data.ObjectUsage usage) {
		this.usage = usage;
	}

	public ITypeName getType() {
		return usage.getType();
	}

	public ITypeName getClassContext() {
		ITypeName superclass = usage.getContext().getSuperclass();
		if (superclass == null) {
			// Logger.log("rewriting class context");
			return usage.getContext().getName().getDeclaringType();
		} else {
			return superclass;
		}
	}

	public IMethodName getMethodContext() {
		EnclosingMethodContext context = usage.getContext();
		IMethodName method = context.getName();
		ITypeName firstType = context.getIntroducedBy();
		if (firstType == null) {
			// Logger.log("rewriting method context");
			return method;
		} else {
			IMethodName firstDeclaration = VmMethodName.rebase(firstType, method);
			return firstDeclaration;
		}
	}

	public DefinitionSite getDefinitionSite() {
		return converter.toRecommenderDefinition(usage.getDef());
	}

	/**
	 * @return concatenation of paths of the underlying usage, which contains
	 *         each callsite exactly once
	 */
	public Set<CallSite> getAllCallsites() {
		// TODO write tests for rebasing
		// TODO get rid of rebasing?
		// site.targetMethod = VmMethodName.rebase(usage.type,
		// site.targetMethod);
		return converter.toRecommenderCalls(usage.getPaths());
	}

	public ObjectUsage getOriginal() {
		return usage;
	}
}