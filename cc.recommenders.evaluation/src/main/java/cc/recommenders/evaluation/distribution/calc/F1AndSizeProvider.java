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
import static cc.recommenders.io.Logger.log;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import cc.recommenders.evaluation.OptionsUtils.OptionsBuilder;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.Averager;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;

import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class F1AndSizeProvider extends AbstractTaskProvider<F1AndSizeTask> {

	private static final ITypeName BUTTON = VmTypeName.get("Lorg/eclipse/swt/widgets/Button");

	private Map<String, Averager> sizes = Maps.newLinkedHashMap();
	private Map<String, BoxplotData> quality = Maps.newLinkedHashMap();

	@Inject
	public F1AndSizeProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	protected boolean useType(ITypeName type) {
		return BUTTON.equals(type);
	}

	@Override
	public void addResult2(F1AndSizeTask r) {

		log("size: %s (raw: %d B)", humanReadableByteCount(r.sizeInB), r.sizeInB);
		log("f1:   %s", BoxplotData.from(r.f1s).getBoxplot());

		getAverager(r.app).add(r.sizeInB);
		getBoxplotData(r.app).addAll(r.f1s);
	}

	private Averager getAverager(String app) {
		Averager avg = sizes.get(app);
		if (avg == null) {
			avg = new Averager();
			sizes.put(app, avg);
		}
		return avg;
	}

	private BoxplotData getBoxplotData(String app) {
		BoxplotData bpd = quality.get(app);
		if (bpd == null) {
			bpd = new BoxplotData();
			quality.put(app, bpd);
		}
		return bpd;
	}

	@Override
	protected void logResults() {
		append("rec\tsize\tf1\t%% boxplot\n");
		Set<String> apps = Sets.newTreeSet(sizes.keySet());
		for (String name : apps) {
			int size = sizes.get(name).getIntAverage();
			Boxplot f1 = quality.get(name).getBoxplot();
			append("%s\t%d\t%.5f\t%% %s\n", name, size, f1.getMean(), f1);
		}
	}

	@Override
	protected Map<String, String> getOptions() {
		Map<String, String> options = Maps.newLinkedHashMap();
		addOptions(options, "", false);
		addOptions(options, "+DEF", true);
		return options;
	}

	private void addOptions(Map<String, String> options, String suffix, boolean useDef) {
		options.put("BMN" + suffix, createBMN(useDef));
		for (int n = 0; n < 26; n++) {
			options.put("PBN" + n + suffix, createPBN(n, useDef));
		}
		for (int n = 26; n < 50; n = n + 2) {
			options.put("PBN" + n + suffix, createPBN(n, useDef));
		}
		for (int n = 50; n <= 100; n = n + 5) {
			options.put("PBN" + n + suffix, createPBN(n, useDef));
		}
	}

	private String createBMN(boolean useDef) {
		return createApp(bmn(), useDef);
	}

	private String createPBN(int n, boolean useDef) {
		return createApp(pbn(n), useDef);
	}

	private String createApp(OptionsBuilder pbn, boolean useDef) {
		return pbn.c(false).d(useDef).p(false).useFloat().ignore(false).min(30).get();
	}

	@Override
	protected int getNumFolds() {
		return 10;
	}

	@Override
	protected String getFileHint() {
		return "plots/data/quality-and-size.txt";
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