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

import static com.google.common.collect.Lists.newLinkedList;

import java.util.List;
import java.util.Set;

import cc.recommenders.io.DataStore;
import cc.recommenders.io.Directory;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.DecoratedObjectUsage;
import cc.recommenders.usages.Usage;

import com.codetrails.data.ObjectUsage;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

/**
 * Stores that can be used to read ObjectUsages and convert them to Usages
 * on-the-fly
 */
public class DecoratedObjectUsageStore implements DataStore<ITypeName, Usage> {

	private ObjectUsageStore ouStore;

	public DecoratedObjectUsageStore(Directory folder, Predicate<ObjectUsage> ouFilter) {
		ouStore = new ObjectUsageStore(folder, ouFilter);
	}

	@Override
	public void store(List<Usage> data) {
		throw new RuntimeException("not implemented");
	}

	@Override
	public void close() {
		ouStore.close();
	}

	@Override
	public Set<ITypeName> getKeys() {
		return ouStore.getKeys();
	}

	@Override
	public List<Usage> read(ITypeName key) {
		Predicate<Usage> alwaysTrue = Predicates.alwaysTrue();
		return read(key, alwaysTrue);
	}

	@Override
	public List<Usage> read(ITypeName key, Predicate<Usage> predicate) {
		List<Usage> usages = newLinkedList();
		for (ObjectUsage usage : ouStore.read(key)) {
			Usage decorated = new DecoratedObjectUsage(usage);
			if (predicate.apply(decorated)) {
				usages.add(decorated);
			}
		}
		return usages;
	}

	@Override
	public void clear() {
		ouStore.clear();
	}
}