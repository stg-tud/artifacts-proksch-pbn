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
package cc.recommenders.utils.gson;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.features.CallFeature;
import cc.recommenders.usages.features.ClassFeature;
import cc.recommenders.usages.features.DefinitionFeature;
import cc.recommenders.usages.features.FirstMethodFeature;
import cc.recommenders.usages.features.ParameterFeature;
import cc.recommenders.usages.features.SuperMethodFeature;
import cc.recommenders.usages.features.TypeFeature;
import cc.recommenders.usages.features.UsageFeature;
import cc.recommenders.utils.dictionary.Dictionary;

import com.google.gson.reflect.TypeToken;

public class GsonUtilTest {

	private static final ITypeName SOME_TYPE = VmTypeName.get("LA");
	private static final IMethodName SOME_METHOD = VmMethodName.get("LA.m()V");

	@Rule
	public TemporaryFolder temp = new TemporaryFolder();

	@Test
	public void serializationRoundtripToFile() throws IOException {

		File file = getFile("aFile.json");
		String expected = "some string";

		GsonUtil.serialize(expected, file);
		String actual = GsonUtil.deserialize(file, String.class);

		assertEquals(expected, actual);
	}

	@Test
	public void serializationRoundtripToString() throws IOException {

		String expected = "some other string";

		String json = GsonUtil.serialize(expected);
		String actual = GsonUtil.deserialize(json, String.class);

		assertEquals(expected, actual);
	}

	@Test
	public void serializationRoundtripWithStreams() throws IOException {

		File file = getFile("afile.json");
		String expected = "some other string";

		FileOutputStream fos = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(fos);
		GsonUtil.serialize(expected, writer);
		writer.close();
		fos.close();

		FileInputStream fis = new FileInputStream(file);
		String actual = GsonUtil.deserialize(fis, String.class);
		fis.close();

		assertEquals(expected, actual);
	}

	private File getFile(String name) {
		return new File(temp.getRoot().getAbsoluteFile() + "/" + name);
	}

	@Test
	public void checkDeSerializationOfCallFeature() {
		assertCorrectDeSerialization(new CallFeature(SOME_METHOD));
	}

	@Test
	public void checkDeSerializationOfDictionaryWithCallFeature() {
		UsageFeature expected = new CallFeature(SOME_METHOD);

		Dictionary<UsageFeature> dict = new Dictionary<UsageFeature>();
		dict.add(expected);

		assertCorrectDeSerialization(dict);
	}

	@Test
	public void checkDeSerializationOfClassFeature() {
		assertCorrectDeSerialization(new ClassFeature(SOME_TYPE));
	}

	@Test
	public void checkDeSerializationOfDefinitionFeature() {
		DefinitionSite d = DefinitionSites.createDefinitionByReturn(SOME_METHOD);
		assertCorrectDeSerialization(new DefinitionFeature(d));
	}

	@Test
	public void checkDeSerializationOfFirstMethodFeature() {
		assertCorrectDeSerialization(new FirstMethodFeature(SOME_METHOD));
	}

	@Test
	public void checkDeSerializationOfParameterFeature() {
		assertCorrectDeSerialization(new ParameterFeature(SOME_METHOD, 13));
	}

	@Test
	public void checkDeSerializationOfSuperMethodFeature() {
		assertCorrectDeSerialization(new SuperMethodFeature(SOME_METHOD));
	}

	@Test
	public void checkDeSerializationOfTypeFeature() {
		assertCorrectDeSerialization(new TypeFeature(SOME_TYPE));
	}

	private static void assertCorrectDeSerialization(UsageFeature expected) {
		String json = GsonUtil.serialize(expected);
		UsageFeature actual = GsonUtil.deserialize(json, UsageFeature.class);
		assertEquals(expected, actual);
	}

	private static void assertCorrectDeSerialization(Dictionary<UsageFeature> expected) {
		Type DICT_TYPE = new TypeToken<Dictionary<UsageFeature>>() {
		}.getType();

		String json = GsonUtil.serialize(expected);
		Dictionary<UsageFeature> actual = GsonUtil.deserialize(json, DICT_TYPE);
		assertEquals(expected, actual);
	}
}