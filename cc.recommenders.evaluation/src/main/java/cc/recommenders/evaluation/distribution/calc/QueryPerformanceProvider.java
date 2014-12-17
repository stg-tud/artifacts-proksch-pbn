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

import static cc.recommenders.evaluation.OptionsUtils.bmn;
import static cc.recommenders.evaluation.OptionsUtils.pbn;
import static cc.recommenders.evaluation.OutputUtils.humanReadableByteCount;
import static cc.recommenders.io.Logger.append;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import cc.recommenders.datastructures.Map2D;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class QueryPerformanceProvider extends AbstractTaskProvider<QueryPerformanceTask> {

	protected static final int[] ALL_SIZES = new int[] { 10, 30, 100, 300, 1000, 3000, 10000, 15000, 20000, 30000,
			40000, 100000 };
	protected static final ITypeName TYPE = VmTypeName.get("Lorg/eclipse/swt/widgets/Button");

	private Set<Integer> inputSizes = Sets.newLinkedHashSet();
	private Map2D<String, Integer, BoxplotData> resSize = Map2D.create();
	private Map2D<String, Integer, BoxplotData> resLearn = Map2D.create();
	private Map2D<String, Integer, BoxplotData> resInfer = Map2D.create();

	@Inject
	public QueryPerformanceProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	protected void addResult2(QueryPerformanceTask r) {
		Logger.log("model size: %s", humanReadableByteCount(r.modelSize));
		Logger.log("learning: %.2fs", r.learningDurationInS);
		Logger.log("per query: %.2fms", r.perQueryDurationInMS);

		inputSizes.add(r.inputSize);
		resSize.getOrAdd(r.app, r.inputSize, new BoxplotData()).add((double) r.modelSize);
		resLearn.getOrAdd(r.app, r.inputSize, new BoxplotData()).add(r.learningDurationInS);
		resInfer.getOrAdd(r.app, r.inputSize, new BoxplotData()).add(r.perQueryDurationInMS);
	}

	@Override
	protected void logResults() {

		Set<String> apps = resSize.keySet();

		append("%% - %d queries per fold\n", QueryPerformanceWorker.NUMBER_OF_QUERIES_PER_FOLD);
		append("%% - filtered for %s\n", TYPE);
		append("%% - units:\n");
		append("%%	- model size: B\n");
		append("%%	- learning speed: s\n");
		append("%%	- query speed: ms\n\n");

		append("input");
		for (String app : apps) {
			append("\t%s_size", app);
			append("\t%s_learn", app);
			append("\t%s_query", app);
		}
		append("\n");

		for (int size : inputSizes) {
			append("%d", size);
			for (String app : apps) {
				append("\t%d", (int) resSize.get(app, size).getMean());
				append("\t%.2f", resLearn.get(app, size).getMean());
				append("\t%.2f", resInfer.get(app, size).getMean());
			}
			append("\n");
		}
	}

	@Override
	protected Collection<QueryPerformanceTask> createTasksFor(String app, ITypeName type, int foldNum,
			List<Usage> training) {
		Set<QueryPerformanceTask> tasks = Sets.newLinkedHashSet();
		for (int inputSize : getApplicableSizes(training)) {
			QueryPerformanceTask task = getAbstractTask(app, type, foldNum);
			task.inputSize = inputSize;
			tasks.add(task);
		}
		return tasks;
	}

	private Collection<Integer> getApplicableSizes(List<Usage> training) {
		List<Integer> applicable = Lists.newLinkedList();
		for (int inputSize : ALL_SIZES) {
			if (training.size() >= inputSize) {
				applicable.add(inputSize);
			}
		}
		return applicable;
	}

	@Override
	protected Map<String, String> getOptions() {
		Map<String, String> options = Maps.newLinkedHashMap();
		options.put("BMN", bmn().c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN0", pbn(0).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN15", pbn(15).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN25", pbn(25).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN30", pbn(30).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN40", pbn(40).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN60", pbn(60).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());

		options.put("BMN+DEF", bmn().c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN0+DEF", pbn(0).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN15+DEF", pbn(15).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN25+DEF", pbn(25).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN30+DEF", pbn(30).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN40+DEF", pbn(40).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN60+DEF", pbn(60).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());

		options.put("BMN+ALL", bmn().c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN0+ALL", pbn(0).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN15+ALL", pbn(15).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN25+ALL", pbn(25).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN30+ALL", pbn(30).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN40+ALL", pbn(40).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("PBN60+ALL", pbn(60).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		return options;
	}

	@Override
	protected boolean useType(ITypeName type) {
		return TYPE.equals(type);
	}

	@Override
	protected int getNumFolds() {
		return 10;
	}

	@Override
	protected String getFileHint() {
		return "plots/data/query_performance.txt";
	}

	@Override
	protected Callable<QueryPerformanceTask> createWorker(QueryPerformanceTask task) {
		return new QueryPerformanceWorker(task);
	}

	@Override
	protected QueryPerformanceTask newTask() {
		return new QueryPerformanceTask();
	}
}