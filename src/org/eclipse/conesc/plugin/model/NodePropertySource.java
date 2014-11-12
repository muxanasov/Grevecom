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

import java.io.Serializable;
import java.util.ArrayList;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class NodePropertySource implements IPropertySource, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 787278339574115978L;
	private Node node;
	
	public NodePropertySource(Node node) {
		this.node = node;
	}

	@Override
	public Object getEditableValue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		ArrayList<IPropertyDescriptor> properties = new ArrayList<IPropertyDescriptor>();
		if (node instanceof Context) {
			properties.add(new TextPropertyDescriptor(Context.PROPERTY_NAME, "Name"));
			properties.add(new TextPropertyDescriptor(Context.PROPERTY_ONENTER, "OnEnter"));
			properties.add(new TextPropertyDescriptor(Context.PROPERTY_ONACTIVE, "OnActive"));
			properties.add(new TextPropertyDescriptor(Context.PROPERTY_ONLEAVE, "OnLeave"));
			properties.add(new CheckboxPropertyDescriptor(Context.PROPERTY_DEFAULT, "IsDefault"));
			properties.add(new CheckboxPropertyDescriptor(Context.PROPERTY_ERROR, "IsError"));
		} else if (node instanceof ContextGroup)
			properties.add(new TextPropertyDescriptor(ContextGroup.PROPERTY_NAME, "Name"));
		else if (node instanceof ContextDiagram)
			properties.add(new TextPropertyDescriptor(ContextDiagram.PROPERTY_NAME, "Name"));
		return properties.toArray(new IPropertyDescriptor[0]);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(Context.PROPERTY_NAME))
			return ((Context)node).getName();
		if (id.equals(Context.PROPERTY_ONENTER))
			return ((Context)node).getOnEnter().replaceAll("\\n", ";");
		if (id.equals(Context.PROPERTY_ONACTIVE))
			return ((Context)node).getOnActive().replaceAll("\\n", ";");;
		if (id.equals(Context.PROPERTY_ONLEAVE))
			return ((Context)node).getOnLeave().replaceAll("\\n", ";");;
		if (id.equals(Context.PROPERTY_DEFAULT))
			return ((Context)node).isDefault();
		if (id.equals(Context.PROPERTY_ERROR))
			return ((Context)node).isError();
		if (id.equals(ContextGroup.PROPERTY_NAME))
			return ((ContextGroup)node).getName();
		if (id.equals(ContextDiagram.PROPERTY_NAME))
			return ((ContextDiagram)node).getName();
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
		if (id.equals(Context.PROPERTY_NAME))
			((Context)node).setName((String)value);
		if (id.equals(Context.PROPERTY_ONENTER))
			((Context)node).setOnEnter((String)value);
		if (id.equals(Context.PROPERTY_ONACTIVE))
			((Context)node).setOnActive((String)value);
		if (id.equals(Context.PROPERTY_ONLEAVE))
			((Context)node).setOnLeave((String)value);
		if (id.equals(Context.PROPERTY_DEFAULT))
			((Context)node).setDefault(((Boolean)value).booleanValue());
		if (id.equals(Context.PROPERTY_ERROR))
			((Context)node).setError(((Boolean)value).booleanValue());
		if (id.equals(ContextGroup.PROPERTY_NAME))
			((ContextGroup)node).setName((String)value);
		if (id.equals(ContextDiagram.PROPERTY_NAME))
			((ContextDiagram)node).setName((String)value);
	}
	
	public class CheckboxPropertyDescriptor extends PropertyDescriptor {
		/**
		* @param id
		* @param displayName
		*/
		public CheckboxPropertyDescriptor(Object id, String displayName) {
			super(id, displayName);
			// TODO Auto-generated constructor stub
		}

		/* (non-Javadoc)
		* @see
		org.eclipse.ui.views.properties.IPropertyDescriptor#createPr opertyEditor(org.eclipse.swt.widgets.Composite)
		*/
		public CellEditor createPropertyEditor(Composite parent) {
			CellEditor editor = new CheckboxCellEditor(parent);
			if (getValidator() != null)
				editor.setValidator(getValidator());
			return editor;
		}

	}

}
