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

public class StringTemplate {
	
	public static String build(String template, HashMap<String, Object> values) {
		String result = template;
		for (String key : values.keySet()){
			result = result.replaceAll("%"+key+"%", values.get(key).toString());
		}
		return result;
	}

}
