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
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.utils.Cashe;
import org.eclipse.conesc.plugin.wizards.ConesCGeneratorWizard;
import org.eclipse.conesc.plugin.wizards.ContextEditWizard;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

public class ConesCGenerateAction extends SelectionAction {
	
	public ConesCGenerateAction(IWorkbenchPart part) {
		super(part);
		setLazyEnablementCalculation(false);
	}
	
	public void init() {
		setText("Generate...");
		setToolTipText("Generate a source-code skeleton for the application based on the diagram.");
		setId(ActionFactory.EXPORT.getId());
		setEnabled(false);
	}

	@Override
	protected boolean calculateEnabled() {
		Command cmd = createGenerateCommand(null);
		return cmd != null;
	}
	
	public Command createGenerateCommand(ConesCGeneratorWizard wizard) {
		Request req = new Request("generate");
		
		HashMap<String, String> reqData = new HashMap<String, String>();
		if (wizard != null) {
			reqData.put("output", wizard.getOutput()+"/");
		}
		
		req.setExtendedData(reqData);
		if (!getSelectedObjects().isEmpty() && getSelectedObjects().get(0) instanceof EditPart)
			return ((EditPart)getSelectedObjects().get(0)).getCommand(req);
		return null;
	}
	
	public void run() {
		ContextDiagram diagram = getSelectedDiagram();
		if (diagram == null) return;
		
		ConesCGeneratorWizard wizard = new ConesCGeneratorWizard(diagram);
		WizardDialog dialog = new WizardDialog(getWorkbenchPart().getSite().getShell(),wizard);
		dialog.create();
		dialog.getShell().setSize(400, 200);
		dialog.setTitle("ConesC Generator output path.");
		dialog.setMessage("Specify an output folder for generator.");
		if (dialog.open() == WizardDialog.OK) {
			Cashe.deposit("output", wizard.getOutput());
			execute(createGenerateCommand(wizard));
		}
	}
	
	private ContextDiagram getSelectedDiagram() {
		List objects = getSelectedObjects();
		if (objects.isEmpty()) return null;
		if (!(objects.get(0) instanceof EditPart)) return null;
		EditPart part = (EditPart)objects.get(0);
		if (!(part.getModel() instanceof ContextDiagram)) return null;
		return (ContextDiagram)part.getModel();
	}

}
