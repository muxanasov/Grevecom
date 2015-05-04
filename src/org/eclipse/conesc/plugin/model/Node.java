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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertySource;

public class Node implements IAdaptable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8095243876329921302L;
	public String name;
	private Rectangle layout;
	private List<Node> children;
	private Node parent;
	private PropertyChangeSupport listeners;
	private IPropertySource propertySource = null;
	
	public static final String PROPERTY_ADD = "NodeAddChild";
	public static final String PROPERTY_REMOVE = "NodeRemoveChild";
	public static final String PROPERTY_LAYOUT = "NodeLayout";
	
	public Node(String name){
		this.name = name;
		this.layout = new Rectangle(10,10,10,10);
		this.children = new ArrayList<Node>();
		this.parent = null;
		this.listeners = new PropertyChangeSupport(this);
	}
	
	public Node() {
		this.name = "Unknown";
		this.layout = new Rectangle(10,10,10,10);
		this.children = new ArrayList<Node>();
		this.parent = null;
		this.listeners = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		listeners.addPropertyChangeListener(l);
	}
	
	public PropertyChangeSupport getListeners() {
		return listeners;
	}
	
	public void removePropertyChangeListener(PropertyChangeListener l) {
		listeners.removePropertyChangeListener(l);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setLayout(Rectangle layout) {
		Rectangle old = this.layout;
		this.layout = layout;
		getListeners().firePropertyChange(PROPERTY_LAYOUT, old, layout);
	}
	
	public Rectangle getLayout() {
		return layout;
	}
	
	public boolean addChild(Node child) {
		if (children.contains(child)) return false;
		if (children.add(child)) {
			child.setParent(this);
			getListeners().firePropertyChange(PROPERTY_ADD, null, child);
			return true;
		}
		return false;
	}
	
	public boolean[] addChildren(Node[] children){
		boolean[] result = new boolean[children.length];
		for(int i = 0; i<children.length;i++)
			result[i]=addChild(children[i]);
		return result;
	}
	
	public boolean removeChild(Node child) {
		if (children.remove(child)) {
			getListeners().firePropertyChange(PROPERTY_REMOVE, child, null);
			return true;
		}
		return false;
	}
	
	public List<Node> getChildrenArray() {
		return children;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			if (propertySource == null)
				propertySource = new NodePropertySource(this);
			return propertySource;
		}
		return null;
	}
}
