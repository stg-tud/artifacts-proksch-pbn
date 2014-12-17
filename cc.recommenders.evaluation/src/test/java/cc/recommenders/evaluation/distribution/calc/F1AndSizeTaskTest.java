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

public class F1AndSizeTaskTest {

	private F1AndSizeTask sut;

	@Test
	public void toStringTest() {
		sut = new F1AndSizeTask();
		sut.app = "APP";
		sut.typeName = "TYPE";
		sut.numFolds = 13;
		sut.currentFold = 4;
		String actual = sut.toString();
		String expected = "F1AndSizeTask: APP - TYPE (fold 5/13)";
		assertEquals(expected, actual);
	}
}