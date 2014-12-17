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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.datastructures.Map2D;
import cc.recommenders.evaluation.OutputUtils;
import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.evaluation.data.BoxplotData;
import cc.recommenders.evaluation.data.NM;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.io.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class QueryTypeProvider extends AbstractTaskProvider<QueryTypeTask> {

	private Set<String> allApps = Sets.newLinkedHashSet();
	private Map2D<NM, String, BoxplotData> results = Map2D.create();

	@Inject
	public QueryTypeProvider(ProjectFoldedUsageStore store, OutputUtils output) {
		super(store, output);
	}

	@Override
	public void addResult2(QueryTypeTask r) {
		allApps.add(r.app);
		Logger.log("f1(nm):");

		for (NM nm : r.results.keySet()) {
			double[] values = r.results.get(nm);
			Boxplot boxplot = BoxplotData.from(values).getBoxplot();
			Logger.log("- %s: %s", nm, boxplot);
			results.getOrAdd(nm, r.app, new BoxplotData()).addAll(values);
		}
	}

	@Override
	protected void logResults() {

		for (String appPrefix : new String[] { "Q0_", "QNM_" }) {
			append("\n%%%%%%%%%% prepend '%s' %%%%%%%%%%\n", appPrefix);
			Iterable<String> apps = Iterables.filter(allApps, startsWith(appPrefix));
			Iterable<NM> nms = Iterables.filter(results.keySet(), useNM(appPrefix));
			logSubResult(appPrefix, apps, nms);
		}
	}

	private void logSubResult(String appPrefix, Iterable<String> apps, Iterable<NM> nms) {

		append("type\tcount");
		for (String app : apps) {
			String appNoPrefix = app.substring(appPrefix.length());
			append("\t%s", appNoPrefix);
		}
		append("\n");

		for (NM nm : nms) {
			append("%s\t%d", _(nm), getNumValues(nm));

			for (String app : apps) {
				Boxplot bp = results.get(nm, app).getBoxplot();
				append("\t%.5f", bp.getMean());
			}
			append("\n");
		}
	}

	private String _(NM nm) {
		if (NM.ELSE_0M.equals(nm)) {
			return "0$|$7+";
		} else if (NM.ELSE_NM.equals(nm)) {
			return "*$|$7+";
		} else {
			return nm.toString().replace("|", "$|$");
		}
	}

	private int getNumValues(NM nm) {
		Iterator<BoxplotData> it = results.get(nm).values().iterator();
		Asserts.assertTrue(it.hasNext());
		return it.next().getRawValues().length;
	}

	@Override
	protected Map<String, String> getOptions() {
		Map<String, String> options = Maps.newLinkedHashMap();
		options.put("Q0_BMN", bmn().c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN0", pbn(0).c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN15", pbn(15).c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN25", pbn(25).c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN30", pbn(30).c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN40", pbn(40).c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN60", pbn(60).c(false).d(false).p(false).useFloat().q0().ignore(false).min(30).get());

		options.put("Q0_BMN+DEF", bmn().c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN0+DEF", pbn(0).c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN15+DEF", pbn(15).c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN25+DEF", pbn(25).c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN30+DEF", pbn(30).c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN40+DEF", pbn(40).c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN60+DEF", pbn(60).c(false).d(true).p(false).useFloat().q0().ignore(false).min(30).get());

		options.put("Q0_BMN+ALL", bmn().c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN0+ALL", pbn(0).c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN15+ALL", pbn(15).c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN25+ALL", pbn(25).c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN30+ALL", pbn(30).c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN40+ALL", pbn(40).c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());
		options.put("Q0_PBN60+ALL", pbn(60).c(true).d(true).p(true).useFloat().q0().ignore(false).min(30).get());

		//
		options.put("QNM_BMN", bmn().c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN0", pbn(0).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN15", pbn(15).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN25", pbn(25).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN30", pbn(30).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN40", pbn(40).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN60", pbn(60).c(false).d(false).p(false).useFloat().qNM().ignore(false).min(30).get());

		options.put("QNM_BMN+DEF", bmn().c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN0+DEF", pbn(0).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN15+DEF", pbn(15).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN25+DEF", pbn(25).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN30+DEF", pbn(30).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN40+DEF", pbn(40).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN60+DEF", pbn(60).c(false).d(true).p(false).useFloat().qNM().ignore(false).min(30).get());

		options.put("QNM_BMN+ALL", bmn().c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN0+ALL", pbn(0).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN15+ALL", pbn(15).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN25+ALL", pbn(25).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN30+ALL", pbn(30).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN40+ALL", pbn(40).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		options.put("QNM_PBN60+ALL", pbn(60).c(true).d(true).p(true).useFloat().qNM().ignore(false).min(30).get());
		return options;
	}

	@Override
	protected int getNumFolds() {
		return 10;
	}

	@Override
	protected String getFileHint() {
		return "plots/data/query-types-(0|n)m[-ext].txt";
	}

	@Override
	protected Callable<QueryTypeTask> createWorker(QueryTypeTask task) {
		return new QueryTypeWorker(task);
	}

	@Override
	protected QueryTypeTask newTask() {
		return new QueryTypeTask();
	}

	private static Predicate<String> startsWith(final String appPrefix) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String app) {
				return app.startsWith(appPrefix);
			}
		};
	}

	private static Predicate<NM> useNM(String appPrefix) {
		final boolean isZero = appPrefix.contains("0");
		return new Predicate<NM>() {
			@Override
			public boolean apply(NM nm) {
				return nm.getNumQueried() == 0 ? isZero : !isZero;
			}
		};
	}
}