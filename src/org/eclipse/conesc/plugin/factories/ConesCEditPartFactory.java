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

import org.eclipse.conesc.plugin.model.Connection;
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.conesc.plugin.parts.ConnectionEditPart;
import org.eclipse.conesc.plugin.parts.ContextDiagramPart;
import org.eclipse.conesc.plugin.parts.ContextGroupPart;
import org.eclipse.conesc.plugin.parts.ContextPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public class ConesCEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		AbstractGraphicalEditPart part = null;

		if (model instanceof ContextDiagram) {
			part = new ContextDiagramPart();
		} else if (model instanceof ContextGroup) {
			part = new ContextGroupPart();
		} else if (model instanceof Context) {
			part = new ContextPart();
		} else if (model instanceof Connection) {
			part = new ConnectionEditPart();
		}

		part.setModel(model);
		return part;
	}

}
