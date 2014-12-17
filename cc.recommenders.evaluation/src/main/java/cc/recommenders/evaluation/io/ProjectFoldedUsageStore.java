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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.io.Directory;
import cc.recommenders.io.ReadingArchive;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.ProjectFoldingIndex;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ProjectFoldedUsageStore {

	private final Directory in;
	private final ProjectFoldingStrategy foldingStrategy;

	private ProjectFoldingIndex index;

	@Inject
	public ProjectFoldedUsageStore(@Named("projectIndexed") Directory in, ProjectFoldingStrategy foldingStrategy) {
		Asserts.assertNotNull(in);
		Asserts.assertNotNull(foldingStrategy);
		this.in = in;
		this.foldingStrategy = foldingStrategy;
	}

	public Set<ITypeName> getTypes() {
		lazyReadIndex();
		return index.getTypes();
	}

	public boolean isAvailable(ITypeName type, int numFolds) {
		lazyReadIndex();
		int projectsWithAtLeastOneUsage = 0;
		for (int count : index.getCounts(type).values()) {
			if (count > 0) {
				projectsWithAtLeastOneUsage++;
			}
		}
		return projectsWithAtLeastOneUsage >= numFolds;
	}

	public TypeStore createTypeStore(ITypeName type, int numFolds) throws IOException {
		Asserts.assertGreaterThan(numFolds, 0);
		Asserts.assertTrue(isAvailable(type, numFolds));

		List<ProjectFoldedUsage> usages = Lists.newLinkedList();

		String fileName = type.toString().replace('/', '_') + ".zip";
		ReadingArchive ra = in.getReadingArchive(fileName);
		while (ra.hasNext()) {
			ProjectFoldedUsage pfu = ra.getNext(ProjectFoldedUsage.class);
			usages.add(pfu);
		}
		ra.close();
		Map<String, Integer> counts = index.getCounts(type);
		Map<String, Integer> mapping = foldingStrategy.createMapping(counts, numFolds);
		return new TypeStore(usages, mapping);
	}

	private void lazyReadIndex() {
		if (index == null) {
			try {
				Asserts.assertTrue(in.list().contains("index.json"));
				index = in.read("index.json", ProjectFoldingIndex.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}