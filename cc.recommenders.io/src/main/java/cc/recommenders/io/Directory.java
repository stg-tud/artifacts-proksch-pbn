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

import static cc.recommenders.utils.gson.GsonUtil.deserialize;
import static cc.recommenders.utils.gson.GsonUtil.serialize;
import static com.google.common.base.Predicates.alwaysTrue;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import cc.recommenders.assertions.Asserts;
import cc.recommenders.assertions.Throws;

import com.google.common.base.Predicate;

public class Directory {

    private final String rootDir;

    public Directory(String rootDir) {

        this.rootDir = rootDir;

        File rootFile = new File(rootDir);
        Asserts.assertFalse(rootFile.isFile(), "unable to create directory (is file)");
        if(!rootFile.isDirectory()) {
            rootFile.mkdirs();
            if(!rootFile.isDirectory()) {
                Asserts.fail("unable to create directory");
            }
        }
    }

    public <T> T read(String relativePath, Type classOfT) throws IOException {

        File file = new File(rootDir + "/" + relativePath);
        T obj = deserialize(file, classOfT);

        return obj;
    }

    public <T> void write(T obj, String relativePath) throws IOException {

        File file = new File(rootDir + "/" + relativePath);
        serialize(obj, file);
    }

    public String readContent(String relativePath) throws IOException {
        File file = new File(rootDir + "/" + relativePath);
        return FileUtils.readFileToString(file);
    }

    public void writeContent(String content, String relativePath) throws IOException {
        File file = new File(rootDir + "/" + relativePath);
        FileUtils.writeStringToFile(file, content);
    }

    public boolean exists(String relativePath) {
        File file = new File(rootDir + "/" + relativePath);
        return file.exists();
    }

    public Directory createDirectory(String relativePath) {
        String folderName = rootDir + "/" + relativePath;
        File folder = new File(folderName);
        folder.mkdirs();
        return new Directory(folderName);
    }

    public void clear() {

        File root = new File(rootDir);

        if (root.exists() && root.isDirectory()) {
            for (File file : root.listFiles()) {
                delete(file);
            }
        }

    }

    private void delete(File file) {
        if (file.isDirectory()) {
            for (File sub : file.listFiles()) {
                delete(sub);
            }
        }
        file.delete();
    }

    public int count() {
        return new File(rootDir).list().length;
    }

    public WritingArchive getWritingArchive(String relativePath) throws IOException {

        File file = new File(rootDir + "/" + relativePath);
        file.createNewFile();
        WritingArchive archive = new WritingArchive(file);

        return archive;
    }

    public WritingArchive reopenWritingArchive(String fileName, Type classOfT) throws IOException {
        String tmpFileName = createTempFile(fileName);
        tmpFileName = tmpFileName.replace('-', '_');
        if(!exists(fileName) || exists(tmpFileName)) {
            Throws.throwIllegalArgumentException("file does not exists or name collision of tmpfile");
        }
        
        File old = new File(rootDir + "/" + fileName);
        File tmp = new File(rootDir + "/" + tmpFileName);
        old.renameTo(tmp);
        
        ReadingArchive oldArchive = getReadingArchive(tmpFileName);
        WritingArchive newArchive = getWritingArchive(fileName);
        
        while(oldArchive.hasNext()) {
            Object o = oldArchive.getNext(classOfT);
            newArchive.add(o);
        }
        oldArchive.close();
        tmp.delete();
        
        return newArchive;
    }

    private String createTempFile(String fileName) {
        return fileName + fileName.hashCode();
    }

    public ReadingArchive getReadingArchive(String relativePath) throws IOException {

        File file = new File(rootDir + "/" + relativePath);
        ReadingArchive archive = new ReadingArchive(file);
        return archive;
    }

    public void delete(String relativePath) {
        File file = new File(rootDir + "/" + relativePath);
        file.delete();
    }

    public Set<String> list() {
        Predicate<String> allFiles = alwaysTrue();
        return list(allFiles);
    }

    public Set<String> list(Predicate<String> predicate) {
    	// TODO create test case for ordering
        Set<String> files = new LinkedHashSet<String>();
        for (String file : new File(rootDir).list()) {
            if (predicate.apply(file)) {
                files.add(file);
            }
        }
        return files;
    }

    public URL getUrl() throws MalformedURLException {
        return new URL("file://" + rootDir);
    }

    public URL getUrl(String fileName) throws MalformedURLException {
        return new URL("file://" + rootDir + "/" + fileName);
    }

    public static String createFileName(String s) {
        return s.trim().replaceAll("[^a-zA-Z0-9_.-]+", "_");
    }
}