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

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

public class CounterTest {

	private Counter<String> sut;

	@Before
	public void setup() {
		sut = Counter.create();
	}

	@Test
	public void defaultCount() {
		assertEquals(0, sut.getCount("a"));
	}

	@Test
	public void countIsSet() {
		sut.setCount("a", 1);
		assertEquals(1, sut.getCount("a"));
	}

	@Test
	public void countIsOverridden() {
		sut.setCount("a", 1);
		sut.setCount("a", 2);
		assertEquals(2, sut.getCount("a"));
	}

	@Test
	public void countIsAdded() {
		sut.addCount("a", 1);
		sut.addCount("a", 2);
		assertEquals(3, sut.getCount("a"));
	}

	@Test
	public void keysAreAdded() {
		sut.setCount("a", 1);
		sut.setCount("b", 2);
		Iterable<String> actuals = sut.getKeys();
		Set<String> expecteds = Sets.newLinkedHashSet();
		expecteds.add("a");
		expecteds.add("b");
		assertEquals(expecteds, actuals);
	}

	@Test
	public void totalCalculation() {
		sut.setCount("a", 1);
		sut.setCount("b", 2);
		sut.setCount("c", 4);
		assertEquals(7, sut.getTotal());
	}

	@Test
	public void totalCalculationWhenEmpty() {
		assertEquals(0, sut.getTotal());
	}
}