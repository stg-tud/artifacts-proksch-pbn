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

public class OptimizationOptions {

	public final int maxIterations;
	public final double convergenceThreshold;

	public final double stepSizeClassContext;
	public final double stepSizeMethodContext;
	public final double stepSizeDefinition;
	public final double stepSizeParameterSites;
	public final double stepSizeMinProbability;

	private OptimizationOptions(int maxIterations, double convergenceThreshold, double stepSizeClassContext,
			double stepSizeMethodContext, double stepSizeDefinition, double stepSizeParameterSites,
			double stepSizeMinProbability) {
		this.maxIterations = maxIterations;
		this.convergenceThreshold = convergenceThreshold;
		this.stepSizeClassContext = stepSizeClassContext;
		this.stepSizeMethodContext = stepSizeMethodContext;
		this.stepSizeDefinition = stepSizeDefinition;
		this.stepSizeParameterSites = stepSizeParameterSites;
		this.stepSizeMinProbability = stepSizeMinProbability;
	}

	public static OptimizationOptionsBuilder newBuilder() {
		return new OptimizationOptionsBuilder();
	}

	public static class OptimizationOptionsBuilder {

		private int maxIterations = 1;
		private double convergenceThreshold = 0;

		private double stepSizeClassContext = 0;
		private double stepSizeMethodContext = 0;
		private double stepSizeDefinition = 0;
		private double stepSizeParameterSites = 0;
		private double stepSizeMinProbability = 0;

		public OptimizationOptionsBuilder maxIterations(int arg) {
			maxIterations = arg;
			return this;
		}

		public OptimizationOptionsBuilder convergence(double arg) {
			convergenceThreshold = arg;
			return this;
		}

		public OptimizationOptionsBuilder stepSizeClassContext(double d) {
			stepSizeClassContext = d;
			return this;
		}

		public OptimizationOptionsBuilder stepSizeMethodContext(double d) {
			stepSizeMethodContext = d;
			return this;
		}

		public OptimizationOptionsBuilder stepSizeDefinition(double d) {
			stepSizeDefinition = d;
			return this;
		}

		public OptimizationOptionsBuilder stepSizeParam(double d) {
			stepSizeParameterSites = d;
			return this;
		}

		public OptimizationOptionsBuilder stepMinProbability(double d) {
			stepSizeMinProbability = d;
			return this;
		}

		public OptimizationOptionsBuilder allSteps(double d) {
			stepSizeClassContext = d;
			stepSizeMethodContext = d;
			stepSizeDefinition = d;
			stepSizeParameterSites = d;
			stepSizeMinProbability = d;
			return this;
		}

		public OptimizationOptions build() {
			return new OptimizationOptions(maxIterations, convergenceThreshold, stepSizeClassContext,
					stepSizeMethodContext, stepSizeDefinition, stepSizeParameterSites, stepSizeMinProbability);
		}
	}
}