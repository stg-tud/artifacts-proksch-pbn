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
package cc.recommenders.evaluation;

import java.io.IOException;
import java.util.List;

import cc.recommenders.evaluation.io.DecoratedObjectUsageStore;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.pbn.ExportMiner;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.Usage;

import com.google.inject.Inject;

public class ExportToMatlab {

	private final DecoratedObjectUsageStore usageStore;
	private final ExportMiner exporter;
	private final MiningOptions mOpts;

	@Inject
	public ExportToMatlab(MiningOptions mOpts, DecoratedObjectUsageStore usageStore, ExportMiner miner) {
		this.mOpts = mOpts;
		this.usageStore = usageStore;
		this.exporter = miner;
	}

	public void run() {

		for (ITypeName type : usageStore.getKeys()) {

			List<Usage> usages = usageStore.read(type);

			try {
				mOpts.setFeatureDropping(false);
				exporter.export(type, usages);
				mOpts.setFeatureDropping(true);
				exporter.export(type, usages);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}