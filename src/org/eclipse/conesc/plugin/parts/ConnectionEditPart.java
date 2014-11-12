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
package org.eclipse.conesc.plugin.parts;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.conesc.plugin.editpolicies.ConnectionBendpointEditPolicy;
import org.eclipse.conesc.plugin.figure.ContextFigure;
import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.ConnectionBendpoint;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.model.commands.ConnectionDeleteCommand;
import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.AccessibleEvent;

/**
 * Edit part for Connection model elements.
 * <p>
 * This edit part must implement the PropertyChangeListener interface, so it can
 * be notified of property changes in the corresponding model element.
 * </p>
 * 
 * @author Elias Volanakis
 */
public class ConnectionEditPart extends AbstractConnectionEditPart implements PropertyChangeListener{
	
	AccessibleEditPart acc;

	@Override
	public void activate() {
		if (!isActive()) {
			super.activate();
			((Connection)getModel()).addPropertyChangeListener(this);
		}
	}
	
	public void activateFigure() {
		super.activateFigure();
		/*
		 * Once the figure has been added to the ConnectionLayer, start
		 * listening for its router to change.
		 */
		getFigure().addPropertyChangeListener(
				org.eclipse.draw2d.Connection.PROPERTY_CONNECTION_ROUTER, this);
	}
	
	@Override
	protected void createEditPolicies() {
		// Selection handle edit policy.
		// Makes the connection show a feedback, when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		// Note that the Connection is already added to the diagram and knows
		// its Router.
		refreshBendpointEditPolicy();
		// Allows the removal of the connection model element
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new ConnectionEditPolicy() {
					@Override
					protected Command getDeleteCommand(GroupRequest request) {
						return new ConnectionDeleteCommand(getCastedModel());
					}
				});
	}

	
	@Override
	protected IFigure createFigure() {
		PolylineConnection connection = (PolylineConnection) super.createFigure();
		PolygonDecoration decor = new PolygonDecoration();
		decor.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		connection.setTargetDecoration(decor);
		connection.setLineJoin(SWT.JOIN_ROUND);
		connection.setConnectionRouter(new BendpointConnectionRouter());
		Label label = new Label();
		label.setText(((Connection)getModel()).getLabel());
		label.setOpaque(true);
		connection.add(label, new ConnectionLocator(connection, ConnectionLocator.MIDDLE));
		return connection;
	}

	/**
	 * Upon deactivation, detach from the model element as a property change
	 * listener.
	 */
	@Override
	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((Connection)getModel()).removePropertyChangeListener(this);
		}
	}
	
	public void deactivateFigure() {
		getFigure().removePropertyChangeListener(
				org.eclipse.draw2d.Connection.PROPERTY_CONNECTION_ROUTER, this);
		super.deactivateFigure();
	}
	
	public AccessibleEditPart getAccessibleEditPart() {
		if (acc == null)
			acc = new AccessibleGraphicalEditPart() {
				public void getName(AccessibleEvent e) {
					e.result = "AccessiblePart";
				}
			};
		return acc;
	}

	private Connection getCastedModel() {
		return (Connection) getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Connection.PROPERTY_LABEL)) refreshVisuals();
		String property = evt.getPropertyName();
		if (org.eclipse.draw2d.Connection.PROPERTY_CONNECTION_ROUTER.equals(property)) {
			refreshBendpoints();
			refreshBendpointEditPolicy();
		}
		if (Connection.PROPERTY_BENDPOINTS.equals(property)) //$NON-NLS-1$
			refreshBendpoints();
	}
	
	protected void refreshBendpoints() {
		//if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
		//	return;
		List modelConstraint = getCastedModel().getBendpoints();
		List figureConstraint = new ArrayList();
		for (int i = 0; i < modelConstraint.size(); i++) {
			ConnectionBendpoint wbp = (ConnectionBendpoint) modelConstraint.get(i);
			RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
			rbp.setRelativeDimensions(wbp.getFirstRelativeDimension(),
					wbp.getSecondRelativeDimension());
			rbp.setWeight((i + 1) / ((float) modelConstraint.size() + 1));
			figureConstraint.add(rbp);
		}
		getConnectionFigure().setRoutingConstraint(figureConstraint);
	}

	private void refreshBendpointEditPolicy() {
		if (getConnectionFigure().getConnectionRouter() instanceof ManhattanConnectionRouter)
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE, null);
		else
			installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
					new ConnectionBendpointEditPolicy());
	}
	
	protected void refreshVisuals() {
		refreshBendpoints();
		PolylineConnection con = (PolylineConnection)getFigure();
		for (Object obj : con.getChildren()) 
			if (obj instanceof Label) {
				((Label)obj).setText(getCastedModel().getLabel());
				break;
			}
	}

}