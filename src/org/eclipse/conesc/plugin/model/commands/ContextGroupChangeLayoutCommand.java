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

package org.eclipse.conesc.plugin.model.commands;

import org.eclipse.conesc.plugin.model.ContextGroup;
import org.eclipse.draw2d.geometry.Rectangle;

public class ContextGroupChangeLayoutCommand extends AbstractLayoutCommand {
	
	private ContextGroup model;
	private Rectangle layout;
	private Rectangle oldLayout;
	
	public void execute() {
		model.setLayout(layout);
	}

	@Override
	public void setConstraint(Rectangle rect) {
		this.layout = rect;
	}

	@Override
	public void setModel(Object model) {
		this.model = (ContextGroup)model;
		this.oldLayout = ((ContextGroup)model).getLayout();
	}
	
	public void undo() {
		this.model.setLayout(oldLayout);
	}

}