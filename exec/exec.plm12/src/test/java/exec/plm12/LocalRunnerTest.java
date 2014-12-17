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

import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Logger;

import com.google.inject.Injector;

public class LocalRunnerTest {

	private LocalRunner sut;

	@Before
	public void setup() {
		sut = new LocalRunner();
		Logger.setPrinting(true);
	}

	@Test(expected = AssertionException.class)
	public void selectorIsCheckedForNull() throws Exception {
		sut.run(null, 1, mock(Injector.class));
	}

	@Test(expected = AssertionException.class)
	public void positiveNumberOfIterations() throws Exception {
		sut.run("query-performance", -1, mock(Injector.class));
	}

	@Test
	@Ignore("TODO: currently hard because of static calls")
	public void happyPath() throws Exception {
		// sut.run(null, "localhost", null, injector);
	}
}