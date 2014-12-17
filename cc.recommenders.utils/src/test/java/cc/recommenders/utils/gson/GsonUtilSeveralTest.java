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
package cc.recommenders.utils.gson;

import static cc.recommenders.usages.CallSites.createReceiverCallSite;
import static cc.recommenders.usages.DefinitionSites.createDefinitionByConstant;
import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Set;

import org.junit.Test;

import cc.recommenders.names.ITypeName;
import cc.recommenders.names.VmFieldName;
import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;
import cc.recommenders.usages.CallSite;
import cc.recommenders.usages.CallSites;
import cc.recommenders.usages.DefinitionSite;
import cc.recommenders.usages.DefinitionSites;
import cc.recommenders.usages.ProjectFoldedUsage;
import cc.recommenders.usages.ProjectFoldingIndex;
import cc.recommenders.usages.Query;
import cc.recommenders.usages.Usage;

import com.google.common.collect.Sets;

public class GsonUtilSeveralTest {
	@Test
	public void serializationTest() {
		ITypeName T1 = VmTypeName.get("LT1");
		ITypeName T2 = VmTypeName.get("LT2");

		ProjectFoldingIndex in = new ProjectFoldingIndex();
		in.setCount(T1, "P1", 1);
		in.setCount(T1, "P2", 22);
		in.setCount(T2, "P1", 333);
		in.setCount(T2, "P2", 4444);

		String json = GsonUtil.serialize(in);
		ProjectFoldingIndex out = GsonUtil.deserialize(json, ProjectFoldingIndex.class);
		assertEquals(in, out);
	}

	@Test
	public void usage() {
		Usage u = createUsage();
		String json = GsonUtil.serialize(u);
		Usage u2 = GsonUtil.deserialize(json, Usage.class);
		assertEquals(u, u2);
	}

	@Test
	public void usageDoesNotContainCallSiteArgIndexIfDefault() {
		int defaultIndex = CallSites.createReceiverCallSite("LT.m()V").getArgIndex();

		Usage u = createUsage();
		u.getDefinitionSite().setArgIndex(123);
		Set<CallSite> sites = u.getAllCallsites();
		sites.clear();
		sites.add(CallSites.createParameterCallSite("LT.m()V", defaultIndex));
		String json = GsonUtil.serialize(u);

		assertFalse(json.contains("\"argIndex\":" + defaultIndex));
	}

	@Test
	public void usageDoesNotContainDefinitionArgIndexIfDefault() {
		int defaultIndex = DefinitionSites.createDefinitionByConstant().getArgIndex();

		Usage u = createUsage();
		u.getDefinitionSite().setArgIndex(defaultIndex);
		Set<CallSite> sites = u.getAllCallsites();
		sites.clear();
		sites.add(CallSites.createParameterCallSite("LT.m()V", 3456));
		String json = GsonUtil.serialize(u);

		assertFalse(json.contains("\"argIndex\":" + defaultIndex));
	}

	@Test
	public void pfUsage() {
		Usage u = createUsage();
		ProjectFoldedUsage pfu = new ProjectFoldedUsage(u, "abc");
		String json = GsonUtil.serialize(pfu);
		ProjectFoldedUsage pfu2 = GsonUtil.deserialize(json, ProjectFoldedUsage.class);
		assertEquals(pfu, pfu2);
	}

	@Test
	public void complexExmapleToMakeSureFieldNamesAreStillConsistent() {
		Usage u = createUsage();
		ProjectFoldedUsage pfu = new ProjectFoldedUsage(u, "abc");
		String json1 = GsonUtil.serialize(u);
		String json2 = GsonUtil.serialize(pfu);

		Usage u2 = GsonUtil.deserialize(json1, Usage.class);
		ProjectFoldedUsage pfu2 = GsonUtil.deserialize(json2, ProjectFoldedUsage.class);

		assertEquals(u2, pfu2.getRawUsage());
	}

	@Test
	public void pfUsageWithEmptyFields() {
		DefinitionSite def = createDefinitionByConstant();
		def.setKind(null);
		CallSite cs = createReceiverCallSite("Lt.m()V");
		cs.setMethod(null);

		Query q = new Query();
		q.setDefinition(def);
		q.setAllCallsites(newHashSet(cs));

		String json = GsonUtil.serialize(q);
		Usage u2 = GsonUtil.deserialize(json, Usage.class);
		assertEquals(q, u2);

		q.setAllCallsites(null);
		q.setDefinition(null);
		String json2 = GsonUtil.serialize(q);
		Usage u3 = GsonUtil.deserialize(json2, Usage.class);
		assertEquals(q, u3);
	}

	private Query createUsage() {
		DefinitionSite ds = DefinitionSites.createUnknownDefinitionSite();
		ds.setArgIndex(14);
		ds.setField(VmFieldName.get("LDS1.field;LDS2"));
		ds.setMethod(VmMethodName.get("LDS3.m()V"));

		Set<CallSite> calls = Sets.newHashSet();
		calls.add(CallSites.createParameterCallSite("LDS5.m()V", 3));

		Query q = new Query();
		q.setAllCallsites(calls);
		q.setClassContext(VmTypeName.get("LS"));
		q.setDefinition(ds);
		q.setMethodContext(VmMethodName.get("LT.m()V"));
		q.setType(VmTypeName.get("Lpackage/Type"));
		return q;
	}
}