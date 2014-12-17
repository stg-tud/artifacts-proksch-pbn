/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.evaluation.queries;

import static cc.recommenders.collections.SublistSelector.shuffle;
import static cc.recommenders.usages.Query.createAsCopyFrom;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * removes the last % of available callsites
 */
public class PartialUsageQueryBuilder implements QueryBuilder<Usage, Query> {

	private double percentage = 0.5;
	private int numOfQueries = 3;

	public void setPercentage(double percentage) {
		Asserts.assertGreaterOrEqual(percentage, 0);
		Asserts.assertGreaterOrEqual(1, percentage);
		this.percentage = percentage;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setNumOfQueries(int numOfQueries) {
		Asserts.assertGreaterThan(numOfQueries, 0);
		this.numOfQueries = numOfQueries;
	}

	public int getNumOfQueries() {
		return numOfQueries;
	}

	@Override
	public List<Query> createQueries(Usage usage) {

		// TODO re-implement this! (ad-hoc implementation)

		int numCalls = calcCountFor(usage.getReceiverCallsites().size());
		int numParams = calcCountFor(usage.getParameterCallsites().size());

		int iteration = 0;
		Set<Set<CallSite>> paths = Sets.newLinkedHashSet();
		while (paths.size() < numOfQueries && iteration++ < 100) {

			Set<CallSite> calls = getRandom(numCalls, usage.getReceiverCallsites());
			Set<CallSite> params = getRandom(numParams, usage.getParameterCallsites());

			Set<CallSite> path = mergeAndShuffle(calls, params);

			if (!paths.contains(path)) {
				paths.add(path);
			}
		}

		List<Query> qs = Lists.newLinkedList();
		for (Set<CallSite> path : paths) {
			Query query = createAsCopyFrom(usage);
			query.setAllCallsites(path);
			qs.add(query);
		}

		return qs;
	}

	private Set<CallSite> mergeAndShuffle(Set<CallSite> a, Set<CallSite> b) {
		a.addAll(b);
		Set<CallSite> c = shuffle(a);
		return c;
	}

	private static Set<CallSite> getRandom(int num, Set<CallSite> in) {
		Set<CallSite> rdm = shuffle(in);
		Iterator<CallSite> it = rdm.iterator();
		int i = 0;
		while (it.hasNext()) {
			it.next();
			if (i >= num) {
				it.remove();
			}
			i++;
		}
		return rdm;
	}

	private int calcCountFor(int size) {
		int numShouldGiven = (int) Math.floor(size * percentage);
		return numShouldGiven;
	}
}