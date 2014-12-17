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
package cc.recommenders.evaluation.optimization;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OptimizationOptionsTest {

	private static final double DOUBLE_DELTA = 0.00001;
	private OptimizationOptions actual;

	@Test
	public void canBeBuildWithDefaults() {
		actual = OptimizationOptions.newBuilder().build();

		assertEquals(1, actual.maxIterations);
		assertEquals(0, actual.convergenceThreshold, DOUBLE_DELTA);
		assertEquals(0, actual.stepSizeClassContext, DOUBLE_DELTA);
		assertEquals(0, actual.stepSizeDefinition, DOUBLE_DELTA);
		assertEquals(0, actual.stepSizeMethodContext, DOUBLE_DELTA);
		assertEquals(0, actual.stepSizeMinProbability, DOUBLE_DELTA);
		assertEquals(0, actual.stepSizeParameterSites, DOUBLE_DELTA);
	}

	@Test
	public void iterationsCanBeChanged() {
		actual = OptimizationOptions.newBuilder().maxIterations(100).build();
		assertEquals(100, actual.maxIterations);
	}

	@Test
	public void convergenceCanBeChanged() {
		actual = OptimizationOptions.newBuilder().convergence(0.001).build();
		assertEquals(0.001, actual.convergenceThreshold, DOUBLE_DELTA);
	}

	@Test
	public void ssClass() {
		actual = OptimizationOptions.newBuilder().stepSizeClassContext(0.2).build();
		assertEquals(0.2, actual.stepSizeClassContext, DOUBLE_DELTA);
	}

	@Test
	public void ssDefinition() {
		actual = OptimizationOptions.newBuilder().stepSizeDefinition(0.2).build();
		assertEquals(0.2, actual.stepSizeDefinition, DOUBLE_DELTA);
	}

	@Test
	public void ssMethod() {
		actual = OptimizationOptions.newBuilder().stepSizeMethodContext(0.2).build();
		assertEquals(0.2, actual.stepSizeMethodContext, DOUBLE_DELTA);
	}

	@Test
	public void ssMinProb() {
		actual = OptimizationOptions.newBuilder().stepMinProbability(0.2).build();
		assertEquals(0.2, actual.stepSizeMinProbability, DOUBLE_DELTA);
	}

	@Test
	public void ssParam() {
		actual = OptimizationOptions.newBuilder().stepSizeParam(0.2).build();
		assertEquals(0.2, actual.stepSizeParameterSites, DOUBLE_DELTA);
	}

	@Test
	public void ssAll() {
		actual = OptimizationOptions.newBuilder().allSteps(0.2).build();
		assertEquals(1, actual.maxIterations);
		assertEquals(0.0, actual.convergenceThreshold, DOUBLE_DELTA);
		assertEquals(0.2, actual.stepSizeClassContext, DOUBLE_DELTA);
		assertEquals(0.2, actual.stepSizeDefinition, DOUBLE_DELTA);
		assertEquals(0.2, actual.stepSizeMethodContext, DOUBLE_DELTA);
		assertEquals(0.2, actual.stepSizeMinProbability, DOUBLE_DELTA);
		assertEquals(0.2, actual.stepSizeParameterSites, DOUBLE_DELTA);
	}

	@Test
	public void ssAllBeatsSpecific() {
		actual = OptimizationOptions.newBuilder().stepSizeClassContext(0.2).allSteps(0.3).stepSizeDefinition(0.4)
				.build();
		assertEquals(0.3, actual.stepSizeClassContext, DOUBLE_DELTA);
		assertEquals(0.4, actual.stepSizeDefinition, DOUBLE_DELTA);
	}

	@Test
	public void bigExample() {
		actual = OptimizationOptions.newBuilder().maxIterations(123).convergence(0.01).stepSizeClassContext(0.2)
				.stepSizeDefinition(0.3).stepSizeMethodContext(0.4).stepMinProbability(0.5).stepSizeParam(0.6).build();

		assertEquals(123, actual.maxIterations);
		assertEquals(0.01, actual.convergenceThreshold, DOUBLE_DELTA);
		assertEquals(0.2, actual.stepSizeClassContext, DOUBLE_DELTA);
		assertEquals(0.3, actual.stepSizeDefinition, DOUBLE_DELTA);
		assertEquals(0.4, actual.stepSizeMethodContext, DOUBLE_DELTA);
		assertEquals(0.5, actual.stepSizeMinProbability, DOUBLE_DELTA);
		assertEquals(0.6, actual.stepSizeParameterSites, DOUBLE_DELTA);
	}
}