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
package cc.recommenders.evaluation.queries;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class QueryBuilderFactory {

	private PartialUsageQueryBuilder nm;
	private ZeroCallQueryBuilder zero;
	private QueryOptions qOpts;

	@Inject
	public QueryBuilderFactory(QueryOptions qOpts, ZeroCallQueryBuilder zero, PartialUsageQueryBuilder nm) {
		this.qOpts = qOpts;
		this.zero = zero;
		this.nm = nm;
	}

	public QueryBuilder<Usage, Query> get() {
		Asserts.assertNotNull(qOpts.queryType);
		switch (qOpts.queryType) {
		case NM:
			return nm;
		case ZERO:
			return zero;
		default:
			throw new AssertionException("unknown QueryBuilder: " + qOpts.queryType);
		}
	}
}