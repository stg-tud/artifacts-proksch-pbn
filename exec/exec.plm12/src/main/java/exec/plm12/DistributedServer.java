/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Proksch - initial API and implementation
 */
package exec.plm12;

import java.util.Map;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.evaluation.distribution.Config;
import cc.recommenders.evaluation.distribution.ITaskProvider;
import cc.recommenders.evaluation.distribution.ITaskScheduler;
import cc.recommenders.evaluation.distribution.RmiUtils;
import cc.recommenders.evaluation.distribution.TaskScheduler;
import cc.recommenders.evaluation.distribution.calc.DefinitionSitesProvider;
import cc.recommenders.evaluation.distribution.calc.F1AndSizeProvider;
import cc.recommenders.evaluation.distribution.calc.F1ForInputProvider;
import cc.recommenders.evaluation.distribution.calc.F1ForInputSeveralProvider;
import cc.recommenders.evaluation.distribution.calc.FeatureComparisonProvider;
import cc.recommenders.evaluation.distribution.calc.MinComparisonProvider;
import cc.recommenders.evaluation.distribution.calc.QueryPerformanceProvider;
import cc.recommenders.evaluation.distribution.calc.QueryTypeProvider;
import cc.recommenders.io.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Injector;

public class DistributedServer {

	private Map<String, Class<? extends ITaskProvider<?>>> providers = getProviders();

	public void run(String selector, String serverIp, Config config, Injector injector) throws Exception {
		Asserts.assertNotNull(serverIp);
		Asserts.assertNotNull(config);
		Asserts.assertNotNull(injector);

		RmiUtils.setRmiDefaults();
		RmiUtils.publish(config, serverIp);

		if (providers.containsKey(selector)) {
			ITaskProvider<?> provider = injector.getInstance(providers.get(selector));
			TaskScheduler<?> scheduler = TaskScheduler.create(provider);
			RmiUtils.publish(scheduler, serverIp, ITaskScheduler.class);
		} else {
			Logger.err("unknown provider '%s', use one of %s", selector, providers.keySet());
		}
	}

	public static Map<String, Class<? extends ITaskProvider<?>>> getProviders() {
		Map<String, Class<? extends ITaskProvider<?>>> providers = Maps.newHashMap();

		providers.put("querytype", QueryTypeProvider.class);
		providers.put("query-performance", QueryPerformanceProvider.class);
		providers.put("min-comparison", MinComparisonProvider.class);
		providers.put("feature-comparison", FeatureComparisonProvider.class);
		providers.put("f1-and-size", F1AndSizeProvider.class);
		providers.put("definition-sites", DefinitionSitesProvider.class);
		providers.put("f1-for-input", F1ForInputProvider.class);
		providers.put("f1-for-input-several", F1ForInputSeveralProvider.class);

		return providers;
	}
}