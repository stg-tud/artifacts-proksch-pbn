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
package integration.options;

import integration.AbstractIntegrationTest;

import java.util.List;

import org.junit.Ignore;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.usages.Usage;

@Ignore
public class MiningClassContext extends AbstractIntegrationTest {

	@Override
	public void init() {
	}

	@Override
	public List<Usage> getTrainingData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Usage> getValidationData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boxplot getExpectation() {
		// TODO Auto-generated method stub
		return null;
	}
}