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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

public class ContextGroup extends Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5860820514007636444L;
	public static final String PROPERTY_NAME = "ContextGroupName";
	private List<String> lFunctions = new ArrayList<String>();
	private String suggestions = "";
	
	public ContextGroup(String name){
		super(name);
	}
	
	public ContextGroup(){
		super();
	}

	@Override
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		getListeners().firePropertyChange(PROPERTY_NAME, oldName, this.name);
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		ContextGroup ctxgrp = new ContextGroup();
		ctxgrp.setName(this.getName()+" copy");
		ctxgrp.setParent(this.getParent());
		ctxgrp.setLayout(new Rectangle(getLayout().x + 10, getLayout().y + 10,
				getLayout().width, getLayout().height));
		return ctxgrp;
	}
	
	public void addLFunction(String lFunction) {
		if (lFunctions.contains(lFunction)) return;
		lFunctions.add(lFunction);
	}
	
	public List<String> getLFunctions() {
		return lFunctions;
	}

	public void setSuggestedFunctions(String suggestions) {
		this.suggestions  = suggestions;
	}

	public String getSuggestions() {
		return suggestions;
	}

}
