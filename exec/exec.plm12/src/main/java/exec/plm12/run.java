/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Sebastian Proksch - initial API and implementation
 */
package exec.plm12;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import cc.recommenders.evaluation.distribution.Config;
import cc.recommenders.io.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

//in case of startup problems, set rmi options manually:
//-Djava.rmi.server.hostname=<hostname> -Djava.net.preferIPv4Stack=true
public class run {

	private static final String PROPERTY_NAME = "evaluationFolder";
	private static final String PROPERTY_FILE = "evaluation.properties";

	public static void main(String[] args) throws Exception {
		initLogger();
		printAvailableMemory();

		String rootFolder = readPropertyFromFile(PROPERTY_NAME);
		String version = readVersion();

		if ("worker".equals(args[0])) {
			ensureArgs(args, 2, "incorrect arguments! usage: java -jar ... worker «server-ip»");
			new DistributedWorker().run(args[1], rootFolder, version);
		} else if ("local".equals(args[0])) {
			ensureArgs(args, 4, "incorrect arguments! usage: java -jar ... local «selector» «dataset» «num-iterations»");
			Injector injector = Guice.createInjector(new Module(rootFolder, args[2]));
			Logger.log("dataset: %s", args[2]);
			Logger.log("");
			new LocalRunner().run(args[1], Integer.parseInt(args[3]), injector);
		} else {
			ensureArgs(args, 3, "incorrect arguments! usage: java -jar ... «selector» «server-ip» «dataset»");
			Logger.log("arguments: %s, %s, %s", args[0], args[1], args[2]);
			Config config = new Config(args[2], version);

			Injector injector = Guice.createInjector(new Module(rootFolder, args[2]));
			new DistributedServer().run(args[0], args[1], config, injector);
		}
	}

	private static void ensureArgs(String[] args, int numArgs, String errorMsg) {
		if (args.length != numArgs) {
			Logger.err(errorMsg);
			System.exit(1);
		}
	}

	private static void initLogger() {
		Logger.setPrinting(true);
		Logger.setDebugging(false);
		Logger.setCapturing(false);
	}

	private static String readVersion() {
		try {
			Properties properties = new Properties();
			properties.load(run.class.getResourceAsStream("/version.properties"));
			String version = properties.getProperty("version");
			if (version == null) {
				throw new RuntimeException("property 'version' not found in properties file");
			}
			if ("${maven.build.timestamp}".equals(version)) {
				version = "DO-NOT-CHECK-VERSION";
			}
			Logger.log("version: %s", version);

			return version;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static String readPropertyFromFile(String propertyName) {
		try {
			Properties properties = new Properties();
			properties.load(new FileReader(PROPERTY_FILE));
			String property = properties.getProperty(propertyName);
			if (property == null) {
				throw new RuntimeException("property '" + propertyName + "' not found in properties file");
			}
			Logger.log("%s: %s", propertyName, property);

			return property;
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void printAvailableMemory() {
		long maxMem = Runtime.getRuntime().maxMemory();
		float maxMemInMb = Math.round(maxMem * 1.0d / (1024 * 1024 * 1.0f));
		Logger.log("maximum memory (-Xmx): %.0f MB", maxMemInMb);
	}
}