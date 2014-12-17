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
package cc.recommenders.evaluation.distribution;

import java.io.Serializable;
import java.rmi.Remote;

public class Config implements Remote, Serializable {

	private static final long serialVersionUID = -4569735818270348080L;

	private String datasetName;

	private String version;

	public Config(String datasetName, String version) {
		this.datasetName = datasetName;
		this.version = version;
	}

	public Config(String version) {
		this.version = version;
	}

	public String getDatasetName() {
		return datasetName;
	}

	public String getVersion() {
		return version;
	}
}