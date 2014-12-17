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
package cc.recommenders.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class SublistSelectorTest {

	private List<String> in;
	private List<String> out;
	private List<String> out2;

	@Test
	public void listsAreAlwaysShuffled() {
		in = createList(10);
		out = SublistSelector.pickRandomSublist(in, 10);
		assertFalse(in.equals(out));
	}

	@Test
	public void exactAmountIsPicked() {
		in = createList(15);
		out = SublistSelector.pickRandomSublist(in, 5);
		int actual = out.size();
		int expected = 5;
		assertEquals(expected, actual);
	}

	@Test
	public void pickingIsRandomized() {
		in = createList(15);
		out = SublistSelector.pickRandomSublist(in, 2);
		out2 = SublistSelector.pickRandomSublist(in, 2);
		assertFalse(out.equals(out2));
	}

	@Test
	public void takeMoreThanExist() {
		in = createList(1);
		out = SublistSelector.pickRandomSublist(in, 2);
		assertTrue(out.size() == 1);
		assertEquals("i0", out.get(0));
	}

	@Test
	public void emptyListsAreNotAProblem() {
		in = createList(0);
		out = SublistSelector.pickRandomSublist(in, 1);
		assertEquals(Lists.newLinkedList(), out);
	}

	private static List<String> createList(int num) {
		List<String> items = Lists.newLinkedList();
		for (int i = 0; i < num; i++) {
			items.add(String.format("i%d", i));
		}
		return items;
	}
}