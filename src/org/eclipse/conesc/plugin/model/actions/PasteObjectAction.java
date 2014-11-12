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

import org.eclipse.conesc.plugin.model.commands.PasteObjectCommand;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

public class PasteObjectAction extends SelectionAction{
	public PasteObjectAction(IWorkbenchPart part) {
		super(part);
		// force calculateEnabled() to be called in every context
		setLazyEnablementCalculation(true);
	}
	protected void init()
	{
		super.init();
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setText("Paste");
		setId(ActionFactory.PASTE.getId());
		setHoverImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
		setEnabled(false);
	}
	private Command createPasteCommand() {
		return new PasteObjectCommand();
	}
	@Override
	protected boolean calculateEnabled() {
		Command command = createPasteCommand();
		return command != null && command.canExecute();
	}
	@Override
	public void run() {
		Command command = createPasteCommand();
		if (command != null && command.canExecute())
			execute(command);
	}
}
