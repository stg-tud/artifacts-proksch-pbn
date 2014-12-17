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
import java.util.Map;
import java.util.Set;

import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;
import cc.recommenders.io.ReadingArchive;
import cc.recommenders.io.WritingArchive;
import cc.recommenders.names.ITypeName;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.ProjectFoldingIndex;
import cc.recommenders.usages.Usage;

import com.codetrails.data.ObjectUsage;
import com.codetrails.data.ObjectUsageValidator;
import com.codetrails.data.UsageConverter;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

public class ProjectIndexer {

	public static final String READING_ERROR = "reading error in '%s'";
	public static final String INVALID = "ignoring invalid ObjectUsage in '%s': %s";

	private final Directory in;
	private final Directory out;
	private final UsageConverter converter;

	private Map<ITypeName, WritingArchive> archives = Maps.newHashMap();
	private Predicate<Usage> isInterestingPredicate;
	private ObjectUsageValidator ouValidator;

	public ProjectIndexer(Directory in, Directory out, UsageConverter converter, ObjectUsageValidator ouValidator,
			Predicate<Usage> isInterestingPredicate) {
		this.in = in;
		this.out = out;
		this.converter = converter;
		this.ouValidator = ouValidator;
		this.isInterestingPredicate = isInterestingPredicate;
	}

	public void createIndex() throws IOException {
		Logger.log("clearing index...");
		out.clear();

		Logger.log("creating index...");
		ProjectFoldingIndex index = new ProjectFoldingIndex();
		for (String fileName : allZipsFromIn()) {
			Logger.log("\tprocessing '%s'...", fileName);
			int numInvalid = 0;
			int numFiltered = 0;
			int numRemaining = 0;
			ReadingArchive ra = in.getReadingArchive(fileName);
			try {
				while (ra.hasNext()) {
					ObjectUsage ou = ra.getNext(ObjectUsage.class);
					if (!ouValidator.isValid(ou)) {
						numInvalid++;
						Logger.err(INVALID, fileName, ouValidator.getLastError());
						continue;
					}
					Usage u = converter.toRecommenderUsage(ou);
					if (!isInterestingPredicate.apply(u)) {
						numFiltered++;
						continue;
					}
					index.count(u.getType(), fileName);
					numRemaining++;
					ProjectFoldedUsage pfu = new ProjectFoldedUsage(u, fileName);
					store(pfu);
				}
				String msg = "\t\tfinished: %d invalid, %d filtered, %d remaining";
				Logger.log(msg, numInvalid, numFiltered, numRemaining);
			} catch (Exception e) {
				Logger.err(READING_ERROR, fileName);
			}
			ra.close();
		}

		out.write(index, "index.json");
		
		for (WritingArchive wa : archives.values()) {
			wa.close();
		}
	}

	private Set<String> allZipsFromIn() {
		return in.list(new Predicate<String>() {
			@Override
			public boolean apply(String fileName) {
				return fileName.endsWith(".zip");
			}
		});
	}

	private void store(ProjectFoldedUsage pfu) throws IOException {
		WritingArchive wa = archives.get(pfu.getType());
		if (wa == null) {
			String fileName = pfu.getType().toString().replace("/", "_") + ".zip";
			wa = out.getWritingArchive(fileName);
			archives.put(pfu.getType(), wa);
		}
		wa.add(pfu);
	}
}