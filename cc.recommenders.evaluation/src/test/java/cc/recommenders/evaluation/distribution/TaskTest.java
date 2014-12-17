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
package cc.recommenders.evaluation.distribution;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.apache.commons.lang.UnhandledException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.testutils.Serialization;

import com.google.inject.Injector;

public class TaskTest {

	@Mock(name = "mockScheduler")
	public ITaskScheduler<String> scheduler;

	@Mock(name = "mockCallable")
	public Callable<String> callable;

	private Task<String> sut;

	@Before
	public void setup() throws RemoteException {
		MockitoAnnotations.initMocks(this);
		sut = new Task<String>(callable, scheduler);
	}

	@Test(expected = AssertionException.class)
	public void cannotPassNull_1() {
		new Task<String>(null, scheduler);
	}

	@Test(expected = AssertionException.class)
	public void cannotPassNull_2() {
		new Task<String>(new TestCallable("a"), null);
	}

	@Test
	public void membersCanGetInjection() {
		Injector injector = mock(Injector.class);
		sut.injectionForMembers(injector);
		verify(injector).injectMembers(eq(callable));
	}

	@Test
	public void byDefaultNoResult() {
		assertFalse(sut.hasResult());
		assertNull(sut.getResult());
	}

	@Test
	public void byDefaultNoCrash() {
		assertFalse(sut.hasCrashed());
		assertNull(sut.getException());
	}

	@Test
	public void computationCreatesResult() throws Exception {
		String expected = "x";
		when(callable.call()).thenReturn(expected);
		sut.run();

		byDefaultNoCrash();
		assertTrue(sut.hasResult());
		String actual = sut.getResult();
		assertEquals(expected, actual);
	}

	@Test
	public void callCrashes() throws Exception {
		RuntimeException e = new RuntimeException();
		when(callable.call()).thenThrow(e);
		sut.run();
		byDefaultNoResult();
		assertTrue(sut.hasCrashed());
		assertEquals(e, sut.getException());
	}

	@Test(expected = UnhandledException.class)
	@SuppressWarnings("unchecked")
	public void remoteExceptionCannotBeHandled() throws RemoteException {
		doThrow(RemoteException.class).when(scheduler).finished(any(Task.class));
		new Task<String>(new TestCallable("a"), scheduler).run();
	}

	@Test
	public void hasUniqueId() {
		Task<String> task = new Task<String>(new TestCallable("a"), scheduler);
		UUID uuid = task.getUuid();
		assertNotNull(uuid);
	}

	@Test
	public void toStringAddsShortenedUuidAndTaskHintToCallable() {
		Task<String> task = new Task<String>(new TestCallable("a"), scheduler);
		String shortenedUuid = task.getUuid().toString().substring(0, 5);
		String actual = task.toString();
		String expected = String.format("[Task@%s: [TestCallable: %s]]", shortenedUuid, "a");
		assertEquals(expected, actual);
	}

	@Test
	public void equalsAndHashCode_different() {
		Task<String> a = new Task<String>(new TestCallable("a"), scheduler);
		Task<String> b = new Task<String>(new TestCallable("a"), scheduler);
		assertNotEquals(a, b);
		assertNotEquals(a.getUuid(), b.getUuid());
		assertTrue(a.hashCode() != b.hashCode());
	}

	@Test
	public void equalsAndHashCode_equalAfterSerilization() throws Exception {
		Task<String> a = new Task<String>(new TestCallable("a"), new TestTaskScheduler());

		byte[] arr = Serialization.serialize(a);
		Task<String> b = Serialization.deserialize(arr);

		assertEquals(a, b);
		assertEquals(a.getUuid(), b.getUuid());
		assertTrue(a.hashCode() == b.hashCode());
	}

	private static class TestTaskScheduler implements ITaskScheduler<String> {
		private static final long serialVersionUID = 1L;

		@Override
		public InjectableRunnable getNextNullableTask() throws RemoteException {
			return null;
		}

		@Override
		public void finished(Task<String> task) throws RemoteException {
		}
	}

	private static class TestCallable implements Callable<String>, Serializable {
		private static final long serialVersionUID = 1L;
		private String content;

		public TestCallable(String content) {
			this.content = content;
		}

		@Override
		public String call() throws Exception {
			return "c-" + content;
		}

		@Override
		public String toString() {
			return String.format("[TestCallable: %s]", content);
		}

	}
}