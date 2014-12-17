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
package cc.recommenders.evaluation.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;

import com.google.common.collect.Maps;

public class ProjectFoldingStrategyTest {

	private static final int MIN_COUNT = 100;
	private ProjectFoldingStrategy sut;
	private Map<String, Integer> counts;
	private Map<String, Integer> actual;
	private Map<String, Integer> expected;

	@Before
	public void setup() {
		counts = Maps.newLinkedHashMap();
		expected = Maps.newHashMap();
		sut = new ProjectFoldingStrategy();
	}

	@Test(expected = AssertionException.class)
	public void atLeastNumFoldProjectsAreNecessary() {
		addCounts(1);
		sut.createMapping(counts, 2);
	}

	@Test
	public void minimalExample() {
		addCounts(123, 23, 345);

		actual = sut.createMapping(counts, 3);

		expected.put("p0", 1);
		expected.put("p1", 2);
		expected.put("p2", 0);

		assertEquals(expected, actual);
	}

	@Test
	public void moreCountsThanFolds() {
		addCounts(123, 23, 345, 19);

		actual = sut.createMapping(counts, 3);

		expected.put("p0", 1);
		expected.put("p1", 2);
		expected.put("p2", 0);
		expected.put("p3", 2);

		assertEquals(expected, actual);
	}

	@Test
	public void complexExample() {
		addCounts(13, 63, 45, 79, 38);
		// -> 79, 63, 45, 38, 13

		actual = sut.createMapping(counts, 2);

		expected.put("p0", 1);
		expected.put("p1", 1);
		expected.put("p2", 1);
		expected.put("p3", 0);
		expected.put("p4", 0);

		assertEquals(expected, actual);
	}

	private void addCounts(int... projectNums) {
		for (int i = 0; i < projectNums.length; i++) {
			counts.put("p" + i, projectNums[i]);
		}
	}

	@Test
	public void bigExampleWithManyProjects() {
		long total = 0;
		Random rnd = new Random();
		for (int i = 0; i < 100; i++) {
			int num = Math.abs(rnd.nextInt() % 900) + MIN_COUNT;
			counts.put("p" + i, num);
			total += num;
		}

		actual = sut.createMapping(counts, 10);

		long[] foldSizes = new long[10];
		for (String pName : counts.keySet()) {
			int count = counts.get(pName);
			int fold = actual.get(pName);
			foldSizes[fold] += count;
		}

		long expectedSize = total / 10;
		for (int foldNum = 0; foldNum < 10; foldNum++) {
			long delta = Math.abs(foldSizes[foldNum] - expectedSize);
			assertTrue("delta too big: " + delta, delta < MIN_COUNT);
		}
	}
}