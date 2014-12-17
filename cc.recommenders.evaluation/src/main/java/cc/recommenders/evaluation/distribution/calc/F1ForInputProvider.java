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
import static cc.recommenders.io.Logger.append;
import static cc.recommenders.io.Logger.log;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import cc.recommenders.datastructures.Map2D;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class F1ForInputProvider extends AbstractTaskProvider<F1ForInputTask> {

	private static final ITypeName TYPE = VmTypeName.get("Lorg/eclipse/swt/widgets/Button");
	protected static final int[] ALL_SIZES = new int[] { 10, 30, 100, 300, 1000, 3000, 9000, 10000, 15000, 20000, 30000,
			40000, 100000 };

	private final Set<Integer> usedSizes = Sets.newTreeSet();
	private final Map2D<String, Integer, BoxplotData> results = Map2D.create();

	@Inject
	public F1ForInputProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	protected String getFileHint() {
		return "plots/data/f1-for-input.txt";
	}

	@Override
	protected int getNumFolds() {
		return 10;
	}

	@Override
	protected Map<String, String> getOptions() {
		Map<String, String> options = Maps.newLinkedHashMap();
		options.put("BMN", bmn().c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("BMN+DEF", bmn().c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("BMN+ALL", bmn().c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		// for (int n : new int[] { 15}){
		for (int n : new int[] { 0, 15, 25, 30, 40, 60 }) {
			String pbn = "PBN" + n;
			options.put(pbn, pbn(n).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
			options.put(pbn + "+DEF", pbn(n).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
			options.put(pbn + "+ALL", pbn(n).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		}
		return options;
	}

	@Override
	protected F1ForInputTask newTask() {
		return new F1ForInputTask();
	}

	@Override
	protected boolean useType(ITypeName type) {
		return TYPE.equals(type);
	}

	@Override
	protected Collection<F1ForInputTask> createTasksFor(String app, ITypeName type, int foldNum, List<Usage> training) {
		Set<F1ForInputTask> tasks = Sets.newLinkedHashSet();
		for (int inputSize : getApplicableSizes(training)) {
			F1ForInputTask task = getAbstractTask(app, type, foldNum);
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
	protected Callable<F1ForInputTask> createWorker(F1ForInputTask task) {
		return new F1ForInputWorker(task);
	}

	@Override
	protected void addResult2(F1ForInputTask r) {
		usedSizes.add(r.inputSize);
		BoxplotData bpd = results.getOrAdd(r.app, r.inputSize, new BoxplotData());
		bpd.addAll(r.f1s);

		log("f1(%d): %s", r.inputSize, BoxplotData.from(r.f1s).getBoxplot());
	}

	@Override
	protected void logResults() {

		Set<String> apps = results.keySet();

		append("inputSize");
		for (String app : apps) {
			append("\t%s", app);
		}
		append("\n");

		for (int size : usedSizes) {
			append("%d", size);
			for (String app : apps) {
				Map<Integer, BoxplotData> appRes = results.get(app);
				BoxplotData bpd = appRes.get(size);
				append("\t%.5f", bpd.getBoxplot().getMean());
			}
			append("\n");
		}
	}
}