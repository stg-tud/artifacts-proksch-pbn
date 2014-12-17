/**
 * Copyright (c) 2011-2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 * 
 * Idea adapted from:
 * http://stackoverflow.com/questions/12036607/ \
 *  bundle-native-dependencies-in-runnable-jar-with-maven
 */
package cc.recommenders.nativelibs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public class NativeLibLoader {

	public static final String OS_ARCH = "os.arch";
	public static final String OS_NAME = "os.name";
	public static final String JAVA_IO_TMPDIR = "java.io.tmpdir";

	public void loadLibrary(String library) {
		try {
			String fileName = copyLibraryToTempFile(library);
			System.load(fileName);
		} catch (IOException e) {
			System.err.println("Could not find library " + library + " as resource, trying fallback lookup through System.loadLibrary");
			System.loadLibrary(library);
		}
	}

	public String copyLibraryToTempFile(String library) throws IOException {
		InputStream in = null;
		OutputStream out = null;

		try {
			String libraryName = getContextAwareLibraryName(library);
			in = getClass().getClassLoader().getResourceAsStream(
					"lib/" + libraryName);

			if (in == null) {
				fail("runtime not supported, '%s' missing in native bundle",
						libraryName);
			}

			File file = createTempFile(library);
			out = new FileOutputStream(file);

			int cnt;
			byte buf[] = new byte[16 * 1024];
			while ((cnt = in.read(buf)) >= 1) {
				out.write(buf, 0, cnt);
			}

			return file.getAbsolutePath();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}

	private String getContextAwareLibraryName(String library) {
		String osName = getOSName();
		String osArch = System.getProperty(OS_ARCH).toLowerCase();
		String extension = getExtension(osName);
		return String.format("%s-%s-%s.%s", library, osName, osArch, extension);
	}

	private String getOSName() {
		String property = System.getProperty(OS_NAME);
		String osNameOrig = property.toLowerCase().replaceAll(" ", "");
		if (osNameOrig.startsWith("win")) {
			return "win";
		} else {
			return osNameOrig;
		}
	}

	private String getExtension(String osName) {
		if (osName.startsWith("win")) {
			return "dll";
		} else if (osName.startsWith("linux")) {
			return "so";
		} else if (osName.startsWith("mac")) {
			return "jnilib";
		}
		fail("operating system '%s' not supported", osName);
		return null;
	}

	private void fail(String msg, Object... args) {
		throw new UnsupportedOperationException(String.format(msg, args));
	}

	private File createTempFile(String library) throws IOException {
		String tmpDirName = System.getProperty(JAVA_IO_TMPDIR);
		File tmpDir = new File(tmpDirName);
		if (!tmpDir.exists()) {
			tmpDir.mkdir();
		}
		File file = File.createTempFile(library + "-", ".tmp", tmpDir);
		// Clean up the file when exiting
		file.deleteOnExit();
		return file;
	}
}