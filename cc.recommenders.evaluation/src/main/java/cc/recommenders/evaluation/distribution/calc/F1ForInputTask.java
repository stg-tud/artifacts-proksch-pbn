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

public class F1ForInputTask extends AbstractTask {

	private static final long serialVersionUID = 1688301581995154488L;

	public int inputSize;
	public double[] f1s;

	@Override
	protected String detailsToString() {
		return String.format("input size: %d", inputSize);
	}
}