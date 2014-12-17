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

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ITaskScheduler<T> extends Remote, Serializable {

	/**
	 * @return <i>next task</i> or <i>null</i> if no more tasks exist
	 */
	public Runnable getNextNullableTask() throws RemoteException;

	public void finished(Task<T> task) throws RemoteException;
}