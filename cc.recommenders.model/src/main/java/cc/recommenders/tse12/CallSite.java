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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import cc.recommenders.names.IMethodName;

/**
 * a site is a location in source code
 */
public class CallSite {
    public static enum SiteType {
        RECEIVER_CALL_SITE, PARAM_CALL_SITE,
    }

    public SiteType type;
    public int lineNumber;
    public IMethodName sourceMethod;
    public IMethodName targetMethod;
    public int argumentIndex;
    public boolean staticSite;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + argumentIndex;
        result = prime * result + lineNumber;
        result = prime * result + ((sourceMethod == null) ? 0 : sourceMethod.hashCode());
        result = prime * result + (staticSite ? 1231 : 1237);
        result = prime * result + ((targetMethod == null) ? 0 : targetMethod.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CallSite other = (CallSite) obj;
        if (argumentIndex != other.argumentIndex) {
            return false;
        }
        if (lineNumber != other.lineNumber) {
            return false;
        }
        if (sourceMethod == null) {
            if (other.sourceMethod != null) {
                return false;
            }
        } else if (!sourceMethod.equals(other.sourceMethod)) {
            return false;
        }
        if (staticSite != other.staticSite) {
            return false;
        }
        if (targetMethod == null) {
            if (other.targetMethod != null) {
                return false;
            }
        } else if (!targetMethod.equals(other.targetMethod)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

}
