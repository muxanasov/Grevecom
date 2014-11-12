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

package org.eclipse.conesc.plugin.editpolicies;

import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.model.commands.AbstractLayoutCommand;
import org.eclipse.conesc.plugin.model.commands.ComponentCreateCommand;
import org.eclipse.conesc.plugin.model.commands.ContextChangeLayoutCommand;
import org.eclipse.conesc.plugin.model.commands.ContextGroupChangeLayoutCommand;
import org.eclipse.conesc.plugin.parts.ContextDiagramPart;
import org.eclipse.conesc.plugin.parts.ContextGroupPart;
import org.eclipse.conesc.plugin.parts.ContextPart;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

public class ConesCEditLayoutPolicy extends XYLayoutEditPolicy {
	
	@Override
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		AbstractLayoutCommand com = null;
		if (child instanceof ContextPart) {
			com = new ContextChangeLayoutCommand();
		} else if (child instanceof ContextGroupPart) {
			com = new ContextGroupChangeLayoutCommand();
		}
		
		com.setModel(child.getModel());
		com.setConstraint((Rectangle)constraint);
		return com;
	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		if (request.getType() == REQ_CREATE &&
			(getHost() instanceof ContextDiagramPart || getHost() instanceof ContextGroupPart)) {
			
			Rectangle bounds = (Rectangle)getConstraintFor(request);
			if (getHost() instanceof ContextDiagramPart){
				bounds.setSize(330,170);
				bounds.setLocation(request.getLocation());
			} else {
				bounds.setSize(150,105);
				bounds.x = request.getLocation().x - ((Node)getHost().getModel()).getLayout().x;
				bounds.y = request.getLocation().y - ((Node)getHost().getModel()).getLayout().y;
			}
			return new ComponentCreateCommand((Node)request.getNewObject(),
											  (Node)getHost().getModel(),
											  bounds);
		}
		return null;
	}

}
