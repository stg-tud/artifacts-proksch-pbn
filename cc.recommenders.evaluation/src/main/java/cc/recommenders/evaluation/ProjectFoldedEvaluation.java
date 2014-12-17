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

import static cc.recommenders.assertions.Asserts.assertGreaterThan;
import static cc.recommenders.io.Logger.append;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.recommenders.datastructures.Tuple;
import cc.recommenders.evaluation.io.ProjectFoldedUsageStore;
import cc.recommenders.evaluation.io.TypeStore;
import cc.recommenders.io.Logger;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.Usage;
import cc.recommenders.utils.DateProvider;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class ProjectFoldedEvaluation {

	private final ProjectFoldedUsageStore foldedStore;
	private final DateProvider dateProvider;

	private int total = 0;
	private Map<ITypeName, Integer> totals = Maps.newLinkedHashMap();
	private Multimap<ITypeName, Integer> counts = LinkedListMultimap.create();
	private Set<Tuple<ITypeName, Integer>> alreadyCountedTypeAndFolds = Sets.newLinkedHashSet();

	@Inject
	public ProjectFoldedEvaluation(@Named("projectFolded") ProjectFoldedUsageStore foldedStore,
			DateProvider dateProvider) {
		this.foldedStore = foldedStore;
		this.dateProvider = dateProvider;
		assertGreaterThan(getNumFolds(), 1);
	}

	public void run() throws IOException {
		Logger.log("####");
		Logger.log("#### running analysis %s (%s)...", getClass(), dateProvider.getDate());
		Logger.log("####");
		Logger.log("");
		Logger.log("project-folded cross validation, %d folds", getNumFolds());
		Logger.log("all types seen in >=%d projects with >=1 usage", getNumFolds());

		init();
		foldAllTypes();

		logResultsHeader();
		logResults();
		logResultsTypeCounts();
	}

	protected void init() {
		// can be overridden, if necessary
	}

	protected void foldAllTypes() throws IOException {
		for (ITypeName type : foldedStore.getTypes()) {
			foldType(type);
		}
	}

	protected void foldType(ITypeName type) throws IOException {
		if (foldedStore.isAvailable(type, getNumFolds())) {
			TypeStore typeStore = foldedStore.createTypeStore(type, getNumFolds());
			if (shouldAnalyze(type, typeStore)) {
				Logger.log("");
				Logger.log("## %s", type);
				for (int foldNum = 0; foldNum < getNumFolds(); foldNum++) {
					List<Usage> training = typeStore.getTrainingData(foldNum);
					List<Usage> validation = typeStore.getValidationData(foldNum);

					String foldMsg = "\t%d/%d: %d training, %d validation";
					Logger.log(foldMsg, (foldNum + 1), getNumFolds(), training.size(), validation.size(), type);

					count(type, foldNum, validation.size());
					runFold(type, foldNum, training, validation);
				}
			}
		} else {
			notAvailable(type);
		}
	}

	protected void notAvailable(ITypeName type) {
	}

	protected boolean shouldAnalyze(ITypeName type, TypeStore typeStore) {
		return true;
	}

	protected void count(ITypeName type, int foldNum, int size) {
		Tuple<ITypeName, Integer> typeAndFold = Tuple.newTuple(type, foldNum);
		if (!alreadyCountedTypeAndFolds.contains(typeAndFold)) {
			alreadyCountedTypeAndFolds.add(typeAndFold);
			total += size;
			if (totals.containsKey(type)) {
				int typeTotal = totals.get(type);
				totals.put(type, (typeTotal + size));
			} else {
				totals.put(type, size);
			}
			counts.put(type, size);
		}
	}

	protected abstract void runFold(ITypeName type, int foldNum, List<Usage> training, List<Usage> validation);

	private void logResultsHeader() {
		append("\n\n--> put outputs into: %s\n\n", getFileHint());
		append("%% do not edit manually, auto-generated on %s\n", dateProvider.getDate());
		append("%%\n");
		append("%% results for %s...\n", getClass());
		append("%% - project-folded cross validation\n", getNumFolds());
		append("%% - num folds: %d\n", getNumFolds());
		append("%% - all types seen in >=%d projects with >=1 usage\n", getNumFolds());
	}

	protected abstract void logResults();

	protected void logResultsTypeCounts() {
		append("\n%% %12d usages total (from types seen in >= %d projects with >=1 usage)\n", total, getNumFolds());
		append("%% ------------\n");
		for (ITypeName type : totals.keySet()) {
			append("%% %12d %s (", totals.get(type), type);

			boolean isFirst = true;
			for (int vSize : counts.get(type)) {
				append("%s%d", isFirst ? "" : ", ", vSize);
				isFirst = false;
			}
			append(")\n");
		}
	}

	protected abstract int getNumFolds();

	protected abstract String getFileHint();
}
