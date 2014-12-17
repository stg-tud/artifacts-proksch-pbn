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
package cc.recommenders.testutils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.io.Logger;

public class LoggerUtilsTest {

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void positiveMatching() {
		Logger.log("a");
		Logger.log("b");
		Logger.log("c");

		LoggerUtils.assertLogContains(0, "a");
		LoggerUtils.assertLogContains(1, "b");
		LoggerUtils.assertLogContains(2, "c");
	}

	@Test(expected = AssertionError.class)
	public void negativeMatching() {
		Logger.log("a");
		LoggerUtils.assertLogContains(0, "b");
	}
}