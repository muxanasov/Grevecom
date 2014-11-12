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
import org.eclipse.conesc.plugin.model.Context;
import org.eclipse.conesc.plugin.model.ContextDiagram;
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.commands.Command;

public class ConesCGenerateCommand extends Command {
	
	private ContextDiagram diagram;
	private String output = "";
	
	public void execute() {
		new ConesCCodeGenerator(diagram).generate(output);
	}
	
	public void setModel(Object model) {
		if (model instanceof ContextDiagram) this.diagram = (ContextDiagram)model;
	}
	
	public void setOutput(String output) {
		this.output = output;
	}
	
	public boolean canUndo() {
		return false;
	}

}