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

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class ContextEditWizard extends Wizard {
	
	private class EditPage extends WizardPage {
		public Text name;
		public Text onEnter;
		public Text onActive;
		public Text onLeave;
		public Button isDefault;
		public Button isError;
		final HashMap<String, HashMap<String,Button> > checks = new HashMap<String, HashMap<String,Button> >();
		public EditPage(String pageName) {
			super(pageName);
			setTitle("Edit Context");
			setDescription("Change the name and actions of context livecycle");
		}
		@Override
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NONE);
			GridLayout gl = new GridLayout();
			int ncol = 2;
			gl.numColumns = ncol;
			composite.setLayout(gl);
			    
			new Label(composite, SWT.NONE).setText("Name:");						
			name = new Text(composite, SWT.BORDER);
			name.setText(model.getName());
			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ncol-1;
			name.setLayoutData(gd);
			
			new Label (composite, SWT.NONE).setText("Action on enter:");				
			onEnter = new Text(composite, SWT.BORDER);
			onEnter.setText(model.getOnEnter());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ncol - 1;
			onEnter.setLayoutData(gd);
			
			new Label (composite, SWT.NONE).setText("Behavior on active:");				
			onActive = new Text(composite, SWT.BORDER);
			onActive.setText(model.getOnActive());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ncol - 1;
			onActive.setLayoutData(gd);
			
			new Label (composite, SWT.NONE).setText("Action on leave:");				
			onLeave = new Text(composite, SWT.BORDER);
			onLeave.setText(model.getOnLeave());
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = ncol - 1;
			onLeave.setLayoutData(gd);
			
			isDefault = new Button(composite, SWT.CHECK);
			isDefault.setText("Default Context");
			isDefault.setSelection(model.isDefault());
			isError = new Button(composite, SWT.CHECK);
			isError.setText("Error Context");
			isError.setSelection(model.isError());
			
			new Label(composite, SWT.NONE).setText("Triggers:");
			new Label(composite, SWT.NONE).setText("");
			ScrolledComposite tArea = new ScrolledComposite(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
			gd = new GridData(GridData.FILL_HORIZONTAL);
			gd.horizontalSpan = 2;
			tArea.setLayoutData(gd);
			Composite tComposite = new Composite(tArea, SWT.NONE);
			FillLayout areaLayout = new FillLayout();
			areaLayout.type = SWT.HORIZONTAL;
			tComposite.setLayout(areaLayout);
			
			for (Node group:model.getParent().getParent().getChildrenArray()){
				if (group.equals(model.getParent())) continue;
				
				Composite gComposite = new Composite(tComposite, SWT.NONE);
				FillLayout gLayout = new FillLayout();
				gLayout.type = SWT.VERTICAL;
				gComposite.setLayout(gLayout);
				
				new Label(gComposite, SWT.NONE).setText(group.getName()+":");
				checks.put(group.getName().replaceAll(" ", ""), new HashMap<String, Button>());
				for (Node child:group.getChildrenArray()) {
					Button b = new Button(gComposite, SWT.CHECK);
					b.setText(child.getName());
					checks.get(group.getName().replaceAll(" ", "")).put(child.getName().replaceAll(" ", ""),b);
				}
				gComposite.setSize(gComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				for (final String key : checks.keySet())
					for (Button button : checks.get(key).values())
						button.addSelectionListener(new SelectionListener(){
							@Override
							public void widgetSelected(SelectionEvent e) {
								Button button = (Button)e.widget;
								if (!button.getSelection()) return;
								for (Button b : checks.get(key).values())
									if (!b.equals(button)) b.setSelection(false);
							}
							@Override
							public void widgetDefaultSelected(SelectionEvent e) {}
						});
				if (model.getTriggers() == null) continue;
				if (!model.getTriggers().contains(group.getName().replaceAll(" ", ""))) continue;
				String[] triggers = model.getTriggers().split("\\n");
				for (int i = 0; i < triggers.length; i++){
					if (triggers[i].split("\\.").length < 2) continue;
					String groupName = triggers[i].split("\\.")[0];
					String context = triggers[i].split("\\.")[1];
					if (!checks.containsKey(groupName)) continue;
					if (!checks.get(groupName).containsKey(context)) continue;
					checks.get(groupName).get(context).setSelection(true);
				}
			}
			tComposite.setSize(tComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			tArea.setContent(tComposite);
			
			setControl(composite);
		}
	}
	
	private String newName;
	private String newOnEnter;
	private String newOnActive;
	private String newOnLeave;
	private boolean newDefault;
	private boolean newError;
	private String newTriggers;
	private Context model;
	
	public ContextEditWizard(Context model) {
		this.model = model;
		addPage(new EditPage("ContextEditPage"));
	}

	@Override
	public boolean performFinish() {
		EditPage page = (EditPage)getPage("ContextEditPage");
		
		if (page.name.getText().isEmpty()) {
			page.setErrorMessage("Name is required!");
			return false;
		}
		
		newName = page.name.getText();
		newOnEnter = page.onEnter.getText();
		newOnActive = page.onActive.getText();
		newOnLeave = page.onLeave.getText();
		newDefault = page.isDefault.getSelection();
		newError = page.isError.getSelection();
		newTriggers = "";
		for (String key:page.checks.keySet())
			for (Button b:page.checks.get(key).values())
				if (b.getSelection()) newTriggers += key.replaceAll(" ", "")+"."+b.getText().replaceAll(" ", "")+";";
		return true;
	}
	
	public String getName() {
		return newName;
	}
	
	public String getOnEnter() {
		return newOnEnter;
	}
	
	public String getOnActive() {
		return newOnActive;
	}
	
	public String getOnLeave() {
		return newOnLeave;
	}
	
	public Boolean isDefault() {
		return new Boolean(newDefault);
	}
	
	public Boolean isError() {
		return new Boolean(newError);
	}
	public String getTriggers() {
		return newTriggers;
	}
}
