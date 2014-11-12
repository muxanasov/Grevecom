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

import java.util.HashMap;
import java.util.List;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.wizards.ContextEditWizard;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

public class ContextEditAction extends SelectionAction {
	
	public ContextEditAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}
	
	public void init() {
		setText("Edit...");
		setToolTipText("Change Conetxt's name and onEnter/Active/Leave actions.");
		setId(ActionFactory.EDIT_ACTION_SETS.getId());
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createEditCommand(null);
		return cmd != null;
	}
	
	public Command createEditCommand(ContextEditWizard wizard) {
		Request req = new Request("edit");
		
		HashMap<String, Object> reqData = new HashMap<String, Object>();
		if (wizard != null) {
			reqData.put("newName", wizard.getName());
			reqData.put("newOnEnter", wizard.getOnEnter());
			reqData.put("newOnActive", wizard.getOnActive());
			reqData.put("newOnLeave", wizard.getOnLeave());
			reqData.put("isDefault", wizard.isDefault());
			reqData.put("isError", wizard.isError());
			reqData.put("triggers", wizard.getTriggers());
		}
		
		req.setExtendedData(reqData);
		if (!getSelectedObjects().isEmpty() && getSelectedObjects().get(0) instanceof EditPart)
			return ((EditPart)getSelectedObjects().get(0)).getCommand(req);
		return null;
	}
	
	public void run() {
		Context context = getSelectedContext();
		if (context == null) return;
		
		ContextEditWizard wizard = new ContextEditWizard(context);
		WizardDialog dialog = new WizardDialog(getWorkbenchPart().getSite().getShell(),wizard);
		dialog.create();
		dialog.getShell().setSize(400, 400);
		dialog.setTitle("Context edit wizard.");
		dialog.setMessage("Editing fields of selected context.");
		if (dialog.open() == WizardDialog.OK) {
			execute(createEditCommand(wizard));
		}
	}
	
	private Context getSelectedContext() {
		List objects = getSelectedObjects();
		if (objects.isEmpty()) return null;
		if (!(objects.get(0) instanceof EditPart)) return null;
		EditPart part = (EditPart)objects.get(0);
		if (!(part.getModel() instanceof Context)) return null;
		return (Context)part.getModel();
	}

}
