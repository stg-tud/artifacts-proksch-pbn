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
package exec.plm12;

import static cc.recommenders.testutils.LoggerUtils.assertLogContains;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cc.recommenders.evaluation.distribution.Config;
import cc.recommenders.evaluation.distribution.ITaskProvider;
import cc.recommenders.evaluation.distribution.calc.DefinitionSitesProvider;
import cc.recommenders.evaluation.distribution.calc.F1AndSizeProvider;
import cc.recommenders.evaluation.distribution.calc.F1ForInputProvider;
import cc.recommenders.evaluation.distribution.calc.F1ForInputSeveralProvider;
import cc.recommenders.evaluation.distribution.calc.FeatureComparisonProvider;
import cc.recommenders.evaluation.distribution.calc.MinComparisonProvider;
import cc.recommenders.evaluation.distribution.calc.QueryPerformanceProvider;
import cc.recommenders.evaluation.distribution.calc.QueryTypeProvider;
import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Logger;

import com.google.inject.Injector;

public class DistributedServerTest {

	private Map<String, Class<? extends ITaskProvider<?>>> providers;
	private DistributedServer sut;
	private Injector injector;

	@Before
	public void setup() {
		Logger.reset();
		Logger.setCapturing(true);
		injector = mock(Injector.class);
		providers = DistributedServer.getProviders();
		sut = new DistributedServer();
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void correctMappingForSelectors() {
		assertEquals(8, providers.size());
		assertProvider("querytype", QueryTypeProvider.class);
		assertProvider("query-performance", QueryPerformanceProvider.class);
		assertProvider("feature-comparison", FeatureComparisonProvider.class);
		assertProvider("min-comparison", MinComparisonProvider.class);
		assertProvider("f1-and-size", F1AndSizeProvider.class);
		assertProvider("definition-sites", DefinitionSitesProvider.class);
		assertProvider("f1-for-input", F1ForInputProvider.class);
		assertProvider("f1-for-input-several", F1ForInputSeveralProvider.class);
	}

	@Test(expected = AssertionException.class)
	public void serverIpIsCheckedForNull() throws Exception {
		sut.run("", null, mock(Config.class), injector);
	}

	@Test(expected = AssertionException.class)
	public void configIsCheckedForNull() throws Exception {
		sut.run("", "localhost", null, injector);
	}

	@Test(expected = AssertionException.class)
	public void injectorIsCheckedForNull() throws Exception {
		sut.run("", "localhost", mock(Config.class), null);
	}

	@Test
	public void nonExistingSelectorExitsWithMessage() throws Exception {
		sut.run("X", "localhost", mock(Config.class), injector);
		assertLogContains(2, "EE unknown provider 'X', use one of [");
		for (String selector : DistributedServer.getProviders().keySet()) {
			assertLogContains(2, selector);
		}
	}

	@Test
	@Ignore("TODO: currently hard because of static calls")
	public void happyPath() throws Exception {
		// sut.run(null, "localhost", null, injector);
	}

	private void assertProvider(String selector, Class<?> clazz) {
		assertEquals(clazz, providers.get(selector));
	}
}