/**
 * Copyright (c) 2010-2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Florian Jakob - initial API and implementation
 *     Sebastian Proksch - adapted the design
 */
package cc.recommenders.evaluation.optimization;

import java.util.List;
import java.util.Set;

import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

public abstract class EvaluationOptionsSearcher {

	public abstract EvaluationOptions findOptimalOptions(List<Usage> usages, OptimizationOptions options,
			Set<EvaluationOptions> startValues);

	public EvaluationOptions findOptimalOptions(List<Usage> usages, OptimizationOptions options) {
		return findOptimalOptions(usages, options, getDefaultStartValues());
	}

	public Set<EvaluationOptions> getDefaultStartValues() {
		Set<EvaluationOptions> defaultOpts = Sets.newHashSet();
		defaultOpts.add(new EvaluationOptions(new MiningOptions(), new QueryOptions()));
		return defaultOpts;
	}
}