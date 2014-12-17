/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package cc.recommenders.tse12;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;

import java.util.List;
import java.util.Set;

import cc.recommenders.names.IMethodName;
import cc.recommenders.names.ITypeName;

import com.google.common.collect.Sets;

public class ObjectUsage {

    public ITypeName type;
    public ITypeName supertypeOfDeclaringClass;
    public IMethodName methodDeclaration;
    public IMethodName superMethodDeclaration;
    public IMethodName firstMethodDeclaration;

    public DefinitionSite definition;
    public Set<List<CallSite>> paths = Sets.newHashSet();

    @Override
    public String toString() {
        return reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
        return reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return reflectionHashCode(this);
    }
}