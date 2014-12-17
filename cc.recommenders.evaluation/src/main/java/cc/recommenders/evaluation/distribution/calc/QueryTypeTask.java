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
package cc.recommenders.evaluation.distribution.calc;

import java.util.Map;

import cc.recommenders.evaluation.data.NM;

import com.google.common.collect.Maps;

public class QueryTypeTask extends AbstractTask {

	private static final long serialVersionUID = -4266925055843333384L;

	public Map<NM, double[]> results = Maps.newLinkedHashMap();
}