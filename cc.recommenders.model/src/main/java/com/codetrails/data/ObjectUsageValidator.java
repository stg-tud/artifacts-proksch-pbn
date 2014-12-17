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

public class ObjectUsageValidator {

	private int errorCount = 0;
	private String lastError = "";

	public String getLastError() {
		return lastError;
	}

	public void reset() {
		errorCount = 0;
		lastError = "";
	}

	public int getMalformedCount() {
		return errorCount;
	}

	public boolean isValid(ObjectUsage usage) {
		if (usage == null) {
			err("usage is null");
			return false;
		}

		if (usage.getUuid() == null) {
			err("uuid is null");
			return false;
		}

		if (usage.getType() == null) {
			err("type is null (%s)", usage.getUuid());
			return false;
		}

		if (!hasFirstMethod(usage)) {
			err("methodContext is missing (%s)", usage.getUuid());
			return false;
		}

		if (hasNullCalls(usage)) {
			err("a CallSite is null (%s)", usage.getUuid());
			return false;
		}

		if (isDefinitionMalformed(usage)) {
			return false;
		}

		return true;
	}

	private boolean hasFirstMethod(ObjectUsage usage) {
		return usage.getContext().getName() != null;
	}

	private boolean hasNullCalls(ObjectUsage usage) {
		for (List<CallSite> path : usage.getPaths()) {
			for (CallSite site : path) {
				if (site.getCall() == null) {
					// TODO remove just the callsite or path instead of ignoring
					// complete usage
					return true;
				}
			}
		}
		return false;
	}

	private boolean isDefinitionMalformed(ObjectUsage usage) {

		DefinitionSite def = usage.getDef();

		boolean isDefParam = DefinitionKind.PARAM.equals(def.getKind());
		boolean isDefNew = DefinitionKind.NEW.equals(def.getKind());
		boolean isDefReturn = DefinitionKind.RETURN.equals(def.getKind());
		boolean isMethodExpected = isDefNew || isDefReturn || isDefParam;

		if (!isDefParam) {
			def.setArgumentIndex(-1);
		}

		if (isMethodExpected) {
			if (def.getMethod() == null) {
				err("definition site lacks method (%s)", usage.getUuid());
				return true;
			}
		} else {
			def.setMethod(null);
		}

		return false;
	}

	private void err(String msg, Object... args) {
		errorCount++;
		lastError = String.format(msg, args);
	}
}