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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SublistSelector {

	public static <T> List<T> pickRandomSublist(List<T> in, int numToPick) {

		List<T> sublist = shuffle(in);

		if (sublist.size() > numToPick) {

			Iterator<T> iterator = sublist.iterator();
			skip(numToPick, iterator);
			removeAllRemaining(iterator);
		}

		return sublist;
	}

	public static <T> List<T> shuffle(List<T> in) {
		LinkedList<T> sublist = Lists.<T> newLinkedList();
		copyTo(in, sublist);
		Collections.shuffle(sublist);
		return sublist;
	}

	public static <T> Set<T> shuffle(Set<T> in) {
		List<T> sublist = Lists.<T> newLinkedList();
		copyTo(in, sublist);
		Collections.shuffle(sublist);
		Set<T> subset = Sets.newLinkedHashSet(sublist);
		return subset;
	}

	public static <T> List<T> forceShuffle(List<T> in) {
		LinkedList<T> sublist = Lists.<T> newLinkedList();
		copyTo(in, sublist);
		if (sublist.size() > 1) {
			while (sublist.equals(in)) {
				Collections.shuffle(sublist);
			}
		}
		return sublist;
	}

	private static void skip(int numToPick, Iterator<?> iterator) {
		int i = 0;
		while (i < numToPick) {
			iterator.next();
			i++;
		}
	}

	private static void removeAllRemaining(Iterator<?> iterator) {
		while (iterator.hasNext()) {
			iterator.next();
			iterator.remove();
		}
	}

	private static <T> void copyTo(Iterable<T> in, Collection<T> to) {
		for (T elem : in) {
			to.add(elem);
		}
	}
}