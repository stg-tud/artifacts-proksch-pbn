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

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import cc.recommenders.assertions.Asserts;

import com.google.common.collect.Maps;

public class ProjectFoldingStrategy {

	public Map<String, Integer> createMapping(Map<String, Integer> projectCounts, int numFolds) {
		Asserts.assertGreaterOrEqual(projectCounts.size(), numFolds);

		Set<String> projectNames = sortNamesByCount(projectCounts);

		Map<String, Integer> mapping = Maps.newHashMap();
		long[] counts = new long[numFolds];

		for (String projectName : projectNames) {
			int size = projectCounts.get(projectName);
			int idx = getSmallestIndex(counts);

			counts[idx] += size;
			mapping.put(projectName, idx);
		}
		return mapping;
	}

	private Set<String> sortNamesByCount(final Map<String, Integer> counts) {
		Set<String> names = new TreeSet<String>(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				int c1 = counts.get(o1);
				int c2 = counts.get(o2);
				int diff = c2 - c1;
				if (diff == 0) {
					return o1.compareTo(o2);
				} else {
					return diff;
				}
			}
		});
		names.addAll(counts.keySet());
		return names;
	}

	private int getSmallestIndex(long[] counts) {
		long smallestValue = Integer.MAX_VALUE;
		int smallestIdx = 0;

		for (int idx = 0; idx < counts.length; idx++) {
			if (counts[idx] < smallestValue) {
				smallestValue = counts[idx];
				smallestIdx = idx;
			}
		}
		return smallestIdx;
	}
}