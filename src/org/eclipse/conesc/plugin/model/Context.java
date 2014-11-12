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

public class Context extends Node {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3202227501792795978L;
	private String onEnter;
	private String onLeave;
	private String onActive;
	private boolean dflt = false;
	private boolean error = false;

	private List sourceConnections;
	private List targetConnections;
	private String triggers = "";
	
	public static final String PROPERTY_EDIT = "ContextEdit";
	public static final String PROPERTY_NAME = "ContextName";
	public static final String PROPERTY_ONENTER = "ContextOnEnter";
	public static final String PROPERTY_ONACTIVE = "ContextOnActive";
	public static final String PROPERTY_ONLEAVE = "ContextOnLeave";
	public static final String PROPERTY_DEFAULT = "ContextDefault";
	public static final String PROPERTY_ERROR = "ContextError";
	public static final String SOURCE_CONNECTION = "SourceConnectionAdded";
	public static final String TARGET_CONNECTION = "TargetConnectionAdded";
	public static final String PROPERTY_TRIGGERS = "ContextTriggers";
	
	public Context() {
		super();
		this.targetConnections = new ArrayList();
		this.sourceConnections = new ArrayList();
	}
	
	public boolean addConnection (Connection con) {
		if (con.getSource() == this) {
			if (!sourceConnections.contains(con)) {
				if (sourceConnections.add(con)){
					getListeners().firePropertyChange(SOURCE_CONNECTION, null, con);
					return true;
				}
				return false;
			}
		} else if (con.getTarget() == this) {
			if (!targetConnections.contains(con)) {
				if (targetConnections.add(con)){
					getListeners().firePropertyChange(TARGET_CONNECTION, null, con);
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	public boolean removeConnection (Connection con) {
		if (con.getSource() == this) {
			if (sourceConnections.contains(con)) {
				if (sourceConnections.remove(con)){
					getListeners().firePropertyChange(SOURCE_CONNECTION, null, con);
					return true;
				}
				return false;
			}
		} else if (con.getTarget() == this) {
			if (targetConnections.contains(con)) {
				if (targetConnections.remove(con)){
					getListeners().firePropertyChange(TARGET_CONNECTION, null, con);
					return true;
				}
				return false;
			}
		}
		return false;
	}
	
	public List getSourceConnections() {
		return this.sourceConnections;
	}
	
	public List getTargetConnections() {
		return this.targetConnections;
	}
	
	public void setDefault(boolean dflt) {
		boolean oldValue = this.dflt;
		this.dflt = dflt;
		getListeners().firePropertyChange(PROPERTY_DEFAULT, oldValue, this.dflt);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldValue, this.dflt);
		if (this.dflt && this.error){
			setError(false);
		}
		if (!dflt) return;
		for (Node child : getParent().getChildrenArray()) {
			if (child == this) continue;
			Context ctx = (Context)child;
			if (ctx.isDefault()) ctx.setDefault(false);
		}
	}
	
	public void setError(boolean error) {
		boolean oldValue = this.error;
		this.error = error;
		getListeners().firePropertyChange(PROPERTY_ERROR, oldValue, this.error);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldValue, this.error);
		if (this.error && this.dflt) {
			setDefault(false);
		}
		if (!error) return;
		for (Node child : getParent().getChildrenArray()) {
			if (child == this) continue;
			Context ctx = (Context)child;
			if (ctx.isError()) ctx.setError(false);
		}
	}

	@Override
	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		getListeners().firePropertyChange(PROPERTY_NAME, oldName, this.name);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldName, this.name);
	}

	public void setOnEnter(String onEnter) {
		String oldOnEnter = this.onEnter;
		this.onEnter = onEnter.replaceAll(";", "\n");
		getListeners().firePropertyChange(PROPERTY_ONENTER, oldOnEnter, this.onEnter);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldOnEnter, this.onEnter);
	}

	public void setOnLeave(String onLeave) {
		String oldOnLeave = this.onLeave;
		this.onLeave = onLeave.replaceAll(";", "\n");
		getListeners().firePropertyChange(PROPERTY_ONLEAVE, oldOnLeave, this.onLeave);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldOnLeave, this.onLeave);
	}

	public void setOnActive(String onActive) {
		String oldOnActive = this.onActive;
		this.onActive = onActive.replaceAll(";", "\n");
		getListeners().firePropertyChange(PROPERTY_ONACTIVE, oldOnActive, this.onActive);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldOnActive, this.onActive);
	}
	
	public void setTriggers(String t) {
		String oldTriggers = this.triggers;
		this.triggers = t.replaceAll(";", "\n");
		getListeners().firePropertyChange(PROPERTY_TRIGGERS, oldTriggers, this.triggers);
		getListeners().firePropertyChange(PROPERTY_EDIT, oldTriggers, this.triggers);
	}

	@Override
	public String getName() {
		return name;
	}
	
	public boolean isDefault() {
		return this.dflt;
	}
	
	public boolean isError() {
		return this.error;
	}

	public String getOnEnter() {
		return onEnter;
	}

	public String getOnLeave() {
		return onLeave;
	}

	public String getOnActive() {
		return onActive;
	}
	public String getTriggers() {
		return this.triggers;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		Context ctx = new Context();
		ctx.setName(this.getName()+" copy");
		ctx.setParent(this.getParent());
		ctx.setOnEnter(this.onEnter);
		ctx.setOnActive(this.onActive);
		ctx.setOnLeave(this.onLeave);
		ctx.setDefault(this.isDefault());
		ctx.setError(this.isError());
		ctx.setTriggers(this.triggers);
		ctx.setLayout(new Rectangle(getLayout().x + 10, getLayout().y + 10,
				getLayout().width, getLayout().height));
		return ctx;
	}

}
