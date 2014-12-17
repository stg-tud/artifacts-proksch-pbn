/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Proksch - initial API and implementation
 */
package exec.plm12;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.evaluation.distribution.Config;
import cc.recommenders.evaluation.distribution.ITaskScheduler;
import cc.recommenders.evaluation.distribution.InjectableRunnable;
import cc.recommenders.evaluation.distribution.RmiUtils;
import cc.recommenders.io.Logger;

import com.google.common.base.Strings;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class DistributedWorker {

	public void run(String serverIp, String rootFolder, String version) throws Exception {

		Asserts.assertNotNull(serverIp);

		RmiUtils.setRmiDefaults();

		while (true) {
			Logger.log(Strings.padEnd("#### restarting worker ", 80, '#'));

			runGarbageCollection();

			try {
				Config config = RmiUtils.request(Config.class, serverIp);
				assertVersion(version, config.getVersion());

				Logger.log("# version: %s", version);
				Logger.log("# root: %s", rootFolder);
				Logger.log("# dataset: %s", config.getDatasetName());

				Injector injector = Guice.createInjector(new Module(rootFolder, config.getDatasetName()));

				ITaskScheduler<?> tp = RmiUtils.request(ITaskScheduler.class, serverIp);
				Logger.log("# requesting task...");
				Runnable task = tp.getNextNullableTask();
				if (task != null) {
					if (task instanceof InjectableRunnable) {
						((InjectableRunnable) task).injectionForMembers(injector);
					}
					Logger.log(Strings.repeat("#", 80));
					task.run();
				} else {
					Logger.log("# no tasks available. waiting...");
					Logger.log(Strings.repeat("#", 80));
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				Logger.log("# communication error:\n%s", e.getMessage());
				Logger.log("# waiting...");
				Logger.log(Strings.repeat("#", 80));
				Thread.sleep(5000);
			}
			Logger.log("");
		}
	}

	private static void runGarbageCollection() {
		System.gc();
		long max = Runtime.getRuntime().maxMemory();
		float maxInMb = Math.round(max / (1024d * 1024d));
		long free = Runtime.getRuntime().maxMemory();
		float freeInMb = Math.round(free / (1024d * 1024d));
		float usedInMb = maxInMb - freeInMb;
		Logger.log("# memory: %.0f/%.0f MB used", usedInMb, maxInMb);
	}

	private static void assertVersion(String clientVersion, String serverVersion) {
		if (!clientVersion.equals(serverVersion)) {
			Logger.err("client expired (%s != %s), exiting...\n", clientVersion, serverVersion);
			System.exit(1);
		}
	}
}