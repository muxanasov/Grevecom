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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class ContextDiagramFigure extends Figure {
	private XYLayout layout;
	private Label name = new Label("");

	public ContextDiagramFigure() {
		layout = new XYLayout();
		setLayoutManager(layout);

		name.setForegroundColor(ColorConstants.black);
		add(name);
		setConstraint(name, new Rectangle(5,5,-1,-1));

		setForegroundColor(ColorConstants.black);
		setBorder(new LineBorder(5));
	}

	public void setLayout(Rectangle rect) {
		setBounds(rect);
	}

	public void setName(String name) {
		this.name.setText(name);
	}
}