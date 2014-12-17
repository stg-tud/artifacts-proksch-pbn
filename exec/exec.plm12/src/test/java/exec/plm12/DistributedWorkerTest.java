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
package exec.plm12;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;

// add timeout to all tests to prevent endless loop in case of exceptions!
public class DistributedWorkerTest {

	private DistributedWorker sut;

	@Before
	public void setup() {
		sut = new DistributedWorker();
	}

	@Test(expected = AssertionException.class, timeout=2000)
	public void serverIpIsCheckedForNull() throws Exception {
		sut.run(null, "", "");
	}
}