/*******************************************************************************
 * Copyright (c) 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 ******************************************************************************/
package cc.recommenders.evaluation.data;

import java.io.Serializable;

public class Point implements Serializable {

	private static final long serialVersionUID = 1L;

	public double recall = 0.0;
	public double precision = 0.0;

	public Point() {
		// for gson
	}

	public Point(double recall, double precision) {
		this.recall = recall;
		this.precision = precision;
	}

	@Override
	public String toString() {
		return String.format("[%f,%f]", recall, precision);
	}
	
	@Override
	public boolean equals(Object obj) {
		return hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {

		int hash = new Double(recall).hashCode();
		hash = hash * 37 + new Double(precision).hashCode();
		return hash;
	}
}
