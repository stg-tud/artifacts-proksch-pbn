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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Directory;
import cc.recommenders.io.ReadingArchive;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.ProjectFoldingIndex;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ProjectFoldedUsageStoreTest {

	private static final ITypeName TYPE = VmTypeName.get("LA/B/C");
	private Directory dir;
	private ProjectFoldingIndex index;
	private HashSet<String> files;

	private ProjectFoldedUsageStore sut;
	private ReadingArchive readingArchive;
	private ProjectFoldingStrategy foldingStrategy;
	private List<ProjectFoldedUsage> usages;
	private Map<String, Integer> counts;
	private Map<String, Integer> mapping;

	@Before
	@SuppressWarnings("unchecked")
	public void setup() throws IOException {
		dir = mock(Directory.class);
		index = mock(ProjectFoldingIndex.class);
		files = Sets.newHashSet("index.json");
		readingArchive = mock(ReadingArchive.class);
		foldingStrategy = mock(ProjectFoldingStrategy.class);
		usages = Lists.newLinkedList();
		usages.add(new ProjectFoldedUsage(mock(Usage.class), "p1"));
		counts = Maps.newHashMap();
		counts.put("A", 12);
		counts.put("B", 23);
		counts.put("C", 34);
		mapping = Maps.newHashMap();
		mapping.put("XYZ", 123);

		when(dir.list()).thenReturn(files);
		when(dir.read(eq("index.json"), eq(ProjectFoldingIndex.class))).thenReturn(index);
		when(dir.getReadingArchive(anyString())).thenReturn(readingArchive);

		when(index.getCounts(any(ITypeName.class))).thenReturn(counts);

		when(readingArchive.hasNext()).thenReturn(true).thenReturn(false);
		when(readingArchive.getNext(any(Class.class))).thenReturn(usages.get(0));

		when(foldingStrategy.createMapping(any(Map.class), anyInt())).thenReturn(mapping);

		sut = new ProjectFoldedUsageStore(dir, foldingStrategy);
	}

	@Test(expected = AssertionException.class)
	public void itDoesNotMakeSenseToRequest0Folds() throws IOException {
		sut.createTypeStore(TYPE, 0);
	}

	@Test(expected = AssertionException.class)
	public void itDoesNotMakeSenseToRequestNegativeFolds() throws IOException {
		sut.createTypeStore(TYPE, -1);
	}
	
	@Test(expected = AssertionException.class)
	public void missingIndexIsHandled_getTypes() throws IOException {
		files.clear();
		sut.getTypes();
	}

	@Test(expected = AssertionException.class)
	public void missingIndexIsHandled_createTypeStore() throws IOException {
		files.clear();
		sut.createTypeStore(null, 1);
	}

	@Test(expected = AssertionException.class)
	public void missingIndexIsHandled_isAvailable() throws IOException {
		files.clear();
		sut.isAvailable(null, 1);
	}

	@Test
	public void typeIsOnlyAvailableIfEnoughProjectsWithUsagesExist() throws IOException {
		counts.clear();
		counts.put("a", 1);
		counts.put("b", 1);
		assertFalse(sut.isAvailable(TYPE, 3));
		counts.put("c", 0);
		assertFalse(sut.isAvailable(TYPE, 3));
		counts.put("d", 1);
		assertTrue(sut.isAvailable(TYPE, 3));
	}

	@Test(expected = RuntimeException.class)
	public void ioExceptionIsconvertedToRuntimeException() throws IOException {
		when(dir.read(anyString(), any(Type.class))).thenThrow(new IOException());
		sut.getTypes();
	}

	@Test
	public void getTypesIsPropagatedToIndex() {
		ITypeName t = mock(ITypeName.class);
		when(index.getTypes()).thenReturn(Sets.newHashSet(t));

		Set<ITypeName> actual = sut.getTypes();

		verify(index).getTypes();
		assertEquals(Sets.newHashSet(t), actual);
	}

	@Test
	public void indexLoadingIsLazyAndOnlyReadOnce() throws IOException {
		sut.getTypes();
		sut.getTypes();
		verify(dir).read(eq("index.json"), any(Type.class));
	}

	@Test
	public void allUsagesAreReadFromCorrectFile() throws IOException {
		when(readingArchive.hasNext()).thenReturn(true).thenReturn(true).thenReturn(true).thenReturn(false);

		sut.createTypeStore(TYPE, 3);

		verify(dir).getReadingArchive("LA_B_C.zip");
		verify(readingArchive, times(3)).getNext(eq(ProjectFoldedUsage.class));
		verify(readingArchive).close();
	}

	@Test
	public void foldingStrategyIsCalled() throws IOException {
		sut.createTypeStore(TYPE, 3);
		verify(foldingStrategy).createMapping(eq(counts), eq(3));
	}

	@Test
	public void resultIsNotNull() throws IOException {
		assertNotNull(sut.createTypeStore(TYPE, 3));
	}

	@Test
	public void typeStoreIsCreatedWithCorrectParameters() throws IOException {
		TypeStore actual = sut.createTypeStore(TYPE, 3);
		assertEquals(usages, actual.getAllUsages());
		assertEquals(mapping, actual.getMapping());
	}
}