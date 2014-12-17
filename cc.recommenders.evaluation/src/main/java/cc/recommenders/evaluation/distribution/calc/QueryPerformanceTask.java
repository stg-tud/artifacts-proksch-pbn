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

public class QueryPerformanceTask extends AbstractTask {

	private static final long serialVersionUID = 436949561959032708L;

	public int inputSize;

	public int modelSize;
	public double learningDurationInS;
	public double perQueryDurationInMS;

	@Override
	protected String detailsToString() {
		return String.format("input size: %d", inputSize);
	}
}