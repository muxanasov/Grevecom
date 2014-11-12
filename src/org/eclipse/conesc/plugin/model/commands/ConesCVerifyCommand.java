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
import org.eclipse.conesc.plugin.ConesCCodeGenerator;
import org.eclipse.conesc.plugin.ConesCModelVerifier;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.gef.commands.Command;


public class ConesCVerifyCommand extends Command {
	
	private ContextDiagram diagram;
	private String constraints = "";
	
	public void execute() {
		new ConesCModelVerifier(diagram).verify(constraints);
	}
	
	public void setModel(Object model) {
		if (model instanceof ContextDiagram) this.diagram = (ContextDiagram)model;
	}
	
	public boolean canUndo() {
		return false;
	}

	public void setConstraints(String string) {
		constraints = string;
	}

}
