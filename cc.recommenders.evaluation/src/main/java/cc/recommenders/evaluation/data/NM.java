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

import java.io.Serializable;

import cc.recommenders.assertions.Asserts;

public class NM implements Serializable {

	private static final long serialVersionUID = -2053594992770017114L;

	public static final NM ELSE_0M = new NM(0, -1, true);
	public static final NM ELSE_NM = new NM(-1, -1, true);

	private final int numQueried;
	private final int numOriginal;

	public NM(int numQueried, int numOriginal) {
		Asserts.assertGreaterThan(numOriginal, numQueried);
		this.numQueried = numQueried;
		this.numOriginal = numOriginal;
	}

	private NM(int numQueried, int numOriginal, boolean markerArg) {
		this.numQueried = numQueried;
		this.numOriginal = numOriginal;
	}

	public int getNumQueried() {
		return numQueried;
	}

	public int getNumOriginal() {
		return numOriginal;
	}

	@Override
	public String toString() {
		return String.format("%d|%d", numQueried, numOriginal);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numOriginal;
		result = prime * result + numQueried;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NM other = (NM) obj;
		if (numOriginal != other.numOriginal)
			return false;
		if (numQueried != other.numQueried)
			return false;
		return true;
	}
}