/*******************************************************************************
 * Copyright (c) 2004, 2010 Elias Volanakis and others.
 * Copyright (c) 2014 Mikhail Afanasov and DeepSe Group.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Elias Volanakis - initial API and implementation
 *    Mikhail Afanasov - refactoring and modifications
 *******************************************************************************/
package org.eclipse.conesc.plugin.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;
import org.eclipse.conesc.plugin.utils.HelperAssistant;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;

/**
 * A connection between two distinct shapes.
 * 
 * @author Elias Volanakis
 */
public class Connection implements IAdaptable, Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4718321942554735519L;
	public static final int CONNECTION_RESOURCES = 2;
	public static final int CONNECTION_WORKSPACES = 3;
	
	private Context source;
	private Context target;
	private boolean isConnected;
	
	private String label;
	public final static String PROPERTY_LABEL = "ConnectionLabel";
	private PropertyChangeSupport listeners;
	private Object propertySource;
	private List<ConnectionBendpoint> bendpoints;
	public final static String PROPERTY_BENDPOINTS = "ConnectionBendpoints";

	public Connection(Context source, Context target) {
		label = "event";
		bendpoints = new ArrayList<ConnectionBendpoint>();
		listeners = new PropertyChangeSupport(this);
		reconnect(source, target);
	}
	
	public void disconnect() {
		if (isConnected) {
			source.removeConnection(this);
			target.removeConnection(this);
			isConnected = false;
		}
	}
	
	public Context getSource() {
		return source;
	}

	public Context getTarget() {
		return target;
	}

	public void reconnect() {
		if (!isConnected) {
			source.addConnection(this);
			target.addConnection(this);
			isConnected = true;
		}
	}
	
	public void reconnect(Context newSource, Context newTarget) {
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setLabel(String l) {
		String old = this.label;
		label = l;
		getListeners().firePropertyChange(PROPERTY_LABEL, old, label);
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
	
	public List<ConnectionBendpoint> getBendpoints() {
		return bendpoints;
	}

	public void insertBendpoint(int index, ConnectionBendpoint point) {
		getBendpoints().add(index, point);
		getListeners().firePropertyChange(PROPERTY_BENDPOINTS , null, null);
	}

	public void removeBendpoint(int index) {
		getBendpoints().remove(index);
		getListeners().firePropertyChange(PROPERTY_BENDPOINTS , null, null);
	}

	public void setBendpoint(int index, ConnectionBendpoint point) {
		getBendpoints().set(index, point);
		getListeners().firePropertyChange(PROPERTY_BENDPOINTS , null, null);
	}

	public void setBendpoints(Vector<ConnectionBendpoint> points) {
		bendpoints = points;
		getListeners().firePropertyChange(PROPERTY_BENDPOINTS , null, null);
	}

	@Override
	public Object getAdapter(Class adapter) {
		if (adapter == IPropertySource.class) {
			if (propertySource == null)
				propertySource = new ConnectionPropertySource();
			return propertySource;
		}
		return null;
	}
	
	private class ConnectionPropertySource implements IPropertySource, Serializable  {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2625658575794313297L;

		@Override
		public Object getEditableValue() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public IPropertyDescriptor[] getPropertyDescriptors() {
			ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
			properties.add(new HelpfulTextPropertyDescriptor(Connection.PROPERTY_LABEL, "Event"));
			return properties.toArray(new IPropertyDescriptor[0]);
		}
		
		private class HelpfulTextPropertyDescriptor extends TextPropertyDescriptor{
			
			private Node diagram;

			public HelpfulTextPropertyDescriptor(Object id, String displayName) {
				super(id, displayName);
				// TODO Auto-generated constructor stub
				diagram = source.getParent().getParent();
			}
			
			public CellEditor createPropertyEditor(Composite parent) {
		        CellEditor editor = new TextCellEditor(parent);
		        if (getValidator() != null) {
					editor.setValidator(getValidator());
				}
		        HelperAssistant.applyAssistentTo(editor.getControl(), diagram);
		        return editor;
		    }
			
		}

		@Override
		public Object getPropertyValue(Object id) {
			if (id.equals(Connection.PROPERTY_LABEL))
				return getLabel();
			return null;
		}

		@Override
		public boolean isPropertySet(Object id) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void resetPropertyValue(Object id) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setPropertyValue(Object id, Object value) {
			if (id.equals(Connection.PROPERTY_LABEL))
				setLabel((String)value);
		}
	
	}
}