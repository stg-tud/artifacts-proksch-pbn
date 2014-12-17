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

import java.util.List;

import cc.recommenders.mining.calls.ICallsRecommender;

public interface Evaluator<In, Out, Query> {

	public void reinit();

	public void query(ICallsRecommender<Query> rec, List<In> validationData);

	public boolean hasResults();

	public Out getResults();
}