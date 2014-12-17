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

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.commons.lang.UnhandledException;

import com.google.inject.Injector;

public class Task<TResult> implements Serializable, InjectableRunnable {

	private static final long serialVersionUID = -584046208094647116L;

	private final ITaskScheduler<TResult> scheduler;

	private final UUID uuid = UUID.randomUUID();
	private final Callable<TResult> callable;

	private TResult result;
	private Exception caughtException;

	public Task(Callable<TResult> callable, ITaskScheduler<TResult> scheduler) {
		assertNotNull(callable);
		assertNotNull(scheduler);
		this.callable = callable;
		this.scheduler = scheduler;
	}

	public UUID getUuid() {
		return uuid;
	}

	public boolean hasResult() {
		return result != null;
	}

	public TResult getResult() {
		return result;
	}

	public boolean hasCrashed() {
		return caughtException != null;
	}

	public Exception getException() {
		return caughtException;
	}

	@Override
	public void injectionForMembers(Injector injector) {
		injector.injectMembers(callable);
	}

	@Override
	public void run() {
		try {
			try {
				result = callable.call();
			} catch (Exception e) {
				caughtException = e;
			}
			scheduler.finished(this);
		} catch (RemoteException e) {
			throw new UnhandledException(e);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task<?> other = (Task<?>) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("[Task@%s: %s]", uuid.toString().substring(0, 5), callable);
	}
}