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
package cc.recommenders.nativelibs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class NativeLibLoaderTest {

	private final static String ORIG_OS_NAME = System.getProperty(NativeLibLoader.OS_NAME);
	private final static String ORIG_OS_ARCH = System.getProperty(NativeLibLoader.OS_ARCH);
	private final static String ORIG_TMPDIR = System.getProperty(NativeLibLoader.JAVA_IO_TMPDIR);

	@Rule
	public TemporaryFolder root = new TemporaryFolder();
	private File tmp;
	private File lib;

	private NativeLibLoader sut;

	@Before
	public void setup() throws IOException {
		tmp = root.newFolder("tmp");
		lib = new File("src/main/resources/lib/");

		System.setProperty(NativeLibLoader.JAVA_IO_TMPDIR, tmp.getPath());
		sut = new NativeLibLoader();
	}

	@After
	public void tearDown() {
		System.setProperty(NativeLibLoader.OS_NAME, ORIG_OS_NAME);
		System.setProperty(NativeLibLoader.OS_ARCH, ORIG_OS_ARCH);
		System.setProperty(NativeLibLoader.JAVA_IO_TMPDIR, ORIG_TMPDIR);
	}

	@Test
	public void canLoadAppropriateSmileLib() {
		sut.loadLibrary("jsmile");
	}

	@Test
	public void tmpDirIsCreatedOnDemand() {
		tmp.delete();
		sut.loadLibrary("jsmile");
	}

	@Test
	public void copyFile_win_x86() throws IOException {
		setSystem("Win", "x86");
		assertCorrectFile("jsmile-win-x86.dll");
	}

	@Test
	public void copyFile_win81_64() throws IOException {
		setSystem("Windows 8.1", "amd64");
		assertCorrectFile("jsmile-win-amd64.dll");
	}

	@Test
	public void copyFile_linux_i386() throws IOException {
		setSystem("Linux", "i386");
		assertCorrectFile("jsmile-linux-i386.so");
	}

	@Test
	public void copyFile_osx_x86() throws IOException {
		setSystem("Mac OS X", "x86");
		assertCorrectFile("jsmile-macosx-x86.jnilib");
	}

	private void assertCorrectFile(String inFileName) {
		FileInputStream fis = null;
		FileInputStream fos = null;
		try {
			fis = new FileInputStream(new File(lib, inFileName));
			String md5in = DigestUtils.md5Hex(fis);
			fis.close();

			String outFileName = sut.copyLibraryToTempFile("jsmile");

			fos = new FileInputStream(new File(outFileName));
			String md5out = DigestUtils.md5Hex(fos);
			fos.close();

			assertEquals(md5in, md5out);
		} catch (IOException e) {
			fail();
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
	}

	private void setSystem(String osName, String osArch) {
		System.setProperty(NativeLibLoader.OS_NAME, osName);
		System.setProperty(NativeLibLoader.OS_ARCH, osArch);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unknownOperatingSystem() throws IOException {
		System.setProperty(NativeLibLoader.OS_NAME, "unsupported");
		sut.copyLibraryToTempFile("jsmile");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void unknownArchitecture() throws IOException {
		System.setProperty(NativeLibLoader.OS_ARCH, "unsupported");
		sut.copyLibraryToTempFile("jsmile");
	}
}