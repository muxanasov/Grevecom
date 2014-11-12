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

package org.eclipse.conesc.plugin.wizards;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.utils.Cashe;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ConesCGeneratorWizard extends Wizard {
	
	private class EditPage extends WizardPage {
		public Text output;
		public EditPage(String pageName) {
			super(pageName);
			setTitle("Output folder for generator.");
			setDescription("Specify an output folder for generator.");
		}
		@Override
		public void createControl(Composite parent) {
			final Composite composite = new Composite(parent, SWT.NONE);
			GridLayout gl = new GridLayout();
			int ncol = 6;
			gl.numColumns = ncol;
			composite.setLayout(gl);
			
			new Label (composite, SWT.NONE).setText("Output folder:");				
			output = new Text(composite, SWT.BORDER);
			output.setText(Cashe.load("output"));
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 4;
			output.setLayoutData(gd);
			
			Button browse = new Button(composite, SWT.PUSH);
			browse.setText("Browse...");
			browse.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent evt){
					DirectoryDialog dlg = new DirectoryDialog(composite.getShell());
					dlg.setFilterPath(output.getText());
					dlg.setText("Generator's output folder.");
					dlg.setMessage("Select a directory.");
					String dir = dlg.open();
					if (dir != null) output.setText(dir);
				}
			});
			
			setControl(composite);
		}
	}
	
	private ContextDiagram diagram;
	private String output;
	
	public ConesCGeneratorWizard(ContextDiagram model) {
		this.diagram = model;
		addPage(new EditPage("GeneratorOutput"));
	}

	@Override
	public boolean performFinish() {
		EditPage page = (EditPage)getPage("GeneratorOutput");
		
		if (page.output.getText().isEmpty()) {
			page.setErrorMessage("Output folder is required!");
			return false;
		}
		
		output = page.output.getText();
		return true;
	}
	
	public String getOutput() {
		return output;
	}
}

