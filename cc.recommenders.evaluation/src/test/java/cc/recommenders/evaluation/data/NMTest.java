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
package cc.recommenders.evaluation.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.testutils.Serialization;

public class NMTest {

	private NM sut;

	@Test
	public void happyPath1() {
		sut = new NM(1, 2);
		assertEquals(1, sut.getNumQueried());
		assertEquals(2, sut.getNumOriginal());
		assertEquals("1|2", sut.toString());
	}

	@Test
	public void happyPath2() {
		sut = new NM(2, 3);
		assertEquals(2, sut.getNumQueried());
		assertEquals(3, sut.getNumOriginal());
		assertEquals("2|3", sut.toString());
	}

	@Test(expected = AssertionException.class)
	public void illegalInstance() {
		new NM(3, 2);
	}

	@Test
	public void elseValues() {
		assertEquals(0, NM.ELSE_0M.getNumQueried());
		assertEquals(-1, NM.ELSE_0M.getNumOriginal());
		assertEquals(-1, NM.ELSE_NM.getNumQueried());
		assertEquals(-1, NM.ELSE_NM.getNumOriginal());
	}

	@Test
	public void hashCodeAndEquals_same() {
		NM a = new NM(1, 2);
		NM b = new NM(1, 2);
		assertEquals(a, b);
		assertTrue(a.hashCode() == b.hashCode());
	}

	@Test
	public void hashCodeAndEquals_different1() {
		NM a = new NM(0, 2);
		NM b = new NM(1, 2);
		assertNotEquals(a, b);
		assertFalse(a.hashCode() == b.hashCode());
	}

	@Test
	public void hashCodeAndEquals_different2() {
		NM a = new NM(1, 3);
		NM b = new NM(1, 2);
		assertNotEquals(a, b);
		assertFalse(a.hashCode() == b.hashCode());
	}

	@Test
	public void isSerializable() throws IOException, ClassNotFoundException {
		NM expected = new NM(4, 5);
		byte[] bytes = Serialization.serialize(expected);
		NM actual = Serialization.deserialize(bytes);
		assertEquals(expected, actual);
	}
}