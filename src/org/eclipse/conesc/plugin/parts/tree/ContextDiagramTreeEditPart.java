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

import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.Node;

public class ContextDiagramTreeEditPart extends ConesCAbstractTreeEditPart {
	
	protected List<Node> getModelChildren() {
		return ((ContextDiagram)getModel()).getChildrenArray();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(Node.PROPERTY_ADD) ||
			evt.getPropertyName().equals(Node.PROPERTY_REMOVE)) refreshChildren();
	}

}
