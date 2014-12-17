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

import java.util.Collection;
import java.util.concurrent.Callable;

public interface ITaskProvider<TResult> {

	public Collection<Callable<TResult>> createWorkers();

	public void addResult(TResult r);

	public void addCrash(String taskToString, Exception e);

	public void done();
}