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
package cc.recommenders.utils;

import static cc.recommenders.assertions.Asserts.assertNotNull;
import static cc.recommenders.assertions.Asserts.assertTrue;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DateProviderTest {

	private DateProvider sut;

	@Before
	public void setup() {
		sut = new DateProvider();
	}

	@Test
	public void returnsValue() {
		assertNotNull(sut.getDate());
	}

	@Test
	public void returnsCorrectDate() {
		Date actual = sut.getDate();
		Date expected = new Date();
		long diffInMilliSeconds = expected.getTime() - actual.getTime();
		assertTrue(diffInMilliSeconds <= 1000);
	}
}