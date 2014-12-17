/**
 * Copyright (c) 2011-2014 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.evaluation.distribution;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import cc.recommenders.io.Logger;

public class RmiUtils {

	private static final int PORT = 1099;
	private static boolean isRegistryCreated = false;

	public static void setRmiDefaults() {
		System.setProperty("java.net.preferIPv4Stack", "true");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void createRegistryIfNotRunning(String host) throws RemoteException {
		if (!isRegistryCreated) {
			Logger.log("Starting RMI Registry on host '%s'...", host);
			System.setProperty("java.rmi.server.hostname", host);

			LocateRegistry.createRegistry(PORT);
			isRegistryCreated = true;
		}
	}

	public static String createUrl(String host, Class<?> serviceClass) {
		return "rmi://" + host + ":" + PORT + "/" + serviceClass.getName();
	}

	public static String createUrlWithoutSchemeComponent(String host, Class<?> serviceClass) {
		return "//" + host + ":" + PORT + "/" + serviceClass.getName();
	}

	public static void publish(Remote r, String serverIp) {
		publish(r, serverIp, r.getClass());
	}

	public static void publish(Remote r, String serverIp, Class<?> asClass) {
		try {
			RmiUtils.createRegistryIfNotRunning(serverIp);
			String url = RmiUtils.createUrlWithoutSchemeComponent(serverIp, asClass);
			Logger.log("# Binding RMI URL '%s'...", url);
			Naming.rebind(url, r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static <T extends Remote> T request(Class<T> clazz, String serverIp) {
		try {
			String url = RmiUtils.createUrl(serverIp, clazz);
			Logger.log("# Looking up RMI URL '%s'...", url);
			return (T) Naming.lookup(url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}