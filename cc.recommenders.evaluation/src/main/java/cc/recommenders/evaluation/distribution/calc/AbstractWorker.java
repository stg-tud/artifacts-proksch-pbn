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

import static cc.recommenders.io.Logger.log;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Callable;

import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.evaluation.io.TypeStore;
import cc.recommenders.evaluation.queries.QueryBuilderFactory;
import cc.recommenders.mining.calls.MinerFactory;
import cc.recommenders.mining.calls.MiningOptions;
import cc.recommenders.mining.calls.QueryOptions;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Usage;
import cc.recommenders.utils.Timer;

import com.google.inject.Inject;

public abstract class AbstractWorker<TTask extends AbstractTask> implements Callable<TTask>, Serializable {

	private static final long serialVersionUID = 2931075356495604625L;

	@Inject
	public transient MiningOptions mOpts;

	@Inject
	public transient QueryOptions qOpts;

	@Inject
	public transient QueryBuilderFactory queryBuilderFactory;

	@Inject
	public transient MinerFactory minerFactory;

	@Inject
	public transient Timer taskDurationTimer;

	@Inject
	public transient ProjectFoldedUsageStore usageStore;

	private TTask task;

	private transient List<Usage> trainingData;

	private transient List<Usage> validationData;

	public AbstractWorker(TTask task) {
		this.task = task;
	}

	@Override
	public TTask call() throws Exception {
		log("## starting task: %s", task.app);
		log("task: %s", task);
		mOpts.setFrom(task.options);
		qOpts.setFrom(task.options);
		log("options: %s%s", mOpts, qOpts);
		log("miner: %s", minerFactory.get().getClass());
		log("queryBuilder: %s", queryBuilderFactory.get().getClass());

		taskDurationTimer.startNew();
		call2();
		taskDurationTimer.stop();
		task.processingTimeInS = taskDurationTimer.getDurationInSeconds();

		log("processing took %.1fs", task.processingTimeInS);
		log("");

		return task;
	}

	protected List<Usage> getTrainingData() {
		lazyLoadData();
		return trainingData;
	}

	protected List<Usage> getValidationData() {
		lazyLoadData();
		return validationData;
	}

	private void lazyLoadData() {
		if (trainingData == null) {
			try {
				TypeStore typeStore = usageStore.createTypeStore(VmTypeName.get(task.typeName), task.numFolds);
				trainingData = typeStore.getTrainingData(task.currentFold);
				validationData = typeStore.getValidationData(task.currentFold);
				log("number of usages:");
				log("- training: %d", trainingData.size());
				log("- validation: %d", validationData.size());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected abstract void call2();

	@Override
	public String toString() {
		return String.format("%s: %s", getClass().getSimpleName(), task);
	}
}