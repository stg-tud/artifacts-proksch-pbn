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
package cc.recommenders.evaluation.distribution;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

public class RmiUtilsTest {
	@Test
	public void createUrl() {
		String actual = RmiUtils.createUrl("X", String.class);
		String expected = "rmi://X:1099/java.lang.String";
		assertEquals(expected, actual);
	}

	@Test
	public void createUrlWithoutScheme() {
		String actual = RmiUtils.createUrlWithoutSchemeComponent("X", List.class);
		String expected = "//X:1099/java.util.List";
		assertEquals(expected, actual);
	}
}