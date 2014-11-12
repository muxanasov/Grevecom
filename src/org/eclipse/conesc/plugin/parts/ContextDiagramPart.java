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
import java.util.List;

import org.eclipse.conesc.plugin.editpolicies.ConesCEditLayoutPolicy;
import org.eclipse.conesc.plugin.editpolicies.ConesCGeneratePolicy;
import org.eclipse.conesc.plugin.editpolicies.ConesCVerifyPolicy;
import org.eclipse.conesc.plugin.figure.ContextDiagramFigure;
import org.eclipse.conesc.plugin.figure.ContextGroupFigure;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class ContextDiagramPart extends ConesCAbstractEditPart{

	@Override
	protected IFigure createFigure() {
		return new ContextDiagramFigure();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new ConesCEditLayoutPolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new ConesCGeneratePolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ConesCVerifyPolicy());
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_LAYOUT)||
			evt.getPropertyName().equals(ContextDiagram.PROPERTY_NAME)) refreshVisuals();
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD) ||
			evt.getPropertyName().equals(Node.PROPERTY_REMOVE)) refreshChildren();
	}
	
	@Override
	protected void refreshVisuals() {
		ContextDiagramFigure figure = (ContextDiagramFigure)getFigure();
		ContextDiagram model = (ContextDiagram)getModel();
		
		figure.setName(model.getName());
		figure.setLayout(model.getLayout());
	}
	
	@Override
	protected List<Node> getModelChildren() {
		return ((ContextDiagram)getModel()).getChildrenArray();
	}

}
