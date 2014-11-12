/*******************************************************************************
 * Copyright (c) 2004, 2005 Elias Volanakis and others.
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Elias Volanakis - initial API and implementation
 * Mikhail Afanasov - refactoring and modifications
?*******************************************************************************/
package org.eclipse.conesc.plugin;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * The plugin class (singleton).
 * <p>
 * This instance can be shared between all extensions in the plugin. Information
 * shared between extensions can be persisted by using the PreferenceStore.
 * </p>
 * 
 * @see org.eclipse.ui.plugin.AbstractUIPlugin#getPreferenceStore()
 * @author Elias Volanakis
 */
public class ConesCPlugin extends AbstractUIPlugin {

	/** Single plugin instance. */
	private static ConesCPlugin singleton;

	/**
	 * Returns the shared plugin instance.
	 */
	public static ConesCPlugin getDefault() {
		return singleton;
	}
	
	public static IWorkspace getWorkspace() {
		return singleton.getWorkspace();
	}

	/**
	 * The constructor.
	 */
	public ConesCPlugin() {
		if (singleton == null) {
			singleton = this;
		}
	}

}