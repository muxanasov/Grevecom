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

package org.eclipse.conesc.plugin.model;

public class ContextDiagram extends Node {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6119062241951516884L;
	public static final String PROPERTY_NAME = "ContextDiagramName";
	
	public void setName(String n) {
		String oldName = this.name;
		this.name = n;
		getListeners().firePropertyChange(PROPERTY_NAME, oldName, this.name);
	}
	
	public String getName() {
		return name;
	}

}