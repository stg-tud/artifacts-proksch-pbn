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
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.distribution.ITaskProvider;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.evaluation.io.TypeStore;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

public abstract class AbstractTaskProvider<TTask extends AbstractTask> implements ITaskProvider<TTask> {

	private final ProjectFoldedUsageStore store;
	private final OutputUtils output;

	private double aggregatedProcessingTimeInS = 0;

	public AbstractTaskProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		this.store = store;
		this.output = output;
	}

	protected abstract int getNumFolds();

	protected abstract Map<String, String> getOptions();

	@Override
	public Collection<Callable<TTask>> createWorkers() {
		Set<Callable<TTask>> workers = Sets.newLinkedHashSet();
		for (TTask task : createTasks()) {
			workers.add(createWorker(task));
		}
		return workers;
	}

	protected abstract Callable<TTask> createWorker(TTask task);

	protected Set<TTask> createTasks() {
		output.startEvaluation();
		int numTasks = 0;
		Logger.log("## creating tasks");
		Set<TTask> tasks = Sets.newLinkedHashSet();
		for (ITypeName type : store.getTypes()) {
			if (useType(type)) {
				if (store.isAvailable(type, getNumFolds())) {
					for (int foldNum = 0; foldNum < getNumFolds(); foldNum++) {
						TypeStore typeStore = createTypeStore(type);
						List<Usage> training = typeStore.getTrainingData(foldNum);
						List<Usage> validation = typeStore.getValidationData(foldNum);

						for (String app : getOptions().keySet()) {
							for (TTask task : createTasksFor(app, type, foldNum, training)) {
								Logger.log("%5d: %s", (++numTasks), task);
								tasks.add(task);
								output.count(type, foldNum, validation.size());
							}
						}
					}
				}
			}
		}
		output.setNumTasks(tasks.size());
		return tasks;
	}

	protected boolean useType(ITypeName type) {
		return true;
	}

	private TypeStore createTypeStore(ITypeName type) {
		try {
			return store.createTypeStore(type, getNumFolds());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Collection<TTask> createTasksFor(String app, ITypeName type, int foldNum, List<Usage> training) {
		Collection<TTask> tasks = Sets.newHashSet();
		tasks.add(getAbstractTask(app, type, foldNum));
		return tasks;
	}

	protected TTask getAbstractTask(String app, ITypeName type, int foldNum) {
		TTask task = newTask();
		task.app = app;
		task.options = getOptions().get(app);
		task.typeName = type.toString();
		task.currentFold = foldNum;
		task.numFolds = getNumFolds();
		return task;
	}

	protected abstract TTask newTask();

	@Override
	public void addResult(TTask r) {
		try {
			output.printProgress("### intermediate result, progress: %s");
			log("task: %s", r);
			log("duration: %.1fs", r.processingTimeInS);
			aggregatedProcessingTimeInS += r.processingTimeInS;
			addResult2(r);
			log("");
		} catch (Exception e) {
			Logger.err("error during execution of addResult(%s):\n", r);
			e.printStackTrace();
		}

	}

	protected double getAggregatedProcessingTimeInS() {
		return aggregatedProcessingTimeInS;
	}

	protected abstract void addResult2(TTask r);

	@Override
	public void addCrash(String taskToString, Exception e) {
		Logger.err("evaluation has crashed whild processing %s\n", taskToString);
		e.printStackTrace();
	}

	@Override
	public void done() {
		try {
			output.stopEvaluation();
			output.printResultHeader(getFileHint(), getClass(), getNumFolds(), getOptions());
			output.printSpeedup(aggregatedProcessingTimeInS);
			logResults();
			output.printTypeCounts();
		} catch (Exception e) {
			Logger.err("error during execution of done:\n");
			e.printStackTrace();
		}
	}

	protected abstract void logResults();

	protected abstract String getFileHint();
}