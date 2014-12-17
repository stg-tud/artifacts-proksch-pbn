/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.evaluation.io;

import static cc.recommenders.assertions.Throws.throwIllegalArgumentException;
import static cc.recommenders.io.Directory.createFileName;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.recommenders.assertions.Throws;
import cc.recommenders.collections.SublistSelector;
import cc.recommenders.io.DataStore;
import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;
import cc.recommenders.io.ReadingArchive;
import cc.recommenders.io.WritingArchive;
import cc.recommenders.names.ITypeName;

import com.codetrails.data.CallSite;
import com.codetrails.data.CallSiteKind;
import com.codetrails.data.ObjectUsage;
import com.google.common.base.Predicate;
import com.google.gson.reflect.TypeToken;

/**
 * store that can be used to read and write the old ObjectUsages
 */
public class ObjectUsageStore implements DataStore<ITypeName, ObjectUsage> {

	private final Predicate<ObjectUsage> ouFilter;

	private Map<ITypeName, WritingArchive> archives = newHashMap();
	private Directory directory;

	public ObjectUsageStore(Directory directory, Predicate<ObjectUsage> ouFilter) {
		this.directory = directory;
		this.ouFilter = ouFilter;
	}

	@Override
	public void store(List<ObjectUsage> data) {
		List<ObjectUsage> shufData = SublistSelector.forceShuffle(data);
		for (ObjectUsage o : shufData) {
			store(o);
		}
		close();
	}

	private void store(ObjectUsage usage) {
		try {
			if (!isValid(usage)) {
				return;
			}

			if (!hasCalls(usage)) {
				return;
			}

			WritingArchive archive = getArchive(usage.getType());
			archive.add(usage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean isValid(ObjectUsage usage) {
		return ouFilter.apply(usage);
	}

	private boolean hasCalls(ObjectUsage usage) {
		for (List<CallSite> sequence : usage.getPaths()) {
			for (CallSite site : sequence) {
				if (site.getKind().equals(CallSiteKind.RECEIVER_CALL_SITE)) {
					return true;
				}
			}
		}
		return false;
	}

	private WritingArchive getArchive(ITypeName type) throws IOException {
		WritingArchive archive = archives.get(type);
		boolean archiveCurrentlyNotOpened = archive == null;
		if (archiveCurrentlyNotOpened) {
			String fileName = createFileName(type + ".zip");
			if (directory.exists(fileName)) {
				throw new IllegalStateException("trying to store data for type that is already closed");
			}
			archive = directory.getWritingArchive(fileName);
			archives.put(type, archive);
		}
		return archive;
	}

	@Override
	public void close() {
		try {
			storeTypesInFile();
			closeAllArchivesThatAreCurrentlyOpen();
		} catch (IOException e) {
			Throws.throwUnhandledException(e);
		}
	}

	private void closeAllArchivesThatAreCurrentlyOpen() throws IOException {
		for (ITypeName type : archives.keySet()) {
			WritingArchive archive = archives.get(type);
			if (archive != null) {
				archive.close();
				archives.put(type, null);
			}
		}
	}

	private void storeTypesInFile() throws IOException {
		Set<ITypeName> types = archives.keySet();
		directory.write(types, "types.json");
	}

	@Override
	public Set<ITypeName> getKeys() {
		try {
			Type type = new TypeToken<Set<ITypeName>>() {
			}.getType();
			Set<ITypeName> types = directory.read("types.json", type);
			return types;
		} catch (IOException e) {
			Throws.throwUnhandledException(e);
			return null;
		}
	}

	@Override
	public List<ObjectUsage> read(ITypeName type) {
		Predicate<ObjectUsage> all = alwaysTrue();
		return read(type, all);
	}

	@Override
	public List<ObjectUsage> read(ITypeName type, Predicate<ObjectUsage> predicates) {

		ensureTypeExists(type);

		String fileName = createFileName(type.toString()) + ".zip";
		try {
			ReadingArchive archive = directory.getReadingArchive(fileName);
			List<ObjectUsage> observations = newArrayList();
			while (archive.hasNext()) {
				ObjectUsage ou = archive.getNext(ObjectUsage.class);
				if (ouFilter.apply(ou)) {
					if (predicates.apply(ou)) {
						observations.add(ou);
					}
				} else {
					Logger.err("ignoring invalid usage");
				}
			}
			return observations;
		} catch (IOException e) {
			Throws.throwUnhandledException(e);
		}
		return null;
	}

	private void ensureTypeExists(ITypeName type) {
		boolean isTypeKnown = getKeys().contains(type);
		if (!isTypeKnown) {
			throwIllegalArgumentException("type %s does not exist", type);
		}
	}

	@Override
	public void clear() {
		directory.clear();
	}
}