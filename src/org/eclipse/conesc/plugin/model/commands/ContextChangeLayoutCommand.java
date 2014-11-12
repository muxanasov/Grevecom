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

import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.draw2d.geometry.Rectangle;


public class ContextChangeLayoutCommand extends AbstractLayoutCommand {
	
	private Context model;
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
		this.model = (Context)model;
		this.oldLayout = ((Context)model).getLayout();
	}
	
	public void undo() {
		this.model.setLayout(oldLayout);
	}

}
