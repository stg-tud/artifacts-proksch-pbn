/**
 * Copyright (c) 2011-2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Dennis Albrecht
 */
package cc.recommenders.utils.gson;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.Query;

import com.google.common.collect.Sets;

public class GsonUtilQueryTest {

	@Test
	public void checkDeSerializationOfQuery() {
		Query expected = new Query();
		expected.setType(VmTypeName.get("Lusages/Query"));
		expected.setDefinition(getDefinition());
		expected.setClassContext(VmTypeName.get("LContext"));
		expected.setMethodContext(VmMethodName.get("LReceiver.equals(LArgument;)LResult;"));
		expected.setAllCallsites(getCallSites());

		String json = GsonUtil.serialize(expected);
		Query actual = GsonUtil.deserialize(json, Query.class);
		assertEquals(expected, actual);
	}

	private Set<CallSite> getCallSites() {
		Set<CallSite> callSites = Sets.newHashSet();
		callSites.add(getParamCallSite());
		callSites.add(getReceiverCallSite());
		return callSites;
	}

	private CallSite getParamCallSite() {
		return CallSites.createParameterCallSite("LCallSite.param(LParam;)LReturn;", 23);
	}

	private CallSite getReceiverCallSite() {
		return CallSites.createReceiverCallSite("LCallSite.param(LParam;)LReturn;");
	}

	private DefinitionSite getDefinition() {
		DefinitionSite site = DefinitionSites.createDefinitionByThis();
		site.setMethod(VmMethodName.get("LDefiner.define(LScheme;)LPattern;"));
		site.setField(VmFieldName.get("LField.field;LType"));
		site.setArgIndex(42);
		return site;
	}
}