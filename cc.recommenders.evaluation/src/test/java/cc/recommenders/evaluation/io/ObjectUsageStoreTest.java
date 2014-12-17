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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import cc.recommenders.io.Directory;
import cc.recommenders.io.WritingArchive;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;

import com.codetrails.data.CallSite;
import com.codetrails.data.CallSiteKind;
import com.codetrails.data.DefinitionSite;
import com.codetrails.data.DefinitionSites;
import com.codetrails.data.EnclosingMethodContext;
import com.codetrails.data.ObjectUsage;
import com.codetrails.data.ObjectUsageFilter;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

public class ObjectUsageStoreTest {

	private static final String TYPE1 = "LType1";
	private static final String TYPE2 = "LType2";

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private Directory directory;
	private Predicate<ObjectUsage> ouFilter;
	private ObjectUsageStore sut;

	@Before
	public void setup() {
		directory = new Directory(temporaryFolder.getRoot().getAbsolutePath());
		ouFilter = Predicates.alwaysTrue();
		sut = createSUT(directory);
	}

	public ObjectUsageStore createSUT(Directory directory) {
		return new ObjectUsageStore(directory, ouFilter);
	}

	@Test(expected = RuntimeException.class)
	public void itIsNotPossibleToStoreUsagesForOneTypeMultipleTimes() {
		ObjectUsage usage1 = createUsage(TYPE1, "LContext.m1()V");
		ObjectUsage usage2 = createUsage(TYPE1, "LContext.m2()V");

		sut.store(newArrayList(usage1));
		sut.store(newArrayList(usage2));
	}

	@Test
	public void allTypesAreRememberedAsKeys() {
		ObjectUsage usage1 = createUsage(TYPE1, "LContext.m1()V");
		ObjectUsage usage2 = createUsage(TYPE2, "LContext.m2()V");

		sut.store(newArrayList(usage1));
		sut.store(newArrayList(usage2));
		sut.close();

		sut = createSUT(directory);
		Set<ITypeName> actual = sut.getKeys();
		Set<ITypeName> expected = newHashSet();
		expected.add(VmTypeName.get(TYPE1));
		expected.add(VmTypeName.get(TYPE2));
		assertEquals(expected, actual);
	}

	@Test
	public void allUsagesAreStoredForAKey() {
		ObjectUsage usage1 = createUsage(TYPE1, "LContext.m1()V");
		ObjectUsage usage2 = createUsage(TYPE1, "LContext.m3()V");

		sut.store(newArrayList(usage1, usage2));
		sut.close();

		sut = createSUT(directory);
		List<ObjectUsage> actual = sut.read(VmTypeName.get(TYPE1));
		List<ObjectUsage> expected = newArrayList(usage1, usage2);
		// input is shuffled
		assertFalse(actual.equals(expected));
		// but everything is remembered
		assertEqualEntries(expected, actual);
	}

	private void assertEqualEntries(List<ObjectUsage> expecteds, List<ObjectUsage> actuals) {
		assertEquals(expecteds.size(), actuals.size());
		for (ObjectUsage expected : expecteds) {
			assertTrue(actuals.contains(expected));
		}
		for (ObjectUsage actual : actuals) {
			assertTrue(expecteds.contains(actual));
		}
	}

	@Test
	public void storeClearRemovesAllFilesFromDirectory() {
		Directory dir = mock(Directory.class);
		new ObjectUsageStore(dir, new ObjectUsageFilter()).clear();
		verify(dir).clear();
	}

	@Test(expected = IllegalArgumentException.class)
	public void exceptionIsThrownIfTypeIsNotKnown() {
		ObjectUsage usage1 = createUsage(TYPE1, "LContext.m1()V");
		sut.store(newArrayList(usage1));
		sut.read(VmTypeName.get(TYPE2));
	}

	@Test(expected = IllegalStateException.class)
	public void ioExceptionsCauseAnotherKindOfException() throws IOException {
		ObjectUsage usage1 = createUsage(TYPE1, "LContext.m1()V");
		Directory dir = mock(Directory.class);
		doThrow(new ZipException()).when(dir).getReadingArchive(anyString());
		WritingArchive archive = mock(WritingArchive.class);
		when(dir.getWritingArchive(anyString())).thenReturn(archive);
		when(dir.reopenWritingArchive(anyString(), any(Type.class))).thenReturn(archive);
		when(dir.read(any(String.class), any(Type.class))).thenReturn(newHashSet(VmTypeName.get(TYPE1)));

		sut = createSUT(dir);
		sut.store(newArrayList(usage1));
		sut.close();

		sut.read(VmTypeName.get(TYPE1));
	}

	@Test
	public void everyArchiveIsClosedAfterAStore() throws IOException {
		WritingArchive a1 = mock(WritingArchive.class);
		WritingArchive a2 = mock(WritingArchive.class);
		directory = mock(Directory.class);
		when(directory.getWritingArchive("LType1.zip")).thenReturn(a1);
		when(directory.getWritingArchive("LType2.zip")).thenReturn(a2);

		sut = createSUT(directory);
		ObjectUsage usage1 = createUsage(TYPE1, "LContext.m1()V");
		ObjectUsage usage2 = createUsage(TYPE2, "LContext.m2()V");
		sut.store(newArrayList(usage1, usage2));

		verify(a1).close();
		verify(a2).close();
	}

	private ObjectUsage createUsage(String typeName, String methodName) {

		DefinitionSite def = DefinitionSites.createDefinitionByConstant();
		def.setType(VmTypeName.get(typeName));

		EnclosingMethodContext ctx = new EnclosingMethodContext();
		ctx.setSuperclass(VmTypeName.get("LSuperType"));
		ctx.setName(VmMethodName.get(methodName));
		ctx.setIntroducedBy(VmTypeName.get("LSuperType"));

		CallSite call = new CallSite();
		call.setKind(CallSiteKind.RECEIVER_CALL_SITE);
		call.setCall(VmMethodName.get(methodName));

		List<CallSite> path = Lists.newLinkedList();
		path.add(call);

		Set<List<CallSite>> paths = newHashSet();
		paths.add(path);

		ObjectUsage usage = new ObjectUsage();
		usage.setUuid(UUID.randomUUID());
		usage.setDef(def);
		usage.setContext(ctx);
		usage.setPaths(paths);
		return usage;
	}
}