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

public class ContextGroupFigure extends Figure {

	private Label labelName = new Label();
	
	public ContextGroupFigure() {
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);

		labelName.setForegroundColor(ColorConstants.black);
		add(labelName, ToolbarLayout.ALIGN_CENTER);
		setConstraint(labelName, new Rectangle(5, 5, -1, -1));

		setForegroundColor(ColorConstants.black);
		setBackgroundColor(ColorConstants.white);
		
		setBorder(new LineBorder(1));
		setOpaque(true);
	}

	public void setLayout(Rectangle rect) {
		getParent().setConstraint(this, rect);
	}

	public void setName(String text) {
		labelName.setText(text);
	}
}
