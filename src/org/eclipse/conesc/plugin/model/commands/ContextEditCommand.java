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
import org.eclipse.conesc.plugin.model.Node;
import org.eclipse.gef.commands.Command;

public class ContextEditCommand extends Command {
	
	private Context model;
	private String oldName;
	private String newName;
	private String oldOnEnter;
	private String newOnEnter;
	private String oldOnActive;
	private String newOnActive;
	private String oldOnLeave;
	private String newOnLeave;
	private boolean oldDflt = false;
	private boolean newDflt = false;
	private boolean oldError = false;
	private boolean newError = false;
	private String oldTriggers;
	private String newTriggers;
	
	public void execute() {
		oldName = model.getName();
		oldOnEnter = model.getOnEnter();
		oldOnActive = model.getOnActive();
		oldOnLeave = model.getOnLeave();
		oldDflt = model.isDefault();
		oldError = model.isError();
		oldTriggers = model.getTriggers();
		model.setName(newName);
		model.setOnEnter(newOnEnter);
		model.setOnActive(newOnActive);
		model.setOnLeave(newOnLeave);
		model.setDefault(newDflt);
		model.setError(newError);
		model.setTriggers(newTriggers);
	}
	
	public void setModel(Object model) {
		if (model instanceof Context) this.model = (Context)model;
	}
	
	public void setNewName(String name) {
		newName = name;
	}
	
	public void setNewOnEnter(String onEnter) {
		newOnEnter = onEnter;
	}
	
	public void setNewOnActive(String onActive) {
		newOnActive = onActive;
	}
	
	public void setNewOnLeave(String onLeave) {
		newOnLeave = onLeave;
	}
	
	public void setDefault(boolean dflt){
		this.newDflt = dflt;
	}
	
	public void setError(boolean error) {
		this.newError = error;
	}
	
	public void setTriggers(String t) {
		this.newTriggers = t;
	}
	
	public void undo() {
		model.setName(oldName);
		model.setOnEnter(oldOnEnter);
		model.setOnActive(oldOnActive);
		model.setOnLeave(oldOnLeave);
		model.setDefault(oldDflt);
		model.setError(oldError);
		model.setTriggers(oldTriggers);
	}

}
