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
package integration.simpleusages;

import static cc.recommenders.mining.calls.QueryOptions.newQueryOptions;
import integration.AbstractIntegrationTest;

import java.util.List;

import org.junit.Ignore;

import cc.recommenders.evaluation.data.Boxplot;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Lists;

@Ignore
public class InSameContext extends AbstractIntegrationTest {

	@Override
	public void init() {
		getQueryOptions().setFrom(newQueryOptions("+METHOD+MIN10"));
	}

	@Override
	public List<Usage> getTrainingData() {
		List<Usage> usages = Lists.newLinkedList();

		for (int i = 0; i < 2; i++) {
			usages.add(createUsageWithB());
		}

		for (int i = 0; i < 1; i++) {
			usages.add(createUsageWithC());
		}

		return usages;
	}

	@Override
	public List<Usage> getValidationData() {
		List<Usage> usages = Lists.newLinkedList();

		usages.add(createUsageWithB());

		return usages;
	}

	@Override
	public Boxplot getExpectation() {
		return new Boxplot(1, 0.667, 0.667, 0.667, 0.667, 0.667, 0.667);
	}

	private static Usage createUsageWithB() {
		Query usage = new Query();

		usage.setType(newType("Lmy/Type"));
		usage.setClassContext(newType("Lmy/Container"));
		usage.setMethodContext(newMethod("Lmy/Container.doit()V"));

		DefinitionSite ds = DefinitionSites.createDefinitionByConstructor(newMethod("Lmy/Type.<init>()V"));
		usage.setDefinition(ds);

		usage.addCallSite(newReceiverCallSite("Lmy/Type.a()V"));
		usage.addCallSite(newReceiverCallSite("Lmy/Type.b()V"));
		return usage;
	}

	private static Usage createUsageWithC() {
		Query usage = new Query();

		usage.setType(VmTypeName.get("Lmy/Type"));
		usage.setClassContext(VmTypeName.get("Lmy/Container"));
		usage.setMethodContext(VmMethodName.get("Lmy/Container.doit()V"));

		DefinitionSite ds = DefinitionSites.createDefinitionByConstructor(VmMethodName.get("Lmy/Type.<init>()V"));
		usage.setDefinition(ds);

		usage.addCallSite(newReceiverCallSite("Lmy/Type.a()V"));
		usage.addCallSite(newReceiverCallSite("Lmy/Type.c()V"));
		return usage;
	}
}