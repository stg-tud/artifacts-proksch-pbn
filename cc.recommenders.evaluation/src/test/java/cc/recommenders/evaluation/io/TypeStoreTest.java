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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class TypeStoreTest {

	private Map<String, Integer> map;
	private List<ProjectFoldedUsage> usages;
	private TypeStore sut;

	@Before
	public void setup() {
		map = Maps.newHashMap();
		usages = Lists.newLinkedList();
	}

	@Test
	public void happyPath() {
		map.put("p1", 0);
		map.put("p2", 1);
		map.put("p3", 1);
		map.put("p4", 0);

		addUsages(1, 2, 3, 4, 3, 1, 2, 4);

		sut = new TypeStore(usages, map);

		assertLists(2);
	}

	private void assertLists(int numFolds) {
		for (int foldNum = 0; foldNum < numFolds; foldNum++) {
			List<Usage> trainingData = sut.getTrainingData(foldNum);
			assertTrue(trainingData.size() > 0);
			for (Usage u : trainingData) {
				assertFalse(foldNum == getAssignedValidationFold(u));
			}
			List<Usage> validationData = sut.getValidationData(foldNum);
			assertTrue(validationData.size() > 0);
			for (Usage u : validationData) {
				assertTrue(foldNum == getAssignedValidationFold(u));
			}
		}
	}

	private int getAssignedValidationFold(Usage u) {
		String projectName = u.getMethodContext().getName();
		int num = map.get(projectName);
		return num;
	}

	private void addUsages(int... projectNums) {
		for (int projectNum : projectNums) {
			String projectName = "p" + projectNum;
			Usage u = mock(Usage.class, projectName);
			when(u.getType()).thenReturn(VmTypeName.get("LT"));
			when(u.getMethodContext()).thenReturn(m(projectName));
			usages.add(new ProjectFoldedUsage(u, projectName));
		}
	}

	private IMethodName m(String projectName) {
		return VmMethodName.get("LT." + projectName + "()V");
	}
}