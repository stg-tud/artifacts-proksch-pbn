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

import java.util.List;
import java.util.Set;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

public class UsageConverter {
	public Usage toRecommenderUsage(ObjectUsage ou) {
		ITypeName declaringType = ou.getContext().getName().getDeclaringType();
		ITypeName superclass = ou.getContext().getSuperclass();
		ITypeName firstMethodType = ou.getContext().getIntroducedBy();
		IMethodName enclosingMethod = ou.getContext().getName();

		Query q = new Query();
		q.setType(ou.getType());
		if (superclass != null) {
			q.setClassContext(superclass);
		} else {
			q.setClassContext(declaringType);
		}
		if (firstMethodType == null) {
			q.setMethodContext(enclosingMethod);
		} else {
			IMethodName firstMethod = VmMethodName.rebase(firstMethodType, enclosingMethod);
			q.setMethodContext(firstMethod);
		}
		IMethodName init = findInit(ou.getPaths());
		if (init == null) {
			q.setDefinition(toRecommenderDefinition(ou.getDef()));
		} else {
			q.setDefinition(DefinitionSites.createDefinitionByConstructor(init));
		}
		q.setAllCallsites(toRecommenderCalls(ou.getPaths()));
		return q;
	}

	private IMethodName findInit(Set<List<CallSite>> paths) {
		for (List<CallSite> path : paths) {
			for (CallSite cs : path) {
				IMethodName call = cs.getCall();
				if (call != null && call.isInit()) {
					return call;
				}
			}
		}
		return null;
	}

	public cc.recommenders.usages.DefinitionSite toRecommenderDefinition(DefinitionSite def) {
		switch (def.getKind()) {
		case CONSTANT:
			return DefinitionSites.createDefinitionByConstant();
		case FIELD:
			return DefinitionSites.createDefinitionByField(def.getField());
		case NEW:
			return DefinitionSites.createDefinitionByConstructor(def.getMethod());
		case PARAM:
			return DefinitionSites.createDefinitionByParam(def.getMethod(), def.getArgumentIndex());
		case RETURN:
			return DefinitionSites.createDefinitionByReturn(def.getMethod());
		case THIS:
			return DefinitionSites.createDefinitionByThis();
		case UNKNOWN:
			return DefinitionSites.createUnknownDefinitionSite();
		default:
			throw new RuntimeException("unreachable code?! null?!");
		}
	}

	public Set<cc.recommenders.usages.CallSite> toRecommenderCalls(Set<List<CallSite>> paths) {
		Set<cc.recommenders.usages.CallSite> calls = Sets.newLinkedHashSet();
		for (List<CallSite> path : paths) {
			for (CallSite cs : path) {
				switch (cs.getKind()) {
				case PARAM_CALL_SITE:
					calls.add(CallSites.createParameterCallSite(cs.getCall(), cs.getArgumentIndex()));
					break;
				case RECEIVER_CALL_SITE:
					IMethodName method = cs.getCall();
					if (method != null && !method.isInit()) {
						calls.add(CallSites.createReceiverCallSite(cs.getCall()));
					}
					break;
				default:
					throw new RuntimeException("unreachable code?! null?!");
				}
			}
		}
		return calls;
	}
}