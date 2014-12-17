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

import static cc.recommenders.assertions.Asserts.assertTrue;
import static cc.recommenders.io.Logger.append;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.utils.DateProvider;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

public class OutputUtils {

	private Set<Pair<ITypeName, Integer>> alreadyCounted = Sets.newHashSet();
	private Map<ITypeName, Integer> typeTotals = Maps.newLinkedHashMap();
	private Multimap<ITypeName, Integer> typeNums = LinkedListMultimap.create();

	private long started;
	private Date startedAt;
	private long stopped;
	private Date stoppedAt;

	private DateProvider dateProvider;
	private int numTasks;
	private int numResults;

	@Inject
	public OutputUtils(DateProvider dateProvider) {
		this.dateProvider = dateProvider;
	}

	public void startEvaluation() {
		started = dateProvider.getTimeSeconds();
		startedAt = dateProvider.getDate();
	}

	public void stopEvaluation() {
		stopped = dateProvider.getTimeSeconds();
		stoppedAt = dateProvider.getDate();
	}

	public void printResultHeader(String file, Class<?> clazz, int numFolds, Map<String, String> options) {
		append("\n\n--> put outputs into: %s\n\n", file);
		append("%% do not edit manually, auto-generated on %s\n", dateProvider.getDate());
		append("%%\n");
		append("%% results for %s...\n", clazz);
		append("%% - project-folded cross validation\n");
		append("%% - num folds: %d\n", numFolds);
		append("%% - all types seen in >=%d projects with >=1 usage\n", numFolds);
		append("%% - options:\n");
		for (String app : options.keySet()) {
			append("%%\t%s: %s\n", app, options.get(app));
		}
	}

	public void printSpeedup(double aggregatedProcessingTimeInS) {
		long durationInS = stopped - started;
		append("%% - started at %s, finished at %s\n", startedAt, stoppedAt);
		append("%%\trunning for %d seconds\n", durationInS);
		append("%%\taggregated processing time is %.1f seconds\n", aggregatedProcessingTimeInS);
		append("%%\t--> speed up of %.1f through distribution\n\n", (aggregatedProcessingTimeInS / (1.0 * durationInS)));
	}

	public void count(ITypeName type, int foldNum, int num) {
		Pair<ITypeName, Integer> p = Pair.of(type, foldNum);
		if (!alreadyCounted.contains(p)) {
			alreadyCounted.add(p);
			if (typeTotals.containsKey(type)) {
				int oldNum = typeTotals.get(type);
				typeTotals.put(type, oldNum + num);
			} else {
				typeTotals.put(type, num);
			}
			typeNums.put(type, num);
		}
	}

	public void printTypeCounts() {
		int total = 0;
		Logger.append("\n%% types:\n");
		for (ITypeName type : typeTotals.keySet()) {
			boolean isFirst = true;
			int num = typeTotals.get(type);
			total += num;
			Logger.append("%% %12d - %s (", num, type);
			for (int typeNum : typeNums.get(type)) {
				Logger.append("%s%d", isFirst ? "" : ", ", typeNum);
				isFirst = false;
			}
			Logger.append(")\n");
		}
		Logger.append("%% ------------\n");
		Logger.append("%% %12d - total\n", total);
	}

	public void setNumTasks(int numTasks) {
		this.numTasks = numTasks;
	}

	public void printProgress(String msg) {
		assertTrue(msg.contains("%s"));
		numResults++;
		String progress = String.format("%.1f%% (%d/%d)", (100 * numResults / (1.0 * numTasks)), numResults, numTasks);
		Logger.log(msg, progress);
	}

	public static String humanReadableByteCount(long bytes) {
		Asserts.assertGreaterOrEqual(bytes, 0);
		// boolean si = false;
		// int unit = si ? 1000 : 1024;
		int unit = 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		// String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" :
		// "i");
		String pre = "KMGTPE".charAt(exp - 1) + "i";
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
}