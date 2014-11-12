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

package org.eclipse.conesc.plugin.model.actions;

import java.util.Iterator;
import java.util.List;

import org.eclipse.conesc.plugin.model.commands.CopyObjectCommand;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

public class CopyObjectAction extends SelectionAction {
	public CopyObjectAction(IWorkbenchPart part) {
		super(part);
		// force calculateEnabled() to be called in every context
		setLazyEnablementCalculation(true);
	}
	@Override
	protected void init() {
		super.init();
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText("Copy");
		setId(ActionFactory.COPY.getId());
		setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));
		setEnabled(false);
	}
	private Command createCopyCommand(List<Object> selectedObjects) {
		if (selectedObjects == null || selectedObjects.isEmpty()) {
			return null;
		}
		CopyObjectCommand cmd = new CopyObjectCommand();
		Iterator<Object> it = selectedObjects.iterator();
		while (it.hasNext()) {
			EditPart ep = (EditPart)it.next();
			Object model = ep.getModel();
			if (!cmd.isCopyable(model))
				return null;
			cmd.addElement(model);
		}
		return cmd;
	}
	@Override
	protected boolean calculateEnabled() {
		Command cmd = createCopyCommand(getSelectedObjects());
		if (cmd == null)
			return false;
		return cmd.canExecute();
	}
	@Override
	public void run() {
		Command cmd = createCopyCommand(getSelectedObjects());
		if (cmd != null && cmd.canExecute()) {
			cmd.execute();
		} 
	}
}