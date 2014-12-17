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
package cc.recommenders.mining.calls;

import java.util.Set;

import cc.recommenders.datastructures.Tuple;
import cc.recommenders.names.IMethodName;

public interface ICallsRecommender<Query> {

	Set<Tuple<IMethodName, Double>> query(Query query);

	Set<Tuple<String, Double>> getPatternsWithProbability();

	Set<Tuple<IMethodName, Double>> queryPattern(String patternName);

	/**
	 * @return the number of bytes necessary to store the model
	 */
	int getSize();
}
