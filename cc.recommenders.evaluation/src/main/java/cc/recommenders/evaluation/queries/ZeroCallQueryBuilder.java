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

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

/**
 * Creates a query by just leaving out all paths from the original usage.
 */
public class ZeroCallQueryBuilder implements QueryBuilder<Usage, Query> {

	@Override
	public List<Query> createQueries(Usage usage) {

		Query query = Query.createAsCopyFrom(usage);

		query.setAllCallsites(Sets.<CallSite> newLinkedHashSet());

		return newArrayList(query);
	}
}