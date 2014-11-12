/*******************************************************************************
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Mikhail Afanasov - initial API and implementation
?*******************************************************************************/

package org.eclipse.conesc.plugin.utils;

import java.util.HashMap;

public class Cashe {
	
	private static HashMap<String, String> _cashe = new HashMap<String, String>();
	
	public static void deposit(String key, String value) {
		_cashe.put(key, value);
	}
	
	public static String load(String key) {
		if(!_cashe.containsKey(key)) return "";
		String result = _cashe.get(key);
		return result == null ? "" : result;
	}

}
