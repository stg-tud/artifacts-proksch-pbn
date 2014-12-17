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
package com.codetrails.data;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Test;

import cc.recommenders.names.VmMethodName;
import cc.recommenders.names.VmTypeName;

public class ObjectUsageTest {

	@Test
	public void equalObjectUsagesEqual() {
		ObjectUsage a = createUsage("LType1", "LContext.m1()V");
		ObjectUsage b = createUsage("LType1", "LContext.m1()V");
		assertEquals(a, b);
	}

	@Test
	public void equalObjectUsagesHaveTheSameHashCode() {
		ObjectUsage a = createUsage("LType1", "LContext.m1()V");
		ObjectUsage b = createUsage("LType1", "LContext.m1()V");
		assertTrue(a.hashCode() == b.hashCode());
	}

	@Test
	public void differentObjectUsagesDoNotEqual() {
		ObjectUsage a = createUsage("LType1", "LContext.m1()V");
		ObjectUsage b = createUsage("LType1", "LContext.m2()V");
		assertNotEquals(a, b);
	}

	@Test
	public void differentObjectUsagesHaveNotTheSameHashCode() {
		ObjectUsage a = createUsage("LType1", "LContext.m1()V");
		ObjectUsage b = createUsage("LType1", "LContext.m2()V");
		assertFalse(a.hashCode() == b.hashCode());
	}
	
    private ObjectUsage createUsage(String typeName, String methodName) {
        ObjectUsage usage = new ObjectUsage();

        DefinitionSite def = DefinitionSites.createUnknownDefinitionSite();
        def.setType(VmTypeName.get(typeName));
        usage.setDef(def);
        
        EnclosingMethodContext ctx = new EnclosingMethodContext();
        ctx.setSuperclass(VmTypeName.get("LSuperType"));
        ctx.setName(VmMethodName.get(methodName));
        ctx.setIntroducedBy(VmTypeName.get("LSuperType"));
        usage.setContext(ctx);
        
        Set<List<CallSite>> paths = newHashSet();
        usage.setPaths(paths);
        return usage;
    }

}