/*******************************************************************************
 * Copyright (c) 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Sebastian Proksch - initial API and implementation
 ******************************************************************************/
package cc.recommenders.io;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import cc.recommenders.utils.gson.GsonUtil;

public class WritingArchive implements Closeable {

	private int count = 0;
	private ZipOutputStream out;

	public WritingArchive(File file) throws FileNotFoundException {
		out = new ZipOutputStream(new FileOutputStream(file));
	}

	public <T> void add(T obj) throws IOException {
		String filename = count + ".json";
		add(obj, filename);
		count++;
	}

	public <T> void add(T obj, String filename) throws IOException {
		out.putNextEntry(new ZipEntry(filename));
		out.write(GsonUtil.serialize(obj).getBytes());
		out.closeEntry();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}
}
