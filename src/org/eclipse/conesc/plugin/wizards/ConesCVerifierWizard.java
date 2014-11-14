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

import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.conesc.plugin.utils.Cashe;
import org.eclipse.conesc.plugin.utils.HelperAssistant;
import org.eclipse.conesc.plugin.verifier.ConesCModelVerifier;
import org.eclipse.conesc.plugin.verifier.NuSMVResultParser;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class ConesCVerifierWizard extends Wizard {
	
	private class EditPage extends WizardPage {
		public Text constraints;
		public Text output;
		public ExpandBar bar;
		public EditPage(String pageName) {
			super(pageName);
			setTitle("Verification.");
			setDescription("Verify you model agains environment evolutions.");
		}
		@Override
		public void createControl(Composite parent) {
			final Composite composite = new Composite(parent, SWT.NONE);
			GridLayout gl = new GridLayout();
			int ncol = 6;
			gl.numColumns = ncol;
			composite.setLayout(gl);
			
			new Label (composite, SWT.NONE).setText("Constraints:");				
			constraints = new Text(composite, SWT.BORDER);
			constraints.setText(Cashe.load("constraints"));
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 4;
			constraints.setLayoutData(gd);

			HelperAssistant.applyAssistentTo(constraints, diagram);
			
			Button verify = new Button(composite, SWT.PUSH);
			verify.setText("Verify...");
			verify.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent evt){
					Cashe.deposit("constraints", getConstraints());
					NuSMVResultParser parser = new NuSMVResultParser(new ConesCModelVerifier(diagram), constraints.getText());
					output.setText(parser.parse());
					parser.displayResultsOn(bar);
				}
			});
			
			output = new Text(composite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.V_SCROLL | SWT.H_SCROLL);
			output.setText("");
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 6;
			gd.heightHint = 6 * output.getLineHeight();
			output.setLayoutData(gd);
			
			// counterexamples' tab
			
			bar = new ExpandBar (composite, SWT.V_SCROLL);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 6;
			gd.heightHint = 200;
			bar.setLayoutData(gd);
			
			setControl(composite);
		}
	}
	
	private ContextDiagram diagram;
	private String constraints;
	
	public ConesCVerifierWizard(ContextDiagram model) {
		this.diagram = model;
		addPage(new EditPage("Verification"));
	}

	@Override
	public boolean performFinish() {
		EditPage page = (EditPage)getPage("Verification");
		
		constraints = page.constraints.getText();
		return true;
	}
	
	public String getConstraints() {
		return constraints;
	}
}

