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

import java.util.Map;
import java.util.concurrent.Callable;

import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.Averager;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

public class FeatureComparisonProvider extends AbstractTaskProvider<F1AndSizeTask> {

	private Map<String, BoxplotData> resultsF1 = Maps.newLinkedHashMap();
	private Map<String, Averager> resultsSizes = Maps.newLinkedHashMap();

	@Inject
	public FeatureComparisonProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	protected void addResult2(F1AndSizeTask r) {
		log("f1:   %s", BoxplotData.from(r.f1s).getBoxplot());
		log("size: %d", r.sizeInB);

		store(r.app, r.f1s, r.sizeInB);
	}

	private void store(String app, double[] f1s, int size) {
		BoxplotData bpd = resultsF1.get(app);
		if (bpd == null) {
			bpd = new BoxplotData();
			resultsF1.put(app, bpd);
		}
		bpd.addAll(f1s);

		Averager avg = resultsSizes.get(app);
		if (avg == null) {
			avg = new Averager();
			resultsSizes.put(app, avg);
		}
		avg.add(size);
	}

	@Override
	protected void logResults() {
		int appNum = 1;
		append("\noption\tf1\tmodel_size\t%% approach: boxplot\n");
		for (String app : resultsF1.keySet()) {
			Boxplot bp = resultsF1.get(app).getBoxplot();
			int avgSize = resultsSizes.get(app).getIntAverage();
			Logger.append("%d\t%.5f\t%d\t%% %s: %s\n", appNum++, bp.getMean(), avgSize, app, bp);
		}
	}

	@Override
	protected Map<String, String> getOptions() {
		Map<String, String> options = Maps.newLinkedHashMap();
		boolean[] trueAndFalse = new boolean[] { false, true };
		for (boolean useClass : trueAndFalse) {
			for (boolean useDef : trueAndFalse) {
				for (boolean useParam : trueAndFalse) {
					String config = String.format("+%s%s%s", _(useClass, "C"), _(useDef, "D"), _(useParam, "P"));
					options.put("BMN" + config, bmn().c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
					options.put("PBN0" + config, pbn(0).c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
					options.put("PBN15" + config, pbn(15).c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
					options.put("PBN25" + config, pbn(25).c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
					options.put("PBN30" + config, pbn(30).c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
					options.put("PBN40" + config, pbn(40).c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
					options.put("PBN60" + config, pbn(60).c(useClass).d(useDef).p(useParam).useFloat().ignore(false).min(30).get());
				}
			}
		}
		return options;
	}

	private String _(boolean cond, String repl) {
		return cond ? repl : "_";
	}

	@Override
	protected int getNumFolds() {
		return 10;
	}

	@Override
	protected String getFileHint() {
		return "plots/data/comparison-features.txt";
	}

	@Override
	protected Callable<F1AndSizeTask> createWorker(F1AndSizeTask task) {
		return new F1AndSizeWorker(task);
	}

	@Override
	protected F1AndSizeTask newTask() {
		return new F1AndSizeTask();
	}
}