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

import java.util.Map;

import com.google.common.collect.Maps;

public class Counter<T> {

	private Map<T, Integer> counts = Maps.newLinkedHashMap();

	public void setCount(T type, int size) {
		counts.put(type, size);
	}

	public void addCount(T type, int size) {
		int count = 0;
		if (counts.containsKey(type)) {
			count = counts.get(type);
		}
		counts.put(type, count + size);
	}

	public int getTotal() {
		int total = 0;
		for (int count : counts.values()) {
			total += count;
		}
		return total;
	}

	public Iterable<T> getKeys() {
		return counts.keySet();
	}

	public int getCount(T type) {
		if (counts.containsKey(type)) {
			return counts.get(type);
		} else {
			return 0;
		}
	}

	public static <U> Counter<U> create() {
		return new Counter<U>();
	}
}