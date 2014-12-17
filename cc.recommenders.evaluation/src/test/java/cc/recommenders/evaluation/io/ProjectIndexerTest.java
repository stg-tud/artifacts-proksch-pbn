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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;
import cc.recommenders.io.ReadingArchive;
import cc.recommenders.io.WritingArchive;
import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.ProjectFoldingIndex;
import cc.recommenders.usages.Usage;

import com.codetrails.data.ObjectUsage;
import com.codetrails.data.ObjectUsageValidator;
import com.codetrails.data.UsageConverter;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ProjectIndexerTest {

	private static final ITypeName TYPE = VmTypeName.get("Lpackage/Type");
	private static final String TYPE_FILENAME = "Lpackage_Type.zip";

	@Captor
	private ArgumentCaptor<Predicate<String>> predicate;
	@Captor
	private ArgumentCaptor<ProjectFoldingIndex> pfiCaptor = null;

	@Mock
	private Predicate<Usage> isInterestingPred;
	private boolean isInteresting = true;
	private ObjectUsageValidator validator;

	private Directory in;
	private Directory out;
	private UsageConverter converter;
	private Map<String, ReadingArchive> readingArchives;
	private WritingArchive writingArchive;
	private ProjectIndexer sut;

	private Set<ObjectUsage> expectedConverts;
	private Set<ProjectFoldedUsage> expectedWrites;

	@Before
	public void setup() throws IOException {
		Logger.reset();
		Logger.setCapturing(true);

		MockitoAnnotations.initMocks(this);
		in = mock(Directory.class);
		out = mock(Directory.class);
		converter = mock(UsageConverter.class);
		readingArchives = Maps.newHashMap();
		writingArchive = mock(WritingArchive.class);
		validator = mock(ObjectUsageValidator.class);

		expectedConverts = Sets.newLinkedHashSet();
		expectedWrites = Sets.newLinkedHashSet();

		when(out.getWritingArchive(anyString())).thenReturn(writingArchive);
		when(isInterestingPred.apply(any(Usage.class))).then(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return isInteresting;
			}
		});
		when(validator.isValid(any(ObjectUsage.class))).thenReturn(true);
		sut = new ProjectIndexer(in, out, converter, validator, isInterestingPred);
	}

	@After
	public void teardown() {
		Logger.setCapturing(false);
	}

	@Test
	public void outputIsClearedByDefault() throws IOException {
		sut.createIndex();
		verify(out).clear();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void onlyZipsAreReadAndClosed() throws IOException {
		givenTheListOfFiles("a.zip");
		withContents("a.zip", 0);

		sut.createIndex();

		verify(in).list(any(Predicate.class));
		verify(in).getReadingArchive(eq("a.zip"));
		verifyNoMoreInteractions(in);

		ReadingArchive ra = readingArchives.get("a.zip");
		verify(ra).hasNext();
		verify(ra).close();
	}

	@Test
	public void predicateMatchesZipFiles() throws IOException {
		givenTheListOfFiles("a.zip");
		withContents("a.zip", 0);

		sut.createIndex();

		Predicate<String> p = predicate.getValue();

		String[] posExamples = new String[] { ".zip", "a.zip", "0.zip", ".asd.zip" };
		for (String pos : posExamples) {
			assertTrue(p.apply(pos));
		}

		String[] negExamples = new String[] { "", "zip", "a", "0" };
		for (String neg : negExamples) {
			assertFalse(p.apply(neg));
		}
	}

	@Test
	public void allReadUsagesAreConvertedAndWritten() throws IOException {
		givenTheListOfFiles("a.zip");
		withContents("a.zip", 3);

		sut.createIndex();

		for (ObjectUsage ou : expectedConverts) {
			verify(converter).toRecommenderUsage(ou);
		}

		verify(out).getWritingArchive(eq(TYPE_FILENAME));
		for (ProjectFoldedUsage u : expectedWrites) {
			verify(writingArchive).add(u);
		}
		verify(writingArchive).close();
	}

	@Test
	public void processingLogsSteps() throws IOException {
		givenTheListOfFiles("a.zip");
		withContents("a.zip", 3);

		sut.createIndex();

		List<String> log = Logger.getCapturedLog();
		assertEquals(4, log.size());
		assertTrue(log.get(0).contains("clearing index..."));
		assertTrue(log.get(1).contains("creating index..."));
		assertTrue(log.get(2).contains("processing 'a.zip'..."));
		assertTrue(log.get(3).contains("finished: 0 invalid, 0 filtered, 3 remaining"));
	}

	@Test
	public void usagesCanBeFilteredWithPredicate() throws IOException {
		givenTheListOfFiles("a.zip");
		withContents("a.zip", 3);

		isInteresting = false;
		sut.createIndex();

		verifyNoMoreInteractions(writingArchive);

		List<String> log = Logger.getCapturedLog();
		assertEquals(4, log.size());
		String entry = log.get(3);
		assertTrue(entry.contains("finished"));
		assertTrue(entry.contains(" 0 invalid"));
		assertTrue(entry.contains(" 3 filtered"));
		assertTrue(entry.contains(" 0 remaining"));
	}

	@Test
	public void doesNotCrashForBrokenUsagesAndLogsThem() throws IOException {
		givenTheListOfFiles("a.zip");
		ReadingArchive ra = readingArchives.get("a.zip");
		when(ra.hasNext()).thenReturn(true);
		when(ra.getNext(any(Type.class))).thenThrow(new RuntimeException());

		sut.createIndex();

		List<String> log = Logger.getCapturedLog();
		assertEquals(4, log.size());

		String entry = log.get(3);
		assertTrue(entry.contains("EE reading error in"));
		assertTrue(entry.contains("a.zip"));
	}

	@Test
	public void invalidValuesAreIgnoredAndLogged() throws IOException {
		givenTheListOfFiles("a.zip");
		ReadingArchive ra = readingArchives.get("a.zip");
		ObjectUsage ou = mock(ObjectUsage.class);
		Usage u = mock(Usage.class);
		when(u.getType()).thenReturn(TYPE);

		when(converter.toRecommenderUsage(ou)).thenReturn(u);

		when(ra.hasNext()).thenReturn(true).thenReturn(true).thenReturn(false);
		when(ra.getNext(any(Type.class))).thenReturn(null).thenReturn(ou);

		when(validator.isValid(any(ObjectUsage.class))).thenReturn(false).thenReturn(true);
		when(validator.getLastError()).thenReturn("XYZ");

		sut.createIndex();

		verify(writingArchive).add(new ProjectFoldedUsage(u, "a.zip"));

		List<String> log = Logger.getCapturedLog();
		assertEquals(5, log.size());

		String entry = log.get(3);
		assertTrue(entry.contains("EE ignoring invalid ObjectUsage in"));
		assertTrue(entry.contains("a.zip"));
		assertTrue(entry.contains("XYZ"));

		String entry2 = log.get(4);
		assertTrue(entry2.contains("finished"));
		assertTrue(entry2.contains(" 1 invalid"));
		assertTrue(entry2.contains(" 0 filtered"));
		assertTrue(entry2.contains(" 1 remaining"));
	}

	@Test
	public void correctIndexIsCreated() throws IOException {
		givenTheListOfFiles("a.zip", "b.zip");
		withContents("a.zip", 3);
		withContents("b.zip", 5);

		sut.createIndex();

		verify(out).write(pfiCaptor.capture(), eq("index.json"));
		ProjectFoldingIndex index = pfiCaptor.getValue();

		Set<ITypeName> actual = index.getTypes();
		Set<ITypeName> expected = Sets.newHashSet(TYPE);
		assertEquals(expected, actual);

		Map<String, Integer> actCounts = index.getCounts(TYPE);
		Map<String, Integer> expCounts = Maps.newHashMap();
		expCounts.put("a.zip", 3);
		expCounts.put("b.zip", 5);
		assertEquals(expCounts, actCounts);
	}

	private void givenTheListOfFiles(String... fileNames) throws IOException {
		when(in.list(predicate.capture())).thenReturn(Sets.<String> newHashSet(fileNames));

		for (String fileName : fileNames) {
			ReadingArchive ra = mock(ReadingArchive.class);
			readingArchives.put(fileName, ra);
			when(in.getReadingArchive(eq(fileName))).thenReturn(ra);
		}
	}

	private void withContents(String fileName, final int numUsages) throws IOException {
		ReadingArchive ra = readingArchives.get(fileName);
		final ObjectUsage[] ous = new ObjectUsage[numUsages];

		for (int i = 0; i < numUsages; i++) {
			ous[i] = mock(ObjectUsage.class);
			Usage u = mock(Usage.class);
			when(u.getType()).thenReturn(TYPE);

			when(converter.toRecommenderUsage(ous[i])).thenReturn(u);
			expectedConverts.add(ous[i]);
			expectedWrites.add(new ProjectFoldedUsage(u, fileName));
		}

		when(ra.hasNext()).thenAnswer(new Answer<Boolean>() {
			private int count = 0;

			@Override
			public Boolean answer(InvocationOnMock invocation) throws Throwable {
				return count++ < numUsages;
			}
		});

		when(ra.getNext(any(Type.class))).thenAnswer(new Answer<ObjectUsage>() {
			private int idx = 0;

			@Override
			public ObjectUsage answer(InvocationOnMock invocation) throws Throwable {
				return ous[idx++];
			}
		});
	}
}