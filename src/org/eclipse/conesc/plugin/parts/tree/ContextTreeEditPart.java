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

package org.eclipse.conesc.plugin.parts.tree;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.conesc.plugin.editpolicies.ConesCDeletePolicy;
import org.eclipse.conesc.plugin.editpolicies.ContextEditPolicy;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.EditPolicy;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class ContextTreeEditPart extends ConesCAbstractTreeEditPart {
	
	protected List<Node> getModelChildren() {
		return ((Context)getModel()).getChildrenArray();
	}
	
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ConesCDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new ContextEditPolicy());
	}
	
	public void refreshVisuals() {
		Context model = (Context)getModel();
		setWidgetText(model.getName());
		
		setWidgetImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_DEF_VIEW));
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD) ||
			evt.getPropertyName().equals(Node.PROPERTY_REMOVE)||
			evt.getPropertyName().equals(Context.PROPERTY_EDIT)) refreshVisuals();
	}

}
