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
package cc.recommenders.evaluation.distribution.calc;

import static java.lang.String.format;

import java.io.Serializable;

public abstract class AbstractTask implements Serializable {

	private static final long serialVersionUID = 9180495389887500614L;

	public String app;
	public String options;
	public String typeName;
	public int currentFold;
	public int numFolds;

	public double processingTimeInS;

	@Override
	public String toString() {
		String taskType = getClass().getSimpleName();
		String basic = format("%s: %s - %s (fold %d/%d)", taskType, app, typeName, (currentFold + 1), numFolds);
		String details = detailsToString().isEmpty() ? "" : format(" - %s", detailsToString());
		String result = hasResult() ? format(" - %s (took %.1fs)", resultToString(), processingTimeInS) : "";
		return format("%s%s%s", basic, details, result);
	}

	protected String detailsToString() {
		return "";
	}

	protected boolean hasResult() {
		return false;
	}

	protected String resultToString() {
		return "";
	}
}