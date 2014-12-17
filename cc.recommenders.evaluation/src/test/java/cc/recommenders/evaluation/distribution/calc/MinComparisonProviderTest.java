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

import static cc.recommenders.evaluation.OptionsUtils.pbn;
import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.evaluation.OptionsUtils.OptionsBuilder;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;

public class MinComparisonProviderTest {

	private MinComparisonProvider sut;

	@Before
	public void setup() {
		ProjectFoldedUsageStore store = mock(ProjectFoldedUsageStore.class);
		OutputUtils output = mock(OutputUtils.class);
		sut = new MinComparisonProvider(store, output);
	}

	@Test
	public void getOptions() {
		assertEquals(30, sut.getOptions().size());

		for (int i = 0; i < 49; i += 2) {
			assertOption("PBN25-" + i, opt(pbn(25), i));
		}
	}

	private static String opt(OptionsBuilder algo, int min) {
		return algo.c(false).d(true).p(false).useFloat().init(false).ignore(false).min(min).qNM().get();
	}

	private void assertOption(String name, String expected) {
		String err = format("does not contain entry for '%s'", name);
		assertTrue(err, sut.getOptions().containsKey(name));
		String actual = sut.getOptions().get(name);
		assertEquals(expected, actual);
	}
}