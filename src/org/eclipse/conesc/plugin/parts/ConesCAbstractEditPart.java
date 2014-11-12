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
import java.beans.PropertyChangeListener;

import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

public abstract class ConesCAbstractEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener {
	
	public void activate() {
		super.activate();
		((Node)getModel()).addPropertyChangeListener(this);
	}
	
	public void deactivate() {
		super.deactivate();
		((Node)getModel()).removePropertyChangeListener(this);
	}
	
	@Override
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)){
			try{
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				page.showView(IPageLayout.ID_PROP_SHEET);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
		}
	}

}
