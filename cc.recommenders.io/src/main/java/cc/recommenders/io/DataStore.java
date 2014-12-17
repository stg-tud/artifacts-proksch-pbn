/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Proksch - initial API and implementation
 */
package cc.recommenders.io;

import java.util.List;
import java.util.Set;

import com.google.common.base.Predicate;

public interface DataStore<Key, Data> {

	public void store(List<Data> data);

	public void close();

	public Set<Key> getKeys();

	public List<Data> read(Key key);

	public List<Data> read(Key key, Predicate<Data> predicate);

	public void clear();
}