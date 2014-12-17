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
package cc.recommenders.evaluation.io;

import java.util.List;
import java.util.Map;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.Usage;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class TypeStore {

	private List<ProjectFoldedUsage> allUsages;
	private Map<String, Integer> mapping;

	public TypeStore(List<ProjectFoldedUsage> allUsages, Map<String, Integer> mapping) {
		this.allUsages = allUsages;
		this.mapping = mapping;

	}

	public List<ProjectFoldedUsage> getAllUsages() {
		return allUsages;
	}

	public Map<String, Integer> getMapping() {
		return mapping;
	}

	public List<Usage> getTrainingData(int foldNum) {
		return filterAndMapUsages(isNotEqual(foldNum));
	}

	private Predicate<ProjectFoldedUsage> isNotEqual(final int val) {
		return new Predicate<ProjectFoldedUsage>() {
			@Override
			public boolean apply(ProjectFoldedUsage in) {
				Integer idx = mapping.get(in.getProjectName());
				Asserts.assertNotNull(idx);
				return idx != val;
			}
		};
	}

	public List<Usage> getValidationData(int foldNum) {
		return filterAndMapUsages(isEqual(foldNum));
	}

	private Predicate<ProjectFoldedUsage> isEqual(final int val) {
		return new Predicate<ProjectFoldedUsage>() {
			@Override
			public boolean apply(ProjectFoldedUsage in) {
				Integer idx = mapping.get(in.getProjectName());
				Asserts.assertNotNull(idx);
				return idx == val;
			}
		};
	}

	private List<Usage> filterAndMapUsages(Predicate<ProjectFoldedUsage> pred) {
		Iterable<ProjectFoldedUsage> filtered = Iterables.filter(allUsages, pred);
		Iterable<Usage> usages = Iterables.transform(filtered, usageTransformation());
		return Lists.newLinkedList(usages);
	}

	private Function<ProjectFoldedUsage, Usage> usageTransformation() {
		return new Function<ProjectFoldedUsage, Usage>() {
			@Override
			public Usage apply(ProjectFoldedUsage input) {
				return input.getRawUsage();
			}
		};
	}
}