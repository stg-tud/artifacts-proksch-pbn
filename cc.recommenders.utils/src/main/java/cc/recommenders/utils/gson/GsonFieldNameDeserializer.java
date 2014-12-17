/**
 * Copyright (c) 2010 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marcel Bruch - initial API and implementation.
 */
package cc.recommenders.utils.gson;

import java.lang.reflect.Type;

import cc.recommenders.names.IFieldName;
import cc.recommenders.names.VmFieldName;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonFieldNameDeserializer implements JsonDeserializer<IFieldName> {
    @Override
    public IFieldName deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
            throws JsonParseException {
        final String identifier = json.getAsString();
        return VmFieldName.get(identifier);
    }
}
