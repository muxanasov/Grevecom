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

package org.eclipse.conesc.plugin.parts;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.editpolicies.ConesCConnectionPolicy;
import org.eclipse.conesc.plugin.editpolicies.ConesCDeletePolicy;
import org.eclipse.conesc.plugin.editpolicies.ConesCEditLayoutPolicy;
import org.eclipse.conesc.plugin.editpolicies.ContextEditPolicy;
import org.eclipse.conesc.plugin.figure.ContextFigure;
import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.model.commands.ConnectionCreateCommand;
import org.eclipse.conesc.plugin.model.commands.ConnectionReconnectCommand;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public class ContextPart extends ConesCAbstractEditPart implements NodeEditPart{

	@Override
	protected IFigure createFigure() {
		return new ContextFigure();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ConesCEditLayoutPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ConesCDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new ContextEditPolicy());
		// allow the creation of connections and
		// and the reconnection of connections between Shape instances
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new ConesCConnectionPolicy());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)||
			evt.getPropertyName().equals(Context.PROPERTY_EDIT)||
			evt.getPropertyName().equals(Context.PROPERTY_NAME)||
			evt.getPropertyName().equals(Context.PROPERTY_ONACTIVE)||
			evt.getPropertyName().equals(Context.PROPERTY_ONENTER)||
			evt.getPropertyName().equals(Context.PROPERTY_ONLEAVE)||
			evt.getPropertyName().equals(Context.PROPERTY_ONLEAVE)) refreshVisuals();
		if (evt.getPropertyName().equals(Context.SOURCE_CONNECTION))
			refreshSourceConnections();
		if (evt.getPropertyName().equals(Context.TARGET_CONNECTION))
			refreshTargetConnections();
	}
	
	protected void refreshVisuals() {
		ContextFigure fig = (ContextFigure)getFigure();
		Context model = (Context)getModel();
		
		fig.setName(model.getName());
		fig.setOnEnter(model.getOnEnter());
		fig.setOnActive(model.getOnActive());
		fig.setOnLeave(model.getOnLeave());
		fig.setLayout(model.getLayout());
		fig.setDefault(model.isDefault());
		fig.setError(model.isError());
		fig.setTriggers(model.getTriggers());
		fig.redraw();
	}
	
	@Override
	protected List<Node> getModelChildren() {
		return new ArrayList<Node>();
	}
	
	public List getModelSourceConnections() {
		return ((Context)getModel()).getSourceConnections();
	}
	
	public List getModelTargetConnections() {
		return ((Context)getModel()).getTargetConnections();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

}
