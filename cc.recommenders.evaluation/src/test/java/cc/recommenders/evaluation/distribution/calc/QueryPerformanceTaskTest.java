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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class QueryPerformanceTaskTest {

	private QueryPerformanceTask sut;

	@Test
	public void detailsToString() {
		sut = new QueryPerformanceTask();
		sut.inputSize = 1234;

		String actual = sut.detailsToString();
		String expected = "input size: 1234";
		assertEquals(expected, actual);
	}
}