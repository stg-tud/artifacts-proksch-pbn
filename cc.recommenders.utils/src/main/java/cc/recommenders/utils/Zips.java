/**
 * Copyright (c) 2010, 2012 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package cc.recommenders.utils;

import static org.apache.commons.io.filefilter.DirectoryFileFilter.DIRECTORY;
import static org.apache.commons.io.filefilter.FileFileFilter.FILE;
import static org.apache.commons.lang3.StringUtils.removeStart;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import cc.recommenders.annotations.Nullable;
import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;

import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.io.InputSupplier;
import com.google.common.io.OutputSupplier;

public class Zips {

    public static void unzip(File source, File dest) throws IOException {
        ZipInputStream zis = null;
        try {
            InputSupplier<FileInputStream> fis = Files.newInputStreamSupplier(source);
            zis = new ZipInputStream(fis.getInput());
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    final File file = new File(dest, entry.getName());
                    Files.createParentDirs(file);
                    Files.write(ByteStreams.toByteArray(zis), file);
                }
            }
        } finally {
            Closeables.closeQuietly(zis);
        }
    }

    public static void zip(File directory, File out) throws IOException {
        ZipOutputStream zos = null;
        try {
            OutputSupplier<FileOutputStream> s = Files.newOutputStreamSupplier(out);
            zos = new ZipOutputStream(s.getOutput());
            Collection<File> files = FileUtils.listFiles(directory, FILE, DIRECTORY);
            for (File f : files) {
                String path = removeStart(f.getPath(), directory.getAbsolutePath() + "/");
                ZipEntry e = new ZipEntry(path);
                zos.putNextEntry(e);
                byte[] data = Files.toByteArray(f);
                zos.write(data);
                zos.closeEntry();
            }
        } finally {
            Closeables.close(zos, false);
        }
    }

    public static String path(ITypeName type, @Nullable String suffix) {
        String name = StringUtils.removeStart(type.getIdentifier(), "L");
        return suffix == null ? name : name + suffix;
    }

    public static ITypeName type(ZipEntry entry, @Nullable String suffix) {
        String name = StringUtils.removeEnd(entry.getName(), suffix);
        return VmTypeName.get("L" + name);
    }

    public static IMethodName method(ZipEntry e, String suffix) {
        String name = "L" + StringUtils.substringBefore(e.getName(), suffix);
        int start = name.lastIndexOf('/');
        char[] chars = name.toCharArray();
        chars[start] = '.';
        for (int i = start + 1; i < chars.length; i++) {
            if (chars[i] == '.')
                chars[i] = '/';
        }
        return VmMethodName.get(new String(chars));
    }

    public static String path(IMethodName method, @Nullable String suffix) {
        ITypeName type = method.getDeclaringType();
        String name = path(type, null) + "/" + method.getSignature().replaceAll("/", ".");
        return suffix == null ? name : name + suffix;
    }

}
