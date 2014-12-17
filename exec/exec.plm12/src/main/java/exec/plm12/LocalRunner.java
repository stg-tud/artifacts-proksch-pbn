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

import static cc.recommenders.io.Logger.log;

import java.rmi.RemoteException;
import java.util.Map;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.evaluation.distribution.ITaskProvider;
import cc.recommenders.evaluation.distribution.InjectableRunnable;
import cc.recommenders.evaluation.distribution.TaskScheduler;
import cc.recommenders.io.Logger;

import com.google.common.base.Strings;
import com.google.inject.Injector;

public class LocalRunner {

	private static Map<String, Class<? extends ITaskProvider<?>>> providers = DistributedServer.getProviders();

	public void run(String selector, int numIterations, Injector injector) throws Exception {
		Asserts.assertNotNull(selector);
		Asserts.assertGreaterThan(numIterations, 0);

		for (int itNum = 0; itNum < numIterations; itNum++) {
			String itHeader = String.format("##### iteration %d/%d ", (itNum + 1), numIterations);
			log(Strings.repeat("#", 80));
			log(Strings.padEnd(itHeader, 80, '#'));
			log(Strings.repeat("#", 80));
			log("");

			TaskScheduler<?> scheduler = createScheduler(selector, injector);
			Runnable task = scheduler.getNextNullableTask();
			while (task != null) {
				String taskHeader = String.format("##### next task (in iteration %d/%d) ", (itNum + 1), numIterations);
				log(Strings.padEnd(taskHeader, 80, '#'));
				log("");
				if (task instanceof InjectableRunnable) {
					((InjectableRunnable) task).injectionForMembers(injector);
				}
				task.run();
				task = scheduler.getNextNullableTask();
			}
		}

		String itHeader = String.format("##### %d iterations finished ", numIterations);
		log(Strings.repeat("#", 80));
		log(Strings.padEnd(itHeader, 80, '#'));
		log(Strings.repeat("#", 80));
	}

	private TaskScheduler<?> createScheduler(String selector, Injector injector) throws RemoteException {
		Asserts.assertNotNull(selector);
		Asserts.assertNotNull(injector);
		if (providers.containsKey(selector)) {
			ITaskProvider<?> provider = injector.getInstance(providers.get(selector));
			TaskScheduler<?> scheduler = TaskScheduler.create(provider);
			return scheduler;
		} else {
			Logger.err("unknown provider '%s', use one of %s", selector, providers.keySet());
			throw new RuntimeException();
		}
	}
}