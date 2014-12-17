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
package cc.recommenders.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Logger;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.utils.DateProvider;

import com.google.common.collect.Maps;

public class OutputUtilsTest {

	private OutputUtils sut;
	private DateProvider dateProvider;
	private Map<String, String> options;

	@Before
	public void setup() {
		dateProvider = mock(DateProvider.class);
		Logger.reset();
		Logger.setCapturing(true);

		options = Maps.newLinkedHashMap();
		options.put("A", "A...");
		options.put("B", "B...");

		sut = new OutputUtils(dateProvider);
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void header() {
		Calendar a = new GregorianCalendar(1982, 12, 1, 1, 2, 0);
		Calendar b = new GregorianCalendar(1982, 12, 1, 1, 2, 13);

		when(dateProvider.getDate()).thenReturn(a.getTime()).thenReturn(b.getTime());
		when(dateProvider.getTimeSeconds()).thenReturn(a.getTimeInMillis() / 1000).thenReturn(
				b.getTimeInMillis() / 1000);

		sut.startEvaluation();
		sut.stopEvaluation();

		sut.printResultHeader("FILE", getClass(), 11, options);

		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("--> put outputs into: FILE"));
		assertTrue(log.get(1).contains("do not edit manually, auto-generated on Sat Jan 01 01:02:13 CET 1983"));
		// empty line
		assertTrue(log.get(3).contains("results for class cc.recommenders.evaluation.OutputUtilsTest"));
		assertTrue(log.get(4).contains("- project-folded cross validation"));
		assertTrue(log.get(5).contains("- num folds: 11"));
		assertTrue(log.get(6).contains("- all types seen in >=11 projects with >=1 usage"));
		assertTrue(log.get(7).contains("- options:"));
		assertTrue(log.get(8).contains("\tA: A..."));
		assertTrue(log.get(9).contains("\tB: B..."));
	}

	@Test
	public void speedup() {
		Calendar a = new GregorianCalendar(1982, 12, 1, 1, 2, 0);
		Calendar b = new GregorianCalendar(1982, 12, 1, 1, 2, 13);

		when(dateProvider.getDate()).thenReturn(a.getTime()).thenReturn(b.getTime());
		when(dateProvider.getTimeSeconds()).thenReturn(a.getTimeInMillis() / 1000).thenReturn(
				b.getTimeInMillis() / 1000);

		sut.startEvaluation();
		sut.stopEvaluation();
		sut.printSpeedup(32);

		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains(String.format("- started at %s, finished at %s", a.getTime(), b.getTime())));
		assertTrue(log.get(1).contains("\trunning for 13 seconds"));
		assertTrue(log.get(2).contains("\taggregated processing time is 32.0 seconds"));
		assertTrue(log.get(3).contains("\t--> speed up of 2.5 through distribution"));
	}

	@Test
	public void typeCounts() {
		sut.count(VmTypeName.get("LType1"), 1, 3);
		sut.count(VmTypeName.get("LType1"), 1, 4);
		sut.count(VmTypeName.get("LType1"), 2, 3);
		sut.count(VmTypeName.get("LType1"), 3, 4);
		sut.count(VmTypeName.get("LType2"), 1, 6);

		sut.printTypeCounts();

		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("types:"));
		assertTrue(log.get(1).contains("10 - LType1 ("));
		assertTrue(log.get(2).contains("3"));
		assertTrue(log.get(3).contains(", 3"));
		assertTrue(log.get(4).contains(", 4"));
		assertTrue(log.get(5).contains(")\n"));
		assertTrue(log.get(6).contains("6 - LType2 ("));
		assertTrue(log.get(7).contains("6"));
		assertTrue(log.get(8).contains(")\n"));
		assertTrue(log.get(9).contains("-------"));
		assertTrue(log.get(10).contains("16 - total"));
	}

	@Test
	public void progress() {
		sut.setNumTasks(10);
		sut.printProgress("%s");
		sut.printProgress("%s");

		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("10.0% (1/10)"));
		assertTrue(log.get(1).contains("20.0% (2/10)"));
	}

	@Test
	public void progressStrangeNumbers() {
		sut.setNumTasks(13);
		sut.printProgress("%s");

		List<String> log = Logger.getCapturedLog();
		assertTrue(log.get(0).contains("7.7% (1/13)"));
	}

	@Test(expected = AssertionException.class)
	public void missingMarker() {
		sut.setNumTasks(10);
		sut.printProgress("");
	}

	@Test(expected = AssertionException.class)
	public void humanReadableByteCountForNegtiveNumbers() {
		OutputUtils.humanReadableByteCount(-1);
	}

	@Test
	public void humanReadableByteCount() {
		assertEquals("0 B", OutputUtils.humanReadableByteCount(0));
		assertEquals("1023 B", OutputUtils.humanReadableByteCount(1023));
		assertEquals("1.0 KiB", OutputUtils.humanReadableByteCount(1024));
		assertEquals("1.0 MiB", OutputUtils.humanReadableByteCount(1024*1024));
		assertEquals("1.0 GiB", OutputUtils.humanReadableByteCount(1024*1024*1024));
	}
}