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
package cc.recommenders.evaluation.evaluators;

import static cc.recommenders.assertions.Throws.throwNotImplemented;
import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.queries.PartialUsageQueryBuilder;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.ICallsRecommender;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.DecoratedObjectUsage;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

@SuppressWarnings("unchecked")
public class MergedCallEvaluatorTest {

	private PartialUsageQueryBuilder queryBuilder;
	private ICallsRecommender<Query> recommender;
	private List<Usage> ous;
	private F1Evaluator sut;
	private Query query;

	@Before
	public void setup() {
		query = createUsage();
		queryBuilder = mock(PartialUsageQueryBuilder.class);
		when(queryBuilder.createQueries(any(Usage.class))).thenReturn(newArrayList(query));

		recommender = mock(ICallsRecommender.class);
		ous = newArrayList();
		QueryBuilderFactory queryBuilderFactory = new QueryBuilderFactory(new QueryOptions(), null, queryBuilder);
		sut = new F1Evaluator(queryBuilderFactory, new QueryOptions());
	}

	@Test
	public void ensureThatAQueryIsBuilt() {
		ous.add(createUsage());
		sut.query(recommender, ous);

		verify(queryBuilder).createQueries(any(DecoratedObjectUsage.class));
	}

	@Test
	public void ensureThatThisQueryIsUsed() {
		ous.add(createUsage());
		sut.query(recommender, ous);
		verify(recommender).query(query);
	}

	@Test
	public void dataIsAvailableAfterFirstQuery() {

		assertFalse(sut.hasResults());

		ous.add(createUsage());
		sut.query(recommender, ous);

		assertTrue(sut.hasResults());
	}

	@Test
	public void resultIsNotNull() {
		ous.add(createUsage());
		sut.query(recommender, ous);
		Boxplot results = sut.getResults();
		assertNotNull(results);
	}

	@Test
	@Ignore
	public void calculatedValueIsCorrect() {
		throwNotImplemented();
	}

	private static Query createUsage() {
		Query q = new Query();
		q.setType(mock(ITypeName.class));
		q.setClassContext(mock(ITypeName.class));
		q.setMethodContext(mock(IMethodName.class));
		q.setDefinition(mock(DefinitionSite.class));
		return q;
	}
}