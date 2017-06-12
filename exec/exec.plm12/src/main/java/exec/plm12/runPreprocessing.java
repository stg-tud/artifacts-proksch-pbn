/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Proksch - initial API and implementation
 */
package exec.plm12;

import static exec.plm12.run.PROPERTY_NAME;
import static exec.plm12.run.initLogger;
import static exec.plm12.run.readPropertyFromFile;

import java.io.IOException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import cc.recommenders.evaluation.io.ProjectIndexer;
import cc.recommenders.io.Logger;

public class runPreprocessing {
	
	private static final String SELECTOR = "5700";

	public static void main(String[] args) throws IOException{
		
		initLogger();
		
		String evalDir = readPropertyFromFile(PROPERTY_NAME);
		Injector injector = Guice.createInjector(new Module(evalDir, SELECTOR));
		ProjectIndexer indexer = injector.getInstance(ProjectIndexer.class);

		
		Logger.log("preprocessing usages in folder '%s/unsorted/%s'", evalDir, SELECTOR);
		Logger.log("");

		indexer.createIndex();
		
		Logger.log("");
		Logger.log("finished preprocessing");
	}
}