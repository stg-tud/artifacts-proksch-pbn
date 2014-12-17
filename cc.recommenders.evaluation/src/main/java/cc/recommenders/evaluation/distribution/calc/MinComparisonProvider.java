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
package cc.recommenders.evaluation.distribution.calc;

import static cc.recommenders.evaluation.OptionsUtils.pbn;

import java.util.Map;

import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class MinComparisonProvider extends FeatureComparisonProvider {

	@Inject
	public MinComparisonProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	protected Map<String, String> getOptions() {
		Map<String, String> options = Maps.newLinkedHashMap();
		for (int min = 0; min < 60; min += 2) {
			options.put("PBN25-"+min, pbn(25).c(false).d(true).p(false).useFloat().ignore(false).min(min).get());
		}
		return options;
	}
}