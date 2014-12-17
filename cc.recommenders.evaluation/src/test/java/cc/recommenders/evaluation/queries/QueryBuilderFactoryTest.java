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

import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.mining.calls.QueryOptions.QueryType;

public class QueryBuilderFactoryTest {

	@Mock
	public ZeroCallQueryBuilder zeroBuilder;

	@Mock
	public PartialUsageQueryBuilder nmBuilder;

	private QueryOptions qOpts;
	private QueryBuilderFactory sut;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		qOpts = new QueryOptions();
		sut = new QueryBuilderFactory(qOpts, zeroBuilder, nmBuilder);
	}

	@Test
	public void zeroCall() {
		qOpts.queryType = QueryType.ZERO;
		assertSame(zeroBuilder, sut.get());
	}

	@Test
	public void nmCall() {
		qOpts.queryType = QueryType.NM;
		assertSame(nmBuilder, sut.get());
	}

	@Test(expected = AssertionException.class)
	public void unknownOrNull() {
		qOpts.queryType = null;
		sut.get();
	}
}