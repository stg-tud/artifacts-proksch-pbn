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
import java.util.Set;
import java.util.concurrent.Callable;

import cc.recommenders.datastructures.Map2D;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;
import cc.recommenders.usages.DefinitionSiteKind;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class DefinitionSitesProvider extends AbstractTaskProvider<DefinitionSitesTask> {

	private Set<String> apps = Sets.newLinkedHashSet();
	private Map2D<DefinitionSiteKind, String, BoxplotData> results = Map2D.create();

	@Inject
	public DefinitionSitesProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	protected void addResult2(DefinitionSitesTask r) {

		apps.add(r.app);
		Logger.log("f1(definition site kind):");

		for (DefinitionSiteKind dsk : r.results.keySet()) {
			double[] values = r.results.get(dsk);
			log("\t%s: %s", dsk, BoxplotData.from(values).getBoxplot());

			results.getOrAdd(dsk, r.app, new BoxplotData()).addAll(values);
		}
	}

	@Override
	protected void logResults() {
		// insert title
		Logger.append("\ntype\tcount");
		for (String app : apps) {
			Logger.append("\t%s", app);
		}
		append("\tnometa\n");

		// insert data
		for (DefinitionSiteKind dsk : results.keySet()) {

			Map<String, BoxplotData> dskRes = results.get(dsk);
			String firstApp = dskRes.keySet().iterator().next();
			int defCount = dskRes.get(firstApp).getRawValues().length;

			append("%s\t%d", dsk, defCount);

			for (String app : apps) {
				Boxplot boxplot = dskRes.get(app).getBoxplot();
				append("\t" + boxplot.getMean());
			}
			append("\t~\n");
		}
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
	protected int getNumFolds() {
		return 10;
	}

	@Override
	protected String getFileHint() {
		return "plots/data/definition-sites.txt";
	}

	@Override
	protected Callable<DefinitionSitesTask> createWorker(DefinitionSitesTask task) {
		return new DefinitionSitesWorker(task);
	}

	@Override
	protected DefinitionSitesTask newTask() {
		return new DefinitionSitesTask();
	}
}