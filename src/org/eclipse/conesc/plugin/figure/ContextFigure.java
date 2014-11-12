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

package org.eclipse.conesc.plugin.figure;

import org.eclipse.conesc.plugin.ConesCPlugin;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;

public class ContextFigure extends Figure{
	private Label name = new Label();
	private Label onEnterLabel = new Label();
	private Label onEnter = new Label();
	private Label onActiveLabel = new Label();
	private Label onActive = new Label();
	private Label onLeaveLabel = new Label();
	private Label onLeave = new Label();
	private Label triggersLabel = new Label();
	private Label triggers = new Label();
	private boolean isDefault = false;
	private boolean isError = false;
	
	private ContextCompartmentFigure behaviours;
	
	public ContextFigure() {
		behaviours = new ContextCompartmentFigure();
		name.setForegroundColor(ColorConstants.black);
		
		onEnterLabel.setText("on enter:");
		onEnterLabel.setForegroundColor(ColorConstants.black);
		onEnter.setForegroundColor(ColorConstants.darkGray);
		
		onActiveLabel.setText("on active:");
		onActiveLabel.setForegroundColor(ColorConstants.black);
		onActive.setForegroundColor(ColorConstants.darkGray);
		
		onLeaveLabel.setText("on leave:");
		onLeaveLabel.setForegroundColor(ColorConstants.black);
		onLeave.setForegroundColor(ColorConstants.darkGray);
		
		triggersLabel.setText("triggers:");
		triggersLabel.setForegroundColor(ColorConstants.black);
		triggers.setForegroundColor(ColorConstants.darkGray);
		
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		
		setForegroundColor(ColorConstants.black);
		setBackgroundColor(new Color(null, 255, 255, 206));
		
		setBorder(new LineBorder(1));
		setOpaque(true);
		
		redraw();
	}
	
	public void redraw() {
		behaviours.removeAll();
		name.setIcon(null);
		if (isDefault) name.setIcon(ImageDescriptor.createFromFile(ConesCPlugin.class, "icons/default_8.png").createImage());
		else if (isError) name.setIcon(ImageDescriptor.createFromFile(ConesCPlugin.class, "icons/error_8.png").createImage());
		if (!triggers.getText().isEmpty()) {
			behaviours.add(triggers, ToolbarLayout.ALIGN_CENTER, 0);
			behaviours.add(triggersLabel, ToolbarLayout.ALIGN_CENTER, 0);
		}
		if (!onLeave.getText().isEmpty()) {
			behaviours.add(onLeave, ToolbarLayout.ALIGN_CENTER, 0);
			behaviours.add(onLeaveLabel, ToolbarLayout.ALIGN_CENTER, 0);
		}
		
		if (!onActive.getText().isEmpty()){
			behaviours.add(onActive, ToolbarLayout.ALIGN_TOPLEFT, 0);
			behaviours.add(onActiveLabel, ToolbarLayout.ALIGN_TOPLEFT, 0);
		}
		
		if (!onEnter.getText().isEmpty()){
			behaviours.add(onEnter, ToolbarLayout.ALIGN_TOPLEFT, 0);
			behaviours.add(onEnterLabel, ToolbarLayout.ALIGN_TOPLEFT, 0);
		}
		add(behaviours, 0);
		add(name, ToolbarLayout.ALIGN_CENTER, 0);
	}
	
	public void setLayout(Rectangle rect) {
		getParent().setConstraint(this, rect);
	}
	
	public void setName(String n) {
		name.setText(n);
	}
	
	public void setOnEnter(String n) {
		onEnter.setText(n);
	}
	
	public void setOnActive(String n) {
		onActive.setText(n);
	}
	
	public void setOnLeave(String n) {
		onLeave.setText(n);
	}
	
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public void setError(boolean isError) {
		this.isError = isError;
	}
	public void setTriggers(String t) {
		triggers.setText(t);
	}
}
