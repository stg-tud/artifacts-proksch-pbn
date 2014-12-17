/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.evaluation.distribution;

import static cc.recommenders.assertions.Asserts.assertNotNull;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Queue;
import java.util.concurrent.Callable;

import cc.recommenders.assertions.Asserts;

import com.google.common.collect.Lists;

public class TaskScheduler<T> extends UnicastRemoteObject implements ITaskScheduler<T> {

	private static final long serialVersionUID = 1596515633547490112L;

	private final Queue<InjectableRunnable> tasks = Lists.newLinkedList();
	private final Queue<InjectableRunnable> started = Lists.newLinkedList();

	private ITaskProvider<T> provider;

	private boolean isRunning = true;

	private TaskScheduler(ITaskProvider<T> provider) throws RemoteException {
		super();
		this.provider = provider;
		for (Callable<T> callable : provider.createWorkers()) {
			tasks.add(new Task<T>(callable, this));
		}
	}

	@Override
	public synchronized Runnable getNextNullableTask() throws RemoteException {

		if (!tasks.isEmpty()) {
			InjectableRunnable task = tasks.poll();
			started.add(task);
			return task;
		}

		if (!started.isEmpty()) {
			InjectableRunnable task = started.poll();
			started.add(task);
			return task;
		}

		return null;
	}

	@Override
	public synchronized void finished(Task<T> task) throws RemoteException {
		if (started.remove(task)) {
			if (task.hasResult()) {
				provider.addResult(task.getResult());
			} else {
				Asserts.assertTrue(task.hasCrashed());
				provider.addCrash(task.toString(), task.getException());
			}
		}
		if (isRunning && tasks.isEmpty() && started.isEmpty()) {
			isRunning = false;
			provider.done();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <U> TaskScheduler<U> create(ITaskProvider<U> provider) throws RemoteException {
		assertNotNull(provider);
		return new TaskScheduler(provider);
	}
}