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

package org.eclipse.conesc.plugin.factories;

import java.util.List;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.parts.tree.ContextDiagramTreeEditPart;
import org.eclipse.conesc.plugin.parts.tree.ContextGroupTreeEditPart;
import org.eclipse.conesc.plugin.parts.tree.ContextTreeEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;

public class ConesCTreeEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if (model instanceof ContextDiagram)
			part = new ContextDiagramTreeEditPart();
		else if (model instanceof ContextGroup)
			part = new ContextGroupTreeEditPart();
		else if (model instanceof Context)
			part = new ContextTreeEditPart();
		
		if (part != null)
			part.setModel(model);
		
		return part;
	}

}
