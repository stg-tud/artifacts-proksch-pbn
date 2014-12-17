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
package integration;

import java.util.List;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;

public class PerfectResultsIfOnlyEqualInputIsUsed extends AbstractIntegrationTest {

	@Override
	public List<Usage> getTrainingData() {
		List<Usage> usages = Lists.newLinkedList();

		usages.add(createUsage());

		return usages;
	}

	@Override
	public List<Usage> getValidationData() {
		List<Usage> usages = Lists.newLinkedList();

		usages.add(createUsage());

		return usages;
	}

	@Override
	public Boxplot getExpectation() {
		return new Boxplot(1, 1, 1, 1, 1, 1, 1);
	}

	private Query createUsage() {
		Query usage = new Query();

		usage.setType(newType("Lmy/Type"));
		usage.setClassContext(newType("Lmy/Container"));
		usage.setMethodContext(newMethod("Lmy/Container.doitA()V"));

		DefinitionSite ds = DefinitionSites.createDefinitionByConstructor(newMethod("Lmy/Type.<init>()V"));
		usage.setDefinition(ds);

		usage.addCallSite(newReceiverCallSite("Lmy/Type.a()V"));
		usage.addCallSite(newReceiverCallSite("Lmy/Type.b()V"));
		return usage;
	}
}