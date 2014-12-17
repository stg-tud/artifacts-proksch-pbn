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
package com.codetrails.data;

import static com.codetrails.data.DefinitionKind.UNKNOWN;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Predicate;

public class ObjectUsageFilter implements Predicate<ObjectUsage> {

	private Pattern anonymousClassPattern = Pattern.compile(".*\\$[1-9]+[0-9]*$");

	@Override
	public boolean apply(ObjectUsage usage) {

		if (isArray(usage)) {
			return false;
		}

		if (isAnonymousClass(usage)) {
			return false;
		}

		if (!hasReceiverCallSites(usage)) {
			return false;
		}

		// TODO discuss whether UNKNOWN definitions should be ignored or not!
		if (UNKNOWN.equals(usage.getDefinitionKind())) {
			return false;
		}

		boolean isSwtWidget = usage.getType().getIdentifier().startsWith("Lorg/eclipse/swt/widgets/");
		if (!isSwtWidget) {
			return false;
		}

		return true;
	}

	private boolean isArray(ObjectUsage usage) {
		return usage.getType().getIdentifier().startsWith("[");
	}

	private boolean isAnonymousClass(ObjectUsage usage) {
		Matcher matcher = anonymousClassPattern.matcher(usage.getType().getIdentifier());
		return matcher.matches();
	}

	private static boolean hasReceiverCallSites(ObjectUsage usage) {
		int numCalls = 0;
		for (List<CallSite> calls : usage.getPaths()) {
			for (CallSite site : calls) {
				if (site.getKind().equals(CallSiteKind.RECEIVER_CALL_SITE)) {
					numCalls++;
				}
			}
		}
		return numCalls > 0;
	}
}