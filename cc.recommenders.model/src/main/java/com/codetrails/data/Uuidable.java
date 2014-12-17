/**
 * Copyright (c) 2010, 2013 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andreas Sewe - initial API and implementation.
 */
package com.codetrails.data;

import java.util.UUID;

public interface Uuidable {

    /**
     * @return a UUID stable for the entire lifetime of this object
     */
    UUID getUuid();
}
