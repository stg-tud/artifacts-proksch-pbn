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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.concurrent.Callable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import cc.recommenders.exceptions.AssertionException;

import com.google.common.collect.Maps;

public class TaskSchedulerTest {

	@Mock
	private ITaskProvider<String> provider;
	private TaskScheduler<String> sut;
	private Map<String, Callable<String>> tasks;

	@Before
	public void setup() throws RemoteException {
		MockitoAnnotations.initMocks(this);

		tasks = Maps.newLinkedHashMap();

		when(provider.createWorkers()).thenReturn(tasks.values());
	}

	private void initSut() throws RemoteException {
		sut = TaskScheduler.create(provider);
	}

	@Test(expected = AssertionException.class)
	public void providerMustNotBeNull() throws RemoteException {
		TaskScheduler.create(null);
	}

	@Test
	public void absentOptionalIsReturnedWithNoTasks() throws RemoteException {
		initSut();
		Runnable task = sut.getNextNullableTask();
		assertNull(task);
	}

	@Test
	public void providerIsInformedAboutIntermediateResults() throws Exception {
		addTasks("a", "b");
		initSut();
		verify(provider).createWorkers();

		Runnable task = sut.getNextNullableTask();
		task.run();

		verify(provider).addResult(eq("a"));
		verifyNoMoreInteractions(provider);
	}

	@Test
	public void providerIsInformedAboutEnd() throws Exception {
		addTasks("a", "b");
		initSut();
		sut.getNextNullableTask().run();
		sut.getNextNullableTask().run();

		verify(provider).addResult(eq("a"));
		verify(provider).addResult(eq("b"));
		verify(provider).done();
	}

	@Test
	public void doneIsCalledOnlyOnce() throws Exception {
		addTasks("a");
		initSut();
		Runnable a1 = sut.getNextNullableTask();
		Runnable a2 = sut.getNextNullableTask();

		a1.run();
		a2.run();
		
		verify(provider).done();
	}

	@Test
	public void finishedTasksAreRemoved() throws Exception {
		addTasks("a");
		initSut();
		sut.getNextNullableTask().run();
		assertNull(sut.getNextNullableTask());
	}

	@Test
	public void unfinishedTasksAreNotDirectlyRescheduled() throws Exception {
		addTasks("a", "b");
		initSut();
		sut.getNextNullableTask();
		Runnable b = sut.getNextNullableTask();

		b.run();
		verify(provider).addResult(eq("b"));
	}

	@Test
	public void unfinishedTasksAreRescheduledIfNoMoreTasksAreAvailable() throws Exception {
		addTasks("a", "b");
		initSut();
		// 1
		sut.getNextNullableTask();
		sut.getNextNullableTask();
		// 2
		sut.getNextNullableTask();
		sut.getNextNullableTask();
		Runnable a = sut.getNextNullableTask();
		Runnable b = sut.getNextNullableTask();

		a.run();
		verify(provider).addResult(eq("a"));
		b.run();
		verify(provider).addResult(eq("b"));
	}

	@Test
	public void failingTasksAreNotRescheduledAndProverIsInformed() throws Exception {
		RuntimeException e = new RuntimeException();
		tasks.put("failing", failingCallable("xyz", e));
		initSut();
		Runnable task = sut.getNextNullableTask();
		task.run();
		assertNull(sut.getNextNullableTask());
		verify(provider).createWorkers();
		verify(provider).addCrash(contains("xyz"), eq(e));
		verify(provider).done();
	}

	private Callable<String> failingCallable(String toString, RuntimeException e) throws Exception {
		@SuppressWarnings("unchecked")
		Callable<String> callable = mock(Callable.class, toString);
		when(callable.call()).thenThrow(e);
		return callable;
	}

	@Test
	public void resulsAreOnlyPassedOnceToProvider() throws Exception {
		addTasks("a", "b");
		initSut();

		Runnable a1 = sut.getNextNullableTask();
		sut.getNextNullableTask();
		Runnable a2 = sut.getNextNullableTask();

		a1.run();
		a2.run();
		verify(provider).addResult(eq("a"));
	}

	@Test
	public void ensureByReflectionThatAllDeclaredPublicMethodsAreSynchronized() {
		for (Method m : TaskScheduler.class.getDeclaredMethods()) {
			boolean isVisible = !Modifier.isPrivate(m.getModifiers()); // public|protected
			boolean isMember = !Modifier.isStatic(m.getModifiers()); // public|protected
			if (isVisible && isMember) {
				boolean isSynchronized = Modifier.isSynchronized(m.getModifiers());
				String problemStatement = String.format("modifier 'synchronized' missing for %s", m);
				assertTrue(problemStatement, isSynchronized);
			}
		}
	}

	private void addTasks(String... contents) {
		for (final String content : contents) {
			Callable<String> callable = new Callable<String>() {
				@Override
				public String call() throws Exception {
					return content;
				}
			};
			tasks.put(content, callable);
		}
	}
}