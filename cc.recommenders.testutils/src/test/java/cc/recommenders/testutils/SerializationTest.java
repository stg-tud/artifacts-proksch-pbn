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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class SerializationTest {

	@Test
	public void simpleRoundtrip() throws IOException, ClassNotFoundException {
		String expected = "hello world!";
		byte[] bytes = Serialization.serialize(expected);
		String actual = Serialization.deserialize(bytes);
		assertEquals(expected, actual);
	}
}